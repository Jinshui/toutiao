package com.yingshi.toutiao.storage.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yingshi.toutiao.model.BaseModel;

public abstract class BaseAdapter<T extends BaseModel> {
    static final String tag = "TT-NewsDBAdapter";
    protected SQLiteDatabase mDb;
    protected String mTableName;
    protected String[] mColumnNames;
	public BaseAdapter(String tableName, String[] columnNames, SQLiteDatabase database){
		mDb = database;
		mTableName = tableName;
	}
	
	public abstract ContentValues toContentValues(T object); 
	public abstract T toObject(Cursor cursor);
	
	public SQLiteDatabase getDatabase(){
		return mDb;
	}
	
	public long insert(T object) {
		Log.d(tag, "insert: id=" + object.getId());
        return mDb.insert(mTableName, null, toContentValues(object));
	}
	
	public int delete(long rowId){
		Log.d(tag, "delete: _id=" + rowId);
		return mDb.delete(mTableName, "_id=" + rowId, null);
	}
	
    public int update(T object) {
		Log.d(tag, "update: _id=" + object.get_id());
        return getDatabase().update(mTableName, toContentValues(object), "_id=" + object.get_id(), null);
    }
	
    public List<T> fetchAll(String orderByColumn) {
		Log.d(tag, "fetchAll: orderByColumn=" + orderByColumn);
        return toObjectList(mDb.query(mTableName, mColumnNames, null, null, null, null, orderByColumn, null));
    }
	
    public List<T> fetchAll(String query, String orderByColumn) {
		Log.d(tag, "fetchAll: orderByColumn=" + orderByColumn);
        return toObjectList(mDb.query(mTableName, mColumnNames, query, null, null, null, orderByColumn, null));
    }
    
	public T fetchOneById(long rowId){
		Log.d(tag, "fetchOneById: _id=" + rowId);
		Cursor cursor = mDb.query(true, mTableName, mColumnNames, "_id="+rowId, null, null, null, null, null);
        if (cursor != null) {
            try{
            	if(cursor.moveToFirst())
            		return toObject(cursor);
            } finally {
                cursor.close();
            }
        }
        return null;
	}
    
    protected List<T> toObjectList(Cursor cursor) {
        List<T> objList = new ArrayList<T>();
        try{
	        if (cursor.moveToFirst()) {
	            do {
	            	objList.add(toObject(cursor));
	            } while (cursor.moveToNext());
	        }
        } finally {
            cursor.close();
        }
        return objList;
    }
}
