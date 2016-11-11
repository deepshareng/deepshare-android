package com.singulariti.deepshare.protocol.httprespmessages;

import com.singulariti.deepshare.protocol.ServerHttpRespJsonMessage;
import com.singulariti.deepshare.protocol.ServerHttpSendJsonMessage;
import com.singulariti.deepshare.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class InstallRespMessage extends ServerHttpRespJsonMessage {
    private String device_fingerprint_id;
    private String identity_id;
    private String session_id;
    private JSONObject data;

    public InstallRespMessage(ServerHttpSendJsonMessage sent) {
        super(sent);
    }


    @Override
    public void getPayload(JSONObject obj) throws JSONException {
        device_fingerprint_id = obj.getString("device_fingerprint_id");
        identity_id = obj.getString("identity_id");
        session_id = obj.getString("session_id");

        if (obj.has("data")) {
            data = Util.getJSONObject(obj.getString("data"));
        }
    }

    public JSONObject getParams() {
        return data;
    }

    public String getIdentity() {
        return identity_id;
    }

    public String getDeviceFingerPrint() {
        return device_fingerprint_id;
    }

    public String getSession() {
        return session_id;
    }

}
