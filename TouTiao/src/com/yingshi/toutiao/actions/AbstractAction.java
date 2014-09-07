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
import com.yingshi.toutiao.util.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public abstract class AbstractAction<Progress, Result> extends AsyncTask<Void, Progress, ActionResult<Result>> implements JSONConstants{
    private static final String TAG = "JIDA-AbstractRequest";
    private static final String URL = Constants.SERVER_ADDRESS;
    protected Context mAppContext;
    protected String mServiceId;
    private CallBack<Result> mCallback;
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
        private T getObject(){
            return mObject;
        }
    }

    public static interface CallBack<T>{
        public void onSuccess(T result);
        public void onFailure(ActionError error);
    }

    public AbstractAction(Context context){
        mAppContext = context;
    }

    protected ActionResult<Result> doInBackground(Void...  params){
        ActionResult<Result> result = null;
        String response = null;
        try{
            JSONObject jsonReq = createJSONRequest();
            HttpRequest httpReq = new HttpRequest(URL, jsonReq.toString().getBytes());
            Log.d(TAG, "Sending JSON request : " + jsonReq.toString(4));

            HttpResponse httpResp = HttpRequestHandler.getInstance(
                            mAppContext.getApplicationContext()).processRequest(httpReq);
            JSONObject jsonResp = null;
            String jsonRespCode = "";
            String jsonRespMsg = "";
            if(httpResp.getData() != null){
                response = new String(httpResp.getData());
                JSONObject jsonObj = new JSONObject(response);
                Log.d(TAG, "Received JSON response : " + jsonObj.toString(4));
                jsonResp = jsonObj.getJSONObject(RESPONSE);

                if(jsonResp != null){
                    if(jsonResp.has(RESP_MSG))
                        jsonRespMsg = Utils.getDecodedValue(jsonResp, RESP_MSG);
                    jsonRespCode = jsonResp.getString(RESP_CODE);
                }
            }

            if(httpResp.getStatusCode() == HttpStatus.SC_OK ||
                            httpResp.getStatusCode() == HttpStatus.SC_ACCEPTED){
                if( RESP_CODE_SUCC.equalsIgnoreCase(jsonRespCode) ){
                    if(jsonResp.has(BODY)){
                        result = new ActionResult<Result>(createRespObject(jsonResp.getJSONObject(BODY)));
                    }else
                        result = new ActionResult<Result>((Result)null);
                } else {
                    result = new ActionResult<Result>(new ActionError(ErrorCode.SERVER_ERROR, jsonRespMsg));
                    Log.w(TAG, "Error response, code=" + jsonRespCode + ", msg=" + jsonRespMsg);
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
        return result;
    }

    public void execute(CallBack<Result> callback){
        mCallback = callback;
        super.execute(new Void[0]);
    }

    protected final void onPostExecute(ActionResult<Result> result) {
        if(mCancelled){
            Log.i(TAG, "Action has been cancelled: " + mServiceId );
            return;
        }
        if(mCallback != null){
            if(result.getError() == null){
                mCallback.onSuccess(result.getObject());
            }else{
                mCallback.onFailure(result.getError());
            }
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
