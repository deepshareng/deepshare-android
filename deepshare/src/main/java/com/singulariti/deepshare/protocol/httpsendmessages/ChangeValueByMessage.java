package com.singulariti.deepshare.protocol.httpsendmessages;

import android.content.Context;
import android.text.TextUtils;

import com.singulariti.deepshare.Configuration;
import com.singulariti.deepshare.ErrorString;
import com.singulariti.deepshare.listeners.OnFailListener;
import com.singulariti.deepshare.protocol.ServerHttpRespMessage;
import com.singulariti.deepshare.protocol.ServerHttpSendJsonMessage;
import com.singulariti.deepshare.protocol.httprespmessages.ChangeValueByRespMessage;
import com.singulariti.deepshare.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangeValueByMessage extends ServerHttpSendJsonMessage {
    public final static String URL_PATH = "changevalueby";
    private JSONObject tagedValues;
    private OnFailListener listener;

    public ChangeValueByMessage(Context context, JSONObject tagValues, OnFailListener listener) {
        super(context);
        this.tagedValues = tagValues;
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
        linkPost.put("tag_values", Util.escapeJSONStrings(tagedValues).toString());

        return linkPost;
    }

    public OnFailListener getListener() {
        return listener;
    }

    @Override
    public ServerHttpRespMessage buildResponse() {
        return new ChangeValueByRespMessage(this);
    }

    @Override
    public String getUrlPath() {
        return URL_PATH;
    }

}
