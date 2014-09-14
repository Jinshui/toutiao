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

public class PhotoPageFragment extends Fragment{
	private static final String tag = "TT-PhotoPageFragment";
	private News mNews;
	private CustomizeImageView mImageView;
	
	public PhotoPageFragment(){
	}
	
	public PhotoPageFragment(News news){
		mNews = news;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mImageView == null){
			mImageView = (CustomizeImageView) inflater.inflate(R.layout.view_news_list_photo_page, container, false);
			mImageView.loadImage(mNews.getPhotoUrl(), new LoadImageCallback(){
				public void onImageLoaded(Drawable drawable) {
					//TODO: Save the image?
				}
			});
		}
		return mImageView;
	}
}
