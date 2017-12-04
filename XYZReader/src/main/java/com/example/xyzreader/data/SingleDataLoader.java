package com.example.xyzreader.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by ayush on 5/12/17.
 */

public class SingleDataLoader extends AsyncTaskLoader<Cursor> {
    private AppDatabase appDatabase;
    private String id;
    public SingleDataLoader(Context context,Context gh,String id) {
        super(gh);
        Log.v("conscall","cons"+id);
        this.id=id;
        appDatabase=((MyApplication)context).getDatabase();

    }

    @Override
    public Cursor loadInBackground() {
        Log.v("singledata","single-data");
        Cursor cursor=appDatabase.myDoa().loadSingleData(id);
        if(cursor==null){
            Log.v("nullCursor","nullCursor");
        }
        return cursor;
    }
}
