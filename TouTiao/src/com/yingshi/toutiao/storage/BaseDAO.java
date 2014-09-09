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

import com.yingshi.toutiao.model.BaseModel;
import com.yingshi.toutiao.storage.adapters.BaseAdapter;

public class BaseDAO<T extends BaseModel> {
    static final String tag = "TT-NewsDAO";
    BaseAdapter<T> mDbAdapter;

    public BaseDAO(BaseAdapter<T> adapter) {
        mDbAdapter = adapter;
    }
    
    public BaseAdapter<T> getDbAdapter(){
    	return mDbAdapter;
    }

    public T save(T object){
    	object.set_id(mDbAdapter.insert(object));
        return object;
    }

    public void save(List<T> objects){
    	for(T object : objects)
    		object.set_id(mDbAdapter.insert(object));
    }

    public void delete(long _id) {
    	mDbAdapter.delete(_id);
    }
    
    public void update(T object){
        mDbAdapter.update(object);;
    }

    public T get(int _id){
    	return mDbAdapter.fetchOneById(_id);
    }

    public List<T> getAll(String orderBy) {
        return mDbAdapter.fetchAll(orderBy);
    }
}
