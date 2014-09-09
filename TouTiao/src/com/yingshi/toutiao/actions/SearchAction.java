package com.yingshi.toutiao.actions;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yingshi.toutiao.model.News;

public class SearchAction extends PaginationAction<News> {

    //request keys
    private static final String NAME = "name";
    //local variables
    private String mKeyword;

    public SearchAction(Context context, String keyword, int pageIndex, int pageSize){
        super(context, pageIndex, pageSize);
        mKeyword = keyword;
        mServiceId = SERVICE_ID_SEARCH;
    }

    @Override
    public void addRequestParameters(JSONObject parameters) throws JSONException {
        super.addRequestParameters(parameters);
        parameters.put(NAME, mKeyword);
    }

    protected SearchAction createNextPageAction(){
        SearchAction action = new SearchAction(
                            mAppContext,
                            mKeyword,
                            getPageIndex(),
                            getPageSize()
                        );
        return action;
    }

    public News convertJsonToResult(JSONObject item) throws JSONException{
        return News.fromJSON(item);
    }
}
