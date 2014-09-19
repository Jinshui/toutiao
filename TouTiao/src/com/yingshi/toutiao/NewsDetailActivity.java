package com.yingshi.toutiao;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView.OnEditorActionListener;

import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.actions.GetCommentsAction;
import com.yingshi.toutiao.actions.ParallelTask;
import com.yingshi.toutiao.model.Comment;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.storage.NewsDAO;
import com.yingshi.toutiao.util.Utils;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.HeaderView;
import com.yingshi.toutiao.view.LoadMoreView;
import com.yingshi.toutiao.view.LoadMoreView.OnMoreResultReturnListener;
import com.yingshi.toutiao.view.CommentListRow;

public class NewsDetailActivity extends Activity implements OnMoreResultReturnListener<Comment>
{
	private View mShareNewsWidget;
	private View mToolsBar;
	private LinearLayout mCommentsList;
	private LoadMoreView<Comment> mLoadComentBtn;
	private News mNews;
	private NewsDAO mNewsDAO;
	private GetCommentsAction mGetCommentsAction;
	@SuppressWarnings("unchecked")
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
		
		final long newsId = getIntent().getLongExtra(Constants.INTENT_EXTRA_NEWS_ID, 0);
		new ParallelTask<News>(){
			protected News doInBackground(Void... params) {
				return ((TouTiaoApp)getApplication()).getNewsDAO().get(newsId);
			}
			
			public void onPostExecute(News news){
				updateUI(news);
				loadComments();
			}
		}.execute();

		
		final EditText commentTextView = (EditText)findViewById(R.id.id_news_detail_comment_text);
		commentTextView.setOnEditorActionListener(new OnEditorActionListener(){
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(event != null && event.getAction() == KeyEvent.ACTION_UP
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
					addComment(commentTextView);
					return true;
				}
				return false;
			}
		});
		mShareNewsWidget = findViewById(R.id.id_news_share_widget);
		mToolsBar = findViewById(R.id.id_news_detail_tools_bar);
		mLoadComentBtn = (LoadMoreView<Comment>)findViewById(R.id.id_news_load_comment);
		mCommentsList = (LinearLayout)findViewById(R.id.id_news_detail_comments);
	}
	
    protected void fillItems(LinearLayout contentView, List<Comment> items){
        for(final Comment item : items){
            CommentListRow view = new CommentListRow(this, null);
            view.setProduct(item);
            LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            contentView.addView(view, params);
        }
    }
    
    private void updateLoadMoreView(){
        if(mGetCommentsAction.hasMore()){
        	mLoadComentBtn.setPaginationAction(mGetCommentsAction.getNewPageAction());
            mLoadComentBtn.setVisibility(View.VISIBLE);
            mLoadComentBtn.setOnMoreResultReturnListener(this);
        }else{
        	mLoadComentBtn.setVisibility(View.GONE);
        }
    }
    
	private void updateUI(final News news){
		mNews = news;
		TextView titleView = (TextView)findViewById(R.id.id_news_detail_title);
		titleView.setText(news.getName());
		
		TextView dateView = (TextView)findViewById(R.id.id_news_detail_time);
		dateView.setText(Utils.formatDate("yyyy/MM/dd HH:mm:ss", news.getTime()));
		
		CustomizeImageView imageView = (CustomizeImageView)findViewById(R.id.id_news_detail_img);
		if(news.getThumbnailUrls().size() == 0)
			imageView.setVisibility(View.GONE);
		else
			imageView.loadImage(news.getThumbnailUrls().get(0));

		View playButton = findViewById(R.id.id_news_detail_play);
		playButton.setVisibility( ( news.isHasVideo() && news.getThumbnailUrls().size()>0 ) ?  View.VISIBLE : View.GONE);
		
		TextView textView = (TextView)findViewById(R.id.id_news_detail_text);
		textView.setText(news.getContent());
	}

    private void loadComments(){
		mGetCommentsAction = new GetCommentsAction(this, mNews.getId(), mNews.getCategory(), 1, 20);
		mGetCommentsAction.execute(new UICallBack<Pagination<Comment>>(){
			public void onSuccess(Pagination<Comment> result) {
                List<Comment> comments = result.getItems();
                if(comments != null && !comments.isEmpty()){
                    fillItems(mCommentsList, comments);
                    updateLoadMoreView();
                }
			}
			public void onFailure(ActionError error) {
				
			}
		});
    }
	
	public void playVideo(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "video/* ";
        Uri uri = Uri.parse(mNews.getVideoUrl());
        intent.setDataAndType(uri, type);
        startActivity(intent);
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
	
	public void addComment(EditText view){
		view.getText().toString();
	}
	
	public void shareWeiChat(View view){
		mShareNewsWidget.setVisibility(View.GONE);
		mToolsBar.setBackgroundColor(Color.RED);
		WXTextObject textObj = new WXTextObject();
		textObj.text = "";
		
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		
		msg.description = "";
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); // transaction�ֶ�����Ψһ��ʶһ������
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline; // SendMessageToWX.Req.WXSceneSession;
		WXAPIFactory.createWXAPI(this, null).sendReq(req);
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
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

	@Override
	public void onMoreResultReturn(List<Comment> result) {
		
	}
}
