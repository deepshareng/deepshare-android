package com.singulariti.deepshare;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.singulariti.deepshare.listeners.OnInitParamsListener;
import com.singulariti.deepshare.protocol.httpsendmessages.CloseMessage;
import com.singulariti.deepshare.protocol.httpsendmessages.InstallMessage;
import com.singulariti.deepshare.protocol.httpsendmessages.OpenMessage;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by joy on 15/10/3.
 */
public class LifeCycleTest extends ApplicationTestCase<Application> {

    public LifeCycleTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        createApplication();
    }

    @LargeTest
    public void testInstall() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(
                        "{\"data\":\"{\\\"key2\\\":true,\\\"key1\\\":\\\"value1\\\",\\\"key3\\\":3}\",\"browser_fingerprint_id\":\"\",\"session_id\":\"F6A49134DD56893D\",\"device_fingerprint_id\":\"301FFE83FBB43A16\",\"identity_id\":\"C9A6C0D96EA02E9D\",\"link\":\"https:\\/\\/error\"}"
                )
        );
        String expectRequest = "{\"update\":1,\"is_hardware_id_real\":true,\"os\":\"Android\",\"has_nfc\":false,\"os_release\":\"4.4.4\",\"model\":\"MI 4LTE\",\"screen_dpi\":480,\"hardware_id\":\"ce354d4e11c45855\",\"screen_width\":1080,\"has_telephone\":true,\"os_version\":\"19\",\"wifi\":false,\"bluetooth\":false,\"brand\":\"Xiaomi\",\"carrier\":\"CMCC\",\"app_id\":\"B20CBE0FEDB9BEC1\",\"sdk\":\"android"+ BuildConfig.VERSION_NAME+ "\",\"bluetooth_version\":\"ble\",\"screen_height\":1920}";
        //For close
        server.enqueue(new MockResponse().setBody("")
        );
        server.start();
        DeepShareTestCase.setMockServerUrl(server);
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
        //To make it a install.
        Configuration config = Configuration.getInstance(getContext());
        config.setIdentityID(null);
        DeepShare.init(mockActivity, DeepShareTestCase.APP_ID, new OnInitParamsListener() {
            @Override
            public void onInitParamsReturned(JSONObject initParams) {
                try {
                    assertEquals("value1", initParams.get("key1"));
                    assertEquals(true, initParams.get("key2"));
                    assertEquals(3, initParams.get("key3"));
                    Configuration config = Configuration.getInstance(getContext());
                    assertEquals("F6A49134DD56893D", config.getSessionID());
                    assertEquals("301FFE83FBB43A16", config.getDeviceFingerPrintID());
                    assertEquals("C9A6C0D96EA02E9D", config.getIdentityID());
                } catch (JSONException e) {
                    fail(e.getMessage());
                }

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
        assertEquals("/" + DSConfig.API_VERSION + InstallMessage.URL_PATH, rr.getPath());

        try {
            signal.await();
        } catch (InterruptedException e) {
            fail("Interrupted execution");
        }

        expectRequest = "{\"session_id\":\"" +
                DeepShareTestCase.SESSION_ID +
                "\",\"sdk\":\"android" +
                BuildConfig.VERSION_NAME +
                "\"}";
        DeepShare.onStop();
        rr = server.takeRequest();
        actualRequest = rr.getBody().readUtf8();
        assertEquals(expectRequest, actualRequest);
        assertEquals("/" + DSConfig.API_VERSION + CloseMessage.URL_PATH, rr.getPath());
        //TODO: we should be able to check below:
//        assertEquals(DeepShareImpl.STATE_IDLE, DeepShare.instance.state);
        server.shutdown();
    }

    @LargeTest
    public void testOpen() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(
                        "{\"data\":\"{\\\"key2\\\":true,\\\"key1\\\":\\\"value1\\\",\\\"key3\\\":3}\",\"browser_fingerprint_id\":\"\",\"session_id\":\"F6A49134DD56893D\",\"device_fingerprint_id\":\"301FFE83FBB43A16\",\"identity_id\":\"C9A6C0D96EA02E9D\",\"link\":\"https:\\/\\/error\"}"
                )
        );
        String expectRequest = "{\"os\":\"Android\",\"os_release\":\"4.4.4\",\"app_id\":\"B20CBE0FEDB9BEC1\",\"sdk\":\"android" + BuildConfig.VERSION_NAME +"\",\"identity_id\":\"C9A6C0D96EA02E9D\",\"device_fingerprint_id\":\"301FFE83FBB43A16\",\"os_version\":\"19\"}";
        //For close
        server.enqueue(new MockResponse().setBody("")
        );
        server.start();
        DeepShareTestCase.setMockServerUrl(server);
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
        DeepShare.init(mockActivity, DeepShareTestCase.APP_ID, new OnInitParamsListener() {
            @Override
            public void onInitParamsReturned(JSONObject initParams) {
                try {
                    assertEquals("value1", initParams.get("key1"));
                    assertEquals(true, initParams.get("key2"));
                    assertEquals(3, initParams.get("key3"));
                    Configuration config = Configuration.getInstance(getContext());
                    assertEquals("F6A49134DD56893D", config.getSessionID());
                    assertEquals("301FFE83FBB43A16", config.getDeviceFingerPrintID());
                    assertEquals("C9A6C0D96EA02E9D", config.getIdentityID());
                } catch (JSONException e) {
                    fail(e.getMessage());
                }

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
        assertEquals("/" + DSConfig.API_VERSION + OpenMessage.URL_PATH, rr.getPath());

        try {
            signal.await();
        } catch (InterruptedException e) {
            fail("Interrupted execution");
        }

        expectRequest = "{\"session_id\":\"" +
                DeepShareTestCase.SESSION_ID +
                "\",\"sdk\":\"android" +
                BuildConfig.VERSION_NAME +
                "\"}";
        DeepShare.onStop();
        rr = server.takeRequest();
        actualRequest = rr.getBody().readUtf8();
        assertEquals(expectRequest, actualRequest);
        assertEquals("/" + DSConfig.API_VERSION + CloseMessage.URL_PATH, rr.getPath());
        //TODO: we should be able to check below:
//        assertEquals(DeepShareImpl.STATE_IDLE, DeepShare.instance.state);
        server.shutdown();
    }
}