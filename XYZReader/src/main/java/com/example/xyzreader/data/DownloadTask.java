package com.example.xyzreader.data;


import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.example.xyzreader.remote.RemoteEndpointUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DownloadTask extends AsyncTask<Void,Void,String>{
    AppDatabase appDatabase;
    private static final String TAG ="field" ;
    JSONArray itemJson;
    List<Data> list=new ArrayList<>();
    Context c;
    public DownloadTask(Context context){
     c=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        appDatabase=((MyApplication)c).getDatabase();
    }

    @Override
    protected String doInBackground(Void... voids) {

        itemJson = RemoteEndpointUtil.fetchJsonArray();
        return itemJson.toString();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        JSONArray ja = null;
        try {
            ja = new JSONArray(s);
            for(int i=0;i<ja.length();i++){
                JSONObject jo=ja.getJSONObject(i);
                Data d=new Data();
                d.id=jo.getString("id" );
                d.author=jo.getString("author" );
                d.title=jo.getString("title" );
                d.body=jo.getString("body" );
                d.thumb=jo.getString("thumb" );
                d.photo=jo.getString("photo");
                d.aspect_ratio=jo.getString("aspect_ratio" );
                d.published_date=jo.getString("published_date");
                list.add(d);
                Log.v("data",d.toString());

            }

            appDatabase.myDoa().insertData(list);

            List<Data> getData=appDatabase.myDoa().loadData();
            for(Data dd:getData){
              Log.v("id",dd.getTitle());
              Log.v("photo",dd.getBody());
              Log.v("body",dd.getBody());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}