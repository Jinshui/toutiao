package com.yingshi.toutiao;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class FavoritesActivity extends FragmentActivity
{
	public static final String tag = "TT-FavoritesActivity";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_favorites);
		getSupportFragmentManager().beginTransaction().replace(R.id.id_favorites_list, new FavoritesFragment()).commit();
	}
}