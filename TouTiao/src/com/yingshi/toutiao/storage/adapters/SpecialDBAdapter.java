package com.yingshi.toutiao.storage.adapters;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yingshi.toutiao.model.Special;

public class SpecialDBAdapter extends BaseAdapter<Special> {
    static final String tag = "TT-NewsDBAdapter";

	public static final String DB_TABLE_SPECIALS = "specials";
    private static String[] SPECIAL_COLUMNS = { "_id", "id", "summary", "photoUrl", "photoFilePath"};
    public SpecialDBAdapter(SQLiteDatabase database) {
    	super(DB_TABLE_SPECIALS, SPECIAL_COLUMNS, database);
    }
    
    public Special toObject(Cursor cursor) {
    	Special special = new Special();
    	special.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
    	special.setId(cursor.getString(cursor.getColumnIndex("id")));
        special.setSummary(cursor.getString(cursor.getColumnIndex("summary")));
        special.setPhotoUrl(cursor.getString(cursor.getColumnIndex("photoUrl")));
        special.setPhotoFilePath(cursor.getString(cursor.getColumnIndex("photoFilePath")));
        return special;
    }
    
    public ContentValues toContentValues(Special special) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("id", special.getId());
        initialValues.put("summary", special.getSummary());
        initialValues.put("photoUrl", special.getPhotoUrl());
        initialValues.put("photoFilePath", special.getPhotoFilePath());
        return initialValues;
    }
}
