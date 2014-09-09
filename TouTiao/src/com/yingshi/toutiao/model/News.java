package com.yingshi.toutiao.model;

import org.json.JSONException;
import org.json.JSONObject;


public class News extends BaseModel{
	private String name;
	private String summary;
	private long time;
	private String content;
	private String category;
	private String contact;
	private int likes;
	private boolean isSpecial;
	private String specialName;
	private boolean hasVideo;
	private String videoUrl;
	private String videoPhotoUrl;
	private String author;
	private String photoUrl;
	private String thumbnailUrl;
	
	//used by client only
	private String videoPhotoFilePath;
	private String photoFilePath;
	private String thumbnailFilePath;
	private boolean isFavorite;

	public News(){}
	
    public static News fromJSON(JSONObject json) throws JSONException{
        if(json == null)
            throw new IllegalArgumentException("JSONObject is null");
        News news = new News();
        if(json.has("Id"))
        	news.setId(json.getString("Id"));
        if(json.has("NewsName"))
        	news.setName(json.getString("NewsName"));
        if(json.has("Dy"))
        	news.setSummary(json.getString("Dy"));
        if(json.has("NewsName"))
        	news.setName(json.getString("NewsName"));
        if(json.has("Time"))
        	news.setTime(Long.valueOf(json.getString("Time")));
        if(json.has("TypeName"))
        	news.setCategory(json.getString("TypeName"));
        if(json.has("LinkContent"))
        	news.setContact(json.getString("LinkContent"));
        if(json.has("Goods"))
        	news.setLikes(Integer.valueOf(json.getString("Goods")));
        if(json.has("IsZt"))
        	news.setSpecial(json.getBoolean("IsZt"));
        if(json.has("ZtName"))
        	news.setSpecialName(json.getString("ZtName"));
        if(json.has("IsSp"))
        	news.setHasVideo(json.getBoolean("IsSp"));
        if(json.has("Author"))
        	news.setAuthor(json.getString("Author"));
        if(json.has("BigUrl"))
        	news.setPhotoUrl(json.getString("BigUrl"));
        if(json.has("SmallUrl"))
        	news.setThumbnailUrl(json.getString("SmallUrl"));
        if(json.has("VidelUrl"))
        	news.setVideoUrl(json.getString("VidelUrl"));
        if(json.has("VidelPicUrl"))
        	news.setVideoPhotoUrl(json.getString("VidelPicUrl"));
        return news;
    }
	
	public int describeContents() {
		return 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public boolean isSpecial() {
		return isSpecial;
	}

	public void setSpecial(boolean isSpecial) {
		this.isSpecial = isSpecial;
	}

	public String getSpecialName() {
		return specialName;
	}

	public void setSpecialName(String specialName) {
		this.specialName = specialName;
	}

	public boolean isHasVideo() {
		return hasVideo;
	}

	public void setHasVideo(boolean hasVideo) {
		this.hasVideo = hasVideo;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getVideoPhotoUrl() {
		return videoPhotoUrl;
	}

	public void setVideoPhotoUrl(String videoPhotoUrl) {
		this.videoPhotoUrl = videoPhotoUrl;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getVideoPhotoFilePath() {
		return videoPhotoFilePath;
	}

	public void setVideoPhotoFilePath(String videoPhotoFilePath) {
		this.videoPhotoFilePath = videoPhotoFilePath;
	}

	public String getPhotoFilePath() {
		return photoFilePath;
	}

	public void setPhotoFilePath(String photoFilePath) {
		this.photoFilePath = photoFilePath;
	}

	public String getThumbnailFilePath() {
		return thumbnailFilePath;
	}

	public void setThumbnailFilePath(String thumbnailFilePath) {
		this.thumbnailFilePath = thumbnailFilePath;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}
}
