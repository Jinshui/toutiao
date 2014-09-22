package com.yingshi.toutiao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.yingshi.toutiao.actions.ParallelTask;
import com.yingshi.toutiao.util.PhotoUtil;
import com.yingshi.toutiao.util.PreferenceUtil;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.CustomizeImageView.LoadImageCallback;

public class MainUserCenterFragment extends Fragment
{
	private final static String tag = "TT-UserCenterFragment";
	private SlidingMenu mSlidingMenu;
	private View mView;
	private ImageButton mReturnBtn;
	private CustomizeImageView mUserPhoto;
	private TextView mUserName;
	private View mBtnFavorites;
	private View mBtnDownloads;
	private View mBtnPush;
	private View mBtnClearCache;
	
	class PhotoUpdateBroadcastReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent) {
			if(Constants.INTENT_ACTION_PHOTO_UPDATED.equals(intent.getAction())){
				Log.d(tag, "Received intent: " + Constants.INTENT_ACTION_PHOTO_UPDATED);
				loadUserInfo();
			}
		}
	}

	private PhotoUpdateBroadcastReceiver mReceiver;
	
	public MainUserCenterFragment(){}
	
	public MainUserCenterFragment(SlidingMenu slidingMenu){
		mSlidingMenu = slidingMenu;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mReceiver = new PhotoUpdateBroadcastReceiver();
	}
	
	public void onResume(){
		super.onResume();
		IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_PHOTO_UPDATED);
		getActivity().registerReceiver(mReceiver, filter);
	}
	
	public void onPause(){
		super.onPause();
		getActivity().unregisterReceiver(mReceiver);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.view_user_center, container, false);
		mReturnBtn = (ImageButton)mView.findViewById(R.id.id_btn_back_to_toutiao);
		mUserPhoto = (CustomizeImageView)mView.findViewById(R.id.id_user_profile_photo);
		mUserName = (TextView)mView.findViewById(R.id.id_user_name);
		mBtnFavorites = mView.findViewById(R.id.id_favorites);
		mBtnDownloads = mView.findViewById(R.id.id_downloads);
		mBtnPush = mView.findViewById(R.id.id_push);
		mBtnClearCache = mView.findViewById(R.id.id_clear_cache);
		addListener();
		loadUserInfo();
		return mView;
	}
	
	private void loadUserInfo(){
		String loginUserName = PreferenceUtil.getString(getActivity(), Constants.USER_NAME , null);
		if(loginUserName != null)
			mUserName.setText(loginUserName);
		String photoUrl = PreferenceUtil.getString(getActivity(), Constants.USER_PHOTO_URL, null);
		if(photoUrl != null){
			mUserPhoto.loadImage(photoUrl);
		}
	}
	
	private void addListener(){
		mReturnBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				mSlidingMenu.showContent();
			}});
		mBtnFavorites.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), FavoritesActivity.class);
				startActivity(intent);
			}});
	}
	
	public void updateProfilePhoto(String url){
		mUserPhoto.loadImage(url);
	}
	
	public void showMyFavorites(){
		
	}
	
	public void download(){
		
	}
	
	public void tooglePush(){
		
	}
	
	public void clearCache(){
		
	}
}
