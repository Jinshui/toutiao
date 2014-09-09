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

import java.util.List;

import android.content.Context;

import com.yingshi.toutiao.model.News;

public class NewsDAO {
    static final String tag = "TT-NewsDAO";
    DBAdapter mDbAdapter;

    public NewsDAO(Context context) {
        mDbAdapter = new DBAdapter(context);
        mDbAdapter.open();
    }

    public News saveNews(News news){
        news.set_id(mDbAdapter.insertNews(news));
        return news;
    }

    public void saveNews(List<News> newsList){
    	for(News news : newsList)
    		news.set_id(mDbAdapter.insertNews(news));
    }

    public News getNews(int _id){
    	return mDbAdapter.fetchNewsById(_id);
    }
    
    public void updateNews(News news){
        mDbAdapter.updateNews(news);;
    }

    public List<News> getAllNews() {
        return mDbAdapter.fetchAllNews();
    }

    public void deleteNews(long id) {
    	mDbAdapter.deleteNews(id);
    }


    public void close() {
        mDbAdapter.close();
    }
}
