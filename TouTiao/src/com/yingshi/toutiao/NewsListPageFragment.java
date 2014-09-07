package com.yingshi.toutiao;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.TextView;

import com.yingshi.toutiao.model.Article;
import com.yingshi.toutiao.model.Article.Type;
import com.yingshi.toutiao.model.NewsPage;
import com.yingshi.toutiao.model.Photo;
import com.yingshi.toutiao.util.ServerMock;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.PhotoPager;
import com.yingshi.toutiao.view.ptr.AbstractPTRFragment;
import com.yingshi.toutiao.view.ptr.PTRListAdapter;

public class NewsListPageFragment extends AbstractPTRFragment<NewsPage, Article> {
	private final static String tag = "TT-SlidePageFragment";
	
	private String mCategory;
	private PhotoPager mPhotoPager;
	
	public NewsListPageFragment() {
	}

	public NewsListPageFragment(String category) {
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
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(tag, mCategory + " onSaveInstanceState");
		outState.putString("mCategory", mCategory);
	}


	public ViewHolder createHeader(LayoutInflater inflater){
		ViewHolder holder = new ViewHolder();
		mPhotoPager = (PhotoPager)inflater.inflate(R.layout.view_news_list_header, null);
		mPhotoPager.getPhotoViewPager().setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			public void onPageSelected(int position) {
				updatePhotoPager(position, getData().getPhotos());
			}
		});
		holder.headerView = mPhotoPager;
		holder.height = 250;
		return holder;
	}


	public NewsArrayAdapter onDataChanged(NewsPage newsPage){
		mPhotoPager.getPhotoViewPager().setAdapter(new PhotoPagerAdapter(getChildFragmentManager(), newsPage.getPhotos()));
		updatePhotoPager(0, newsPage.getPhotos());
		return new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, newsPage.getArticles());
	}
	
	private void updatePhotoPager(int position, List<Photo> photos){
		if(photos.size() > 0){
			mPhotoPager.setVisibility(View.VISIBLE);
			mPhotoPager.getPhotoNumView().setText(String.format("%d/%d", position + 1, photos.size()));
			mPhotoPager.getPhotoDescriptionView().setText(photos.get(position).getDescription());
		}else{
			mPhotoPager.setVisibility(View.GONE);
		}
	}
	
	public static class NewsArrayAdapter extends PTRListAdapter<Article> {
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
            
            final Type type = article.getType();
            holder.newsVideoSign.setVisibility( type == Type.VIDEO ? View.VISIBLE : View.INVISIBLE);
            holder.newsSpecialSign.setVisibility( type == Type.SPECIAL ? View.VISIBLE : View.INVISIBLE);
            
            convertView.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					Intent showNewsDetailIntent = new Intent();
					if(type == Type.SPECIAL){
						showNewsDetailIntent.setClass(getContext(), SpecialNewsActivity.class);
						showNewsDetailIntent.putExtra(SpecialNewsActivity.INTENT_EXTRA_SPECIAL_ID, "speical_id");
					}else{
						showNewsDetailIntent.setClass(getContext(), NewsDetailActivity.class);
						showNewsDetailIntent.putExtra(NewsDetailActivity.INTENT_EXTRA_ARTICLE, article);
					}
					getContext().startActivity(showNewsDetailIntent);
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
		List<Photo> photos;
		public PhotoPagerAdapter(FragmentManager fm, List<Photo> photos) {
			super(fm);
			this.photos = photos;
		}

		@Override
		public Fragment getItem(int position) {
			PhotoPageFragment page = pages.get(position);
			if(page == null){
				page = new PhotoPageFragment(getActivity(), photos.get(position));
				pages.put(position, page);
			}
			return page;
		}

		@Override
		public int getCount() {
			return photos.size();
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
	
	public List<Article> loadMoreList(){
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		return null;
	}
}