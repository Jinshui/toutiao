package com.yingshi.toutiao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yingshi.toutiao.model.Photo;
import com.yingshi.toutiao.util.ServerMock;

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
		mImageView = (ImageView) inflater.inflate(R.layout.view_news_list_photo_page, container, false);
		return mImageView;
	}
	
	public void onSaveInstanceState (Bundle outState){
		outState.putParcelable("mPhoto", mPhoto);
	}
	
	@SuppressWarnings("deprecation")
	private void showImage(){
		mImageView.setBackgroundDrawable(new BitmapDrawable(mPhoto.getData()));
	}
	
	private void loadImage(){
		new AsyncTask<Void, Void, Bitmap>() {
			protected Bitmap doInBackground(Void... params) {
				Log.d(tag, "started loading image");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				if(mContext != null)
					return ServerMock.getPhoto(mPhoto.getUrl(), mContext);
				return null;
			}
			
			protected void onPostExecute(Bitmap result) {
				if(result!=null){
					Log.d(tag, "finished loading image");
					mPhoto.setData(result);
					showImage();
				}else{
					Log.d(tag, "Failed Loading  image: " );
				}
			}
		}.execute(new Void[]{});
	}
}
