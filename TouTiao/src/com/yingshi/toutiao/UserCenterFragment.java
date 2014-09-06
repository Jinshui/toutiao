package com.yingshi.toutiao;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class UserCenterFragment extends Fragment
{
	private SlidingMenu mSlidingMenu;
	private View mView;
	private ImageButton mReturnBtn;
	private ImageView mUserPhoto;
	private TextView mUserName;
	private View mBtnFavorites;
	private View mBtnDownloads;
	private View mBtnPush;
	private View mBtnClearCache;

	public UserCenterFragment(SlidingMenu slidingMenu){
		mSlidingMenu = slidingMenu;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		if (mView == null)
		{
			initView(inflater, container);
		}
		return mView;
	}

	private void initView(LayoutInflater inflater, ViewGroup container)
	{
		mView = inflater.inflate(R.layout.view_user_center, container, false);
		mReturnBtn = (ImageButton)mView.findViewById(R.id.id_btn_back_to_toutiao);
		mUserPhoto = (ImageView)mView.findViewById(R.id.id_user_profile_photo);
		mUserName = (TextView)mView.findViewById(R.id.id_user_name);
		mBtnFavorites = mView.findViewById(R.id.id_favorites);
		mBtnDownloads = mView.findViewById(R.id.id_downloads);
		mBtnPush = mView.findViewById(R.id.id_push);
		mBtnClearCache = mView.findViewById(R.id.id_clear_cache);
		addListener();
	}
	
	private void addListener(){
		mReturnBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				mSlidingMenu.showContent();
			}});
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
