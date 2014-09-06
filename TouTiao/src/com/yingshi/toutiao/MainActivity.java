package com.yingshi.toutiao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.yingshi.toutiao.view.HeaderView;

public class MainActivity extends SlidingFragmentActivity
{

	private List<RelativeLayout> mTabs = new ArrayList<RelativeLayout>();
	private static String[] tabTitles = { "HEADLINE", "TELEPLAY", "MOVIE", "RATINGS"};//, "明星", "综艺", "公司" };

	private TabHost.TabContentFactory mEmptyTabContentFactory = new TabHost.TabContentFactory(){
		public View createTabContent(String tag) {
			return new TextView(MainActivity.this);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		// 初始化SlideMenu
		initUserCenterMenu();
		// 初始化ContentView
		initContentView();
	}

	private void initContentView()
	{
		final HeaderView headerView = (HeaderView)findViewById(R.id.id_main_header);
		final HorizontalScrollView tabScrollView = (HorizontalScrollView)findViewById(R.id.tabs_scrollView);
		final TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
		//Header view
		headerView.setLeftImage(R.drawable.gerenzhongxin_shoucang, new OnClickListener(){
			public void onClick(View v) {
				getSlidingMenu().showMenu();
			}});
		headerView.setRightImage(R.drawable.sousuo_sousuo, new OnClickListener(){
			public void onClick(View v) {
			}});
		headerView.setTitle(R.string.title_toutiao);
		
		//Tabhost
		tabHost.setup();
		for (String tabTitle : tabTitles) {
			RelativeLayout tabIndicator = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.view_tab_widget, null);
			TextView tvTab = (TextView) tabIndicator.findViewById(R.id.tv_title);
			tvTab.setText(tabTitle);
			mTabs.add(tabIndicator);
			tabHost.addTab(tabHost.newTabSpec(tabTitle).setIndicator(tabIndicator).setContent(mEmptyTabContentFactory));
		}
		//点击tabhost中的tab时，要切换下面的viewPager
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				for (int i = 0; i < tabTitles.length; i++) {
					if (tabTitles[i].equals(tabId)) {
						viewPager.setCurrentItem(i);
					}
				}
			}
		});
		
		//Pager
		viewPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager(), tabTitles.length));
		//切换Pager时,要将当前对应的Tab滚动到可见位置
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			public void onPageSelected(int position) {
				tabHost.setCurrentTab(position);
				int moveLeft = (int) mTabs.get(position).getLeft() - (int) mTabs.get(1).getLeft();
				tabScrollView.smoothScrollTo(moveLeft, 0);
				highlightTab(position);
			}
		});
		highlightTab(0);
	}
	
	private void highlightTab(int position){
		for(int i=0; i<mTabs.size(); i++){
			TextView tvTab = (TextView) mTabs.get(i).findViewById(R.id.tv_title);
			if(i == position){
				tvTab.setTextColor(Color.RED);
			}else{
				tvTab.setTextColor(Color.BLACK);
			}
		}
	}

	private void initUserCenterMenu()
	{
		SlidingMenu menu = getSlidingMenu();
		Fragment leftMenuFragment = new UserCenterFragment(menu);
		setBehindContentView(R.layout.view_user_center_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.id_left_menu_frame, leftMenuFragment).commit();
		menu.setMode(SlidingMenu.LEFT);
		// 设置触摸屏幕的模式
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidth(0);
		menu.setShadowDrawable(R.drawable.shadow);
		// 设置滑动菜单视图的宽度
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		menu.setFadeDegree(0.35f);
		// menu.setBehindScrollScale(1.0f);
		menu.setSecondaryShadowDrawable(R.drawable.shadow);
	}

	/**
	 * A simple pager adapter that represents 5 {@link SlidePageFragment}
	 * objects, in sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		Map<String, SlidePageFragment> pages = new HashMap<String, SlidePageFragment>();
		public ScreenSlidePagerAdapter(FragmentManager fm, int count) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			SlidePageFragment page = pages.get(tabTitles[position]);
			if(page == null){
				page = new SlidePageFragment(tabTitles[position]);
				pages.put(tabTitles[position], page);
			}
			return page;
//			return new SlidePageFragment(tabTitles[position]);
		}

		@Override
		public int getCount() {
			return tabTitles.length;
		}
	}
}
