package com.yingshi.toutiao.actions;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yingshi.toutiao.model.News;

public class GetNewsAction extends PaginationAction<News> {

    //request keys
    private static final String NAME = "name";
    //local variables
    private String mCategory;

    public GetNewsAction(Context context, String category, int pageIndex, int pageSize){
        super(context, pageIndex, pageSize);
        mCategory = category;
        mServiceId = SERVICE_ID_NEWS;
    }

    @Override
    public void addRequestParameters(JSONObject parameters) throws JSONException {
        super.addRequestParameters(parameters);
        parameters.put(NAME, mCategory);
    }

    protected GetNewsAction createNextPageAction(){
        GetNewsAction action = new GetNewsAction(
                            mAppContext,
                            mCategory,
                            getPageIndex(),
                            getPageSize()
                        );
        return action;
    }

    public News convertJsonToResult(JSONObject item) throws JSONException{
        return News.fromJSON(item);
    }
}
