package com.example.xyzreader.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


/**
 * Created by ayush on 5/12/17.
 */

public class ReadAllData extends AsyncTaskLoader<Cursor> {
    private AppDatabase appDatabase;



    public ReadAllData(Context context, Context gh){
        super(gh);

        appDatabase=((MyApplication)context).getDatabase();

    }


    @Override
    public Cursor loadInBackground() {
        Cursor cursor=appDatabase.myDoa().loadDataintoCursor();
        return cursor;
    }
}
