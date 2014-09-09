/*
 * Copyright (C) 2011, Motorola Mobility, Inc,
 * All Rights Reserved.
 * Motorola Confidential Restricted.
 *
 * Modification History:
 **********************************************************
 * Date           Author         Comments
 * 12-Apr-2011    Jinshui Tang   Created file
 **********************************************************
 */
package com.yingshi.toutiao.storage;

import java.util.ArrayList;
import java.util.List;

import com.yingshi.toutiao.model.News;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    static final String tag = "TT-DBAdapter";

    private static final String CREATE_DATABASE_NEWS = "create table news("
            + "_id integer primary key autoincrement," 
            + "id text not null," //id returned from server
            + "name text not null,"
            + "summary text null,"
            + "content text null,"
            + "time integer null,"
            + "category text null,"
            + "contact text null,"
            + "likes integer null,"
            + "isSpecial integer null,"
            + "specialName text null,"
            + "hasVideo integer null,"
            + "videoUrl text null,"
            + "videoPhotoUrl text null,"
            + "author text null,"
            + "photoUrl text null,"
            + "thumbnailUrl text null,"
            + "videoPhotoFilePath text null,"
            + "photoFilePath text null,"
            + "thumbnailFilePath text null"
            + ");";
    
    public static final String DB_NAME = "news.db";
    public static final String DB_TABLE_NEWS = "news";
    public static final int DATABASE_VERSION = 1;
    private static String[] reports_columns = { "_id", "id", "name", "summary", 
    	"content", "time", "category", "contact", "likes", "isSpecial", 
    	"specialName", "hasVideo", "videoUrl", "videoPhotoUrl", "author", 
    	"photoUrl", "thumbnailUrl", "videoPhotoFilePath", "photoFilePath", "thumbnailFilePath"};

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DATABASE_NEWS);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(tag, "Upgrading database from version " + oldVersion + " to " + newVersion
                  + ", this will destroy all old data");
        }
    }

    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public DBAdapter(Context context) {
        mCtx = context;
    }

    public DBAdapter open() throws SQLException {
        Log.d(tag, "Connecting to database");
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        Log.d(tag, "Disconnecting to Database");
        mDbHelper.close();
    }
    
    public long insertNews(News news) {
        Log.v(tag, "inserting a news to database " + news.getName());
        ContentValues initialValues = new ContentValues();
        initialValues.put("id", news.getId());
        initialValues.put("name", news.getName());
        initialValues.put("summary", news.getSummary());
        initialValues.put("content", news.getContent());
        initialValues.put("time", news.getTime());
        initialValues.put("category", news.getCategory());
        initialValues.put("contact", news.getContact());
        initialValues.put("likes", news.getLikes());
        initialValues.put("isSpecial", news.isSpecial());
        initialValues.put("specialName", news.getSpecialName());
        initialValues.put("hasVideo", news.isHasVideo());
        initialValues.put("videoUrl", news.getVideoUrl());
        initialValues.put("videoPhotoUrl", news.getVideoPhotoUrl());
        initialValues.put("author", news.getAuthor());
        initialValues.put("photoUrl", news.getPhotoUrl());
        initialValues.put("thumbnailUrl", news.getThumbnailUrl());
        initialValues.put("videoPhotoFilePath", news.getVideoPhotoFilePath());
        initialValues.put("photoFilePath", news.getPhotoFilePath());
        initialValues.put("thumbnailFilePath", news.getThumbnailFilePath());
        return mDb.insert(DB_TABLE_NEWS, null, initialValues);
    }

    public int updateNews(News news) {
        Log.v(tag, "updating news : " + news.get_id());
        ContentValues args = new ContentValues();
        args.put("content", news.getContent());
        return mDb.update(DB_TABLE_NEWS, args, "_id=" + news.get_id(), null);
    }


    public int deleteNews(long rowId) {
        Log.v(tag, "deleting a news from database : " + rowId);
        return mDb.delete(DB_TABLE_NEWS, "_id=" + rowId, null);
    }

    /**
     * Return a Cursor over the list of all reports in the database
     *
     * @return Cursor over all reports
     */
    public List<News> fetchAllNews() {
        return cursorToNewsList(mDb.query(DB_TABLE_NEWS, reports_columns, null, null, null, null, "time", null));
    }
    
    private List<News> cursorToNewsList(Cursor cursor) {
        List<News> newsList = new ArrayList<News>();
        try{
	        if (cursor.moveToFirst()) {
	            do {
	                newsList.add(cursorToNews(cursor, false));
	            } while (cursor.moveToNext());
	        }
        } finally {
            cursor.close();
        }
        return newsList;
    }
    
    private News cursorToNews(Cursor cursor, boolean close) {
        try{
	    	News news = new News();
	        news.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
	        news.setId(cursor.getString(cursor.getColumnIndex("name")));
	        news.setSummary(cursor.getString(cursor.getColumnIndex("summary")));
	        news.setTime(cursor.getInt(cursor.getColumnIndex("time")));
	        news.setContent(cursor.getString(cursor.getColumnIndex("content")));
	        news.setCategory(cursor.getString(cursor.getColumnIndex("category")));
	        news.setContact(cursor.getString(cursor.getColumnIndex("contact")));
	        news.setLikes(cursor.getInt(cursor.getColumnIndex("likes")));
	        news.setSpecial(cursor.getInt(cursor.getColumnIndex("isSpecial")) > 0);
	        news.setSpecialName(cursor.getString(cursor.getColumnIndex("specialName")));
	        news.setHasVideo(cursor.getInt(cursor.getColumnIndex("hasVideo")) > 0);
	        news.setVideoUrl(cursor.getString(cursor.getColumnIndex("videoUrl")));
	        news.setVideoPhotoUrl(cursor.getString(cursor.getColumnIndex("videoPhotoUrl")));
	        news.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
	        news.setPhotoUrl(cursor.getString(cursor.getColumnIndex("photoUrl")));
	        news.setThumbnailUrl(cursor.getString(cursor.getColumnIndex("thumbnailUrl")));
	        news.setVideoPhotoFilePath(cursor.getString(cursor.getColumnIndex("videoPhotoFilePath")));
	        news.setPhotoFilePath(cursor.getString(cursor.getColumnIndex("photoFilePath")));
	        news.setThumbnailFilePath(cursor.getString(cursor.getColumnIndex("thumbnailFilePath")));
	        return news;
        } finally {
            if(close) 
            	cursor.close();
        }
    }
    
    
    public News fetchNewsById(long rowId) throws SQLException {
        Cursor cursor = mDb.query(true, DB_TABLE_NEWS, reports_columns, "_id="+rowId, null, null, null, null, null);
        if (cursor != null) {
        	cursor.moveToFirst();
            return cursorToNews(cursor, true);
        }
        return null;

    }
}
