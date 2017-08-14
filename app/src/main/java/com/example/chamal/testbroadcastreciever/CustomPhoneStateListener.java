package com.example.chamal.testbroadcastreciever;


import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.chamal.testbroadcastreciever.Model.Contact;
import com.example.chamal.testbroadcastreciever.Sqlite.MyDatabaseAdapter;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class CustomPhoneStateListener extends PhoneStateListener {

    Context context;
    private static int lastState = -1;
    AudioManager am;
    Dialog dialog;
    private static int ringMode;
    private boolean isDialogShowing;
    int curVolume;
    int curMusicStreamVolume = 0;
    MyDatabaseAdapter adapter;
    Map<String, Integer> map;
    int count = 0;
    boolean isPrefEnabled;
    Realm realm;


    public CustomPhoneStateListener(Context context) {

            this.context = context;
            isDialogShowing = false;
            am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            adapter = new MyDatabaseAdapter(context);
            map = new HashMap<String, Integer>();
            map.put("trailer", R.raw.trailer);
            map.put("trailer02", R.raw.trailer02);
            map.put("trailer03", R.raw.trailer03);

    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        //Check whether video playback preference has enabled
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        isPrefEnabled = sharedPreferences.getBoolean("prefEnabled", true);

        System.out.println("The state is: "+state);

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:// 1 when 4n is ringing

//                if (lastState != state && isPrefEnabled && count == 0) {
                if (lastState != state && isPrefEnabled && lastState != TelephonyManager.CALL_STATE_OFFHOOK) {
                    ringMode = am.getRingerMode();
                    curMusicStreamVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                    curVolume = am.getStreamVolume(AudioManager.STREAM_RING);
                    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    showDialog(context, incomingNumber, false);
                    isDialogShowing = true;
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE: // 0 When 4n is in the idle mode
                count = 0;
                if (isDialogShowing) {
                    dialog.dismiss();
                    isDialogShowing = false;
                    am.setRingerMode(ringMode);
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, curMusicStreamVolume, 0);
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:// 2 Take a call & answer a incoming call
                if (lastState != state && count == 0 && isPrefEnabled && lastState != TelephonyManager.CALL_STATE_RINGING) {
                    ringMode = am.getRingerMode();
                    showDialog(context, "", true);
                    isDialogShowing = true;
                }
                break;
        }
        lastState = state;
    }

    public void showDialog(Context context, String number, boolean out) {
        String videoPath, videoName = null;
//        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog = new Dialog(context);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);
        TextView tvCaller = (TextView) dialog.findViewById(R.id.tvCaller);

        if (!out) {
            String contact = getContactName(context, number);

            if (contact == null) {
//            dialog.setTitle(number+"  calling...");
                tvCaller.setText(number + "  calling...");
                count = 0;
            } else {
//            dialog.setTitle(contact+" calling...");
                tvCaller.setText(contact + " calling...");
                count = 0;
            }
        }
        if (out) {
            count = 122;
            tvCaller.setText("Outgoing call..");
        }

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        wmlp.x = 1;   //x position
        wmlp.y = 1;   //y position

        final VideoView videoView = (VideoView) dialog.findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(videoView);
        mediaController.setMediaPlayer(videoView);
        RealmConfiguration config = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();

        Contact realmResult;

        Uri uri = null;
        try {
            //Get assigned video from db
            realmResult = realm.where(Contact.class).findFirst();
            videoName = realmResult.getVideoId();

            uri = Uri.parse(videoName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();

        MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer m) {

                if (ringMode == AudioManager.RINGER_MODE_SILENT) {
                    m.setVolume(0, 0);
                }
                if (ringMode == AudioManager.RINGER_MODE_NORMAL) {
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume + 5, 0);
                }
            }
        };

        videoView.setOnPreparedListener(PreparedListener);

        videoView.start();

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dialog.dismiss();
                isDialogShowing = false;
                am.setRingerMode(ringMode);
                return true;
            }
        });
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }


    public static String getContactName(Context context, String phoneNumber) {

        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }
}
