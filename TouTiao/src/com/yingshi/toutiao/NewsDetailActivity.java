package com.yingshi.toutiao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.actions.GetCommentsAction;
import com.yingshi.toutiao.actions.ParallelTask;
import com.yingshi.toutiao.model.Comment;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.social.AccountInfo;
import com.yingshi.toutiao.util.DialogHelper;
import com.yingshi.toutiao.util.Utils;
import com.yingshi.toutiao.view.CommentListRow;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.HeaderView;

public class NewsDetailActivity extends Activity
{
	private final static String tag = "TT-NewsDetailActivity";
	private View mShareNewsWidget;
	private LinearLayout mCommentsList;
	private ImageButton mShowCommentsBtn;
	private EditText mCommentTextView;
	private CustomizeImageView mImageView;
	private News mNews;
	private GetCommentsAction mGetCommentsAction;
	

    
    private IWXAPI mWxapi = null;
    private Tencent mTencent = null;
	
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
		
		mCommentTextView = (EditText)findViewById(R.id.id_news_detail_comment_text);
		mCommentTextView.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(((TouTiaoApp)getApplication()).getUserInfo() == null){
					showLonginConfirmDialog();
				}
			}
		});
		mCommentTextView.setOnEditorActionListener(new OnEditorActionListener(){
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(event != null && event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
					showAddCommentConfirmDialog();
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
		
        //Register weixin
        mWxapi = WXAPIFactory.createWXAPI(this, Constants.APP_WEIXIN_ID, true);
        mWxapi.registerApp(Constants.APP_WEIXIN_ID);
        mTencent = Tencent.createInstance(Constants.APP_TENCENT_ID, this);
	}
	
	private void showLonginConfirmDialog(){
		DialogHelper.createDialog(NewsDetailActivity.this, R.string.add_comment_login_dlg_msg, R.string.add_comment_login_dlg_title, 0, android.R.string.ok, new android.content.DialogInterface.OnClickListener(){
			@Override
			public void onClick(android.content.DialogInterface dialog, int which) {
				Intent intent = new Intent(NewsDetailActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}, android.R.string.cancel, null).show();
	}
    
	private void showAddCommentConfirmDialog(){
		DialogHelper.createDialog(this, R.string.confirm_add_comment_dlg_msg, R.string.confirm_add_comment_dlg_title, 0, android.R.string.ok, new android.content.DialogInterface.OnClickListener(){
			@Override
			public void onClick(android.content.DialogInterface dialog, int which) {
				addComment();
			}
		}, android.R.string.cancel, null).show();
	}
	
    private void addComment(){
    	AccountInfo userInfo = ((TouTiaoApp)getApplication()).getUserInfo();
    	if(userInfo == null){
    		showLonginConfirmDialog();
    		return;
    	}
    	AsyncHttpClient httpClient = new AsyncHttpClient();
    	RequestParams params = new RequestParams();
    	params.put("newsid", mNews.getId());
    	params.put("username", userInfo.getUserName());
    	params.put("content", Utils.encode(mCommentTextView.getText().toString(), "UTF-8"));
    	params.put("img", userInfo.getPhotoBase64());
    	httpClient.post(Constants.UPLOAD_ADDRESS, params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
                Log.d(tag, "Received JSON response : " + response.toString());
                Toast.makeText(NewsDetailActivity.this, R.string.add_comment_succ, Toast.LENGTH_SHORT).show();
                loadComments(null);
			}
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				super.onSuccess(statusCode, headers, response);
                Log.d(tag, "Received JSON response : " + response.toString());
                Toast.makeText(NewsDetailActivity.this, R.string.add_comment_succ, Toast.LENGTH_SHORT).show();
                loadComments(null);
			}
			public void onSuccess(int statusCode, Header[] headers, String response) {
				super.onSuccess(statusCode, headers, response);
                Log.d(tag, "Received JSON response : " + response);
                Toast.makeText(NewsDetailActivity.this, R.string.add_comment_succ, Toast.LENGTH_SHORT).show();
                loadComments(null);
			}
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
				onFailure(statusCode, headers, errorResponse == null? "" : errorResponse.toString(), throwable);
			}
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse){
				onFailure(statusCode, headers, errorResponse == null? "" : errorResponse.toString(), throwable);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(NewsDetailActivity.this, R.string.add_comment_fail, Toast.LENGTH_SHORT).show();
                Log.e(tag, "Received Error response : StatusCode: " + statusCode + ", Received response : " + responseString);
			} 
		});    		
    }
    
	private void updateUI(final News news){
		mNews = news;
		TextView titleView = (TextView)findViewById(R.id.id_news_detail_title);
		titleView.setText(mNews.getName());
		
		TextView dateView = (TextView)findViewById(R.id.id_news_detail_time);
		String dateViewText = String.format("%s  %s", Utils.formatDate("yyyy/MM/dd HH:mm:ss", mNews.getTime()), mNews.getAuthor());
		dateView.setText(dateViewText);
		
		mImageView = (CustomizeImageView)findViewById(R.id.id_news_detail_img);
		if(mNews.getPhotoUrls().size() == 0)
			mImageView.setVisibility(View.GONE);
		else
			mImageView.loadImage(mNews.getPhotoUrls().get(0));

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
	
	public void loadComments(View view){
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
	
    protected void fillItems(LinearLayout contentView, List<Comment> items){
    	contentView.removeAllViews();
        for(final Comment item : items){
            CommentListRow view = new CommentListRow(this, null);
            view.setProduct(item);
            @SuppressWarnings("deprecation")
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            contentView.addView(view, params);
        }
    }
	
	public void shareWeiChat(View view){
		Log.d(tag, "shareWeiChat");
		mShareNewsWidget.setVisibility(View.GONE);
	    WXWebpageObject webpage = new WXWebpageObject();  
	    webpage.webpageUrl = mNews.getPhotoUrls().get(0);  
		
		WXMediaMessage msg = new WXMediaMessage(webpage);
	    msg.title = mNews.getName().length() > 512 ? mNews.getName().substring(0, 512) : mNews.getName();
	    msg.description = mNews.getContent().length() > 1024 ? mNews.getContent().substring(0, 1024) : mNews.getContent();
	    Bitmap thumbnail = null;
	    if(!mNews.getThumbnailUrls().isEmpty()){
	    	String imagePath = CustomizeImageView.getCachedImagePath(mNews.getThumbnailUrls().get(0));
	    	if(imagePath != null){
	    		thumbnail = BitmapFactory.decodeFile(imagePath);
	    	}
	    }
	    if(thumbnail == null){
	    	thumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
	    }
    	msg.setThumbImage(thumbnail);
	    
	    
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); 
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline; // SendMessageToWX.Req.WXSceneSession;
		mWxapi.sendReq(req);
		Log.d(tag, "shareWeiChat done");
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
		Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, mNews.getName());
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mNews.getContent());
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mNews.getPhotoUrls().get(0));
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, (ArrayList<String>)mNews.getPhotoUrls());
        doShareToQzone(params);
	}
	
    private void doShareToQzone(final Bundle params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
            	mTencent.shareToQzone(NewsDetailActivity.this, params, new IUiListener() {
                    public void onCancel() {
                    }
                    public void onError(UiError e) {
                    }
					public void onComplete(Object response) {
					}

                });
            }
        }).start();
    }
	
	public void cancelShare(View view){
		mShareNewsWidget.setVisibility(View.GONE);
	}
}
