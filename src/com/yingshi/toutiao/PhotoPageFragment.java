package com.yingshi.toutiao;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PhotoPageFragment extends Fragment{
	private int mPhotoRes;
	
	public PhotoPageFragment(){
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			mPhotoRes = savedInstanceState.getInt("mPhotoRes");
		}
	}
	
	public PhotoPageFragment(int photoRes){
		mPhotoRes = photoRes;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ImageView imageView = (ImageView) inflater.inflate(R.layout.photo_page, container, false);
		imageView.setBackgroundResource(mPhotoRes);
		return imageView;
	}
	
	public void onSaveInstanceState (Bundle outState){
		outState.putInt("mPhotoRes", mPhotoRes);
	}
}
