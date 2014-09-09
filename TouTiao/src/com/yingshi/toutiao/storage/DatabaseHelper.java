package com.yingshi.toutiao.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	static final String tag = "TT-DatabaseHelper";

	private static final String CREATE_DATABASE_NEWS = "create table news("
			+ "_id integer primary key autoincrement,"
			+ "id text not null," // id returned from server
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
			+ "thumbnailFilePath text null,"
			+ "isFavorite integer null"
			+ ");";

	private static final String CREATE_DATABASE_SPECIAL = "create table specials("
			+ "_id integer primary key autoincrement,"
			+ "id text not null," // id returned from server
			+ "summary text null,"
			+ "photoUrl text null,"
			+ "thumbnailUrl text null,"
			+ "photoFilePath text null"
			+ ");";

	public static final String DB_NAME = "news.db";
	public static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DATABASE_NEWS);
		db.execSQL(CREATE_DATABASE_SPECIAL);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(tag, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", this will destroy all old data");
	}
}
