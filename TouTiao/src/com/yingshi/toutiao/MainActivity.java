package com.yingshi.toutiao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
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
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.actions.GetCategoryAction;
import com.yingshi.toutiao.model.Category;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.view.HeaderView;

public class MainActivity extends SlidingFragmentActivity
{

	private List<RelativeLayout> mTabs = new ArrayList<RelativeLayout>();
//	private static String[] tabTitles = { "HEADLINE", "TELEPLAY", "MOVIE", "RATINGS"};//, "明星", "综艺", "公司" };
	private List<Category> mCategories;
	private TabHost mTabHost;
	private HorizontalScrollView mTabScrollView ;
	private ViewPager mViewPager;

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
		setContentView(R.layout.activity_news);
		// 初始化ContentView
		initContentView();
		// 初始化SlideMenu
		initUserCenterMenu();
	}

	private void initContentView()
	{
		final HeaderView headerView = (HeaderView)findViewById(R.id.id_main_header);
	    mTabScrollView = (HorizontalScrollView)findViewById(R.id.tabs_scrollView);
		mTabHost = (TabHost) findViewById(R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		//Header view
		headerView.setLeftImage(R.drawable.gerenzhongxin_shoucang, new OnClickListener(){
			public void onClick(View v) {
				getSlidingMenu().showMenu();
			}});
		headerView.setRightImage(R.drawable.sousuo_sousuo, new OnClickListener(){
			public void onClick(View v) {
				Intent showNewsDetailIntent = new Intent();
				showNewsDetailIntent.setClass(MainActivity.this, SearchActivity.class);
				startActivity(showNewsDetailIntent);
			}});
		headerView.setTitle(R.string.title_toutiao);
		
		GetCategoryAction getCategoryAction = new GetCategoryAction(this, 1, 100);
		getCategoryAction.execute(new UICallBack<Pagination<Category>>(){
			public void onSuccess(Pagination<Category> result) {
				updateUI(mCategories = result.getItems());
			}
			public void onFailure(ActionError error) {
				//TODO: HOW?
			}
		});
	}
	
	private void updateUI(final List<Category> categories){
		for (Category category : categories) {
			RelativeLayout tabIndicator = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.view_news_tab_widget, null);
			TextView tvTab = (TextView) tabIndicator.findViewById(R.id.tv_title);
			tvTab.setText(category.getName());
			mTabs.add(tabIndicator);
			mTabHost.addTab(mTabHost.newTabSpec(category.getName()).setIndicator(tabIndicator).setContent(mEmptyTabContentFactory));
		}
		//点击tabhost中的tab时，要切换下面的viewPager
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				for (int i = 0; i < categories.size(); i++) {
					if (categories.get(i).getName().equals(tabId)) {
						mViewPager.setCurrentItem(i);
					}
				}
			}
		});
		
		//Pager
		mViewPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager(), categories));
		//切换Pager时,要将当前对应的Tab滚动到可见位置
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			public void onPageSelected(int position) {
				mTabHost.setCurrentTab(position);
				int moveLeft = (int) mTabs.get(position).getLeft() - (int) mTabs.get(1).getLeft();
				mTabScrollView.smoothScrollTo(moveLeft, 0);
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
//		FrameLayout framelayout = new FrameLayout(this);
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
		List<Category> mCategories;
		Map<String, HomePageFragment> pages = new HashMap<String, HomePageFragment>();
		public ScreenSlidePagerAdapter(FragmentManager fm, List<Category> categories) {
			super(fm);
			mCategories = categories;
		}

		@Override
		public Fragment getItem(int position) {
			HomePageFragment page = pages.get(mCategories.get(position).getName());
			if(page == null){
				page = new HomePageFragment(mCategories.get(position).getName());
				pages.put(mCategories.get(position).getName(), page);
			}
			return page;
		}

		@Override
		public int getCount() {
			return mCategories.size();
		}
	}
}
