package com.yingshi.toutiao.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
	private String author;
	private long createTime;
	private String content;
	public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
		public Comment createFromParcel(Parcel in) {
			return new Comment(in);
		}

		public Comment[] newArray(int size) {
			return new Comment[size];
		}
	};

	private Comment(Parcel in) {
		author = in.readString();
		createTime = in.readLong();
		content = in.readString();
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(author);
		dest.writeLong(createTime);
		dest.writeString(content);
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
