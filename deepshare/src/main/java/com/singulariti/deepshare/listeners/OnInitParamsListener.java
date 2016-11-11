package com.singulariti.deepshare.listeners;

import org.json.JSONObject;

public interface OnInitParamsListener extends OnFailListener {
    public void onInitParamsReturned(JSONObject initParams);
}
