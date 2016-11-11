package com.singulariti.deepshare;

import android.test.suitebuilder.annotation.LargeTest;

import com.singulariti.deepshare.listeners.OnFailListener;
import com.singulariti.deepshare.listeners.TaggedValueListener;
import com.singulariti.deepshare.protocol.httpsendmessages.ChangeValueByMessage;
import com.singulariti.deepshare.protocol.httpsendmessages.ClearValueMessage;
import com.singulariti.deepshare.protocol.httpsendmessages.GetValueMessage;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class TaggedValueTest extends DeepShareTestCase {

    @LargeTest
    public void testChangeValueBy() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(""));
        String expectRequest = "{\"tag_values\":\"{\\\"aaa\\\":5}\",\"session_id\":\"" + DeepShareTestCase.SESSION_ID + "\",\"sdk\":\"android" + BuildConfig.VERSION_NAME + "\"}";
        server.start();
        setMockServerUrl(server);
        final CountDownLatch signal = new CountDownLatch(1);
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("aaa", 5);
        DeepShare.changeValueBy(map, new OnFailListener(){

            @Override
            public void onFailed(String reason) {
                fail(reason);
                signal.countDown();
            }
        });

        RecordedRequest rr = server.takeRequest();
        String actualRequest = rr.getBody().readUtf8();
        assertEquals(expectRequest, actualRequest);
        assertEquals("/" + DSConfig.API_VERSION + ChangeValueByMessage.URL_PATH, rr.getPath());

        try {
            signal.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("Interrupted execution");
        }
    }

    @LargeTest
    public void testGetValue() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("{\"tag_values\":{\"aaa\":5}}"));
        String expectRequest = "{\"tags\":[\"aaa\"],\"session_id\":\"" + DeepShareTestCase.SESSION_ID + "\",\"sdk\":\"android" + BuildConfig.VERSION_NAME + "\"}";
        server.start();
        setMockServerUrl(server);
        final CountDownLatch signal = new CountDownLatch(1);
        String[] tags = new String[1];
        tags[0] = "aaa";
        DeepShare.getValueFromMe(tags, new TaggedValueListener() {
            @Override
            public void onTaggedValueReturned(HashMap<String, Integer> tagToValue) {
                assertEquals(5, (int)tagToValue.get("aaa"));
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
        assertEquals("/" + DSConfig.API_VERSION + GetValueMessage.URL_PATH, rr.getPath());

        try {
            signal.await();
        } catch (InterruptedException e) {
            fail("Interrupted execution");
        }
    }

    @LargeTest
    public void testClearValue() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(""));
        String expectRequest = "{\"tags\":[\"aaa\"],\"session_id\":\"" + DeepShareTestCase.SESSION_ID + "\",\"sdk\":\"android"+ BuildConfig.VERSION_NAME +"\"}";
        server.start();
        setMockServerUrl(server);
        final CountDownLatch signal = new CountDownLatch(1);
        String[] tags = new String[1];
        tags[0] = "aaa";
        DeepShare.clearValueFromMe(tags, new OnFailListener() {

            @Override
            public void onFailed(String reason) {
                fail(reason);
                signal.countDown();
            }
        });

        RecordedRequest rr = server.takeRequest();
        String actualRequest = rr.getBody().readUtf8();
        assertEquals(expectRequest, actualRequest);
        assertEquals("/" + DSConfig.API_VERSION + ClearValueMessage.URL_PATH, rr.getPath());

        try {
            signal.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("Interrupted execution");
        }
    }
}