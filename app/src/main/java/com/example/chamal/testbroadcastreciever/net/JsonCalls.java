package com.example.chamal.testbroadcastreciever.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.example.chamal.testbroadcastreciever.Model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Chamal on 12/27/2016.
 */

public class JsonCalls extends AsyncTask<Void, Void, ArrayList<Video>> {

    JSONObject object;
    ProgressDialog pd;
    Context context;
    Video videoModel;
    ArrayList<Video> list;

    private final String url = "http://220.247.244.22:8082/JSON_Service/VideoInfo_JSON.svc/GetVideoInfo";

    public JsonCalls(Context context) {
        this.context = context;

        list = new ArrayList<Video>();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = ProgressDialog.show(context, "Loading..Please wait", null);
        pd.setCancelable(true);
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected ArrayList<Video> doInBackground(Void... params) {
        ArrayList<Video> list = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
            String jsonData = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jArray = jsonObject.getJSONArray("Video");

            for (int i = 0; i < jArray.length(); i++) {
                object = jArray.getJSONObject(i);
                videoModel = new Video();

                videoModel.setName(object.getString("name").toString());
                videoModel.setUrl(object.getString("url").toString());
                videoModel.setVid(object.getInt("vid"));

                list.add(i, videoModel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    protected void onPostExecute(ArrayList<Video> strings) {
        super.onPostExecute(strings);
        pd.dismiss();
    }
}

