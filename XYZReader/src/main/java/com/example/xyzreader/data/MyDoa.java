package com.example.xyzreader.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;
import android.widget.ListView;

import java.util.List;

/**
 * Created by ayush on 2/12/17.
 */
@Dao
public interface MyDoa {
    @Insert
    public void insertData(List<Data> data);
    @Query("Delete from data")
    public void deleteData();
    @Query("Select * FROM data")
    public List<Data> loadData();
    @Query("Select id FROM data")
    public Cursor loadDataintoCursor();
    @Query("Select * FROM data where id = :id")
    public Cursor loadSingleData(String id);
}
