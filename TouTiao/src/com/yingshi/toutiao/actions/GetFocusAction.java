package com.yingshi.toutiao.actions;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yingshi.toutiao.model.News;

public class GetFocusAction extends AbstractAction<List<News>> {
    //request keys
    private static final String NAME = "name";
    //local variables
    private String mCategory;

    public GetFocusAction(Context context, String category){
        super(context);
        mCategory = category;
        mServiceId = SERVICE_ID_FOCUS;
    }

    @Override
    public void addRequestParameters(JSONObject parameters) throws JSONException {
        parameters.put(NAME, mCategory);
    }

	@Override
	protected List<News> createRespObject(JSONObject response) throws JSONException {
		List<News> mNewsList = new ArrayList<News>();
        if(response.has(RESP_LIST)){
            JSONArray items = response.getJSONArray(RESP_LIST);
            for(int i=0; i<items.length(); i++){
                JSONObject item = items.getJSONObject(i);
                mNewsList.add(News.fromJSON(item));
            }
        }
		return mNewsList;
	}
}
