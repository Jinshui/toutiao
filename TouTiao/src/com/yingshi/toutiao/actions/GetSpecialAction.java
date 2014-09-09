package com.yingshi.toutiao.actions;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yingshi.toutiao.model.Special;

public class GetSpecialAction extends PaginationAction<Special> {
    //request keys
    private static final String NAME = "name";
    //local variables
    private String mSpecialName;

    public GetSpecialAction(Context context, String specialName){
		super(context, 1, 100);
		mSpecialName = specialName;
        mServiceId = SERVICE_ID_SPECIAL_HEAD;
    }

    @Override
    public void addRequestParameters(JSONObject parameters) throws JSONException {
        parameters.put(NAME, mSpecialName);
    }
	
	@Override
	protected PaginationAction<Special> createNextPageAction() {
		return new GetSpecialAction(mAppContext, mSpecialName);
	}

	@Override
	public Special convertJsonToResult(JSONObject item) throws JSONException {
		return Special.fromJSON(item);
	}
}
