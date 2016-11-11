package com.singulariti.test;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.singulariti.deepshare.DeepShare;
import com.singulariti.deepshare.URLParam;
import com.singulariti.deepshare.listeners.OnInitParamsListener;
import com.singulariti.deepshare.listeners.OnURLGeneratedListener;

import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;


public class TestActivity extends Activity {

    private static final String TAG = "TestActivity";
    public static JSONObject curParams;
    public static boolean isActivityOut;
    String shortUrl = "";

    public void generateShortUrl(JSONObject dataToInclude){
        final CountDownLatch signal = new CountDownLatch(1);
        URLParam builder = new URLParam();
        builder.attachInitParams(dataToInclude);
        DeepShare.generateURL(builder, new OnURLGeneratedListener() {
            @Override
            public void onGenerated(String url) {
                shortUrl = url;
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
        }
    }

    public void openBrowser(String url){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(this.getIntent().getData() != null) {
            Log.d("onCreate; intent data:", this.getIntent().getData().toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        DeepShare.init(this, "B20CBE0FEDB9BEC1", new OnInitParamsListener() {
            @Override
            public void onInitParamsReturned(JSONObject params) {
                curParams = params;
                isActivityOut = false;
            }

            @Override
            public void onFailed(String s) {

            }
        });
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        DeepShare.onStop();
    }
}
