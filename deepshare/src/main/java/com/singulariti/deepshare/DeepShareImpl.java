package com.singulariti.deepshare;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.singulariti.deepshare.listeners.NewUsageFromMeListener;
import com.singulariti.deepshare.listeners.OnFailListener;
import com.singulariti.deepshare.listeners.OnInitParamsListener;
import com.singulariti.deepshare.listeners.OnURLGeneratedListener;
import com.singulariti.deepshare.listeners.TaggedValueListener;
import com.singulariti.deepshare.protocol.ServerMessage;
import com.singulariti.deepshare.protocol.ServerNetworkError;
import com.singulariti.deepshare.protocol.httprespmessages.ChangeValueByRespMessage;
import com.singulariti.deepshare.protocol.httprespmessages.ClearUsageRespMessage;
import com.singulariti.deepshare.protocol.httprespmessages.ClearValueRespMessage;
import com.singulariti.deepshare.protocol.httprespmessages.CloseRespMessage;
import com.singulariti.deepshare.protocol.httprespmessages.GetValueRespMessage;
import com.singulariti.deepshare.protocol.httprespmessages.InstallRespMessage;
import com.singulariti.deepshare.protocol.httprespmessages.NewUsageRespMessage;
import com.singulariti.deepshare.protocol.httprespmessages.OpenRespMessage;
import com.singulariti.deepshare.protocol.httprespmessages.UrlRespMessage;
import com.singulariti.deepshare.protocol.httpsendmessages.ChangeValueByMessage;
import com.singulariti.deepshare.protocol.httpsendmessages.ClearUsageMessage;
import com.singulariti.deepshare.protocol.httpsendmessages.ClearValueMessage;
import com.singulariti.deepshare.protocol.httpsendmessages.CloseMessage;
import com.singulariti.deepshare.protocol.httpsendmessages.GetValueMessage;
import com.singulariti.deepshare.protocol.httpsendmessages.InstallMessage;
import com.singulariti.deepshare.protocol.httpsendmessages.NewUsageMessage;
import com.singulariti.deepshare.protocol.httpsendmessages.OpenMessage;
import com.singulariti.deepshare.protocol.httpsendmessages.UrlMessage;
import com.singulariti.deepshare.transport.ServerHttpConnection;
import com.singulariti.deepshare.utils.Log;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by joy on 15/9/3.
 */
public class DeepShareImpl {
    private static final String TAG = "DeepShareImpl";
    private int state;
    private static final int STATE_IDLE = 0;
    private static final int STATE_INITIALIZING = 1;
    private static final int STATE_INITIALIZED = 2;

    final Configuration config;
    private final Context context;


    DeepShareImpl(Context context, String appId, OnInitParamsListener callback) {
        this.context = context;
        this.config = Configuration.getInstance(context.getApplicationContext());
        this.state = STATE_IDLE;

        ServerMessageMgr.getInstance().registerHandler(UrlRespMessage.class, handler);
        ServerMessageMgr.getInstance().registerHandler(InstallRespMessage.class, handler);
        ServerMessageMgr.getInstance().registerHandler(OpenRespMessage.class, handler);
        ServerMessageMgr.getInstance().registerHandler(CloseRespMessage.class, handler);
        ServerMessageMgr.getInstance().registerHandler(NewUsageRespMessage.class, handler);
        ServerMessageMgr.getInstance().registerHandler(ClearUsageRespMessage.class, handler);
        ServerMessageMgr.getInstance().registerHandler(ChangeValueByRespMessage.class, handler);
        ServerMessageMgr.getInstance().registerHandler(GetValueRespMessage.class, handler);
        ServerMessageMgr.getInstance().registerHandler(ClearValueRespMessage.class, handler);
        ServerMessageMgr.getInstance().registerHandler(ServerNetworkError.class, handler);

        config.setAppKey(appId);
    }


