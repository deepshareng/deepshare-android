package com.singulariti.deepshare.protocol.httpsendmessages;

import android.content.Context;

import com.singulariti.deepshare.Configuration;
import com.singulariti.deepshare.protocol.ServerHttpRespMessage;
import com.singulariti.deepshare.protocol.ServerHttpSendJsonMessage;
import com.singulariti.deepshare.protocol.httprespmessages.CloseRespMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class CloseMessage extends ServerHttpSendJsonMessage {
    public final static String URL_PATH = "close";
    public CloseMessage(Context context) {
        super(context);
    }

    @Override
    public JSONObject getJSONObject(Configuration config) throws JSONException {
        JSONObject closePost = new JSONObject();

        closePost.put("session_id", config.getSessionID());

        return closePost;
    }

    @Override
    public ServerHttpRespMessage buildResponse() {
        return new CloseRespMessage(this);
    }

    @Override
    public String getUrlPath() {
        return URL_PATH;
    }

}
