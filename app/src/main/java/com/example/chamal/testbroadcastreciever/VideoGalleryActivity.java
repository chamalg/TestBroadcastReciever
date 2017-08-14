package com.example.chamal.testbroadcastreciever;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.chamal.testbroadcastreciever.Model.Video;
import com.example.chamal.testbroadcastreciever.net.FileDownloader;
import com.example.chamal.testbroadcastreciever.net.JsonCalls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideoGalleryActivity extends AppCompatActivity {
    FileDownloader downloader;
    JsonCalls jsonCalls;
    ArrayList<Video> listResponce;
    ArrayList<String> listVidNames;
    ListView lvServerVidz;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_gallery);
        lvServerVidz = (ListView) findViewById(R.id.lvServerVidz);
        listResponce = new ArrayList<Video>();


        jsonCalls = new JsonCalls(VideoGalleryActivity.this);
        try {
            listResponce = jsonCalls.execute().get();
            listVidNames = new ArrayList<String>(listResponce.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < listResponce.size(); i++) {
            listVidNames.add(i, listResponce.get(i).getUrl());
        }


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listVidNames);
        lvServerVidz.setAdapter(adapter);

        lvServerVidz.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                downloader = FileDownloader.getInstance(VideoGalleryActivity.this);
                downloader.execute(listVidNames.get(position));
            }
        });
    }

}