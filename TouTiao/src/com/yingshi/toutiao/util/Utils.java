package com.yingshi.toutiao.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utils {
    private final static String TAG = "JIDA-Utils";
    private static SimpleDateFormat sdf = null;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null)
            return info.isConnected();
        return false;
    }

    public static String formatDate(String format, long date) {
    	return formatDate(format, new Date(date));
    }

    public static String formatDate(String format, Date date) {
        if (sdf == null)
            sdf = new SimpleDateFormat(format);
        else
            sdf.applyPattern(format);
        return sdf.format(date);
    }

    public static Date parseDate(String format, String date){
        if(sdf == null)
            sdf = new SimpleDateFormat(format);
        else
            sdf.applyPattern(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            Log.e(TAG, String.format("Invalid params : %s , %s", format , date), e);
            return null;
        }
    }

    private static final long MINUTE_TIME = 60 * 1000;
    private static final long HOUR_TIME = 60 * MINUTE_TIME;
    private static final long DAY_TIME = 24 * HOUR_TIME;

    public static String getDecodedValue(JSONObject json, String name) throws JSONException{
        String value = null;
        try {
            value = json.getString(name);
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new JSONException("Invalid json object. name:" + name + ", value:" + value);
        }
    }
}
