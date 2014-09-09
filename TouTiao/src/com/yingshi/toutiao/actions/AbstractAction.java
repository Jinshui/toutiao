package com.yingshi.toutiao.actions;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import com.yingshi.toutiao.Constants;
import com.yingshi.toutiao.R;
import com.yingshi.toutiao.actions.AbstractAction.ActionResult;
import com.yingshi.toutiao.http.HttpRequest;
import com.yingshi.toutiao.http.HttpRequestHandler;
import com.yingshi.toutiao.http.HttpResponse;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public abstract class AbstractAction<Result> extends AsyncTask<Void, Void, ActionResult<Result>> implements JSONConstants{
    private static final String TAG = "JIDA-AbstractRequest";
    private static final String URL = Constants.SERVER_ADDRESS;
    protected String mServiceId;
    protected Context mAppContext;
    private UICallBack<Result> mUICallback;
    private BackgroundCallBack<Result> mBackgroundCallBack;
    private IBackgroundProcessor<Result> mBackgroundProcessor = new NetworkBackgroundProcessor();
    private boolean mCancelled = false;
    public static enum ErrorCode{
        INVALID_REQUEST,
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
		public ActionResult<Result> doInBackground() {
	        ActionResult<Result> result = null;
	        String response = null;
	        try{
	            JSONObject jsonReq = createJSONRequest();
	            HttpRequest httpReq = new HttpRequest(URL, jsonReq.toString().getBytes());
	            Log.d(TAG, "Sending JSON request : " + jsonReq.toString(4));

	            HttpResponse httpResp = HttpRequestHandler.getInstance(
	                            mAppContext.getApplicationContext()).processRequest(httpReq);
	            JSONObject jsonResp = null;
	            String jsonRespStaus = null;
	            String jsonRespMsg = null;
	            if(httpResp.getData() != null){
	                response = new String(httpResp.getData());
	                jsonResp = new JSONObject(response);
	                Log.d(TAG, "Received JSON response : " + jsonResp.toString(4));
	                if(jsonResp.has(RESP_STATUS))
	                	jsonRespStaus = jsonResp.getString(RESP_STATUS);
	                if(jsonResp.has(RESP_MSG))
	                	jsonRespMsg = jsonResp.getString(RESP_MSG);
	            }

	            if(httpResp.getStatusCode() == HttpStatus.SC_OK ||
	                            httpResp.getStatusCode() == HttpStatus.SC_ACCEPTED){
	                if( RESP_STATUS_OK.equalsIgnoreCase(jsonRespStaus) ){
	                    result = new ActionResult<Result>(createRespObject(jsonResp));
	                } else {
	                    result = new ActionResult<Result>(new ActionError(ErrorCode.SERVER_ERROR, jsonRespMsg));
	                    Log.w(TAG, "Error response, code=" + jsonRespStaus + ", msg=" + jsonRespMsg);
	                }
	            } else if(httpResp.getStatusCode() >= HttpStatus.SC_BAD_REQUEST &&
	                            httpResp.getStatusCode() < HttpStatus.SC_INTERNAL_SERVER_ERROR){
	                result = new ActionResult<Result>(new ActionError(ErrorCode.INVALID_REQUEST, jsonRespMsg));
	            } else if(httpResp.getStatusCode() >= HttpStatus.SC_INTERNAL_SERVER_ERROR){
	                result = new ActionResult<Result>(new ActionError(ErrorCode.SERVER_ERROR, jsonRespMsg));
	            } else if(httpResp.getStatusCode() == HttpResponse.NETWORK_TIMEOUT){
	                result = new ActionResult<Result>(new ActionError(ErrorCode.NETWORK_TIMEOUT,
	                                mAppContext.getText(R.string.msg_network_timeout).toString()));
	            } else if(httpResp.getStatusCode() == HttpResponse.NETWORK_ERROR){
	                result = new ActionResult<Result>(new ActionError(ErrorCode.NETWORK_ERROR,
	                                mAppContext.getText(R.string.msg_network_error).toString()));
	            } else if(httpResp.getStatusCode() == HttpResponse.NETWORK_DISCONNECTED){
	                result = new ActionResult<Result>(new ActionError(ErrorCode.NETWORK_DISCONNECTED,
	                                mAppContext.getText(R.string.msg_network_disconnected).toString()));
	            } else if(httpResp.getStatusCode() == HttpResponse.UNKNOWN_ERROR){
	                result = new ActionResult<Result>(new ActionError(ErrorCode.NETWORK_ERROR,
	                                httpResp.getException().getMessage()));
	            }
	        } catch(Exception e) {
	            Log.e(TAG, "Failed to process action : " + mServiceId + "\n" + response, e);
	            result = new ActionResult<Result>(new ActionError(ErrorCode.NETWORK_ERROR, e.getMessage()));
	        }
	        if(mBackgroundCallBack != null){
	            if(result.hasError()){
	            	mBackgroundCallBack.onFailure(result.getError());
	            }else{
	            	mBackgroundCallBack.onSuccess(result.getObject());
	            }
	        }
	        return result;
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

    public void execute(BackgroundCallBack<Result> backgroundCallBack, UICallBack<Result> uiCallback){
        mUICallback = uiCallback;
        mBackgroundCallBack = backgroundCallBack;
        super.execute(new Void[0]);
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
