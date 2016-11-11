package com.singulariti.deepshare.protocol.httpsendmessages;

import android.content.Context;
import android.text.TextUtils;

import com.singulariti.deepshare.Configuration;
import com.singulariti.deepshare.listeners.OnInitParamsListener;
import com.singulariti.deepshare.protocol.ServerHttpRespMessage;
import com.singulariti.deepshare.protocol.ServerHttpSendJsonMessage;
import com.singulariti.deepshare.protocol.httprespmessages.OpenRespMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class OpenMessage extends ServerHttpSendJsonMessage {
    public final static String URL_PATH = "open";
    private OnInitParamsListener listener;

    public OpenMessage(Context context, OnInitParamsListener listener) {
        super(context);
        this.listener = listener;
    }

    public OnInitParamsListener getListener() {
        return listener;
    }

    @Override
    public JSONObject getJSONObject(Configuration config) throws JSONException {
        JSONObject openPost = new JSONObject();

        openPost.put("app_id", config.getAppKey());
        openPost.put("device_fingerprint_id", config.getDeviceFingerPrintID());
        openPost.put("identity_id", config.getIdentityID());

        String text = config.getAppVersion();
//        if (!TextUtils.isEmpty(text)) {
//            openPost.put("app_version", text);
//        }
        openPost.put("os_version", "" + config.getOSVersion());
        openPost.put("os_release", "" + config.getOSRelease());

        text = config.getOS();
        if (!TextUtils.isEmpty(text)) {
            openPost.put("os", text);
        }
        if (!TextUtils.isEmpty(config.getClickId())) {
            openPost.put("click_id", config.getClickId());
        }

        return openPost;
    }

    @Override
    public ServerHttpRespMessage buildResponse() {
        return new OpenRespMessage(this);
    }

    @Override
    public String getUrlPath() {
        return URL_PATH;
    }
}
