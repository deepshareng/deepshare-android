package com.singulariti.deepshare.protocol.httprespmessages;

import com.singulariti.deepshare.protocol.ServerHttpRespJsonMessage;
import com.singulariti.deepshare.protocol.ServerHttpSendJsonMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class NewUsageRespMessage extends ServerHttpRespJsonMessage {

    private int new_install;
    private int new_open;

    public NewUsageRespMessage(ServerHttpSendJsonMessage sent) {
        super(sent);
    }


    @Override
    public void getPayload(JSONObject obj) throws JSONException {
        new_install = obj.getInt("new_install");
        new_open = obj.getInt("new_open");
    }

    public int getNewOpen() {
        return new_open;
    }

    public int getNewInstall() {
        return new_install;
    }
}
