package com.singulariti.deepshare.protocol.httpsendmessages;

import android.content.Context;
import android.text.TextUtils;

import com.singulariti.deepshare.Configuration;
import com.singulariti.deepshare.ErrorString;
import com.singulariti.deepshare.listeners.OnFailListener;
import com.singulariti.deepshare.protocol.ServerHttpRespMessage;
import com.singulariti.deepshare.protocol.ServerHttpSendJsonMessage;
import com.singulariti.deepshare.protocol.httprespmessages.ClearValueRespMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClearValueMessage extends ServerHttpSendJsonMessage {
    public final static String URL_PATH = "clearvalue";
    private String[] tags;
    private OnFailListener listener;

    public ClearValueMessage(Context context, String[] tags, OnFailListener listener) {
        super(context);
        this.tags = tags;
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
        if (tags != null) {
            JSONArray tagArray = new JSONArray();
            for (String tag : tags) {
                tagArray.put(tag);
            }
            linkPost.put("tags", tagArray);
        }

        return linkPost;
    }

    public OnFailListener getListener() {
        return listener;
    }

    @Override
    public ServerHttpRespMessage buildResponse() {
        return new ClearValueRespMessage(this);
    }

    @Override
    public String getUrlPath() {
        return URL_PATH;
    }

}
