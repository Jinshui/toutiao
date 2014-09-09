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

import android.database.sqlite.SQLiteDatabase;

import com.yingshi.toutiao.model.Special;
import com.yingshi.toutiao.storage.adapters.SpecialDBAdapter;

public class SpecialDAO extends BaseDAO<Special>{
    static final String tag = "TT-NewsDAO";
    public SpecialDAO(SQLiteDatabase mDb) {
        super(new SpecialDBAdapter(mDb));
    }
}
