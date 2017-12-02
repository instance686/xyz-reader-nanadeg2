package com.example.xyzreader.data;

import android.arch.persistence.room.Room;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ayush on 2/12/17.
 */
public class MyApplication extends MultiDexApplication {
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-db").build();
    }

    public AppDatabase getDatabase() {
        return database;
    }
}

