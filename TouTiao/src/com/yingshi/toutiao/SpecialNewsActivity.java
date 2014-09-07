package com.yingshi.toutiao;

import com.yingshi.toutiao.view.HeaderView;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

public class SpecialNewsActivity extends FragmentActivity {

	public static final String INTENT_EXTRA_SPECIAL_ID = "special_id";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_special);

		HeaderView headerView = (HeaderView) findViewById(R.id.id_news_special_header);
		headerView.setTitle(R.string.special_header_text);
		headerView.setLeftImage(R.drawable.fanhui, new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		String specialId = getIntent().getStringExtra(INTENT_EXTRA_SPECIAL_ID);
		getSupportFragmentManager().beginTransaction().replace(R.id.id_special_news_list, new SpecialPageFragment(specialId)).commit();
	}

}