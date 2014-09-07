package com.yingshi.toutiao.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Special implements Parcelable {
	private String id;
	private String summary;
	private Photo photo; 
	private int totalCount;
	private List<Article> articles;
	public static final Parcelable.Creator<Special> CREATOR = new Parcelable.Creator<Special>() {
		public Special createFromParcel(Parcel in) {
			return new Special(in);
		}

		public Special[] newArray(int size) {
			return new Special[size];
		}
	};
	
	public Special() {
	}

	private Special(Parcel in) {
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<Article> getArticles() {
		if(articles == null)
			articles = new ArrayList<Article>();
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}
}
