package com.example.xyzreader.data;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.xyzreader.remote.RemoteEndpointUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DownloadTask extends AsyncTask<Void,Void,List<Data>>{
    AppDatabase appDatabase;
    private static final String TAG ="field" ;
    JSONArray itemJson;
    List<Data> list=new ArrayList<>();
    Context c,gh;
    int choice;

    private  OnLoadFinished loadFinished;

    public DownloadTask(Context context,Context gh){
     c=context;
     loadFinished= (OnLoadFinished) gh;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        appDatabase=((MyApplication)c).getDatabase();
    }

    @Override
    protected List<Data> doInBackground(Void... voids) {


            appDatabase.myDoa().deleteData();
            try {
                itemJson = RemoteEndpointUtil.fetchJsonArray();

                for (int i = 0; i < itemJson.length(); i++) {
                    JSONObject jo = null;
                    try {
                        jo = itemJson.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Data d = new Data();
                    try {
                        d.id = jo.getString("id");
                        d.author = jo.getString("author");
                        d.title = jo.getString("title");
                        d.body = jo.getString("body");
                        d.thumb = jo.getString("thumb");
                        d.photo = jo.getString("photo");
                        d.aspect_ratio = jo.getString("aspect_ratio");
                        d.published_date = jo.getString("published_date");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    list.add(d);
                }
                appDatabase.myDoa().insertData(list);
            }
            catch (NullPointerException ex){
                return null;
            }
      //  List<Data> getData = appDatabase.myDoa().loadData();
        return list;

            }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<Data> da) {
        super.onPostExecute(da);

        loadFinished.loadFinished(da);

    }
}