package com.singulariti.deepshare;

import org.json.JSONObject;

/**
 * Created by joy on 15/9/2.
 */
public class URLParam {
    JSONObject params;
    String downloadTitle;
    String downloadMsg;
    String redirectURL;

    /**
     * 与此URL绑定的参数，用户点击链接后，启动APP即可接收到此参数
     * @param params
     * @return 绑定参数后的ParamBuilder
     */
    public URLParam attachInitParams(JSONObject params){
        this.params = params;
        return this;
    }

    /**
     * 与此URL绑定的APP下载对话框相关信息
     * @param downloadTitle App下载对话框的标题
     * @param downloadMsg App下载对话框的内容信息
     * @return 绑定的APP下载信息后的ParamBuilder
     */
    public URLParam attachDownloadMessage(String downloadTitle, String downloadMsg){
        this.downloadTitle = downloadTitle;
        this.downloadMsg = downloadMsg;
        return this;
    }

    /**
     * 与此URL绑定的APP重定向跳转页面的url
     * 由于微信等聊天工具的内置浏览器封闭了浏览器对APP的跳转，因此需要用户手动选择用系统浏览器打开页面，在此提示页面中
     * 加载此redirectURL，以给用户更好的体验。
     * @param redirectURL 在提示页面中加载的自定义页面的URL
     * @return 绑定的APP下载信息后的ParamBuilder
     */
    public URLParam attachRedirectURL(String redirectURL){
        this.redirectURL = redirectURL;
        return this;
    }
}
