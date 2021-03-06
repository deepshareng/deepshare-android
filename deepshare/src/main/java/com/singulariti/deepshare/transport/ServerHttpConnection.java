package com.singulariti.deepshare.transport;

import com.singulariti.deepshare.BuildConfig;
import com.singulariti.deepshare.DSConfig;
import com.singulariti.deepshare.ErrorString;
import com.singulariti.deepshare.ServerMessageMgr;
import com.singulariti.deepshare.protocol.ServerHttpRespJsonMessage;
import com.singulariti.deepshare.protocol.ServerHttpRespMessage;
import com.singulariti.deepshare.protocol.ServerHttpSendMessage;
import com.singulariti.deepshare.protocol.ServerMessage;
import com.singulariti.deepshare.protocol.ServerNetworkError;
import com.singulariti.deepshare.utils.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class ServerHttpConnection {
    public static final String TAG = "ServerHttpConnection";

    private static final int BUFFSIZE = 20000;

    public static boolean networkAvailable = true;

    private static ServerHttpConnection instance = null;

    private ExecutorService executorService = null;

    private ConcurrentLinkedQueue<ServerHttpSendMessage> queue = new ConcurrentLinkedQueue<>();

    public synchronized static void reset(int threads) {
        if (instance != null) {
            if (instance.executorService != null) {
                instance.executorService.shutdownNow();
            }
            instance.queue.clear();
        }
        instance = new ServerHttpConnection();
        if (threads > 0) {
            instance.executorService = Executors.newFixedThreadPool(threads);
        }
        ServerMessage.newSession();
    }

    public synchronized static void send(ServerHttpSendMessage msg) {
        msg.resetSendRetry();
        msg.saveSession();
        instance.sendInternal(msg);
    }

    private void sendInternal(ServerHttpSendMessage msg) {
        if (queue.isEmpty()) {
            queue.add(msg);
            executorService.execute(new HandleSendMessage(msg));
        } else {
            queue.add(msg);
        }
    }

    private boolean execute(ServerHttpSendMessage msg) {
        try {
            executorService.execute(new HandleSendMessage(msg));
            return true;
        } catch (RejectedExecutionException e) {
        }
        return false;
    }

    private class HandleSendMessage implements Runnable {
        private final ServerHttpSendMessage msg;
        private long startTick = 0, connTick = 0, endTick = 0;
        private int uploaded = 0, downloaded = 0;
        private byte[] content = null;

        public HandleSendMessage(ServerHttpSendMessage msg) {
            this.msg = msg;
        }

        private boolean checkSession() {
            if (msg.isSessionOutDate()) {
                msg.getResponse().setError(ErrorString.ERR_INIT_TWICE);
                Log.w(TAG, "Outdated " + (content != null ? "ok " : "err ") + (System.currentTimeMillis() - startTick) + "ms " + msg.getClass().getSimpleName() + "(" + msg.getSendRetry() + ") " + msg.toString());
                ServerMessageMgr.getInstance().fireServerMessage(new ServerNetworkError(msg, ServerNetworkError.HTTP_NETWORK_RESET, 2));
                processNext(msg.getResponse());
                return true;
            }
            return false;
        }

        private void processNext(ServerMessage resp) {
            ServerMessageMgr.getInstance().fireServerMessage(resp);
            queue.remove(msg);
            ServerHttpSendMessage next = queue.peek();
            while (next != null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                if (execute(next)) {
                    break;
                } else {
                    ServerMessageMgr.getInstance().fireServerMessage(next.getResponse());
                    queue.remove(next);
                    next = queue.peek();
                }
            }
        }

        @Override
        public void run() {
            if (!networkAvailable) {
                msg.getResponse().setError(ErrorString.ERR_HTTP);
                Log.d(TAG, "Network unavailable " + msg.getClass().getSimpleName() + " send empty " + msg.getResponse().getClass().getSimpleName());
                ServerMessageMgr.getInstance().fireServerMessage(new ServerNetworkError(msg, ServerNetworkError.NET_UNAVAILABLE_WHEN_SEND_MESSAGE, 2));
                processNext(msg.getResponse());
                return;
            }

            if (!msg.prepare()) {
                Log.d(TAG, "Message invalid " + msg.getClass().getSimpleName() + " send empty " + msg.getResponse().getClass().getSimpleName());
                processNext(msg.getResponse());
                return;
            }

            if (checkSession()) {
                return;
            }

            startTick = System.currentTimeMillis();

            HttpURLConnection conn = null;
            BufferedInputStream in = null;
            int subwhy = 7;

            try {
                if (checkSession()) {
                    return;
                }
                String urlBase;
                if(BuildConfig.DEBUG){
                    urlBase = DSConfig.API_BASE_URL_DEBUG;
                }else{
                    urlBase = DSConfig.API_BASE_URL;
                }
                URL url = new URL(urlBase + msg.getUrlPath());
                subwhy = 6;
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10 * 1000);
                conn.setReadTimeout(10 * 1000);
                if (checkSession()) {
                    return;
                }
                subwhy = 5;
                conn.setRequestMethod(msg.getMethod());

                if (!msg.getMethod().equals(ServerHttpSendMessage.GET)) {
                    conn.setDoOutput(true);
                }
                conn.setDoInput(true);
                conn.setUseCaches(false);

                conn.setRequestProperty("Accept", "*/*");
                msg.setRequestProperty(conn);
                if (checkSession()) {
                    return;
                }
                subwhy = 4;
                conn.connect();
                connTick = System.currentTimeMillis() - startTick;
                if (checkSession()) {
                    return;
                }
                subwhy = 3;
                if (!msg.getMethod().equals(ServerHttpSendMessage.GET)) {
                    uploaded = msg.send(conn.getOutputStream());
                }
                if (checkSession()) {
                    return;
                }
                subwhy = 2;
                ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFSIZE);
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST &&
                            conn.getResponseCode() <= HttpURLConnection.HTTP_INTERNAL_ERROR) {
                        msg.getResponse().setIsClientBadRequest(true);
                    }
                    in = new BufferedInputStream(conn.getErrorStream());
                } else {
                    in = new BufferedInputStream(conn.getInputStream());
                }
                byte[] temp = new byte[BUFFSIZE];

                if (checkSession()) {
                    return;
                }
                subwhy = 1;

                int size;
                while ((size = in.read(temp)) != -1) {
                    out.write(temp, 0, size);
                }

                subwhy = 0;

                content = out.toByteArray();
                downloaded = content.length;

                endTick = System.currentTimeMillis() - startTick;

                if (checkSession()) {
                    return;
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, "file not found");
            } catch (IOException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
            Log.i(TAG, "Send " + (content != null ? "ok " : "err ") + DSConfig.API_BASE_URL + msg.getUrlPath() + " " + (System.currentTimeMillis() - startTick) + "ms " + msg.getClass().getSimpleName() + "(" + msg.getSendRetry() + ") " + msg.toString());
            ServerHttpRespMessage res = msg.getResponse();
            res.receive(content);

            boolean fireRes = true;

            if (content != null) {
                if (res instanceof ServerHttpRespJsonMessage && content.length != 0) {
                    String str = null;
                    try {
                        str = new String(content, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                    }
                    Log.d(TAG, "Receive " + content.length + " " + res.getClass().getSimpleName() + " " + res.toString() + " " + str);
                } else {
                    Log.d(TAG, "Receive " + content.length + " " + res.getClass().getSimpleName() + " " + res.toString());
                }
            } else if (msg.nextSendRetry()) {
                Log.w(TAG, "Retry (" + msg.getSendRetry() + ") " + msg.getClass().getSimpleName() + " subway=" + subwhy);
                if (execute(msg)) {
                    fireRes = false;
                }
            } else if (res instanceof ServerHttpRespJsonMessage) {
                ServerMessageMgr.getInstance().fireServerMessage(new ServerNetworkError(msg, ServerNetworkError.HTTP_JSON_MESSAGE, subwhy));
            } else {
                ServerMessageMgr.getInstance().fireServerMessage(new ServerNetworkError(msg, ServerNetworkError.HTTP_OTHER_MESSAGE, subwhy));
            }

            if (fireRes) {
                processNext(res);
            }
        }
    }
}
