package com.yingshi.toutiao;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.yingshi.toutiao.model.Article;
import com.yingshi.toutiao.model.Article.Type;
import com.yingshi.toutiao.model.NewsPage;
import com.yingshi.toutiao.util.ServerMock;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.CustomizedPullToRefreshListView;
import com.yingshi.toutiao.view.PhotoPager;

public class SlidePageFragment extends Fragment implements OnRefreshListener2<ListView> {
	private final static String tag = "TT-SlidePageFragment";
	private final static long REFRESH_INTERVAL = 600000; 
	
	private String mCategory;
	private NewsPage mData;
	private long mLastRefreshTime;

	
	private CustomizedPullToRefreshListView mListView;
	private View mProgressBar;
	private boolean mIsLoadingPage = false;

	public SlidePageFragment() {
	}

	public SlidePageFragment(String category) {
		Log.d(tag, category + " new SlidePageFragment");
		mCategory = category;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(tag, mCategory + " onCreate");
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mCategory = savedInstanceState.getString("mCategory");
			mData = savedInstanceState.getParcelable("mData");
			mLastRefreshTime = savedInstanceState.getLong("mRefreshTime");
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		Log.d(tag, mCategory + " onSaveInstanceState");
		outState.putLong("mRefreshTime", mLastRefreshTime);
		outState.putString("mCategory", mCategory);
		outState.putParcelable("mData", mData);
	}

	public void onResume() {
		super.onResume();
		if (mData == null || System.currentTimeMillis() - mLastRefreshTime > REFRESH_INTERVAL) {
			loadPage();
		} else {
			showContent();
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadMore();
	}

	public void onPullUpToRefresh(final PullToRefreshBase<ListView> refreshView) {
		loadMore();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(tag, mCategory + " onCreateView");
		View layout = inflater.inflate(R.layout.view_news_list, null);
		mListView = (CustomizedPullToRefreshListView) layout.findViewById(R.id.id_content);
		mListView.setOnRefreshListener(this);
		mProgressBar = layout.findViewById(R.id.id_loading);
		final PhotoPager photoPager = mListView.getPhotoPager();
		photoPager.getPhotoViewPager().setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					public void onPageSelected(int position) {
						updatePhotoPager(position);
					}
				});
		return layout;
	}

	private void loadMore() {
		new LoadMoreTask(mListView).execute();
	}

	private void loadPage() {
		Log.d(tag, mCategory + " loadPage " + mIsLoadingPage);
		if (!mIsLoadingPage) {
			mIsLoadingPage = true;
			showLoadingbar();
			new LoadPageTask().execute();
		}
	}

	private void showLoadingbar() {
		mProgressBar.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.GONE);
		// mPhotos.setVisibility(View.GONE);
		// mList.setVisibility(View.GONE);
	}

	LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
			LayoutParams.MATCH_PARENT);

	private void showContent() {
		mProgressBar.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
		if(isResumed()){
			mListView.setAdapter(new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, mData.getArticles()));
			mListView.getPhotoPager().getPhotoViewPager().setAdapter(new PhotoPagerAdapter(getChildFragmentManager()));
			updatePhotoPager(0);
		}
	}

	private void updatePhotoPager(int position){
		if(mData.getPhotos().size() > 0){
			mListView.showPhotoPager();
			mListView.getPhotoPager().getPhotoNumView().setText(String.format("%d/%d", position + 1, mData.getPhotos().size()));
			mListView.getPhotoPager().getPhotoDescriptionView().setText(mData.getPhotos().get(position).getDescription());
		}else{
			mListView.hidePhotoPager();
		}
	}
	
	protected class NewsArrayAdapter extends ArrayAdapter<Article> {
        private LayoutInflater mInflater;
        public NewsArrayAdapter(Context context, int res, List<Article> items) {
            super(context, res, items);
            mInflater = LayoutInflater.from(context);
        }
        

        @SuppressWarnings("deprecation")
		public View getView(final int position, View convertView,
                ViewGroup parent) {
        	final Article article = getItem(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate( R.layout.view_news_list_item, null);
                holder = new ViewHolder();
                holder.newsThumbnail = (CustomizeImageView) convertView.findViewById(R.id.id_news_thumbnail);
                holder.newsTitle = (TextView) convertView.findViewById(R.id.id_news_title);
                holder.newsVideoSign = convertView.findViewById(R.id.id_news_video_sign);
                holder.newsSpecialSign = convertView.findViewById(R.id.id_news_special_sign);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(!TextUtils.isEmpty(article.getTitle())){
                holder.newsTitle.setText(article.getTitle());
            }
            if( article.getPhoto()!=null && article.getPhoto().getUrl() != null){
            	if(article.getPhoto().getData() != null)
            		holder.newsThumbnail.setBackgroundDrawable(new BitmapDrawable(article.getPhoto().getData()));
            	else
            		holder.newsThumbnail.loadImage(article.getPhoto().getUrl());
            }
            
            Type type = article.getType();
            holder.newsVideoSign.setVisibility( type == Type.VIDEO ? View.VISIBLE : View.INVISIBLE);
            holder.newsSpecialSign.setVisibility( type == Type.SPECIAL ? View.VISIBLE : View.INVISIBLE);
            
            convertView.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					Intent showNewsDetailIntent = new Intent();
					showNewsDetailIntent.setClass(getContext(), NewsDetailActivity.class);
					showNewsDetailIntent.putExtra(NewsDetailActivity.INTENT_EXTRA_ARTICLE, article);
					getActivity().startActivity(showNewsDetailIntent);
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
		public PhotoPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			PhotoPageFragment page = pages.get(position);
			if(page == null){
				page = new PhotoPageFragment(getActivity(), mData.getPhotos().get(position));
				pages.put(position, page);
			}
			return page;
		}

		@Override
		public int getCount() {
			return mData.getPhotos().size();
		}
	}

	private class LoadPageTask extends AsyncTask<Void, Void, NewsPage> {
		protected NewsPage doInBackground(Void... params) {
			Log.d(tag, "started loading page for " + mCategory);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			return ServerMock.getNewsPage(mCategory);
		}

		protected void onPostExecute(NewsPage result) {
			Log.d(tag, "finished loading page for " + mCategory);
			mIsLoadingPage = false;
			mLastRefreshTime = System.currentTimeMillis();
			mData = result;
			showContent();
		}
	}

	private class LoadMoreTask extends AsyncTask<Void, Void, String[]> {

		private PullToRefreshBase<?> mListView;

		public LoadMoreTask(PullToRefreshBase<?> refreshedView) {
			mListView = refreshedView;
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
			mListView.onRefreshComplete();
		}
	}
	// public void onAttach (Activity activity){
	// Log.d(tag, mCategory + " onAttach");
	// super.onAttach(activity);
	// }
	// public void onStart (){
	// Log.d(tag, mCategory + " onStart");
	// super.onStart();
	// }
	// public void onResume(){
	// Log.d(tag, mCategory + " onResume");
	// loadPage();
	// super.onResume();
	// }
	//
	// public void onPause (){
	// Log.d(tag, mCategory + " onPause");
	// super.onPause();
	// }
	// public void onStop (){
	// Log.d(tag, mCategory + " onStop");
	// super.onStop();
	// }
	// public void onDetach (){
	// Log.d(tag, mCategory + " onDetach");
	// super.onDetach();
	// }
	// public void onDestroy (){
	// Log.d(tag, mCategory + " onStart");
	// super.onDestroy();
	// }
}