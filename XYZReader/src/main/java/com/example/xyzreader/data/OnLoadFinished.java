package com.example.xyzreader.data;

import android.database.Cursor;

import java.util.List;

/**
 * Created by ayush on 2/12/17.
 */

public interface OnLoadFinished {
public void loadFinished(List<Data> data);
public void isRunning(boolean val,int choice);
public void isRunning(boolean val);
public void loadFinished(Cursor cursor);
}
