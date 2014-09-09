package com.yingshi.toutiao.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Special extends BaseModel{
	private String summary;
	private String photoUrl; 
	private String photoFilePath;
	
	public Special() {
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getPhotoFilePath() {
		return photoFilePath;
	}

	public void setPhotoFilePath(String photoFilePath) {
		this.photoFilePath = photoFilePath;
	}
	
    public static Special fromJSON(JSONObject json) throws JSONException{
        if(json == null)
            throw new IllegalArgumentException("JSONObject is null");
        Special special = new Special();
        if(json.has("Id"))
        	special.setId(json.getString("Id"));
        if(json.has("Dy"))
        	special.setSummary(json.getString("Dy"));
        if(json.has("BigUrl"))
        	special.setPhotoUrl(json.getString("BigUrl"));
        return special;
    }
}
