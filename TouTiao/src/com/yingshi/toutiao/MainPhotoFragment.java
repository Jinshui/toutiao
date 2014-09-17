package com.yingshi.toutiao;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.CustomizeImageView.LoadImageCallback;

public class MainPhotoFragment extends Fragment{
	private static final String tag = "TT-PhotoPageFragment";
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
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mImageView == null){
			mImageView = (CustomizeImageView) inflater.inflate(R.layout.view_news_list_photo_page, container, false);
			if(mNews.getThumbnailUrls().size() > 0){
				mImageView.loadImage(mNews.getThumbnailUrls().get(0), new LoadImageCallback(){
					public void onImageLoaded(Drawable drawable) {
						//TODO: Save the image?
					}
				});
			}
		}else{
			((ViewGroup)mImageView.getParent()).removeView(mImageView);
		}
		return mImageView;
	}
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("mNewsId", mNews.get_id());
	}
}
