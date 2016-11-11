package com.singulariti.deepshare;

public interface ErrorString {
    public static final String ERR_NOT_INITIALIZED = "DeepShare is not initialized, call init() or check your network connection.";
    public static final String ERR_JSON = "Server communication error. Please retry it later.";
    public static final String ERR_HTTP = "Cannot access server. Please check your network connectivity.";
    public static final String ERR_NO_API_KEY = "App ID doesn't exist in AndroidManifest.xml. Please register a APP ID.";
    public static final String ERR_INIT_TWICE = "You have called initSession(...) more than once.";
    public static final String ERR_NETWORK_PERMISSION = "Internet permission is not granted in AndroidManifest.xml.";
    public static final String ERR_ON_START_NOT_CALLED = "Method DeepShare.onStart(Context) has not yet been invoked in application's onStart().";
    public static final String ERR_PARAMS_EMPTY = "Params of the link is an empty string.";
}
