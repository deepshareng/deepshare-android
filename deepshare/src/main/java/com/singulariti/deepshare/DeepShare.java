package com.singulariti.deepshare;

import android.app.Activity;
import android.net.Uri;

import com.singulariti.deepshare.listeners.NewUsageFromMeListener;
import com.singulariti.deepshare.listeners.OnFailListener;
import com.singulariti.deepshare.listeners.OnInitParamsListener;
import com.singulariti.deepshare.listeners.OnURLGeneratedListener;
import com.singulariti.deepshare.listeners.TaggedValueListener;
import com.singulariti.deepshare.utils.Log;

import java.util.HashMap;

public class DeepShare {
    private static final String TAG = "DeepShare";
    public static final int LINK_TYPE_UNLIMITED_USE = 0;

    /**
     * 初始化并返回程序启动参数
     * @param activity 当前activity
     * @param appId APP注册时生成的APP ID.
     * @param callback 启动参数的回调方法.
     */
    public static void init(Activity activity, String appId, OnInitParamsListener callback){
        if (instance == null) {
            instance = new DeepShareImpl(activity.getApplicationContext(), appId, callback);
        }
        Uri data = activity.getIntent().getData();
        instance.initSession(callback, !instance.hasUser(), data, null);
    }

    /**
     * 返回我的分享ID
     */
    public static String getMyDeepShareId(){
        if (instance != null) {
            return instance.config.getIdentityID();
        } else {
            Log.e(TAG, ErrorString.ERR_NOT_INITIALIZED);
            return null;
        }
    }

    /**
     *异步生成深度分享链接
     * @param URLParam 与此链接所绑定的参数.
     * @param callback 链接成功生成后的回调方法.
     */
    public static void generateURL(URLParam URLParam, OnURLGeneratedListener callback) {
        if (instance != null) {
            instance.generateShortLink(URLParam,callback);
        } else {
            Log.e(TAG, ErrorString.ERR_NOT_INITIALIZED);
        }
    }

    /**
     * 异步返回通过我的分享带来的此应用的新使用，包括新安装的用户量和再次激活打开的用户量
     *
     */
    public static void getNewUsageFromMe(NewUsageFromMeListener callback){
        if (instance != null) {
            instance.getNewUsageFromMe(callback);
        } else {
            Log.e(TAG, ErrorString.ERR_NOT_INITIALIZED);
        }
    }

    /**
     * 清空通过我的分享带来的此应用的新使用，包括新安装的用户量和再次激活打开的用户量
     *
     */
    public static void clearNewUsageFromMe(OnFailListener callback){
        if (instance != null) {
            instance.clearUsageFromMe(callback);
        } else {
            Log.e(TAG, ErrorString.ERR_NOT_INITIALIZED);
        }
    }

    /**
     * 改变指定价值标签的值
     * @param tagToValue 所指定价值标签和其增加或减少的价值量所组成的HashMap.
     *
     */
    public static void changeValueBy(HashMap<String, Integer> tagToValue, OnFailListener callback){
        if (instance != null) {
            instance.changeValueBy(tagToValue, callback);
        } else {
            Log.e(TAG, ErrorString.ERR_NOT_INITIALIZED);
        }
    }

    /**
     * 异步获取某些指定价值标签的值,
     * @param tags 所指定价值标签数组.
     * @param callback 返回改变后的新的价值标签和其对应值.
     */
    public static void getValueFromMe(String[] tags, TaggedValueListener callback){
        if (instance != null) {
            instance.getValue(tags, callback);
        } else {
            Log.e(TAG, ErrorString.ERR_NOT_INITIALIZED);
        }
    }

    /**
     * 重置指定价值标签的值为0
     * @param tags 所指定价值标签数组.
     */
    public static void clearValueFromMe(String[] tags, OnFailListener callback){
        if (instance != null) {
            instance.clearValues(tags, callback);
        } else {
            Log.e(TAG, ErrorString.ERR_NOT_INITIALIZED);
        }
    }

    private static DeepShareImpl instance;

    /**
     *通知DeepShare停止会话，应该在Android的onStop()方法中调用
     */
    public static void onStop() {
        if (instance != null) {
            instance.close();
        } else {
            Log.e(TAG, ErrorString.ERR_NOT_INITIALIZED);
        }
    }

}
