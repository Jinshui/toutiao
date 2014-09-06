package com.yingshi.toutiao;

import com.yingshi.toutiao.model.Article;
import com.yingshi.toutiao.model.Article.Type;
import com.yingshi.toutiao.util.Utils;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.HeaderView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class NewsDetailActivity extends Activity
{
	public static final String INTENT_EXTRA_ARTICLE = "article";
	private View mShareNewsWidget;
	private View mToolsBar;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_detail);
		HeaderView header = (HeaderView)findViewById(R.id.id_news_detail_header);
		header.setLeftImage(R.drawable.fanhui, new OnClickListener(){
			public void onClick(View v) {
				finish();
			}
		});
		
		Article article = getIntent().getParcelableExtra(INTENT_EXTRA_ARTICLE);
		
		TextView titleView = (TextView)findViewById(R.id.id_news_detail_title);
		titleView.setText(article.getTitle());
		
		TextView dateView = (TextView)findViewById(R.id.id_news_detail_time);
		dateView.setText(Utils.formatDate("yyyy/MM/dd HH:mm:ss", article.getCreateTime()) + " " + article.getSource());
		
		CustomizeImageView imageView = (CustomizeImageView)findViewById(R.id.id_news_detail_img);
		if(article.getPhoto() == null)
			imageView.setVisibility(View.GONE);
		else
			imageView.loadImage(article.getPhoto().getUrl());
		
		if(article.getType() != Type.VIDEO){
			View playButton = findViewById(R.id.id_news_detail_play);
			playButton.setVisibility(View.INVISIBLE);
		}
		
		TextView textView = (TextView)findViewById(R.id.id_news_detail_text);
		textView.setText(article.getContent());
		
		final EditText commentTextView = (EditText)findViewById(R.id.id_news_detail_comment_text);
		commentTextView.setOnEditorActionListener(new OnEditorActionListener(){
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(event != null){
					addComment(commentTextView);
					return true;
				}
				return false;
			}
		});
		mShareNewsWidget = findViewById(R.id.id_news_share_widget);
		mToolsBar = findViewById(R.id.id_news_detail_tools_bar);
	}
	
	public void playVideo(View view){
		
	}
	
	public void share(View view){
		if(mShareNewsWidget.getVisibility() == View.GONE){
			mShareNewsWidget.setVisibility(View.VISIBLE);
			mToolsBar.setBackgroundResource(R.drawable.share_bg);
		}else{
			mShareNewsWidget.setVisibility(View.GONE);
			mToolsBar.setBackgroundColor(Color.RED);
		}
	}
	
	public void addToFavorites(View view){
		
	}
	
	public void addComment(View view){
		
	}
	
	public void shareWeiChat(View view){
		mShareNewsWidget.setVisibility(View.GONE);
		mToolsBar.setBackgroundColor(Color.RED);
	}
	
	public void shareWeibo(View view){
		mShareNewsWidget.setVisibility(View.GONE);
		mToolsBar.setBackgroundColor(Color.RED);
	}
	
	public void shareQQ(View view){
		mShareNewsWidget.setVisibility(View.GONE);
		mToolsBar.setBackgroundColor(Color.RED);
	}
	
	public void cancelShare(View view){
		mShareNewsWidget.setVisibility(View.GONE);
		mToolsBar.setBackgroundColor(Color.RED);
	}
}
