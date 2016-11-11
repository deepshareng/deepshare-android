package com.singulariti.deepshare.protocol.httpsendmessages;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.singulariti.deepshare.Configuration;
import com.singulariti.deepshare.listeners.OnInitParamsListener;
import com.singulariti.deepshare.protocol.ServerHttpRespMessage;
import com.singulariti.deepshare.protocol.ServerHttpSendJsonMessage;
import com.singulariti.deepshare.protocol.httprespmessages.InstallRespMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class InstallMessage extends ServerHttpSendJsonMessage {
    public final static String URL_PATH = "install";
    private OnInitParamsListener listener;

    public InstallMessage(Context context, OnInitParamsListener listener) {
        super(context);
        this.listener = listener;
    }

    public OnInitParamsListener getListener() {
        return listener;
    }

    @Override
    public JSONObject getJSONObject(Configuration config) throws JSONException {
        JSONObject installPost = new JSONObject();

        installPost.put("app_id", config.getAppKey());

        String text = config.getUniqueID();
        if (!TextUtils.isEmpty(text)) {
            installPost.put("hardware_id", text);
            installPost.put("is_hardware_id_real", config.hasRealHardwareId());
        }
        text = config.getAppVersion();
        if (!TextUtils.isEmpty(text)) {
            installPost.put("app_version", text);
        }
        text = config.getCarrier();
        if (!TextUtils.isEmpty(text)) {
            installPost.put("carrier", text);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            installPost.put("bluetooth", config.isBluetoothPresent());
        }

        text = config.getBluetoothVersion();
        if (!TextUtils.isEmpty(text)) {
            installPost.put("bluetooth_version", text);
        }
        installPost.put("has_nfc", config.isNFCPresent());
        installPost.put("has_telephone", config.isTelephonePresent());

        text = config.getPhoneBrand();
        if (!TextUtils.isEmpty(text)) {
            installPost.put("brand", text);
        }
        text = config.getPhoneModel();
        if (!TextUtils.isEmpty(text)) {
            installPost.put("model", text);
        }

        text = config.getOS();
        if (!TextUtils.isEmpty(text)) {
            installPost.put("os", text);
        }

        installPost.put("os_version", "" + config.getOSVersion());
        installPost.put("os_release", "" + config.getOSRelease());
        DisplayMetrics dMetrics = config.getScreenDisplay();
        installPost.put("screen_dpi", dMetrics.densityDpi);
        installPost.put("screen_height", dMetrics.heightPixels);
        installPost.put("screen_width", dMetrics.widthPixels);
        installPost.put("wifi", config.isWifiConnected());
        installPost.put("update", config.getUpdateState());
        if (!TextUtils.isEmpty(config.getClickId())) {
            installPost.put("click_id", config.getClickId());
        }

        return installPost;
    }

    @Override
    public ServerHttpRespMessage buildResponse() {
        return new InstallRespMessage(this);
    }

    @Override
    public String getUrlPath() {
        return URL_PATH;
    }


}
