package com.singulariti.deepshare;

import android.test.suitebuilder.annotation.LargeTest;

import com.singulariti.deepshare.listeners.NewUsageFromMeListener;
import com.singulariti.deepshare.listeners.OnFailListener;
import com.singulariti.deepshare.protocol.httpsendmessages.ClearUsageMessage;
import com.singulariti.deepshare.protocol.httpsendmessages.NewUsageMessage;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class UsageTest extends DeepShareTestCase {

    @LargeTest
    public void testGetNewUsage() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        String expectRequest = "{\"session_id\":\"" +
                DeepShareTestCase.SESSION_ID +
                "\",\"sdk\":\"android" +
                BuildConfig.VERSION_NAME +
                "\"}";
        server.enqueue(new MockResponse().setBody("{\"new_install\":5,\"new_open\":3}"));
        server.start();
        setMockServerUrl(server);
        final CountDownLatch signal = new CountDownLatch(1);

        DeepShare.getNewUsageFromMe(new NewUsageFromMeListener() {
            @Override
            public void onNewUsageFromMe(int newInstall, int newOpen) {
                assertEquals(5, newInstall);
                assertEquals(3, newOpen);
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
        assertEquals("/" + DSConfig.API_VERSION + NewUsageMessage.URL_PATH, rr.getPath());

        try {
            signal.await();
        } catch (InterruptedException e) {
            fail("Interrupted execution");
        }
    }

    @LargeTest
    public void testClearNewUsage() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        String expectRequest = "{\"session_id\":\"" +
                DeepShareTestCase.SESSION_ID +
                "\",\"sdk\":\"android" +
                BuildConfig.VERSION_NAME +
                "\"}";
        server.enqueue(new MockResponse().setBody(""));
        server.start();
        setMockServerUrl(server);
        final CountDownLatch signal = new CountDownLatch(1);

        DeepShare.clearNewUsageFromMe(new OnFailListener() {
            @Override
            public void onFailed(String reason) {
                fail(reason);
                signal.countDown();
            }
        });
        RecordedRequest rr = server.takeRequest();
        String actualRequest = rr.getBody().readUtf8();
        assertEquals(expectRequest, actualRequest);
        assertEquals("/" + DSConfig.API_VERSION + ClearUsageMessage.URL_PATH, rr.getPath());
        try {
            signal.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("Interrupted execution");
        }
    }
}