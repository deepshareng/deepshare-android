package com.singulariti.deepshare.protocol.httpsendmessages;

import android.content.Context;
import android.text.TextUtils;

import com.singulariti.deepshare.Configuration;
import com.singulariti.deepshare.ErrorString;
import com.singulariti.deepshare.listeners.TaggedValueListener;
import com.singulariti.deepshare.protocol.ServerHttpRespMessage;
import com.singulariti.deepshare.protocol.ServerHttpSendJsonMessage;
import com.singulariti.deepshare.protocol.httprespmessages.GetValueRespMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetValueMessage extends ServerHttpSendJsonMessage {
    public final static String URL_PATH = "getvalue";
    private String[] tags;
    private TaggedValueListener listener;

    public GetValueMessage(Context context, String[] tags, TaggedValueListener listener) {
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

    public TaggedValueListener getListener() {
        return listener;
    }

    @Override
    public ServerHttpRespMessage buildResponse() {
        return new GetValueRespMessage(this);
    }

    @Override
    public String getUrlPath() {
        return URL_PATH;
    }

}
