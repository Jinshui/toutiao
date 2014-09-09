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

import android.database.sqlite.SQLiteDatabase;

import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.storage.adapters.NewsDBAdapter;

public class NewsDAO extends BaseDAO<News>{
    static final String tag = "TT-NewsDAO";
    public NewsDAO(SQLiteDatabase mDb) {
        super(new NewsDBAdapter(mDb));
    }
    
    public List<News> findFavorites(int pageSize, int pageIndex){
    	return ((NewsDBAdapter)getDbAdapter()).findFavorites(pageSize, pageIndex);
    }
}