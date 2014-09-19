package com.yingshi.toutiao;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.view.CustomizeImageView;

public class MainPhotoFragment extends Fragment{
	private static final String tag = "TT-MainPhotoFragment";
	private News mNews;
	private long mNewsId;
	private CustomizeImageView mImageView;
	
	public MainPhotoFragment(){
	}
	
	public MainPhotoFragment(News news){
		mNews = news;
	}
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mNewsId = savedInstanceState.getLong("mNewsId");
			if(mNewsId > 0){
				mNews = ((TouTiaoApp)getActivity().getApplication()).getNewsDAO().get(mNewsId);
			}
		}
		Log.d(tag, (mNews!=null?mNews.getCategory():"") +" onAttach()");
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mImageView = (CustomizeImageView) inflater.inflate(R.layout.view_news_list_photo_page, container, false);
		if(mNews.getThumbnailUrls().size() > 0){
			mImageView.loadImage(mNews.getThumbnailUrls().get(0));
			mImageView.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					Intent showNewsDetailIntent = new Intent();
					showNewsDetailIntent.putExtra(Constants.INTENT_EXTRA_NEWS_ID, mNews.get_id());
					showNewsDetailIntent.setClass(getActivity(), NewsDetailActivity.class);
					getActivity().startActivity(showNewsDetailIntent);
				}
			});
		}
		return mImageView;
	}
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("mNewsId", mNews.get_id());
	}
}
