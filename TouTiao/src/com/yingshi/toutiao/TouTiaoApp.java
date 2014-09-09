package com.yingshi.toutiao;

import android.app.Application;
import android.util.Log;

import com.yingshi.toutiao.storage.DatabaseHelper;
import com.yingshi.toutiao.storage.NewsDAO;
import com.yingshi.toutiao.storage.SpecialDAO;

public class TouTiaoApp extends Application {
    private static final String tag = "JIDA-App";
    
    private NewsDAO mNewsDao;
    private SpecialDAO mSpecialDAO;
    private DatabaseHelper mDatabaseHelper;
    
    public void onCreate() {
        Log.i(tag, "JIDA App is initializing");
        super.onCreate();
        mDatabaseHelper = new DatabaseHelper(this);
        mNewsDao = new NewsDAO(mDatabaseHelper.getWritableDatabase());
    }

    public NewsDAO getNewsDAO() {
        return mNewsDao;
    }

    public SpecialDAO getSpecialDAO() {
        return mSpecialDAO;
    }
}
