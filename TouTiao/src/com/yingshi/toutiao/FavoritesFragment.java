package com.yingshi.toutiao;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.yingshi.toutiao.MainFragment.NewsArrayAdapter;
import com.yingshi.toutiao.actions.GetFavoritesAction;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.view.ptr.HeaderLoadingSupportPTRListFragment;
import com.yingshi.toutiao.view.ptr.PTRListAdapter;

public class FavoritesFragment extends HeaderLoadingSupportPTRListFragment {
	private final static String tag = "TT-MyFavoritesPageFragment";
	private GetFavoritesAction mGetFavoritesAction;
	private PTRListAdapter<News> mNewsListAdapter;
	private UICallBack<Pagination<News>> getFavoritesListUICallback = new UICallBack<Pagination<News>>(){
		public void onSuccess(Pagination<News> newsList) {
			if(mNewsListAdapter == null){
				mNewsListAdapter = new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, newsList.getItems());
				setAdapter(mNewsListAdapter);
			}else{
				mNewsListAdapter.addMore(newsList.getItems());
			}
			showListView();
			refreshComplete();
		}
		public void onFailure(ActionError error) {
			//TODO: Show failure
			showListView();
			refreshComplete();
		}
	};
	
	public FavoritesFragment() {
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setMode(Mode.PULL_FROM_END);
		mGetFavoritesAction  = new GetFavoritesAction(getActivity(), 1, 20);
		mGetFavoritesAction.execute(getFavoritesListUICallback);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		mGetFavoritesAction.createNextPageAction().execute(getFavoritesListUICallback);
	}
	public ViewHolder createHeaderView(LayoutInflater inflater) {
		return null;
	}
}