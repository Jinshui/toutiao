package com.yingshi.toutiao;

import java.util.List;

import android.util.Log;
import android.view.LayoutInflater;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.yingshi.toutiao.NewsListPageFragment.NewsArrayAdapter;
import com.yingshi.toutiao.model.Article;
import com.yingshi.toutiao.model.SearchResult;
import com.yingshi.toutiao.util.ServerMock;
import com.yingshi.toutiao.view.ptr.AbstractPTRFragment;

public class SearchPageFragment extends AbstractPTRFragment<SearchResult, Article> {
	private final static String tag = "TT-SearchPageFragment";
	
	private String mKeyword;
	
	public SearchPageFragment() {
	}

	public ViewHolder createHeader(LayoutInflater inflater){
		return null;
	}

	public void doSearch(String keyword){
		mKeyword = keyword;
		loadPage();
	}
	
	/**
	 * Do nothing but super's implementation when resuming
	 */
	protected void doResume(){
		setMode(Mode.PULL_FROM_END);
	}
	
	public NewsArrayAdapter onDataChanged(SearchResult result){
		return new NewsArrayAdapter(getActivity(), R.layout.view_special_list_item, result.getArticles());
	}
	
	public SearchResult loadData(){
		Log.d(tag, "started loading search page");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		return ServerMock.getSearchResult(mKeyword);
	}
	
	public List<Article> loadMoreList(){
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e){
		}
		return null;
	}
}