    private UiServerMessageHandler handler = new UiServerMessageHandler() {
        @Override
        protected void processEvent(ServerMessage msg) {
            if (msg instanceof UrlRespMessage) {
                UrlRespMessage resp = (UrlRespMessage) msg;
                if (resp.isOk()) {
                    String url = resp.getUrl();
                    if (((UrlMessage) resp.getRequest()).getListener() != null) {
                        ((UrlMessage) resp.getRequest()).getListener().onGenerated(url);
                    }
                } else if (((UrlMessage) resp.getRequest()).getListener() != null) {
                    ((UrlMessage) resp.getRequest()).getListener().onFailed(resp.getError());
                }
            } else if (msg instanceof InstallRespMessage) {
                InstallRespMessage resp = (InstallRespMessage) msg;
                if (resp.isOk()) {
                    config.setDeviceFingerPrintID(resp.getDeviceFingerPrint());
                    config.setIdentityID(resp.getIdentity());
                    config.setSessionID(resp.getSession());
                    config.setClickId("");

                    if (config.getIsReferrable() == 1 && resp.getParams() != null) {
                        config.setInstallParams(resp.getParams().toString());
                    }
                    if (resp.getParams() != null) {
                        config.setSessionParams(resp.getParams().toString());
                    }

                    if (((InstallMessage) resp.getRequest()).getListener() != null) {
                        ((InstallMessage) resp.getRequest()).getListener().onInitParamsReturned(resp.getParams());
                    }

                    state = STATE_INITIALIZED;

                } else if (((InstallMessage) resp.getRequest()).getListener() != null) {
                    ((InstallMessage) resp.getRequest()).getListener().onFailed(resp.getError());
                }
            } else if (msg instanceof OpenRespMessage) {
                OpenRespMessage resp = (OpenRespMessage) msg;
                if (resp.isOk()) {
                    config.setSessionID(resp.getSession());
                    config.setDeviceFingerPrintID(resp.getDevice());
                    config.setClickId("");
                    config.setIdentityID(resp.getIdentity());

                    if (resp.getParams() != null) {
                        config.setSessionParams(resp.getParams().toString());
                    }

                    if (((OpenMessage) resp.getRequest()).getListener() != null) {
                        ((OpenMessage) resp.getRequest()).getListener().onInitParamsReturned(resp.getParams());
                    }

                    state = STATE_INITIALIZED;
                } else if (((OpenMessage) resp.getRequest()).getListener() != null) {
                    ((OpenMessage) resp.getRequest()).getListener().onFailed(resp.getError());
                }
            } if (msg instanceof ChangeValueByRespMessage) {
                ChangeValueByRespMessage resp = (ChangeValueByRespMessage) msg;
                if (resp.isOk()) {
                    //TODO:Should have a success callback
                } else if (((ChangeValueByMessage) resp.getRequest()).getListener() != null) {
                    ((ChangeValueByMessage) resp.getRequest()).getListener().onFailed(resp.getError());
                }
            } if (msg instanceof ClearUsageRespMessage) {
                ClearUsageRespMessage resp = (ClearUsageRespMessage) msg;
                if (resp.isOk()) {
                    //TODO:Should have a success callback
                } else if (((ClearUsageMessage) resp.getRequest()).getListener() != null) {
                    ((ClearUsageMessage) resp.getRequest()).getListener().onFailed(resp.getError());
                }
            } if (msg instanceof ClearValueRespMessage) {
                ClearValueRespMessage resp = (ClearValueRespMessage) msg;
                if (resp.isOk()) {
                    //TODO:Should have a success callback
                } else if (((ClearValueMessage) resp.getRequest()).getListener() != null) {
                    ((ClearValueMessage) resp.getRequest()).getListener().onFailed(resp.getError());
                }
            } if (msg instanceof GetValueRespMessage) {
                GetValueRespMessage resp = (GetValueRespMessage) msg;
                if (resp.isOk()) {
                    if (((GetValueMessage) resp.getRequest()).getListener() != null) {
                        ((GetValueMessage) resp.getRequest()).getListener().onTaggedValueReturned(resp.getTagedValues());
                    }
                } else if (((GetValueMessage) resp.getRequest()).getListener() != null) {
                    ((GetValueMessage) resp.getRequest()).getListener().onFailed(resp.getError());
                }
            } if (msg instanceof NewUsageRespMessage) {
                NewUsageRespMessage resp = (NewUsageRespMessage) msg;
                if (resp.isOk()) {
                    if (((NewUsageMessage) resp.getRequest()).getListener() != null) {
                        ((NewUsageMessage) resp.getRequest()).getListener().onNewUsageFromMe(resp.getNewInstall(), resp.getNewOpen());
                    }
                } else if (((NewUsageMessage) resp.getRequest()).getListener() != null) {
                    ((NewUsageMessage) resp.getRequest()).getListener().onFailed(resp.getError());
                }
            } else if (msg instanceof CloseRespMessage) {
                Log.i(TAG, "DeepShare session closed");
                //TODO:Should have a success callback
                ServerHttpConnection.reset(0);
                state = STATE_IDLE;
            } else if (msg instanceof ServerNetworkError) {
                com.singulariti.deepshare.protocol.ServerNetworkError resp = (ServerNetworkError) msg;
                Log.e(TAG, "Network error " + resp.getWhy() + " " + resp.getSubWhy());
            }
        }
    };
    boolean initSession(OnInitParamsListener callback, boolean isReferrable, Uri data, String key) {
        state = STATE_INITIALIZING;

        if (isReferrable) {
            config.setIsReferrable();
        } else {
            config.clearIsReferrable();
        }
        boolean uriHandled = false;
        if (data != null && data.isHierarchical()) {
            if (data.getQueryParameter("click_id") != null) {
                uriHandled = true;
                config.setClickId(data.getQueryParameter("click_id"));
            }
        }else{
            config.setClickId("");
        }

        ServerHttpConnection.reset(1);

        if (hasUser()) {
            ServerHttpConnection.send(new OpenMessage(context, callback));
        } else {
            ServerHttpConnection.send(new InstallMessage(context, callback));
        }

        if (key != null) {
            config.setAppKey(key);
        }

        return uriHandled;
    }

    boolean hasUser() {
        return !TextUtils.isEmpty(config.getIdentityID());
    }

    void close(){
        if (state != DeepShareImpl.STATE_IDLE) {
            ServerHttpConnection.send(new CloseMessage(context));
        }
    }

    void generateShortLink(final URLParam parambuilder, OnURLGeneratedListener callback) {
        ServerHttpConnection.send(new UrlMessage(context, parambuilder.params, parambuilder.downloadTitle, parambuilder.downloadMsg, parambuilder.redirectURL, callback));
    }

    void getNewUsageFromMe(NewUsageFromMeListener callback){
        ServerHttpConnection.send(new NewUsageMessage(context, callback));
    }

    void clearUsageFromMe(OnFailListener callback){
        ServerHttpConnection.send(new ClearUsageMessage(context, callback));
    }

    void changeValueBy(HashMap<String, Integer> tagToValue, OnFailListener callback) {
        JSONObject tagToValues = new JSONObject(tagToValue);
        ServerHttpConnection.send(new ChangeValueByMessage(context, tagToValues, callback));
    }

    void getValue(String[] tags, TaggedValueListener callback) {
        ServerHttpConnection.send(new GetValueMessage(context, tags, callback));
    }

    void clearValues(String[] tags, OnFailListener callback) {
        ServerHttpConnection.send(new ClearValueMessage(context, tags, callback));
    }
}
