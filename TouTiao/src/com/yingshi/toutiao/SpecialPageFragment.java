package com.yingshi.toutiao;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yingshi.toutiao.model.Article;
import com.yingshi.toutiao.model.Article.Type;
import com.yingshi.toutiao.model.Special;
import com.yingshi.toutiao.util.ServerMock;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.SpecialHeader;
import com.yingshi.toutiao.view.ptr.AbstractPTRFragment;
import com.yingshi.toutiao.view.ptr.PTRListAdapter;

public class SpecialPageFragment extends AbstractPTRFragment<Special, Article> {
	private final static String tag = "TT-SpecialPageFragment";
	
	private String mId;
	private SpecialHeader mSpecialHeader;
	
	public SpecialPageFragment() {
	}

	public SpecialPageFragment(String id) {
		Log.d(tag, id + " new SpecialPageFragment");
		mId = id;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(tag, mId + " onCreate");
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mId = savedInstanceState.getString("mId");
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(tag, mId + " onSaveInstanceState");
		outState.putString("mId", mId);
	}


	public ViewHolder createHeader(LayoutInflater inflater){
		ViewHolder holder = new ViewHolder();
		mSpecialHeader = (SpecialHeader)inflater.inflate(R.layout.view_special_list_header, null);
		holder.headerView = mSpecialHeader;
		holder.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		return holder;
	}


	public SpecialNewsArrayAdapter onDataChanged(Special special){
		mSpecialHeader.getHeaderImageView().loadImage(special.getPhoto().getUrl());
		mSpecialHeader.getSummaryView().setText(special.getSummary());
		mSpecialHeader.getPageIndicatorView().setText("1/9");//TODO: Set count
		return new SpecialNewsArrayAdapter(getActivity(), R.layout.view_special_list_item, special.getArticles());
	}
	
	protected class SpecialNewsArrayAdapter extends PTRListAdapter<Article> {
        private LayoutInflater mInflater;
        public SpecialNewsArrayAdapter(Context context, int res, List<Article> items) {
            super(context, res, items);
            mInflater = LayoutInflater.from(context);
        }
        

        @SuppressWarnings("deprecation")
		public View getView(final int position, View convertView,
                ViewGroup parent) {
        	final Article article = getItem(position);
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
            if(!TextUtils.isEmpty(article.getTitle())){
                holder.newsTitle.setText(article.getTitle());
            }
            if( article.getThumbnail()!=null && article.getThumbnail().getUrl() != null){
            	if(article.getThumbnail().getData() != null)
            		holder.newsThumbnail.setBackgroundDrawable(new BitmapDrawable(article.getThumbnail().getData()));
            	else
            		holder.newsThumbnail.loadImage(article.getThumbnail().getUrl());
            }

            if(!TextUtils.isEmpty(article.getSummary())){
                holder.newsSummary.setText(article.getSummary());
            }
            Type type = article.getType();
            holder.newsVideoSign.setVisibility( type == Type.VIDEO ? View.VISIBLE : View.INVISIBLE);
            
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
            TextView  newsSummary;
            View newsVideoSign;
        }
    }
	

	public Special loadData(){
		Log.d(tag, "started loading special page for " + mId);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		return ServerMock.getSpecial("");
	}
	
	public List<Article> loadMoreList(){
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		return null;
	}
}