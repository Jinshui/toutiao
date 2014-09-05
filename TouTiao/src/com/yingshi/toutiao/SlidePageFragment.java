package com.yingshi.toutiao;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.yingshi.toutiao.model.NewsPage;
import com.yingshi.toutiao.util.ServerMock;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class SlidePageFragment extends Fragment {
	private final static String tag = "TT-SlidePageFragment";
	
	private String mCategory;	
	private NewsPage mData;
	private long mRefreshTime;
	
	private PullToRefreshScrollView mScrollView;
	private ViewPager mPhotoViewPager;
	private View mProgressBar;
	private View mPhotos;
	private LinearLayout mList;
	private boolean mIsLoadingPage = false;

	public SlidePageFragment(){}
	
	public SlidePageFragment(String category){
		Log.d(tag, category +" new SlidePageFragment");
		mCategory = category;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(tag, mCategory + " onCreate");
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			mCategory = savedInstanceState.getString("mCategory");
			mData = savedInstanceState.getParcelable("mData");
			mRefreshTime = savedInstanceState.getLong("mRefreshTime");
		}
	}
	
	public void onSaveInstanceState (Bundle outState){
		Log.d(tag, mCategory + " onSaveInstanceState");
		outState.putLong("mRefreshTime", mRefreshTime);
		outState.putString("mCategory", mCategory);
		outState.putParcelable("mData", mData);
	}
	
	public void onResume(){
		super.onResume();
		if(mData == null){
			loadPage();
		}else{
			showContent();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(tag, mCategory + " onCreateView");
		mScrollView = (PullToRefreshScrollView) inflater.inflate(R.layout.news_list, null);
		mScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				loadMore();
			}
		});
		mProgressBar = mScrollView.findViewById(R.id.id_loading);
		mPhotos = mScrollView.findViewById(R.id.id_photos);
		mList = (LinearLayout)mScrollView.findViewById(R.id.id_list);
		TextView text = (TextView) mScrollView.findViewById(R.id.tv_show);
		text.setText("This is " + mCategory);
		text.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Toast.makeText(getActivity(), "hello world", Toast.LENGTH_SHORT).show();
			}
		});
		mPhotoViewPager = (ViewPager)mScrollView.findViewById(R.id.id_photos_pager);
		final TextView photoNumbmer = (TextView) mScrollView.findViewById(R.id.id_photo_number);
		final TextView photoDescription = (TextView) mScrollView.findViewById(R.id.id_photo_description);
		mPhotoViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			public void onPageSelected(int position) {
				photoNumbmer.setText(String.format("%d/%d", position, mData.getPhotos().size()));
				photoDescription.setText("这是图"+position);
			}
		});
		return mScrollView;
	}

	private void loadMore(){
		new LoadMoreTask(mScrollView).execute();
	}
	
	private void loadPage(){
		Log.d(tag, mCategory + " loadPage " + mIsLoadingPage);
		if( ! mIsLoadingPage ){
			mIsLoadingPage = true;
			showLoadingbar();
			new LoadPageTask().execute();
		}
	}

	private void showLoadingbar(){
		mProgressBar.setVisibility(View.VISIBLE);
		mPhotos.setVisibility(View.GONE);
		mList.setVisibility(View.GONE);
	}

    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	private void showContent(){
		mProgressBar.setVisibility(View.GONE);
		mPhotos.setVisibility(View.VISIBLE);
		mList.setVisibility(View.VISIBLE);
		mPhotoViewPager.setAdapter(new PhotoPagerAdapter(getChildFragmentManager()));
	}
	
	private class PhotoPagerAdapter extends FragmentStatePagerAdapter {
		public PhotoPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return new PhotoPageFragment(getActivity(), mData.getPhotos().get(position));
		}

		@Override
		public int getCount() {
			return mData.getPhotos().size();
		}
	}
	
	private class LoadPageTask extends AsyncTask<Void, Void, NewsPage> {
		protected NewsPage doInBackground(Void... params) {
			Log.d(tag, "started loading page for "+mCategory);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			return ServerMock.getNewsPage(mCategory);
		}
		
		protected void onPostExecute(NewsPage result) {
			Log.d(tag, "finished loading page for "+mCategory);
			mIsLoadingPage = false;
			mRefreshTime = System.currentTimeMillis();
			mData = result;
			showContent();
		}
	}
	
	private class LoadMoreTask extends AsyncTask<Void, Void, String[]> {

		private PullToRefreshScrollView mSlideView;
		
		public LoadMoreTask(PullToRefreshScrollView slideView){
			mSlideView = slideView;
		}
		
		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Do some stuff here
			// Call onRefreshComplete when the list has been refreshed.
			mSlideView.onRefreshComplete();
		}
	}
//	public void onAttach (Activity activity){
//		Log.d(tag, mCategory + " onAttach");
//		super.onAttach(activity);
//	}
//	public void onStart (){
//		Log.d(tag, mCategory + " onStart");
//		super.onStart();
//	}
//	public void onResume(){
//		Log.d(tag, mCategory + " onResume");
//		loadPage();
//		super.onResume();
//	}
//	
//	public void onPause (){
//		Log.d(tag, mCategory + " onPause");
//		super.onPause();
//	}
//	public void onStop (){
//		Log.d(tag, mCategory + " onStop");
//		super.onStop();
//	}
//	public void onDetach (){
//		Log.d(tag, mCategory + " onDetach");
//		super.onDetach();
//	}
//	public void onDestroy (){
//		Log.d(tag, mCategory + " onStart");
//		super.onDestroy();
//	}
}
