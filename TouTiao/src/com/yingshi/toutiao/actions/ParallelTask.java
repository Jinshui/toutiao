package com.yingshi.toutiao.actions;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

@SuppressLint("NewApi")
public abstract class ParallelTask<Result> extends AsyncTask<Void, Void, Result>{
    public void execute(){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            super.execute();
        } else {
        	super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
