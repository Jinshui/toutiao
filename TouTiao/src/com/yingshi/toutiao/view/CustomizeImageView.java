package com.yingshi.toutiao.view;

import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class CustomizeImageView extends ImageView{
    private static final String tag = "JIDA-JidaImageView";

    public CustomizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void loadImage(final String url){
        new AsyncTask<Void, Void, Drawable>(){
            protected Drawable doInBackground(Void... params) {
                try {
                    Log.d(tag, "Loading img : " + url);
                    InputStream is = (InputStream) new URL(url).getContent();
                    return Drawable.createFromStream(is, "src name");
                } catch (Exception e) {
                    return null;
                }
            }
            protected void onPostExecute(Drawable drawable){
                if(drawable != null){
                    setImageDrawable(drawable);
                }
            }
        }.execute(new Void[0]);
    }
}
