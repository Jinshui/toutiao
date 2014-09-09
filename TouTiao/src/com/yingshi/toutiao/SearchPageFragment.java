package com.yingshi.toutiao;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.yingshi.toutiao.HomePageFragment.NewsArrayAdapter;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.actions.SearchAction;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.view.ptr.HeaderLoadingSupportPTRListFragment;
import com.yingshi.toutiao.view.ptr.PTRListAdapter;

public class SearchPageFragment extends HeaderLoadingSupportPTRListFragment {
	private final static String tag = "TT-SearchPageFragment";
	private String mKeyword;
	private SearchAction mSearchAction;
	private PTRListAdapter<News> mNewsListAdapter;
	private UICallBack<Pagination<News>> searchCallBack = new UICallBack<Pagination<News>>(){
		public void onSuccess(Pagination<News> newsList) {
			if(mNewsListAdapter == null){
				mNewsListAdapter = new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, newsList.getItems());
				setAdapter(mNewsListAdapter);
			}else{
				mNewsListAdapter.addMore(newsList.getItems());
			}
		}
		public void onFailure(ActionError error) {
			//TODO: show error
		}
	}; 
	
	public SearchPageFragment() {
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setMode(Mode.PULL_FROM_END);
	}

	public void doSearch(String keyword){
		mKeyword = keyword;
		mSearchAction = new SearchAction(getActivity(), mKeyword, 1, 20);
		mSearchAction.execute(searchCallBack);
	}
	
	public ViewHolder createHeaderView(LayoutInflater inflater){
		return null;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		mSearchAction.getNewPageAction().execute(searchCallBack);
	}
}