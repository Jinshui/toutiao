package com.yingshi.toutiao;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.yingshi.toutiao.MainFragment.NewsArrayAdapter;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.BackgroundCallBack;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.actions.SearchAction;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.storage.NewsDAO;
import com.yingshi.toutiao.view.ptr.HeaderLoadingSupportPTRListFragment;
import com.yingshi.toutiao.view.ptr.PTRListAdapter;

public class SearchFragment extends HeaderLoadingSupportPTRListFragment {
	private final static String tag = "TT-SearchPageFragment";
	private String mKeyword;
	private SearchAction mSearchAction;
	private NewsDAO mNewsDAO;
	private PTRListAdapter<News> mNewsListAdapter;
	private BackgroundCallBack<Pagination<News>> mSearchNewsListBackgroundCallback = new BackgroundCallBack<Pagination<News>>(){
		public void onSuccess(Pagination<News> newsPage) {
			mNewsDAO.save(newsPage.getItems());
		}
		public void onFailure(ActionError error) {}
	};
	private UICallBack<Pagination<News>> searchUICallBack = new UICallBack<Pagination<News>>(){
		public void onSuccess(Pagination<News> newsList) {
			if(mNewsListAdapter == null){
				mNewsListAdapter = new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, newsList.getItems());
				setAdapter(mNewsListAdapter);
			}else{
				mNewsListAdapter.addMore(newsList.getItems());
			}
			
			if(newsList.getItems().isEmpty()){
				Toast.makeText(getActivity(), R.string.load_complete, Toast.LENGTH_SHORT).show();
			}
			
			showListView();
			refreshComplete();
		}
		public void onFailure(ActionError error) {
			Toast.makeText(getActivity(), R.string.load_complete, Toast.LENGTH_SHORT).show();
			showListView();
			refreshComplete();
		}
	}; 
	
	public SearchFragment() {
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNewsDAO = ((TouTiaoApp)getActivity().getApplication()).getNewsDAO();
	}

	public void doSearch(String keyword){
		mKeyword = keyword;
		showLoadingView();
		mSearchAction = new SearchAction(getActivity(), mKeyword, 1, 20);
		mSearchAction.execute(mSearchNewsListBackgroundCallback, searchUICallBack);
	}
	
	public ViewHolder createHeaderView(LayoutInflater inflater){
		setMode(Mode.PULL_FROM_END);
		return null;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		mSearchAction = (SearchAction)mSearchAction.getNextPageAction();
		mSearchAction.execute(mSearchNewsListBackgroundCallback, searchUICallBack);
	}
}