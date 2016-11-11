package com.singulariti.deepshare.protocol.httpsendmessages;

import android.content.Context;
import android.text.TextUtils;

import com.singulariti.deepshare.Configuration;
import com.singulariti.deepshare.ErrorString;
import com.singulariti.deepshare.listeners.OnURLGeneratedListener;
import com.singulariti.deepshare.protocol.ServerHttpRespMessage;
import com.singulariti.deepshare.protocol.ServerHttpSendJsonMessage;
import com.singulariti.deepshare.protocol.httprespmessages.UrlRespMessage;
import com.singulariti.deepshare.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

public class UrlMessage extends ServerHttpSendJsonMessage {
    public final static String URL_PATH = "url";
    private Collection<String> tags;
    private String alias;
    private String channel;
    private String feature;
    private String stage;
    private JSONObject params;
    private OnURLGeneratedListener listener;
    private String downloadTitle;
    private String downloadMsg;
    private String redirectUrl;

    public UrlMessage(Context context, JSONObject params, String downloadTitle, String downloadMsg, String redirectUrl, OnURLGeneratedListener listener) {
        super(context);
        this.downloadTitle = downloadTitle;
        this.downloadMsg = downloadMsg;
        this.redirectUrl = redirectUrl;
        this.params = params;
        this.listener = listener;
    }

    @Override
    public JSONObject getJSONObject(Configuration config) throws JSONException {
        if (TextUtils.isEmpty(config.getIdentityID())) {
            getResponse().setError(ErrorString.ERR_NOT_INITIALIZED);
            return null;
        }

        JSONObject linkPost = new JSONObject();

        linkPost.put("app_id", config.getAppKey());
        linkPost.put("identity_id", config.getIdentityID());
        linkPost.put("device_fingerprint_id", config.getDeviceFingerPrintID());
        linkPost.put("session_id", config.getSessionID());

        if (tags != null) {
            JSONArray tagArray = new JSONArray();
            for (String tag : tags) {
                tagArray.put(tag);
            }
            linkPost.put("tags", tagArray);
        }

        linkPost.put("alias", alias);
        linkPost.put("channel", channel);
        linkPost.put("feature", feature);
        linkPost.put("stage", stage);
        linkPost.put("stage", stage);
        linkPost.put("new_download_title", downloadTitle);
        linkPost.put("new_download_msg", downloadMsg);
        linkPost.put("redirect_url", redirectUrl);

        if (params == null) {
            params = new JSONObject();
        }
//        params.put("source", "android");

        linkPost.put("data", Util.escapeJSONStrings(params).toString());

        return linkPost;
    }

    public OnURLGeneratedListener getListener() {
        return listener;
    }

    @Override
    public ServerHttpRespMessage buildResponse() {
        return new UrlRespMessage(this);
    }

    @Override
    public String getUrlPath() {
        return URL_PATH;
    }

}
