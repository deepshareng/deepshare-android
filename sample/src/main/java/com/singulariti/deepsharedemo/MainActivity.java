package com.singulariti.deepsharedemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.singulariti.deepshare.DeepShare;
import com.singulariti.deepshare.URLParam;
import com.singulariti.deepshare.listeners.NewUsageFromMeListener;
import com.singulariti.deepshare.listeners.OnFailListener;
import com.singulariti.deepshare.listeners.OnInitParamsListener;
import com.singulariti.deepshare.listeners.OnURLGeneratedListener;
import com.singulariti.deepshare.listeners.TaggedValueListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    EditText editShortUrl, editTag, editValue, editGetTag, editGetValue, editClearTag;
    Button buttonShortURL, buttonViewURL, buttonNewUsage, buttonClearUsage, buttonChangeValueBy, buttonGetValue, buttonClearValue;
    TextView textStatus, textViewLink, textViewParam, textViewNewInstall, textViewNewOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editShortUrl = (EditText) findViewById(R.id.editShortUrl);
        buttonShortURL = (Button) findViewById(R.id.buttonShortURL);
        textStatus = (TextView) findViewById(R.id.textStatus);
        buttonViewURL = (Button) findViewById(R.id.buttonViewURL);
        textViewNewInstall = (TextView) findViewById(R.id.textViewNewInstall);
        textViewNewOpen = (TextView) findViewById(R.id.textViewNewOpen);

        buttonShortURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateShortUrl();
            }
        });

        buttonViewURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = editShortUrl.getText().toString();
                if (!TextUtils.isEmpty(url)) {
                    openBrowser(url);
                }
            }
        });

        textViewLink = (TextView) findViewById(R.id.textLink);
        if(this.getIntent().getData() != null) {
            textViewLink.setText(this.getIntent().getData().toString());
            Log.d("onCreate; intent data:", this.getIntent().getData().toString());
        }

        textViewParam = (TextView) findViewById(R.id.textViewParams);

        buttonNewUsage = (Button) findViewById(R.id.buttonNewUsage);
        buttonNewUsage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getNewUsage();
            }
        });
        buttonClearUsage = (Button) findViewById(R.id.buttonClearUsage);
        buttonClearUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearNewUsage();
            }
        });

        editTag = (EditText) findViewById(R.id.editTextTag);
        editValue = (EditText) findViewById(R.id.editTextValue);
        buttonChangeValueBy = (Button) findViewById(R.id.buttonChangeValueBy);
        buttonChangeValueBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeValueBy(editTag.getText().toString(), editValue.getText().toString());
            }
        });

        editGetTag = (EditText) findViewById(R.id.editTextGetTag);
        editGetValue = (EditText) findViewById(R.id.editTextGetValue);
        buttonGetValue = (Button) findViewById(R.id.buttonGetValue);
        buttonGetValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValue(editGetTag.getText().toString());
            }
        });

        editClearTag = (EditText) findViewById(R.id.editTextClearTag);
        buttonClearValue = (Button) findViewById(R.id.buttonClearValue);
        buttonClearValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearValue(editClearTag.getText().toString());
            }
        });
    }

    private void generateShortUrl(){
        editShortUrl.setText("");

        URLParam urlParam = new URLParam();
        JSONObject dataToInclude = new JSONObject();
        try {
            dataToInclude.put("key1", "value1");
            dataToInclude.put("key2", true);
            dataToInclude.put("test3", 3);
        } catch (JSONException ex) {
        }
        urlParam.attachInitParams(dataToInclude);
        DeepShare.generateURL(urlParam, new OnURLGeneratedListener() {
            @Override
            public void onGenerated(String url) {
                Log.i(TAG, "onGenerated " + url);
                textStatus.setText("Short URL created");
                editShortUrl.setText(url);
            }

            @Override
            public void onFailed(String reason) {
                textStatus.setText(reason);
                Log.e(TAG, "onFailed " + reason);
            }
        });
    }

    private void openBrowser(String url){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }

    private void clearValue(String tag) {
        if(TextUtils.isEmpty(tag)){
            return;
        }
        String[] tags = new String[1];
        tags[0] = tag;
        DeepShare.clearValueFromMe(tags, new OnFailListener() {
            @Override
            public void onFailed(String reason) {
                textStatus.setText(reason);
                Log.e(TAG, "onFailed " + reason);
            }
        });
    }

    private void getValue(final String tag) {
        if(TextUtils.isEmpty(tag)){
            return;
        }
        String[] tags = new String[1];
        tags[0] = tag;
        DeepShare.getValueFromMe(tags, new TaggedValueListener(){

            @Override
            public void onTaggedValueReturned(HashMap<String, Integer> tagToValue) {
                int value = tagToValue.get(tag);
                editGetValue.setText("" + value);
            }

            @Override
            public void onFailed(String reason) {

            }
        });
    }

    private void changeValueBy(String tag, String value) {
        if(TextUtils.isEmpty(tag) || TextUtils.isEmpty(value)){
            return;
        }
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put(tag, Integer.valueOf(value));
        DeepShare.changeValueBy(map, new OnFailListener(){

            @Override
            public void onFailed(String reason) {

            }
        });
    }

    private void clearNewUsage() {
        DeepShare.clearNewUsageFromMe(new OnFailListener() {
            @Override
            public void onFailed(String reason) {

            }
        });
    }

    private void getNewUsage() {
        DeepShare.getNewUsageFromMe(new NewUsageFromMeListener() {
            @Override
            public void onNewUsageFromMe(int newInstall, int newOpen) {
                textViewNewInstall.setText(newInstall + "New Install");
                textViewNewOpen.setText(newOpen + "New Open");
            }

            @Override
            public void onFailed(String reason) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        DeepShare.init(this, "7713337217A6E150", new OnInitParamsListener() {
            @Override
            public void onInitParamsReturned(JSONObject params) {
                try {
                    Iterator<?> keys = params.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        textViewParam.append("key:" + key + "; value:" + params.getString(key) + " ");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String s) {

            }
        });
        textStatus.setText("MainActivity started");
        textViewParam.setText("");
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
