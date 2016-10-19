package com.example.chamal.testbroadcastreciever;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;


public class CustomPhoneStateListener extends PhoneStateListener {
        Context context;
        boolean isRinging;
        private static int lastState = TelephonyManager.CALL_STATE_IDLE;

    public CustomPhoneStateListener(Context context) {
        super();
        this.context = context;
        isRinging = false;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        if(lastState == state){
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isRinging = true;
                showDialog(context, incomingNumber);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                isRinging = false;
                break;
        }

        lastState = state;
    }

    public void showDialog(Context context, String number){
//        final Dialog dialog = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(true);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        String contact = getContactName(context, number);
        dialog.setTitle(contact);
        dialog.setContentView(R.layout.dialog);

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = 1;   //x position
        wmlp.y = 1;   //y position



        final VideoView videoView = (VideoView)dialog.findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(videoView);
        String videoPath = "android.resource://com.example.chamal.testbroadcastreciever/"+R.raw.trailer;
        Uri uri = Uri.parse(videoPath);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.start();

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dialog.dismiss();
                return true;
            }
        });

        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
//        Toast.makeText(context,"Call from: "+contact, Toast.LENGTH_LONG).show();
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

}
