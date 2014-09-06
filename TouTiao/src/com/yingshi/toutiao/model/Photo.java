package com.yingshi.toutiao.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {
	private String url;
	private Bitmap data;
	private String name;
	private String description;
	public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
		public Photo createFromParcel(Parcel in) {
			return new Photo(in);
		}

		public Photo[] newArray(int size) {
			return new Photo[size];
		}
	};

	public Photo(){}
	
	private Photo(Parcel in) {
		url = in.readString();
		data = in.readParcelable(null);
		name = in.readString();
		description = in.readString();
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);
		dest.writeParcelable(data, 0);
		dest.writeString(name);
		dest.writeString(description);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Bitmap getData() {
		return data;
	}

	public void setData(Bitmap data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
