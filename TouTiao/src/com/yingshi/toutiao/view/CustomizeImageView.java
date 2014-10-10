package com.yingshi.toutiao.view;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.yingshi.toutiao.TouTiaoApp;
import com.yingshi.toutiao.actions.ParallelTask;
import com.yingshi.toutiao.util.PhotoUtil;
import com.yingshi.toutiao.util.Utils;

public class CustomizeImageView extends ImageView{
    private static final String tag = "TT-CustomizeImageView";
    private static String IMAGE_CACHE_PATH = null;

    public static interface LoadImageCallback{
    	void onImageLoaded(Drawable drawable);
    }
    
    public CustomizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(IMAGE_CACHE_PATH == null){
        	IMAGE_CACHE_PATH = ((TouTiaoApp)context.getApplicationContext()).getCachePath()  + "/images/";
        }
    }

    public void loadImage(final String url){
    	loadImage(url, null);
    }

    public void loadImage(final String url, final LoadImageCallback mLoadImageCallback){
    	if(TextUtils.isEmpty(url))
    		return;
        new ParallelTask<Drawable>(){
            protected Drawable doInBackground(Void... params) {
                try {
                    String cachedFileDir = IMAGE_CACHE_PATH + url.hashCode();
                    File existingFile = new File(cachedFileDir);
                    if(existingFile.exists()){
                    	return Drawable.createFromPath(cachedFileDir);
                    }
                    Log.d(tag, "Loading img : " + url);
                    InputStream is = (InputStream) new URL(url).getContent();
                    Utils.saveDataToFile(is, cachedFileDir);
                    return Drawable.createFromPath(cachedFileDir);
                } catch (Exception e) {
                	Log.e(tag, "Failed to load image : " + url +". Error: " + e.getMessage());
                	return null;
                }
            }
            @SuppressWarnings("deprecation")
			protected void onPostExecute(Drawable drawable){
                if(drawable != null){
//                    setImageDrawable(drawable);
                	setBackgroundDrawable(drawable);
                }
                if(mLoadImageCallback != null){
                	mLoadImageCallback.onImageLoaded(drawable);
                }
            }
        }.execute();
    }
    
    public static String getCachedImagePath(String url){
        String cachedFileDir = IMAGE_CACHE_PATH + url.hashCode();
        File existingFile = new File(cachedFileDir);
        if(existingFile.exists()){
        	return cachedFileDir;
        }else{
        	return null;
        }
    }
    
    public void loadImage(final InputStream is){
    	loadImage(is, null);
    }
    
    public void loadImage(final byte[] data){
    	if(data == null || data.length == 0)
    		return;
        new ParallelTask<Drawable>(){
            protected Drawable doInBackground(Void... params) {
            	return PhotoUtil.bytes2Drawable(data);
            }
            @SuppressWarnings("deprecation")
			protected void onPostExecute(Drawable drawable){
                if(drawable != null){
                	setBackgroundDrawable(drawable);
              }
            }
        }.execute();
    }
    
    public void loadImage(final InputStream is, final LoadImageCallback callback){
        new AsyncTask<Void, Void, Drawable>(){
            protected Drawable doInBackground(Void... params) {
                try {
                    return Drawable.createFromStream(is, "src name");
                } catch (Exception e) {
                    return null;
                }
            }
            @SuppressWarnings("deprecation")
			protected void onPostExecute(Drawable drawable){
                if(drawable != null){
//                    setImageDrawable(drawable);
                    setBackgroundDrawable(drawable);
//                    setBackground(drawable);
                }
                if(callback != null)
                	callback.onImageLoaded(drawable);
            }
        }.execute();
    }
}
