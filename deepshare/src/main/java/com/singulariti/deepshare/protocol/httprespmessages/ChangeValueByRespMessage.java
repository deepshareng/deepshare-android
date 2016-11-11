package com.singulariti.deepshare.protocol.httprespmessages;

import com.singulariti.deepshare.protocol.ServerHttpRespJsonMessage;
import com.singulariti.deepshare.protocol.ServerHttpSendJsonMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangeValueByRespMessage extends ServerHttpRespJsonMessage {

    public ChangeValueByRespMessage(ServerHttpSendJsonMessage sent) {
        super(sent);
    }


    @Override
    public void getPayload(JSONObject obj) throws JSONException {

    }

}
