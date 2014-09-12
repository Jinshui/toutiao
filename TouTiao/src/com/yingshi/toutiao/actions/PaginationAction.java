package com.yingshi.toutiao.actions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yingshi.toutiao.model.Pagination;

import android.content.Context;

public abstract class PaginationAction<Result> extends AbstractAction<Pagination<Result>> {
    //request keys
    private static final String PAGE_INDEX = "page";
    private static final String PAGE_SIZE = "num";
    //response keys
    private static final String COUNT = "Count";

    private int mPageIndex;
    private int mPageSize;
    private int mLastPageNum;
    private Pagination<Result> mLastResult;
    /**
     * For GET_RECOMMENDATION and GET_HOT and GET_FAVORITE actions
     * @param mContext
     * @param shopId
     * @param type
     * @param orderBy
     * @param orderWay
     * @param pageNum
     * @param itemsPerPage
     */
    public PaginationAction(Context context, int pageNum, int itemsPerPage){
        super(context);
        mPageIndex = pageNum;
        mPageSize = itemsPerPage;
    }


    @Override
    public void addRequestParameters(JSONObject parameters) throws JSONException {
        parameters.put(PAGE_INDEX, String.valueOf(mPageIndex));
        parameters.put(PAGE_SIZE, String.valueOf(mPageSize));
    }

    @Override
    public final Pagination<Result> createRespObject(JSONObject response) throws JSONException {
        Pagination<Result> pagination = new Pagination<Result>();
        pagination.setPageSize(mPageSize);
        if(response.has(COUNT))
            pagination.setTotalCounts(response.getInt(COUNT));
        if(response.has(RESP_LIST)){
            JSONArray items = response.getJSONArray(RESP_LIST);
            for(int i=0; i<items.length(); i++){
                JSONObject item = items.getJSONObject(i);
                pagination.getItems().add(convertJsonToResult(item));
            }
        }
        mLastPageNum = mPageIndex;
        mLastResult = pagination;
        return mLastResult;
    }

    /**
     * This method must be called before the execute(CallBack<Result> callback) is called
     * and can only be called once before each execution for it increases the page number
     * for each call
     * @return
     */
    public boolean hasMore(){
        if(mLastResult == null){
            mPageIndex = 1;
            return true;
        }
        if( mLastResult.getTotalCounts() > (mLastPageNum * mPageSize)){
            mPageIndex ++;
            return true;
        }
        return false;
    }
    public PaginationAction<Result> getNewPageAction(){
        PaginationAction<Result> action = createNextPageAction();
        action.mPageIndex = mPageIndex;
        action.mPageSize = mPageSize;
        action.mLastPageNum = mLastPageNum;
        action.mLastResult = mLastResult;
        return action;
    }

    public final int getPageIndex(){
        return mPageIndex;
    }

    public final int getPageSize(){
        return mPageSize;
    }

    /**
     * This is used to clone a new action for the current page request for retry.
     * 
     * For an AysnTask action can only be executed once, so we have to create a new one
     * for each execution. Subclasses of this class should clone all fields to the new action
     * @return
     */
    public PaginationAction<Result> createRetryPageAction(){
    	PaginationAction<Result> nextPageAction = createNextPageAction();
    	nextPageAction.mPageIndex = nextPageAction.mPageIndex > 1 ? nextPageAction.mPageIndex -1 : 1;
    	return nextPageAction;
    }
    /**
     * For an AysnTask action can only be executed once, so we have to create a new one
     * for each execution. Subclasses of this class should clone all fields to the action
     * of the next page
     * @return
     */
    protected abstract PaginationAction<Result> createNextPageAction();
    public abstract Result convertJsonToResult(JSONObject item) throws JSONException;
}
