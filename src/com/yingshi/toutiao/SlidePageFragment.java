package com.yingshi.toutiao;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class SlidePageFragment extends Fragment {
	public static final String ARG_PAGE = "category";
	int[] photoRes = {R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5};
	private String mCategory;	
	private ViewPager mPhotoViewPager;
	private TextView mNumbmerText;
	private TextView mDescriptionText;

	public SlidePageFragment(){}
	
	public SlidePageFragment(String category){
		mCategory = category;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			mCategory = savedInstanceState.getString("mCategory");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ScrollView slideView = (ScrollView) inflater.inflate(R.layout.news_list, container, false);
		
		TextView textView = (TextView) slideView.findViewById(R.id.tv_show);
		textView.setText("This is " + mCategory);
		
		mPhotoViewPager = (ViewPager)slideView.findViewById(R.id.id_photos_pager);
		mPhotoViewPager.setAdapter(new PhotoPagerAdapter(getChildFragmentManager()));
		mPhotoViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			public void onPageSelected(int position) {
				mNumbmerText.setText(String.format("%d/%d", position, photoRes.length));
				mDescriptionText.setText("这是图"+position);
			}
		});
		mNumbmerText = (TextView) slideView.findViewById(R.id.id_photo_number);
		mDescriptionText = (TextView) slideView.findViewById(R.id.id_photo_description);
		
		return slideView;
	}
	
	public void onSaveInstanceState (Bundle outState){
		outState.putString("mCategory", mCategory);
	}
	
	private class PhotoPagerAdapter extends FragmentStatePagerAdapter {
		public PhotoPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return new PhotoPageFragment(photoRes[position]);
		}

		@Override
		public int getCount() {
			return photoRes.length;
		}
	}
}
