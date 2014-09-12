package com.yingshi.toutiao;

import com.tencent.tauth.Tencent;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.storage.NewsDAO;
import com.yingshi.toutiao.util.Utils;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.CustomizeImageView.LoadImageCallback;
import com.yingshi.toutiao.view.HeaderView;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class NewsDetailActivity extends Activity
{
	private View mShareNewsWidget;
	private View mToolsBar;
	private News mNews;
	private NewsDAO mNewsDAO;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_detail);

		mNewsDAO = ((TouTiaoApp)getApplication()).getNewsDAO();
		
		HeaderView header = (HeaderView)findViewById(R.id.id_news_detail_header);
		header.setLeftImage(R.drawable.fanhui, new OnClickListener(){
			public void onClick(View v) {
				finish();
			}
		});
		
		final int newsId = getIntent().getIntExtra(Constants.INTENT_EXTRA_NEWS_ID, 0);
		new AsyncTask<Void, Void, News>(){
			protected News doInBackground(Void... params) {
				return ((TouTiaoApp)getApplication()).getNewsDAO().get(newsId);
			}
			
			public void onPostExecute(News news){
				updateUI(news);
			}
		}.execute();

		
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
	
	private void updateUI(News news){
		mNews = news;
		TextView titleView = (TextView)findViewById(R.id.id_news_detail_title);
		titleView.setText(news.getName());
		
		TextView dateView = (TextView)findViewById(R.id.id_news_detail_time);
		dateView.setText(Utils.formatDate("yyyy/MM/dd HH:mm:ss", news.getTime()));
		
		CustomizeImageView imageView = (CustomizeImageView)findViewById(R.id.id_news_detail_img);
		if(TextUtils.isEmpty(news.getPhotoUrl()))
			imageView.setVisibility(View.GONE);
		else
			imageView.loadImage(news.getPhotoUrl(), new LoadImageCallback(){
				public void onImageLoaded(Drawable drawable) {
					//TODO: Save Image?
				}
			});
		
		if(news.isHasVideo()){
			View playButton = findViewById(R.id.id_news_detail_play);
			playButton.setVisibility(View.INVISIBLE);
		}
		
		TextView textView = (TextView)findViewById(R.id.id_news_detail_text);
		textView.setText(news.getContent());
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
		mNews.setFavorite(true);
		mNewsDAO.save(mNews);
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
		//分享类型
//		Bundle params = new Bundle();
//		params.putString(Tencent.SHARE_TO_QQ_KEY_TYPE, SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
//	    params.putString(Tencent.SHARE_TO_QQ_TITLE, "标题");//必填
//	    params.putString(Tencent.SHARE_TO_QQ_SUMMARY, "摘要");//选填
//	    params.putString(Tencent.SHARE_TO_QQ_TARGET_URL, "跳转URL");//必填
//	    params.putStringArrayList(Tencent.SHARE_TO_QQ_IMAGE_URL, "图片链接ArrayList");
//	    mTencent.shareToQzone(this, params, new BaseUiListener());
	}
	
	public void cancelShare(View view){
		mShareNewsWidget.setVisibility(View.GONE);
		mToolsBar.setBackgroundColor(Color.RED);
	}
}
