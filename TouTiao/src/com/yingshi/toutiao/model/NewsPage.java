package com.yingshi.toutiao.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsPage implements Parcelable{
	private List<Photo> photos;
	private List<Article> articles;
	public static final Parcelable.Creator<NewsPage> CREATOR = new Parcelable.Creator<NewsPage>() {
		public NewsPage createFromParcel(Parcel in) {
			return new NewsPage(in);
		}

		public NewsPage[] newArray(int size) {
			return new NewsPage[size];
		}
	};

	public NewsPage(){}
	
	private NewsPage(Parcel in) {
		photos = in.createTypedArrayList(Photo.CREATOR);
		articles = in.createTypedArrayList(Article.CREATOR);
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(photos);
		dest.writeTypedList(articles);
	}

	public List<Photo> getPhotos() {
		if(photos == null){
			photos = new ArrayList<Photo>();
		}
		return photos;
	}

	public List<Article> getArticles() {
		if(articles == null){
			articles = new ArrayList<Article>();
		}
		return articles;
	}
}
