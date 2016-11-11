package com.singulariti.deepshare.protocol.httprespmessages;

import com.singulariti.deepshare.protocol.ServerHttpRespJsonMessage;
import com.singulariti.deepshare.protocol.ServerHttpSendJsonMessage;
import com.singulariti.deepshare.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class GetValueRespMessage extends ServerHttpRespJsonMessage {
    private HashMap<String, Integer> tagToValue;

    public GetValueRespMessage(ServerHttpSendJsonMessage sent) {
        super(sent);
    }


    @Override
    public void getPayload(JSONObject obj) throws JSONException {
        if (obj.has("tag_values")) {
            tagToValue = new HashMap<String, Integer>();
            JSONObject tagedValues = Util.getJSONObject(obj.getString("tag_values"));

            if(tagedValues != null){
                Iterator<String> it = tagedValues.keys();
                while (it.hasNext()){
                    String key = it.next();
                    int value = tagedValues.getInt(key);
                    tagToValue.put(key, value);
                }
            }
        }

    }

    public HashMap<String, Integer> getTagedValues() {
        return tagToValue;
    }

}
