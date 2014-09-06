package com.yingshi.toutiao;

import com.yingshi.toutiao.view.HeaderView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SpecialNewsActivity extends FragmentActivity {

	private ImageView mHeaderImage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_news_special);
	    
	    HeaderView header = (HeaderView)findViewById(R.id.id_news_detail_header);
	    header.setTitle(R.string.special_header_text);
	    header.setLeftImage(R.drawable.fanhui, new OnClickListener(){
			public void onClick(View v) {
				finish();
			}
	    });
	    mHeaderImage = (ImageView)findViewById(R.id.id_special_header_img);
	}

}