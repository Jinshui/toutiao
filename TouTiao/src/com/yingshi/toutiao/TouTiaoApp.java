package com.yingshi.toutiao;

import android.app.Application;
import android.util.Log;

import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yingshi.toutiao.social.AccountInfo;
import com.yingshi.toutiao.storage.AccountInfoDAO;
import com.yingshi.toutiao.storage.DatabaseHelper;
import com.yingshi.toutiao.storage.NewsDAO;
import com.yingshi.toutiao.storage.SpecialDAO;

public class TouTiaoApp extends Application {
    private static final String tag = "TT-TouTiaoApp";
    
    private NewsDAO mNewsDao;
    private SpecialDAO mSpecialDAO;
    private AccountInfoDAO mAccountInfoDAO;
    private DatabaseHelper mDatabaseHelper;
    private AccountInfo mUserInfo;
    
    public void onCreate() {
        Log.i(tag, "影视头条App is initializing");
        super.onCreate();
        mDatabaseHelper = new DatabaseHelper(this);
        mNewsDao = new NewsDAO(mDatabaseHelper.getWritableDatabase());
        mSpecialDAO  = new SpecialDAO(mDatabaseHelper.getWritableDatabase());
        mAccountInfoDAO = new AccountInfoDAO(mDatabaseHelper.getWritableDatabase());
        //Register weixin
        WXAPIFactory.createWXAPI(this, null).registerApp(Constants.APP_WEIXIN_ID);
    }

    public NewsDAO getNewsDAO() {
        return mNewsDao;
    }

    public SpecialDAO getSpecialDAO() {
        return mSpecialDAO;
    }
    
    public AccountInfoDAO getAccountInfoDAO(){
    	return mAccountInfoDAO;
    }
    
    public void setUserInfo(AccountInfo userInfo){
    	this.mUserInfo = userInfo;
    }
    
    public AccountInfo getUserInfo(){
    	return mUserInfo;
    }
}
