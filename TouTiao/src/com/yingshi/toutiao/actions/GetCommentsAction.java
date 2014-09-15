package com.yingshi.toutiao.actions;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yingshi.toutiao.model.Comment;

public class GetCommentsAction extends PaginationAction<Comment> {

    //request keys
    private static final String NAME = "name";
    private static final String NEWS_ID = "newsid";
    //local variables
    private String mNewsId;
    private String mCategory;

    public GetCommentsAction(Context context, String newsId, String category, int pageIndex, int pageSize){
        super(context, pageIndex, pageSize);
        mNewsId = newsId;
        mCategory = category;
        mServiceId = SERVICE_ID_COMMENTS;
    }

    @Override
    public void addRequestParameters(JSONObject parameters) throws JSONException {
        super.addRequestParameters(parameters);
        parameters.put(NEWS_ID, mNewsId);
        parameters.put(NAME, mCategory);
    }

    protected GetCommentsAction createNextPageAction(){
        GetCommentsAction action = new GetCommentsAction(
                            mAppContext,
                            mNewsId,
                            mCategory,
                            getPageIndex(),
                            getPageSize()
                        );
        return action;
    }

    public Comment convertJsonToResult(JSONObject item) throws JSONException{
        return Comment.fromJSON(item);
    }
}
