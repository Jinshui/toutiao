package com.yingshi.toutiao;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.yingshi.toutiao.social.AccountInfo;
import com.yingshi.toutiao.social.ISocialProvider;
import com.yingshi.toutiao.social.SinaSocialProvider;
import com.yingshi.toutiao.social.SocialResponseListener;
import com.yingshi.toutiao.social.TecentSocialProvider;
import com.yingshi.toutiao.util.PreferenceUtil;
import com.yingshi.toutiao.util.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class LoginActivity extends Activity implements SocialResponseListener{
	private static final String tag = "TT-LoginActivity";
	private ISocialProvider mAuthProvider;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
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
		qqAuthInfo.put(Constants.USER_OPEN_ID, accountInfo.getOpenId());
		qqAuthInfo.put(Constants.USER_ACCESS_TOKEN, accountInfo.getToken());
		qqAuthInfo.put(Constants.USER_TOKEN_EXPIRES_IN, String.valueOf(accountInfo.getExpiresIn()));
		PreferenceUtil.saveStringMap(this, qqAuthInfo);
		mAuthProvider.getAccountInfo(this);
		showHomeActivity();
	}

	public void onFailure(String error) {
		Toast.makeText(this, error, Toast.LENGTH_LONG).show();
	}
	
	public void onGetAccountInfo(AccountInfo accountInfo){
		PreferenceUtil.saveString(this, Constants.USER_PHOTO_URL, accountInfo.getPhotoUrl());
		PreferenceUtil.saveString(this, Constants.USER_NAME, accountInfo.getUserName());
		loadProfilePicture(accountInfo.getPhotoUrl());
	}
	
	private void loadProfilePicture(final String photoUrl){
		Log.d(tag, "loadUserProfilePhoto");
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
		        InputStream is = null;
		        FileOutputStream os = null;
				try {
					is = (InputStream) new URL(photoUrl).getContent();
					os = openFileOutput("user_profile_photo.png", 0);
					Utils.saveDataToOutput(is, os);
					Intent intent = new Intent(Constants.INTENT_ACTION_PHOTO_UPDATED);
					sendBroadcast(intent);
					Log.d(tag, "Successfully loaded user photo");
				} catch (Exception e) {
					Log.e(tag, "Failed load user photo", e);
				}finally{
					if(is != null)
						try { is.close();} catch (Exception e) {}
					if(os != null)
						try { os.close();} catch (Exception e) {}
				}
				return null;
			}
		}.execute();
	}

	private void showHomeActivity(){
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		intent.putExtra(MainActivity.INTENT_EXTRA_SHOW_USER_CENTER, true);
		startActivity(intent);
		finish();
	}
}