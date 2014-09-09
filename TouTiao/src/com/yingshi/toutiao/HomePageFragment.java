package com.yingshi.toutiao;

import java.util.List;

import android.content.Context;
import android.content.Intent;
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
import com.yingshi.toutiao.model.NewsPage;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.storage.NewsDAO;
import com.yingshi.toutiao.util.ServerMock;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.PhotoPager;
import com.yingshi.toutiao.view.ptr.HeaderLoadingSupportPTRListFragment;
import com.yingshi.toutiao.view.ptr.PTRListAdapter;

public class HomePageFragment extends HeaderLoadingSupportPTRListFragment {
	private final static String tag = "TT-SlidePageFragment";
	
	private String mCategory;
	private PhotoPager mPhotoPager;
	private PTRListAdapter<News> mNewsListAdapter;
	private List<News> mFocusNews;
	private NewsDAO mNewsDAO;
	private GetNewsAction mGetnewsAction;
	
	private int mAsyncTaskCount = 0;
	
	BackgroundCallBack<Pagination<News>> getNewsListBackgroundCallback = new BackgroundCallBack<Pagination<News>>(){
		public void onSuccess(Pagination<News> newsPage) {
			mNewsDAO.save(newsPage.getItems());
		}
		public void onFailure(ActionError error) {}
	};
	
	UICallBack<Pagination<News>> getNewsListUICallback = new UICallBack<Pagination<News>>(){
		public void onSuccess(Pagination<News> newsList) {
			if(mNewsListAdapter == null){
				mNewsListAdapter = new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, newsList.getItems());
				setAdapter(mNewsListAdapter);
			}else{
				mNewsListAdapter.addMore(newsList.getItems());
			}
			if(--mAsyncTaskCount == 0){
				showListView();
			}
			refreshComplete();
		}
		public void onFailure(ActionError error) {
			//TODO: Show failure
			if(--mAsyncTaskCount == 0){
				showListView();
			}
			refreshComplete();
		}
	};
	
	public HomePageFragment() {
	}

	public HomePageFragment(String category) {
		Log.d(tag, category + " new SlidePageFragment");
		mCategory = category;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(tag, mCategory + " onCreate");
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mCategory = savedInstanceState.getString("mCategory");
		}
		mNewsDAO = ((TouTiaoApp)getActivity().getApplication()).getNewsDAO();
		loadPage(true);
	}
	
	private void loadPage(boolean showLoadingView){
		if(showLoadingView)
			showLoadingView();
		mAsyncTaskCount = 2;
		new GetFocusAction(getActivity(), mCategory).execute(new BackgroundCallBack<List<News>>(){
			public void onSuccess(List<News> newsList) {
				mNewsDAO.save(newsList);
			}
			public void onFailure(ActionError error) {}
		},new UICallBack<List<News>>(){
			public void onSuccess(List<News> newsList) {
				mPhotoPager.getPhotoViewPager().setAdapter(new PhotoPagerAdapter(getChildFragmentManager(), newsList));
				updatePhotoPager(0, newsList);
				if(--mAsyncTaskCount == 0){
					showListView();
				}
				refreshComplete();
			}
			public void onFailure(ActionError error) {
				//TODO: Show failure
				if(--mAsyncTaskCount == 0){
					showListView();
				}
				refreshComplete();
			}
		});
		
		mGetnewsAction = new GetNewsAction(getActivity(), mCategory, 1, 20);
		mGetnewsAction.execute(getNewsListBackgroundCallback, getNewsListUICallback);
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
		SparseArray<PhotoPageFragment> pages = new  SparseArray<PhotoPageFragment>();
		List<News> news;
		public PhotoPagerAdapter(FragmentManager fm, List<News> news) {
			super(fm);
			this.news = news;
		}

		@Override
		public Fragment getItem(int position) {
			PhotoPageFragment page = pages.get(position);
			if(page == null){
				page = new PhotoPageFragment(news.get(position));
				pages.put(position, page);
			}
			return page;
		}

		@Override
		public int getCount() {
			return news.size();
		}
	}


	public NewsPage loadData(){
		Log.d(tag, "started loading page for " + mCategory);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		return ServerMock.getNewsPage(mCategory);
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadPage(false);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		mGetnewsAction.getNewPageAction().execute(getNewsListBackgroundCallback, getNewsListUICallback);
	}
}