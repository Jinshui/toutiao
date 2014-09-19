package com.yingshi.toutiao;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yingshi.toutiao.actions.ParallelTask;
import com.yingshi.toutiao.social.AccountInfo;
import com.yingshi.toutiao.social.ISocialProvider;
import com.yingshi.toutiao.social.SinaSocialProvider;
import com.yingshi.toutiao.social.SocialResponseListener;
import com.yingshi.toutiao.social.TecentSocialProvider;
import com.yingshi.toutiao.util.PreferenceUtil;

@SuppressLint("ShowToast")
public class LoginActivity extends Activity implements SocialResponseListener{
	private static final String tag = "TT-LoginActivity";
	private ISocialProvider mAuthProvider;
	private TouTiaoApp mApp;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mApp = (TouTiaoApp)getApplication();
		loadAccountInfo();
	}
	
	private void loadAccountInfo(){
		new ParallelTask<AccountInfo>() {
			protected AccountInfo doInBackground(Void... params) {
				long expiresIn = Long.parseLong(PreferenceUtil.getString(LoginActivity.this, Constants.USER_TOKEN_EXPIRES_IN, "0"));
				if(expiresIn > System.currentTimeMillis()){
					//the session is still valid
					AccountInfo account = new AccountInfo();
					account.setExpiresIn(expiresIn);
					account.setOpenId(PreferenceUtil.getString(LoginActivity.this, Constants.USER_OPEN_ID, null));
					account.setToken(PreferenceUtil.getString(LoginActivity.this, Constants.USER_ACCESS_TOKEN, null));
					account.setProvider(PreferenceUtil.getString(LoginActivity.this, Constants.USER_PROVIDER, null));
					account.setUserName(PreferenceUtil.getString(LoginActivity.this, Constants.USER_NAME, null));
					account.setPhotoUrl(PreferenceUtil.getString(LoginActivity.this, Constants.USER_PHOTO_URL, null));
					mApp.setUserInfo(account);
					return account;
				}else{
					//session has expired
					PreferenceUtil.removeKey(LoginActivity.this, Constants.USER_TOKEN_EXPIRES_IN);
					PreferenceUtil.removeKey(LoginActivity.this, Constants.USER_OPEN_ID);
					PreferenceUtil.removeKey(LoginActivity.this, Constants.USER_ACCESS_TOKEN);
					PreferenceUtil.removeKey(LoginActivity.this, Constants.USER_PROVIDER);
					PreferenceUtil.removeKey(LoginActivity.this, Constants.USER_NAME);
					PreferenceUtil.removeKey(LoginActivity.this, Constants.USER_PHOTO_URL);
					return null;
				}
			}
			
			public void onPostExecute(AccountInfo account){
				if(account != null){
					Log.d(tag, "User has already login and the token has not expired yet!");
					showHomeActivity();
				}
			}
		}.execute();
	}
	
	public void loginQQ(View view) {
		mAuthProvider = new TecentSocialProvider(this);
		mAuthProvider.login(this);
	}

	public void loginWeibo(View view) {
		mAuthProvider = new SinaSocialProvider(this);
		mAuthProvider.login(this);
	}

	public void loginWeixin(View view) {

	}

	public void enter(View view) {
		showHomeActivity();
	}

	public void onLoginSuccess(AccountInfo accountInfo) {
		Map<String, String> qqAuthInfo = new HashMap<String, String>();
		qqAuthInfo.put(Constants.USER_PROVIDER, accountInfo.getProvider());
		qqAuthInfo.put(Constants.USER_OPEN_ID, accountInfo.getOpenId());
		qqAuthInfo.put(Constants.USER_ACCESS_TOKEN, accountInfo.getToken());
		qqAuthInfo.put(Constants.USER_TOKEN_EXPIRES_IN, String.valueOf(accountInfo.getExpiresIn()));
		PreferenceUtil.saveStringMap(this, qqAuthInfo);
		mApp.setUserInfo(accountInfo);
		mAuthProvider.getAccountInfo(this);
		showHomeActivity();
	}

	public void onFailure(String error) {
		Toast.makeText(this, error, Toast.LENGTH_LONG).show();
	}
	
	public void onGetAccountInfo(AccountInfo accountInfo){
		PreferenceUtil.saveString(this, Constants.USER_PHOTO_URL, accountInfo.getPhotoUrl());
		PreferenceUtil.saveString(this, Constants.USER_NAME, accountInfo.getUserName());
		sendBroadcast(new Intent(Constants.INTENT_ACTION_PHOTO_UPDATED));
		mApp.getUserInfo().setUserName(accountInfo.getUserName());
		mApp.getUserInfo().setPhotoUrl(accountInfo.getPhotoUrl());
	}
	
	private void showHomeActivity(){
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		intent.putExtra(MainActivity.INTENT_EXTRA_SHOW_USER_CENTER, true);
		startActivity(intent);
		finish();
	}
}