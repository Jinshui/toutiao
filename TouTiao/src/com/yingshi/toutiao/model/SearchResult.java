package com.yingshi.toutiao.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchResult implements Parcelable{
	private int totalCount;
	private List<Article> articles;
	public static final Parcelable.Creator<SearchResult> CREATOR = new Parcelable.Creator<SearchResult>() {
		public SearchResult createFromParcel(Parcel in) {
			return new SearchResult(in);
		}

		public SearchResult[] newArray(int size) {
			return new SearchResult[size];
		}
	};

	public SearchResult(){}
	
	private SearchResult(Parcel in) {
		totalCount = in.readInt();
		articles = in.createTypedArrayList(Article.CREATOR);
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(totalCount);
		dest.writeTypedList(articles);
	}

	public List<Article> getArticles() {
		if(articles == null){
			articles = new ArrayList<Article>();
		}
		return articles;
	}
}
