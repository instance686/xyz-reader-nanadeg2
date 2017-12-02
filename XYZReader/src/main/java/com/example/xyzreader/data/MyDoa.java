package com.example.xyzreader.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.widget.ListView;

import java.util.List;

/**
 * Created by ayush on 2/12/17.
 */
@Dao
public interface MyDoa {
    @Insert
    public void insertData(List<Data> data);
    @Delete
    public void deleteData(List<Data> data);
    @Query("Select * FROM data")
    public List<Data> loadData();
}
