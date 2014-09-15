package com.yingshi.toutiao;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.BackgroundCallBack;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.actions.GetFocusAction;
import com.yingshi.toutiao.actions.GetNewsAction;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.storage.NewsDAO;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.PhotoPager;
import com.yingshi.toutiao.view.ptr.HeaderLoadingSupportPTRListFragment;
import com.yingshi.toutiao.view.ptr.PTRListAdapter;

public class MainFragment extends HeaderLoadingSupportPTRListFragment {
	private final static String tag = "TT-SlidePageFragment";
	
	private String mCategory;
	private PhotoPager mPhotoPager;
	private PTRListAdapter<News> mNewsListAdapter;
	private List<News> mFocusNews;
	private NewsDAO mNewsDAO;
	private GetNewsAction mGetnewsAction;
	private boolean mFocusLoaded = false;
	private boolean mNewsLoaded = false;
	
	private int mAsyncTaskCount = 0;
	private BackgroundCallBack<Pagination<News>> mGetNewsListBackgroundCallback = null;
	private UICallBack<Pagination<News>> mGetNewsListUICallback = null;
	
	public MainFragment() {
	}

	public MainFragment(String category) {
		Log.d(tag, category + " new SlidePageFragment");
		mCategory = category;
	}
	public void onAttach (Activity activity){
		Log.d(tag, mCategory +" onAttach()");
		super.onAttach(activity);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(tag, mCategory +" onCreate()");
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mCategory = savedInstanceState.getString("mCategory");
		}
		mNewsDAO = ((TouTiaoApp)getActivity().getApplication()).getNewsDAO();
	}
	public void onResume(){
		Log.d(tag, mCategory +" onResume()");
		super.onResume();
		showLoadingView();
		if( !mFocusLoaded )
			loadFocusFromServer();
		else
			loadFocusFromDB();
		if( !mNewsLoaded )
			loadNewsFromServer();
		else
			loadNewsFromDB();
	}
	
	private void loadFocusFromServer(){
		Log.d(tag, mCategory +" loadFocusFromServer()");
		mAsyncTaskCount ++;
		new GetFocusAction(getActivity(), mCategory).execute(new BackgroundCallBack<List<News>>(){
			public void onSuccess(List<News> newsList) {
				mNewsDAO.save(newsList);
			}
			public void onFailure(ActionError error) {}
		},new UICallBack<List<News>>(){
			public void onSuccess(List<News> newsList) {
				mFocusLoaded = true;
				mPhotoPager.getPhotoViewPager().setAdapter(new PhotoPagerAdapter(getChildFragmentManager(), newsList));
				updatePhotoPager(0, newsList);
				afterLoadReturned();
			}
			public void onFailure(ActionError error) {
				//TODO: Show failure
				afterLoadReturned();
			}
		});
	}
	
	private void loadNewsFromServer(){
		Log.d(tag, mCategory +" loadNewsFromServer()");
		mAsyncTaskCount ++;
		mGetnewsAction = new GetNewsAction(getActivity(), mCategory, 0, 20);
		mGetnewsAction.execute(mGetNewsListBackgroundCallback = new BackgroundCallBack<Pagination<News>>(){
				public void onSuccess(Pagination<News> newsPage) {
					mNewsDAO.save(newsPage.getItems());
				}
				public void onFailure(ActionError error) {}
			}, 
			mGetNewsListUICallback = new UICallBack<Pagination<News>>(){
				public void onSuccess(Pagination<News> newsList) {
					mNewsLoaded = true;
					if(isDetached()) //DO NOT update the view if this fragment is detached from the activity.
						return;
					if(mNewsListAdapter == null){
						mNewsListAdapter = new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, newsList.getItems());
						setAdapter(mNewsListAdapter);
					}else{
						mNewsListAdapter.addMore(newsList.getItems());
					}
					afterLoadReturned();
				}
				public void onFailure(ActionError error) {
					//TODO: Show failure
					afterLoadReturned();
				}
		});
	}
	
	private void loadFocusFromDB(){
		Log.d(tag, mCategory +" loadFocusFromDB()");
		mAsyncTaskCount ++;
		new AsyncTask<Void, Void, List<News>>() {
			protected List<News> doInBackground(Void... params) {
				return mNewsDAO.findFocusByCategory(mCategory);
			}
			public void onPostExecute(List<News> newsList){
				mPhotoPager.getPhotoViewPager().setAdapter(new PhotoPagerAdapter(getChildFragmentManager(), newsList));
				updatePhotoPager(0, newsList);
				afterLoadReturned();
			}
		}.execute();
	}
	
	private void loadNewsFromDB(){
		Log.d(tag, mCategory +" loadNewsFromDB()");
		mAsyncTaskCount ++;
		new AsyncTask<Void, Void, List<News>>() {
			protected List<News> doInBackground(Void... params) {
				return mNewsDAO.findNewsByCategory(mCategory);
			}
			public void onPostExecute(List<News> newsList){
				if(mNewsListAdapter == null){
					mNewsListAdapter = new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, newsList);
					setAdapter(mNewsListAdapter);
				}else{
					mNewsListAdapter.clear();
					mNewsListAdapter.addMore(newsList);
				}
				afterLoadReturned();
			}
		}.execute();
	}
	
	private void afterLoadReturned(){
		mAsyncTaskCount --;
		if(mAsyncTaskCount == 0){
			showListView();
		}
		refreshComplete();
	}
	
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(tag, mCategory + " onSaveInstanceState");
		outState.putString("mCategory", mCategory);
	}

	public ViewHolder createHeaderView(LayoutInflater inflater){
		ViewHolder holder = new ViewHolder();
		mPhotoPager = (PhotoPager)inflater.inflate(R.layout.view_news_list_header, null);
		mPhotoPager.getPhotoViewPager().setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				int currentPosition = mPhotoPager.getPhotoViewPager().getCurrentItem();
				News news = mNewsListAdapter.getItem(currentPosition);
				showNews(getActivity(), news);
			}
		});
		mPhotoPager.getPhotoViewPager().setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			public void onPageSelected(int position) {
				updatePhotoPager(position, mFocusNews);
			}
		});
		holder.headerView = mPhotoPager;
		holder.height = 250;
		return holder;
	}

	private void updatePhotoPager(int position, List<News> focusNews){
		if(focusNews.size() > 0){
			mPhotoPager.setVisibility(View.VISIBLE);
			mPhotoPager.getPhotoNumView().setText(String.format("%d/%d", position + 1, focusNews.size()));
			mPhotoPager.getPhotoDescriptionView().setText(focusNews.get(position).getName());
		}else{
			mPhotoPager.setVisibility(View.GONE);
		}
	}
	
	private static void showNews(Context context, News news){
		Intent showNewsDetailIntent = new Intent();
		if(news.isSpecial()){
			showNewsDetailIntent.putExtra(Constants.INTENT_EXTRA_NEWS_ID, news.getName());
			showNewsDetailIntent.setClass(context, SpecialNewsActivity.class);
		}else{
			showNewsDetailIntent.putExtra(Constants.INTENT_EXTRA_NEWS_ID, news.get_id());
			showNewsDetailIntent.setClass(context, NewsDetailActivity.class);
		}
		context.startActivity(showNewsDetailIntent);
	}
	
	public static class NewsArrayAdapter extends PTRListAdapter<News> {
        private LayoutInflater mInflater;
        public NewsArrayAdapter(Context context, int res, List<News> items) {
            super(context, res, items);
            mInflater = LayoutInflater.from(context);
        }

		public View getView(final int position, View convertView,
                ViewGroup parent) {
        	final News news = getItem(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate( R.layout.view_news_list_item, parent, false);
                holder = new ViewHolder();
                holder.newsThumbnail = (CustomizeImageView) convertView.findViewById(R.id.id_news_thumbnail);
                holder.newsTitle = (TextView) convertView.findViewById(R.id.id_news_title);
                holder.newsVideoSign = convertView.findViewById(R.id.id_news_video_sign);
                holder.newsSpecialSign = convertView.findViewById(R.id.id_news_special_sign);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(!TextUtils.isEmpty(news.getName())){
                holder.newsTitle.setText(news.getName());
            }
            if( news.getThumbnailUrl() != null){
            	if(news.getThumbnailFilePath() != null)
            		holder.newsThumbnail.loadImage("file://"+news.getThumbnailFilePath());
            	else
            		holder.newsThumbnail.loadImage(news.getThumbnailUrl());
            }
            
            holder.newsVideoSign.setVisibility( news.isHasVideo() ? View.VISIBLE : View.INVISIBLE);
            holder.newsSpecialSign.setVisibility( news.isSpecial() ? View.VISIBLE : View.INVISIBLE);
            
            convertView.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					showNews(getContext(), news);
				}
            });
            
            return convertView;
        }

        class ViewHolder {
        	CustomizeImageView newsThumbnail;
            TextView  newsTitle;
            View newsVideoSign;
            View newsSpecialSign;
        }
    }
	
	private class PhotoPagerAdapter extends FragmentStatePagerAdapter {
		SparseArray<MainPhotoFragment> pages = new  SparseArray<MainPhotoFragment>();
		List<News> news;
		public PhotoPagerAdapter(FragmentManager fm, List<News> news) {
			super(fm);
			this.news = news;
		}

		@Override
		public Fragment getItem(int position) {
			MainPhotoFragment page = pages.get(position);
			if(page == null){
				page = new MainPhotoFragment(news.get(position));
				pages.put(position, page);
			}
			return page;
		}

		@Override
		public int getCount() {
			return news.size();
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadFocusFromServer();
		loadNewsFromServer();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		mGetnewsAction.getNewPageAction().execute(mGetNewsListBackgroundCallback, mGetNewsListUICallback);
	}
	
	public void onActivityCreated (Bundle savedInstanceState){
		Log.d(tag, mCategory +" onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
	}
	public void onViewStateRestored (Bundle savedInstanceState){
		Log.d(tag, mCategory +" onViewStateRestored()");
		super.onViewStateRestored(savedInstanceState);
	}
	public void onStart (){
		Log.d(tag, mCategory +" onStart()");
		super.onStart();
	}
	
	public void onPause (){
		Log.d(tag, mCategory +" onPause()");
		super.onPause();
	}
	public void onStop (){
		Log.d(tag, mCategory +" onStop()");
		super.onStop();
	}
	public void onDestroyView (){
		Log.d(tag, mCategory +" onDestroyView()");
		super.onDestroyView();
	}
	public void onDestroy (){
		Log.d(tag, mCategory +" onDestroy()");
		super.onDestroy();
	}
	public void onDetach (){
		Log.d(tag, mCategory +" onDetach()");
		super.onDetach();
	}
}