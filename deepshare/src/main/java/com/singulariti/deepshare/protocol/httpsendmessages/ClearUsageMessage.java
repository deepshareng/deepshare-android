package com.singulariti.deepshare.protocol.httpsendmessages;

import android.content.Context;
import android.text.TextUtils;

import com.singulariti.deepshare.Configuration;
import com.singulariti.deepshare.ErrorString;
import com.singulariti.deepshare.listeners.OnFailListener;
import com.singulariti.deepshare.protocol.ServerHttpRespMessage;
import com.singulariti.deepshare.protocol.ServerHttpSendJsonMessage;
import com.singulariti.deepshare.protocol.httprespmessages.ClearUsageRespMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ClearUsageMessage extends ServerHttpSendJsonMessage {
    public final static String URL_PATH = "clearusage";
    private OnFailListener listener;

    public ClearUsageMessage(Context context, OnFailListener callback) {
        super(context);
        this.listener = listener;
    }

    @Override
    public JSONObject getJSONObject(Configuration config) throws JSONException {
        if (TextUtils.isEmpty(config.getIdentityID())) {
            getResponse().setError(ErrorString.ERR_NOT_INITIALIZED);
            return null;
        }

        JSONObject linkPost = new JSONObject();

        linkPost.put("session_id", config.getSessionID());

        return linkPost;
    }

    public OnFailListener getListener() {
        return listener;
    }

    @Override
    public ServerHttpRespMessage buildResponse() {
        return new ClearUsageRespMessage(this);
    }

    @Override
    public String getUrlPath() {
        return URL_PATH;
    }

}
