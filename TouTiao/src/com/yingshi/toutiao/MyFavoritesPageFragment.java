package com.yingshi.toutiao;

import java.util.List;

import android.util.Log;
import android.view.LayoutInflater;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.yingshi.toutiao.NewsListPageFragment.NewsArrayAdapter;
import com.yingshi.toutiao.model.Article;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.model.SearchResult;
import com.yingshi.toutiao.util.ServerMock;
import com.yingshi.toutiao.view.ptr.AbstractPTRFragment;
import com.yingshi.toutiao.view.ptr.PTRListAdapter;

public class MyFavoritesPageFragment extends AbstractPTRFragment<Pagination<Article>, Article> {
	private final static String tag = "TT-SearchPageFragment";
	
	
	public MyFavoritesPageFragment() {
	}

	public ViewHolder createHeader(LayoutInflater inflater){
		return null;
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
	
	public Pagination<Article> loadData(){
		Log.d(tag, "started loading search page");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		return null;
	}
	
	public List<Article> loadMoreList(){
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e){
		}
		return null;
	}

	@Override
	public PTRListAdapter<Article> onDataChanged(Pagination<Article> data) {
		// TODO Auto-generated method stub
		return null;
	}
}