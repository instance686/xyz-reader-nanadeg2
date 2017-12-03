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
    private AppDatabase appDatabase;
    private static final String TAG ="field" ;
    private JSONArray itemJson;
    Context c,gh;
    int choice;

    private  OnLoadFinished loadFinished;

    public DownloadTask(Context context,Context gh,int choice){
        c=context;
        loadFinished= (OnLoadFinished) gh;
        this.choice=choice;

    }

    @Override
    protected void onPreExecute() {
        loadFinished.isRunning(true,choice);
        super.onPreExecute();
        appDatabase=((MyApplication)c).getDatabase();
    }
    public List<Data> loadAndInsertData(){
        List<Data> list=new ArrayList<>();

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
        return list;
    }
    public List<Data> loadFromDb(){
        ArrayList<Data>  data= (ArrayList<Data>) appDatabase.myDoa().loadData();

        return data;
    }
    @Override
    protected List<Data> doInBackground(Void... voids) {
        switch (choice){
            case 1:
                Log.v("fromInsert",loadAndInsertData().get(0).toString());
                return loadAndInsertData();

            case 2:
                return loadFromDb();
            default:
                return null;

        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<Data> da) {
        super.onPostExecute(da);
        loadFinished.isRunning(false,choice);
        loadFinished.loadFinished(da);

    }
}