package com.singulariti.deepshare;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.Suppress;

import com.singulariti.deepshare.listeners.OnInitParamsListener;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

@Suppress
public class DeepShareTestCase extends ApplicationTestCase<Application> {
    public static String APP_ID = "B20CBE0FEDB9BEC1";
    public static String SESSION_ID = "F6A49134DD56893D";
    public DeepShareTestCase() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        init();
    }

    @Override
    public void tearDown() throws Exception {
        stop();
        super.tearDown();
    }

    protected static void setMockServerUrl(MockWebServer server){
        HttpUrl baseUrl = server.url("");
        DSConfig.API_BASE_URL_DEBUG = baseUrl + DSConfig.API_VERSION;
    }

    protected void init() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(
                        "{\"data\":\"\",\"browser_fingerprint_id\":\"\",\"session_id\":\"" + SESSION_ID +"\",\"device_fingerprint_id\":\"301FFE83FBB43A16\",\"identity_id\":\"C9A6C0D96EA02E9D\",\"link\":\"https:\\/\\/error\"}"
                )
        );
        server.start();
        setMockServerUrl(server);
        final CountDownLatch signal = new CountDownLatch(1);
        Activity mockActivity = new Activity() {

            @Override
            public Context getApplicationContext() {
                return getContext();
            }

            @Override
            public Intent getIntent() {
                return new Intent();
            }
        };
        DeepShare.init(mockActivity, APP_ID, new OnInitParamsListener() {
            @Override
            public void onInitParamsReturned(JSONObject initParams) {
                signal.countDown();
            }

            @Override
            public void onFailed(String reason) {
                signal.countDown();
            }
        });

        try {
            signal.await();
        } catch (InterruptedException e) {
            fail("Interrupted execution");
        }
        server.shutdown();
    }

    protected void stop() throws IOException {
        MockWebServer server = new MockWebServer();
        //For close
        server.enqueue(new MockResponse().setBody("")
        );
        server.start();
        setMockServerUrl(server);
        DeepShare.onStop();
        server.shutdown();
    }

}