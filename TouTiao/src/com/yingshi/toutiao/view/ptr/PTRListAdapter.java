package com.yingshi.toutiao.view.ptr;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class PTRListAdapter<T> extends ArrayAdapter<T>{
	private List<T> mObjects;
	public PTRListAdapter(Context context, int resource, List<T> objects) {
		super(context, resource, objects);
		mObjects = objects;
	}
	public void addObjects(List<T> newObjects){
		mObjects.addAll(newObjects);
		this.notifyDataSetChanged();
	}
}
