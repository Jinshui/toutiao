package com.yingshi.toutiao;

import android.app.Application;
import android.util.Log;

import com.yingshi.toutiao.storage.NewsDAO;

public class TouTiaoApp extends Application {
    private static final String tag = "JIDA-App";
    private NewsDAO mNewsDao;;

    public void onCreate() {
        Log.i(tag, "JIDA App is initializing");
        super.onCreate();
        mNewsDao = new NewsDAO(this);
    }

    public NewsDAO getNewsDAO() {
        return mNewsDao;
    }
}
