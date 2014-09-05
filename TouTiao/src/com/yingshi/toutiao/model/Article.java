package com.yingshi.toutiao.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {
	private String title;
	private String author;
	private long createTime;
	private String content;
	private Photo photo;
	private List<Comment> comments;
	public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
		public Article createFromParcel(Parcel in) {
			return new Article(in);
		}

		public Article[] newArray(int size) {
			return new Article[size];
		}
	};

	private Article(Parcel in) {
		title = in.readString();
		author = in.readString();
		createTime = in.readLong();
		content = in.readString();
		photo = in.readParcelable(null);
		comments = in.createTypedArrayList(Comment.CREATOR);
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(author);
		dest.writeLong(createTime);
		dest.writeString(content);
		dest.writeParcelable(photo, 0);
		dest.writeTypedList(comments);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	public List<Comment> getComments() {
		if(comments == null){
			comments = new ArrayList<Comment>();
		}
		return comments;
	}
}
