package com.yingshi.toutiao.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
	private String id;
	private String name;

	public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
		public Category createFromParcel(Parcel in) {
			return new Category(in);
		}

		public Category[] newArray(int size) {
			return new Category[size];
		}
	};

	public Category() {
	}

	private Category(Parcel in) {
		id = in.readString();
		name = in.readString();
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
	}
}
