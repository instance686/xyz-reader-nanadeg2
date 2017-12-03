package com.example.xyzreader.data;

import java.util.List;

/**
 * Created by ayush on 2/12/17.
 */

public interface OnLoadFinished {
public void loadFinished(List<Data> data);
public void isRunning(boolean val,int choice);
}
