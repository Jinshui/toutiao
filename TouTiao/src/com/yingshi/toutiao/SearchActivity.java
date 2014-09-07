package com.yingshi.toutiao;

import com.yingshi.toutiao.view.HeaderView;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class SearchActivity extends FragmentActivity {

	public static final String INTENT_EXTRA_SPECIAL_ID = "special_id";
	private EditText mKeywordText;
	private SearchPageFragment mSearchFragment;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		HeaderView headerView = (HeaderView) findViewById(R.id.id_search_header);
		headerView.setTitle(R.string.search_header_text);
		headerView.setLeftImage(R.drawable.fanhui, new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		mKeywordText = (EditText) findViewById(R.id.id_search_keyword_text);
		mSearchFragment = new SearchPageFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.id_search_result_list, mSearchFragment).commit();
	}

	public void doSearch(View view){
		String keyWord = mKeywordText.getText().toString();
		if(!TextUtils.isEmpty(keyWord)){
			mSearchFragment.doSearch(keyWord);
		}
	}
	
}