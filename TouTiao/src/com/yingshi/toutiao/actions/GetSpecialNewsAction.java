package com.yingshi.toutiao.actions;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yingshi.toutiao.model.News;

public class GetSpecialNewsAction extends PaginationAction<News> {
    //request keys
    private static final String NAME = "name";
    //local variables
    private String mSpecialName;

    public GetSpecialNewsAction(Context context, String specialName, int pageIndex, int pageSize){
        super(context, pageIndex, pageSize);
        mSpecialName = specialName;
        mServiceId = SERVICE_ID_SPECIAL_NEWS;
    }

    @Override
    public void addRequestParameters(JSONObject parameters) throws JSONException {
        super.addRequestParameters(parameters);
        parameters.put(NAME, mSpecialName);
    }

    protected GetSpecialNewsAction createNextPageAction(){
        GetSpecialNewsAction action = new GetSpecialNewsAction(
                            mAppContext,
                            mSpecialName,
                            getPageIndex(),
                            getPageSize()
                        );
        return action;
    }

    public News convertJsonToResult(JSONObject item) throws JSONException{
        return News.fromJSON(item);
    }
}
