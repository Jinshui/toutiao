package com.yingshi.toutiao.model;

public class BaseModel {
	//used by client only
	private long _id;
	//server id
	private String id;

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
