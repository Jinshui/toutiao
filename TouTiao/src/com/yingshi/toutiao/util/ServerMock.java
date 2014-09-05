package com.yingshi.toutiao.util;

import android.content.Context;
import android.util.Log;

import com.yingshi.toutiao.R;
import com.yingshi.toutiao.model.Category;
import com.yingshi.toutiao.model.NewsPage;
import com.yingshi.toutiao.model.Photo;

public class ServerMock {
	private final static String tag = "TT-ServerMock";
	public static NewsPage getNewsPage(String category){
		Log.d(tag, "getNewsPage " + category);
		NewsPage news = new NewsPage();
		if(Category.HEADLINE.name() == category){
			Photo photo1 = new Photo();
			Photo photo2 = new Photo();
			photo1.setUrl("HEADLINE1");
			photo2.setUrl("HEADLINE2");
			photo2.setUrl("HEADLINE3");
			photo2.setUrl("HEADLINE4");
			news.getPhotos().add(photo1);
			news.getPhotos().add(photo2);
		}else if(Category.MOVIE.name() == category){
			Photo photo1 = new Photo();
			Photo photo2 = new Photo();
			photo1.setUrl("MOVIE1");
			photo2.setUrl("MOVIE2");
			photo2.setUrl("MOVIE3");
			news.getPhotos().add(photo1);
			news.getPhotos().add(photo2);
		}else if(Category.RATINGS.name() == category){
			Photo photo1 = new Photo();
			Photo photo2 = new Photo();
			photo1.setUrl("RATINGS1");
			photo2.setUrl("RATINGS2");
			photo2.setUrl("RATINGS3");
			news.getPhotos().add(photo1);
			news.getPhotos().add(photo2);
		}else if(Category.TELEPLAY.name() == category){
			Photo photo1 = new Photo();
			Photo photo2 = new Photo();
			photo1.setUrl("TELEPLAY1");
			photo2.setUrl("TELEPLAY2");
			photo2.setUrl("TELEPLAY3");
			news.getPhotos().add(photo1);
			news.getPhotos().add(photo2);
		}
		return news;
	}
	

	public static byte[] getPhoto(String url, Context ctx){
		Log.d(tag, "getPhoto " + url);
		int resId = 0;
		if(url.equals("HEADLINE1")){
			resId = R.drawable.headline1;
		}else if(url.equals("HEADLINE2")){
			resId = R.drawable.headline2;
		}else if(url.equals("HEADLINE3")){
			resId = R.drawable.headline3;
		}else if(url.equals("HEADLINE4")){
			resId = R.drawable.headline4;
		}else if(url.equals("MOVIE1")){
			resId = R.drawable.movie1;
		}else if(url.equals("MOVIE2")){
			resId = R.drawable.movie2;
		}else if(url.equals("MOVIE3")){
			resId = R.drawable.movie3;
		}else if(url.equals("RATINGS1")){
			resId = R.drawable.rating1;
		}else if(url.equals("RATINGS2")){
			resId = R.drawable.rating2;
		}else if(url.equals("RATINGS3")){
			resId = R.drawable.rating3;
		}else if(url.equals("TELEPLAY1")){
			resId = R.drawable.teleplay1;
		}else if(url.equals("TELEPLAY2")){
			resId = R.drawable.teleplay2;
		}else if(url.equals("TELEPLAY3")){
			resId = R.drawable.teleplay3;
		}
		return PhotoUtil.bitmap2Bytes(PhotoUtil.resId2Bitmap(resId, ctx));
	}
	
}
