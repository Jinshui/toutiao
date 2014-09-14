package com.yingshi.toutiao.actions;

import java.util.Iterator;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.yingshi.toutiao.Constants;
import com.yingshi.toutiao.actions.AbstractAction.ActionResult;

public abstract class AbstractAction<Result> extends AsyncTask<Void, Void, ActionResult<Result>> implements JSONConstants{
    private static final String TAG = "TT-AbstractRequest";
    private static final String URL = Constants.SERVER_ADDRESS;
    protected String mServiceId;
    protected Context mAppContext;
    private UICallBack<Result> mUICallback;
    private BackgroundCallBack<Result> mBackgroundCallBack;
    private IBackgroundProcessor<Result> mBackgroundProcessor = new NetworkBackgroundProcessor();
    private boolean mCancelled = false;
    public static enum ErrorCode{
        INVALID_REQUEST,
        INVALID_RESPONSE,
        SERVER_ERROR,
        NETWORK_TIMEOUT,
        NETWORK_DISCONNECTED,
        NETWORK_ERROR;
    }

    public static class ActionError{
        private ErrorCode mError;
        private String mMessage;
        public ActionError(ErrorCode error, String msg){
            mError = error;
            mMessage = msg;
        }
        public ErrorCode getError() {
            return mError;
        }
        public void setError(ErrorCode error) {
            this.mError = error;
        }
        public String getMessage() {
            return mMessage;
        }
        public void setMessage(String message) {
            this.mMessage = message;
        }
    }

    public static class ActionResult<T>{
        private ActionError mError;
        private T mObject;

        public ActionResult(T object){
            mObject = object;
        }
        public ActionResult(ActionError error){
            mError = error;
        }

        public ActionError getError() {
            return mError;
        }
        public boolean hasError() {
            return mError != null;
        }
        private T getObject(){
            return mObject;
        }
    }

    public static interface UICallBack<T>{
        public void onSuccess(T result);
        public void onFailure(ActionError error);
    }
    
    public static interface BackgroundCallBack<T>{
    	public void onSuccess(T result);
    	public void onFailure(ActionError error);
    }
    
    public static interface IBackgroundProcessor<T> {
    	public ActionResult<T> doInBackground();
    }

    public class NetworkBackgroundProcessor implements IBackgroundProcessor<Result>{
        private ActionResult<Result> mResult;
		public ActionResult<Result> doInBackground() {
	        String response = null;
	        try{
	            JSONObject jsonReq = createJSONRequest();
	            Log.d(TAG, "Sending JSON request : " + jsonReq.toString(4));

	            SyncHttpClient httpClient = new SyncHttpClient();
	            RequestParams params=new RequestParams();
	            @SuppressWarnings("rawtypes")
				Iterator keys = jsonReq.keys();
	            while(keys.hasNext()){
	            	String key = keys.next().toString();
	            	params.put(key, jsonReq.get(key));
	            }
	            httpClient.post(URL, params, new JsonHttpResponseHandler(){
	    			@Override
	    			public void onSuccess(int statusCode, Header[] headers,
	    					JSONObject response) {
	    				try {
	    	                Log.d(TAG, "Received JSON response : " + response.toString(4));
		    				super.onSuccess(statusCode, headers, response);
							mResult = new ActionResult<Result>(createRespObject(response));
						} catch (JSONException e) {
			            	mResult = new ActionResult<Result>(new ActionError(ErrorCode.INVALID_RESPONSE, e.getMessage()));
						}
	    			}
	    			
	    			@Override
	    			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
	    				super.onFailure(statusCode, headers, responseString, throwable);
    	                Log.e(TAG, "Received Error response : StatusCode: " + statusCode + ", Received response : " + responseString);
	    				if(statusCode >= HttpStatus.SC_BAD_REQUEST &&
	    						statusCode < HttpStatus.SC_INTERNAL_SERVER_ERROR){
	    					mResult = new ActionResult<Result>(new ActionError(ErrorCode.INVALID_REQUEST, responseString));
			            } else if(statusCode >= HttpStatus.SC_INTERNAL_SERVER_ERROR){
			            	mResult = new ActionResult<Result>(new ActionError(ErrorCode.SERVER_ERROR, responseString));
			            } else{
			            	mResult = new ActionResult<Result>(new ActionError(ErrorCode.NETWORK_ERROR, throwable.getMessage()));
			            }
	    			}
	    		});
	        } catch(Exception e) {
	            Log.e(TAG, "Failed to process action : " + mServiceId + "\n" + response, e);
	            mResult = new ActionResult<Result>(new ActionError(ErrorCode.NETWORK_ERROR, e.getMessage()));
	        }
	        if(mBackgroundCallBack != null){
	            if(mResult.hasError()){
	            	mBackgroundCallBack.onFailure(mResult.getError());
	            }else{
	            	mBackgroundCallBack.onSuccess(mResult.getObject());
	            }
	        }
	        return mResult;
		}
    }
    
    public void setBackgroundProcessor(IBackgroundProcessor<Result> processor){
    	mBackgroundProcessor = processor;
    }
    
    public AbstractAction(Context context){
    	this(context, null);
    }
    
    public AbstractAction(Context context, IBackgroundProcessor<Result> processor){
        mAppContext = context;
        if(processor != null)
        	mBackgroundProcessor = processor;
    }

    protected ActionResult<Result> doInBackground(Void...  params){
    	return mBackgroundProcessor.doInBackground();
    }

    protected final void onPostExecute(ActionResult<Result> result) {
        if(mCancelled){
            Log.i(TAG, "Action has been cancelled: " + mServiceId );
            return;
        }
        if(mUICallback != null){
            if(result.hasError()){
            	mUICallback.onFailure(result.getError());
            }else{
            	mUICallback.onSuccess(result.getObject());
            }
        }
    }

    public void execute(){
    	execute(null, null);
    }
    
    public void execute(UICallBack<Result> uiCallback){
    	execute(null, uiCallback);
    }

    @SuppressLint("NewApi")
	public void execute(BackgroundCallBack<Result> backgroundCallBack, UICallBack<Result> uiCallback){
        mUICallback = uiCallback;
        mBackgroundCallBack = backgroundCallBack;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            super.execute();
        } else {
        	super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * Cancel the action, and will not call the callback.
     */
    public void cancel(){
        Log.i(TAG, "Cancelling action: " + mServiceId );
        mCancelled = true;
        cancel(true);
    }

    private JSONObject createJSONRequest() throws JSONException{
        JSONObject request = new JSONObject();
        request.put(SERVICE_ID, mServiceId);
        addRequestParameters(request);
        return request;
    }

    protected abstract void addRequestParameters(JSONObject params) throws JSONException;

    protected abstract Result createRespObject(JSONObject response) throws JSONException;
}
