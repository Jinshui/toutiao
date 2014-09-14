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
package com.yingshi.toutiao.storage.adapters;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yingshi.toutiao.model.News;

public class NewsDBAdapter extends BaseAdapter<News> {

	public static final String DB_TABLE_NEWS = "news";
    private static String[] NEWS_COLUMNS = { "_id", "id", "name", "summary", 
    	"content", "time", "category", "contact", "likes", "isSpecial", 
    	"specialName", "hasVideo", "videoUrl", "videoPhotoUrl", "author", 
    	"photoUrl", "thumbnailUrl", "videoPhotoFilePath", "photoFilePath", 
    	"thumbnailFilePath", "isFocus", "isFavorite"};

    public NewsDBAdapter(SQLiteDatabase database) {
    	super(DB_TABLE_NEWS, NEWS_COLUMNS, database);
    }

    public ContentValues toContentValues(News news) {
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
        initialValues.put("isFocus", news.isFocus());
        initialValues.put("isFavorite", news.isFavorite());
        return initialValues;
    }
    
    public List<News> findFavorites(int pageSize, int pageIndex){
    	String columns = "";
    	for(int i=0; i<NEWS_COLUMNS.length; i++){
    		columns = columns + NEWS_COLUMNS[i];
    		if(i != NEWS_COLUMNS.length - 1)
    			columns = columns + ", ";
    	}
    	String sql = String.format("select %s from %s where isFavorite=1 order by time limit %d offset %d", columns, DB_TABLE_NEWS, pageSize, (pageIndex - 1)*pageSize);
    	return toObjectList(getDatabase().rawQuery(sql, null));
    }
    
    public News toObject(Cursor cursor) {
    	News news = new News();
        news.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
        news.setId(cursor.getString(cursor.getColumnIndex("id")));
        news.setName(cursor.getString(cursor.getColumnIndex("name")));
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
        news.setFocus(cursor.getInt(cursor.getColumnIndex("isFocus")) > 0);
        news.setFavorite(cursor.getInt(cursor.getColumnIndex("isFavorite")) > 0);
        return news;
    }
}
