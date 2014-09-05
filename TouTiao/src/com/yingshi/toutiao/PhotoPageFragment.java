package com.yingshi.toutiao;

import com.yingshi.toutiao.model.Photo;

import com.yingshi.toutiao.util.PhotoUtil;
import com.yingshi.toutiao.util.ServerMock;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PhotoPageFragment extends Fragment{
	private static final String tag = "TT-PhotoPageFragment";
	private Photo mPhoto;
	private ImageView mImageView;
	private Context mContext;
	public PhotoPageFragment(){
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			mPhoto = savedInstanceState.getParcelable("mPhoto");
		}
	}
	
	public void onResume(){
		super.onResume();
		if(mPhoto.getData() == null){
			loadImage();
		}else{
			showImage();
		}
	}
	
	public PhotoPageFragment(Context context, Photo photo){
		mPhoto = photo;
		mContext = context;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mImageView = (ImageView) inflater.inflate(R.layout.photo_page, container, false);
		return mImageView;
	}
	
	public void onSaveInstanceState (Bundle outState){
		outState.putParcelable("mPhoto", mPhoto);
	}
	
	private void showImage(){
		mImageView.setBackgroundDrawable(PhotoUtil.bytes2Drawable(mPhoto.getData()));
	}
	
	private void loadImage(){
		new AsyncTask<Void, Void, byte[]>() {
			protected byte[] doInBackground(Void... params) {
				Log.d(tag, "started loading image");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
				return ServerMock.getPhoto(mPhoto.getUrl(), mContext);
			}
			
			protected void onPostExecute(byte[] result) {
				Log.d(tag, "finished loading image: " + result.length);
				if(result.length > 0){
					mPhoto.setData(result);
					showImage();
				}
			}
		}.execute(new Void[]{});
	}
}
