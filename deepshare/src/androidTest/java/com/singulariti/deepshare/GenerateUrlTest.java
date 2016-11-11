package com.singulariti.deepshare;

import android.test.suitebuilder.annotation.LargeTest;

import com.singulariti.deepshare.listeners.OnURLGeneratedListener;
import com.singulariti.deepshare.protocol.httpsendmessages.UrlMessage;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class GenerateUrlTest extends DeepShareTestCase {

    @LargeTest
    public void testGenerateShortUrl() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("{\"url\":\"http://fds.so/u/7713337217A6E150/1hO\"}"));
        String expectRequest = "{\"data\":\"{\\\"key2\\\":2,\\\"key1\\\":\\\"value1\\\"}\",\"session_id\":\"" + DeepShareTestCase.SESSION_ID + "\",\"app_id\":\"B20CBE0FEDB9BEC1\",\"sdk\":\"android" + BuildConfig.VERSION_NAME + "\",\"device_fingerprint_id\":\"301FFE83FBB43A16\",\"identity_id\":\"C9A6C0D96EA02E9D\"}";

        server.start();
        setMockServerUrl(server);
        final CountDownLatch signal = new CountDownLatch(1);

        JSONObject data = new JSONObject();
        try {
            data.put("key1", "value1");
            data.put("key2", 2);
        } catch (JSONException ex) {
        }

        URLParam urlParam = new URLParam();
        urlParam.attachInitParams(data);
        DeepShare.generateURL(urlParam, new OnURLGeneratedListener() {

            @Override
            public void onGenerated(String url) {
                assertEquals("http://fds.so/u/7713337217A6E150/1hO", url);
                signal.countDown();
            }

            @Override
            public void onFailed(String reason) {
                fail(reason);
                signal.countDown();
            }
        });

        RecordedRequest rr = server.takeRequest();
        String actualRequest = rr.getBody().readUtf8();
        assertEquals(expectRequest, actualRequest);
        assertEquals("/" + DSConfig.API_VERSION + UrlMessage.URL_PATH, rr.getPath());

        try {
            signal.await();
        } catch (InterruptedException e) {
            fail("Interrupted execution");
        }
    }
}