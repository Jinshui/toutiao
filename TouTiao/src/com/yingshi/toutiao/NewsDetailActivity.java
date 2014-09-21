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
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.actions.GetCommentsAction;
import com.yingshi.toutiao.actions.ParallelTask;
import com.yingshi.toutiao.model.Comment;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.util.Utils;
import com.yingshi.toutiao.view.CommentListRow;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.HeaderView;

public class NewsDetailActivity extends Activity
{
	private View mShareNewsWidget;
	private LinearLayout mCommentsList;
	private ImageButton mShowCommentsBtn;
	private News mNews;
	private GetCommentsAction mGetCommentsAction;
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
		
		final EditText commentTextView = (EditText)findViewById(R.id.id_news_detail_comment_text);
		commentTextView.setOnEditorActionListener(new OnEditorActionListener(){
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(event != null && event.getAction() == KeyEvent.ACTION_UP
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
					return true;
				}
				return false;
			}
		});
		mShareNewsWidget = findViewById(R.id.id_news_share_widget);
		mCommentsList = (LinearLayout)findViewById(R.id.id_news_detail_comments);
		mShowCommentsBtn = (ImageButton)findViewById(R.id.id_news_get_comment);
		
		mNews = getIntent().getParcelableExtra(Constants.INTENT_EXTRA_NEWS);
		updateUI(mNews);
	}
	
    protected void fillItems(LinearLayout contentView, List<Comment> items){
        for(final Comment item : items){
            CommentListRow view = new CommentListRow(this, null);
            view.setProduct(item);
            @SuppressWarnings("deprecation")
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            contentView.addView(view, params);
        }
    }
    
	private void updateUI(final News news){
		mNews = news;
		TextView titleView = (TextView)findViewById(R.id.id_news_detail_title);
		titleView.setText(mNews.getName());
		
		TextView dateView = (TextView)findViewById(R.id.id_news_detail_time);
		String dateViewText = String.format("%s  %s", Utils.formatDate("yyyy/MM/dd HH:mm:ss", mNews.getTime()), mNews.getAuthor());
		dateView.setText(dateViewText);
		
		CustomizeImageView imageView = (CustomizeImageView)findViewById(R.id.id_news_detail_img);
		if(mNews.getPhotoUrls().size() == 0)
			imageView.setVisibility(View.GONE);
		else
			imageView.loadImage(mNews.getPhotoUrls().get(0));

		View playButton = findViewById(R.id.id_news_detail_play);
		playButton.setVisibility( ( mNews.isHasVideo() && mNews.getThumbnailUrls().size()>0 ) ?  View.VISIBLE : View.GONE);
		
		TextView textView = (TextView)findViewById(R.id.id_news_detail_text);
		textView.setText(mNews.getContent());
	}

	public void playVideo(View view){
		if(mNews == null)
			return;
        String extension = MimeTypeMap.getFileExtensionFromUrl(mNews.getVideoUrl());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
        mediaIntent.setDataAndType(Uri.parse(mNews.getVideoUrl()), mimeType);
        startActivity(mediaIntent);
	}
	
	public void share(View view){
		if(mShareNewsWidget.getVisibility() == View.GONE){
			mShareNewsWidget.setVisibility(View.VISIBLE);
		}else{
			mShareNewsWidget.setVisibility(View.GONE);
		}
	}
	
	public void addToFavorites(View view){
		new ParallelTask<Void>() {
			protected Void doInBackground(Void... params) {
				((TouTiaoApp)getApplication()).getFavoritesDAO().save(mNews);
				return null;
			}
			public void onPostExecute(Void result){
				Toast.makeText(getApplication(), R.string.add_favorite_succ, Toast.LENGTH_SHORT).show();
			}
		}.execute();
	}
	
	public void getComments(View view){
		mShowCommentsBtn.setVisibility(View.GONE);
		mGetCommentsAction = new GetCommentsAction(this, mNews.getId(), mNews.getCategory(), 1, 30);
		mGetCommentsAction.execute(new UICallBack<Pagination<Comment>>(){
			public void onSuccess(Pagination<Comment> result) {
                List<Comment> comments = result.getItems();
                if(comments != null && !comments.isEmpty()){
                    fillItems(mCommentsList, comments);
    				Toast.makeText(NewsDetailActivity.this, R.string.no_more_load, Toast.LENGTH_SHORT).show();
                }else{
    				Toast.makeText(NewsDetailActivity.this, R.string.no_comments_found, Toast.LENGTH_SHORT).show();
                }
			}
			public void onFailure(ActionError error) {
				mShowCommentsBtn.setVisibility(View.VISIBLE);
				Toast.makeText(NewsDetailActivity.this, R.string.load_comment_failed, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void shareWeiChat(View view){
		mShareNewsWidget.setVisibility(View.GONE);
		WXTextObject textObj = new WXTextObject();
		textObj.text = "";
		
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		
		msg.description = "";
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); 
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline; // SendMessageToWX.Req.WXSceneSession;
		WXAPIFactory.createWXAPI(this, null).sendReq(req);
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	public void shareWeibo(View view){
		mShareNewsWidget.setVisibility(View.GONE);
	}
	
	public void shareQQ(View view){
		mShareNewsWidget.setVisibility(View.GONE);
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
	}
}
