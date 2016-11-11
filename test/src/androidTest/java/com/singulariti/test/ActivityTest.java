package com.singulariti.test;

import android.annotation.TargetApi;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ActivityTest extends ActivityInstrumentationTestCase2 {

    @TargetApi(Build.VERSION_CODES.FROYO)
    public ActivityTest() {
        super(TestActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @LargeTest
    public void testGetShortUrl() throws InterruptedException, JSONException {
        TestActivity activity = (TestActivity) getActivity();
        JSONObject dataToInclude = new JSONObject();
        try {
            dataToInclude.put("key1", "v1");
        } catch (JSONException ex) {
        }
        activity.generateShortUrl(dataToInclude);
        activity.isActivityOut = true;
        activity.openBrowser(activity.shortUrl);

        while(activity.isActivityOut){
            Thread.sleep(400);
        }

        String value = (String) activity.curParams.get("key1");
        assertEquals("v1", value);
    }

    @LargeTest
    public void testNewUsage() throws InterruptedException, JSONException {

    }
}