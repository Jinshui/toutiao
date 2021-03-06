package com.yingshi.toutiao;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.BackgroundCallBack;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.actions.GetSpecialAction;
import com.yingshi.toutiao.actions.GetSpecialNewsAction;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.model.Special;
import com.yingshi.toutiao.storage.NewsDAO;
import com.yingshi.toutiao.storage.SpecialDAO;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.CustomizeImageView.LoadImageCallback;
import com.yingshi.toutiao.view.SpecialHeader;
import com.yingshi.toutiao.view.ptr.HeaderLoadingSupportPTRListFragment;
import com.yingshi.toutiao.view.ptr.PTRListAdapter;

public class SpecialPageFragment extends HeaderLoadingSupportPTRListFragment{
	private final static String tag = "TT-SpecialPageFragment";
	
	private String mSpecialName;
	private SpecialHeader mSpecialHeader;
	private SpecialNewsArrayAdapter mSpecialNewsArrayAdapter;
	private SpecialDAO mSpecialDAO;
	private NewsDAO mNewsDAO;
	private GetSpecialNewsAction mGetSpecialNewsAction;
	private int mAsyncTaskCount = 0;
	
	BackgroundCallBack<Pagination<News>> getSpecialNewsListBackgroundCallback = new BackgroundCallBack<Pagination<News>>(){
		public void onSuccess(Pagination<News> newsPage) {
			mNewsDAO.save(newsPage.getItems());
		}
		public void onFailure(ActionError error) {
			//TODO: Show error
		}
	};
	
	UICallBack<Pagination<News>> getSpecialNewsListUICallback = new UICallBack<Pagination<News>>(){
		public void onSuccess(Pagination<News> newsList) {
			if(mSpecialNewsArrayAdapter == null){
				mSpecialNewsArrayAdapter = new SpecialNewsArrayAdapter(getActivity(), R.layout.view_special_list_item, newsList.getItems());
				setAdapter(mSpecialNewsArrayAdapter);
			}else{
				mSpecialNewsArrayAdapter.addMore(newsList.getItems());
			}
			mSpecialHeader.getPageIndicatorView().setText(newsList.getCurrentPage() + "/" + newsList.getTotalCounts()/newsList.getPageSize());
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
	
	public SpecialPageFragment() {
	}
	
	public SpecialPageFragment(String specialName) {
		Log.d(tag, specialName + " new SpecialPageFragment");
		mSpecialName = specialName;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(tag, mSpecialName + " onCreate");
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mSpecialName = savedInstanceState.getString("mSpecialName");
		}
		mSpecialDAO = ((TouTiaoApp)getActivity().getApplication()).getSpecialDAO();
		mNewsDAO = ((TouTiaoApp)getActivity().getApplication()).getNewsDAO();
		loadPage(true);
	}
	
	private void loadPage(boolean showLoadingView){
		if(showLoadingView)
			showLoadingView();
		mAsyncTaskCount = 2;
		new GetSpecialAction(getActivity(), mSpecialName).execute(new BackgroundCallBack<Pagination<Special>>(){
			public void onSuccess(Pagination<Special> specials) {
				mSpecialDAO.save(specials.getItems());
			}
			public void onFailure(ActionError error) {}
		},new UICallBack<Pagination<Special>>(){
			public void onSuccess(Pagination<Special> specials) {
				if( ! specials.getItems().isEmpty() ){
					Special special = specials.getItems().get(0);
					mSpecialHeader.getHeaderImageView().loadImage(special.getPhotoUrl(), new LoadImageCallback(){
						public void onImageLoaded(Drawable drawable) {
							//TODO: Save image
						}
					});
					mSpecialHeader.getSummaryView().setText(special.getSummary());
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
		});
		
		mGetSpecialNewsAction = new GetSpecialNewsAction(getActivity(), mSpecialName, 1, 20);
		mGetSpecialNewsAction.execute(getSpecialNewsListBackgroundCallback, getSpecialNewsListUICallback);
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(tag, mSpecialName + " onSaveInstanceState");
		outState.putString("mSpecialName", mSpecialName);
	}


	public ViewHolder createHeaderView(LayoutInflater inflater){
		ViewHolder holder = new ViewHolder();
		mSpecialHeader = (SpecialHeader)inflater.inflate(R.layout.view_special_list_header, null);
		holder.headerView = mSpecialHeader;
		holder.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		return holder;
	}

	protected class SpecialNewsArrayAdapter extends PTRListAdapter<News> {
        private LayoutInflater mInflater;
        public SpecialNewsArrayAdapter(Context context, int res, List<News> items) {
            super(context, res, items);
            mInflater = LayoutInflater.from(context);
        }

		public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate( R.layout.view_special_list_item, null);
                holder = new ViewHolder();
                holder.newsThumbnail = (CustomizeImageView) convertView.findViewById(R.id.id_special_thumbnail);
                holder.newsTitle = (TextView) convertView.findViewById(R.id.id_special_title);
                holder.newsSummary = (TextView)  convertView.findViewById(R.id.id_special_summary);
                holder.newsVideoSign = convertView.findViewById(R.id.id_special_video_sign);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
        	final News news = getItem(position);
            if(!TextUtils.isEmpty(news.getName())){
                holder.newsTitle.setText(news.getName());
            }
            if( news.getThumbnailUrl() != null){
            	if(news.getThumbnailFilePath() != null)
            		holder.newsThumbnail.loadImage("file://"+news.getThumbnailFilePath());
            	else
            		holder.newsThumbnail.loadImage(news.getThumbnailUrl());
            }

            if(!TextUtils.isEmpty(news.getSummary())){
                holder.newsSummary.setText(news.getSummary());
            }
            holder.newsVideoSign.setVisibility( news.isHasVideo() ? View.VISIBLE : View.INVISIBLE);
            convertView.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					Intent showNewsDetailIntent = new Intent();
					showNewsDetailIntent.setClass(getContext(), NewsDetailActivity.class);
					showNewsDetailIntent.putExtra(Constants.INTENT_EXTRA_NEWS_ID, news.get_id());
					getActivity().startActivity(showNewsDetailIntent);
				}
            });
            return convertView;
        }

        class ViewHolder {
        	CustomizeImageView newsThumbnail;
            TextView  newsTitle;
            TextView  newsSummary;
            View newsVideoSign;
        }
    }

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadPage(false);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		mGetSpecialNewsAction.getNewPageAction().execute(getSpecialNewsListBackgroundCallback, getSpecialNewsListUICallback);
	}
}