package com.yingshi.toutiao.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Special implements Parcelable {
	private int id;
	private String summary;
	private Photo photo; 
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
}
