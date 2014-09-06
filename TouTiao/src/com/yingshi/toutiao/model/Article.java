package com.yingshi.toutiao.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {
	public static enum Type{
		TEXT,
		VIDEO,
		SPECIAL
	}
	private int id;
	private String title;
	private String author;
	private long createTime;
	private String source;
	private String content;
	private Photo photo;
	private Type type = Type.TEXT;
	private List<Comment> comments;
	public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
		public Article createFromParcel(Parcel in) {
			return new Article(in);
		}

		public Article[] newArray(int size) {
			return new Article[size];
		}
	};

	public Article(){}
	
	private Article(Parcel in) {
		id = in.readInt();
		title = in.readString();
		author = in.readString();
		createTime = in.readLong();
		source = in.readString();
		content = in.readString();
		photo = in.readParcelable(this.getClass().getClassLoader());
		type = Type.valueOf(in.readString());
		comments = in.createTypedArrayList(Comment.CREATOR);
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(title);
		dest.writeString(author);
		dest.writeLong(createTime);
		dest.writeString(source);
		dest.writeString(content);
		dest.writeParcelable(photo, 0);
		dest.writeString(type.name());
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

	public String getSource() {
		if(source == null)
			return "";
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<Comment> getComments() {
		if(comments == null){
			comments = new ArrayList<Comment>();
		}
		return comments;
	}
	
	public String toString(){
		return title;
	}
}
