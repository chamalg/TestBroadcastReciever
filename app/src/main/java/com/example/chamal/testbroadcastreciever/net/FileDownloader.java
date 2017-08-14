package com.example.chamal.testbroadcastreciever.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.chamal.testbroadcastreciever.MainActivity;
import com.example.chamal.testbroadcastreciever.Model.Contact;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Chamal on 12/19/2016.
 */

public class FileDownloader extends AsyncTask<String, Void, Void> {
    private ProgressDialog PD;
    private static Context context;
    private final String LOG_TAG = "FtpFileDownloader";
    private final int MILLIS_IN_SEC = 1000;

    private static FileDownloader instance = null;

    private FileDownloader(Context context) {
        this.context = context;

    }

    public static FileDownloader getInstance(Context context) {
        if (instance == null) {
            instance = new FileDownloader(context);
        }
        return instance;
    }


    @Override
    protected void onPreExecute() {
        PD = ProgressDialog.show(context, "Loading..", "Please Wait ...", true);
        PD.setCancelable(true);
    }

    @Override
    protected Void doInBackground(String... name) {
//            downloadFile("http://220.247.244.22:8082/HesCBMulTest_H/Images/raw/trailer.mp4", "Sample.mp4");
//            downloadFile("http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4", "Sample.mp4");

        String rootDir = Environment.getExternalStorageDirectory()
                + File.separator + "Video";
        File rootFile = new File(rootDir);

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "test.mp4");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            //downloadAndSaveFile("220.247.244.22", 21, "Chamal", "Chamal", "C:\\Users\\Administrator\\Desktop\\raw", file);
            downloadAndSaveFile("220.247.244.22", 21, "Chamal", "Chamal", "\\" + name[0], file);

            //downloadAndSaveFile("192.168.0.199", 21, "user02", "user1234", "D:\\Season 01\\The Simpsons S01E01 480p HDTV x265 - BrB.mkv", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        PD.dismiss();
    }


    private Boolean downloadAndSaveFile(String server, int portNumber, String user, String password, String filename, File localFile) throws IOException {

        FTPClient ftp = null;

        try {
            ftp = new FTPClient();
            ftp.setConnectTimeout(5 * MILLIS_IN_SEC);
            ftp.connect(server, portNumber);
            Log.d(LOG_TAG, "Connected. Reply: " + ftp.getReplyString());

            ftp.login(user, password);
            Log.d(LOG_TAG, "Logged in");

            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            Log.d(LOG_TAG, "Downloading " + filename);
            ftp.enterLocalPassiveMode();

            OutputStream outputStream = null;
            boolean success = false;
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(
                        localFile));
                success = ftp.retrieveFile(filename, outputStream);
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            return success;

        } finally {
            if (ftp != null) {
                ftp.logout();
                ftp.disconnect();
            }
        }
    }

    private void downloadFile(String fileURL, String fileName) {
        try {
            String rootDir = Environment.getExternalStorageDirectory()
                    + File.separator + "Video";
            File rootFile = new File(rootDir);
            rootFile.mkdir();
            URL url = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            FileOutputStream f = new FileOutputStream(new File(rootFile,
                    fileName));
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
        } catch (IOException e) {
            Log.d("Error....", e.toString());
        }

    }

}