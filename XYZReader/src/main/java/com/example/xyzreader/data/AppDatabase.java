package com.example.xyzreader.data;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by ayush on 2/12/17.
 */
@Database(entities = {Data.class},version=1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MyDoa myDoa();

}
