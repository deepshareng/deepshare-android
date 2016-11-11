package com.singulariti.deepshare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.singulariti.deepshare.utils.ApkParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.jar.JarFile;

public class Configuration {
    private static final String TAG = "Configuration";
    private static final String SHARED_PREF_FILE = "deep_share_preference";

    private static final String KEY_APP_KEY = "app_key";

    private static final String KEY_DEVICE_FINGERPRINT_ID = "device_fingerprint_id";
    private static final String KEY_SESSION_ID = "session_id";
    private static final String KEY_IDENTITY_ID = "identity_id";
    private static final String KEY_LINK_CLICK_ID = "link_click_id";
    private static final String KEY_LINK_CLICK_IDENTIFIER = "link_click_identifier";
    private static final String KEY_SESSION_PARAMS = "session_params";
    private static final String KEY_INSTALL_PARAMS = "install_params";
    private static final String KEY_USER_URL = "user_url";
    private static final String KEY_IS_REFERRABLE = "is_referrable";

    private static Configuration config;
    private SharedPreferences preference;

    private Context context;

    private Configuration(Context context) {
        preference = context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        this.context = context;
    }

    public static Configuration getInstance(Context context) {
        if (config == null) {
            config = new Configuration(context);
        }
        return config;
    }

    public String getAppKey() {
        String appKey = getString(KEY_APP_KEY);

        if (TextUtils.isEmpty(appKey)) {
            try {
                final ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (ai.metaData != null) {
                    appKey = ai.metaData.getString("com.singulariti.deepshare.APP_KEY");
                }
            } catch (final PackageManager.NameNotFoundException e) {
            }
        }

        return appKey;
    }

    public void setAppKey(String key) {
        setString(KEY_APP_KEY, key);
    }

    public String getDeviceFingerPrintID() {
        return getString(KEY_DEVICE_FINGERPRINT_ID);
    }

    public void setDeviceFingerPrintID(String device_fingerprint_id) {
        setString(KEY_DEVICE_FINGERPRINT_ID, device_fingerprint_id);
    }

    public String getSessionID() {
        return getString(KEY_SESSION_ID);
    }

    public void setSessionID(String session_id) {
        setString(KEY_SESSION_ID, session_id);
    }

    public String getIdentityID() {
        return getString(KEY_IDENTITY_ID);
    }

    public void setIdentityID(String identity_id) {
        setString(KEY_IDENTITY_ID, identity_id);
    }

    public void setLinkClickID(String link_click_id) {
        setString(KEY_LINK_CLICK_ID, link_click_id);
    }

    public String getClickId() {
        return getString(KEY_LINK_CLICK_IDENTIFIER);
    }

    public void setClickId(String identifier) {
        setString(KEY_LINK_CLICK_IDENTIFIER, identifier);
    }

    public String getSessionParams() {
        return getString(KEY_SESSION_PARAMS);
    }

    public void setSessionParams(String params) {
        setString(KEY_SESSION_PARAMS, params);
    }

    public String getInstallParams() {
        return getString(KEY_INSTALL_PARAMS);
    }

    public void setInstallParams(String params) {
        setString(KEY_INSTALL_PARAMS, params);
    }

    public String getUserURL() {
        return getString(KEY_USER_URL);
    }

    public void setUserURL(String user_url) {
        setString(KEY_USER_URL, user_url);
    }

    public int getIsReferrable() {
        return getInteger(KEY_IS_REFERRABLE);
    }

    public void setIsReferrable() {
        setInteger(KEY_IS_REFERRABLE, 1);
    }

    public void clearIsReferrable() {
        setInteger(KEY_IS_REFERRABLE, 0);
    }

    private int getInteger(String key) {
        return getInteger(key, 0);
    }

    private int getInteger(String key, int defaultValue) {
        return preference.getInt(key, defaultValue);
    }

    private String getString(String key) {
        return preference.getString(key, null);
    }

    private void setInteger(String key, int value) {
        preference.edit().putInt(key, value).commit();
    }

    private void setString(String key, String value) {
        preference.edit().putString(key, value).commit();
    }

    public String getUniqueID() {
        String androidID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        if (androidID == null) {
            androidID = UUID.randomUUID().toString();
        }
        return androidID;
    }

    public boolean hasRealHardwareId() {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID) != null;
    }

    public String getURIScheme() {
        return getURIScheme(context.getPackageName());
    }

    //TODO:should be removed when open source
    public String getURIScheme(String packageName) {
        String scheme = null;
        if (!isLowOnMemory()) {
            PackageManager pm = context.getPackageManager();
            try {
                ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
                String sourceApk = ai.publicSourceDir;
                JarFile jf = null;
                InputStream is = null;
                byte[] xml;
                try {
                    jf = new JarFile(sourceApk);
                    is = jf.getInputStream(jf.getEntry("AndroidManifest.xml"));
                    xml = new byte[is.available()];
                    //noinspection ResultOfMethodCallIgnored
                    is.read(xml);
                    scheme = new ApkParser().decompressXML(xml);
                } catch (Exception ignored) {
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (jf != null) {
                            jf.close();
                        }
                    } catch (IOException ignored) {
                    }
                }
            } catch (NameNotFoundException ignored) {
            }
        }
        return scheme;
    }

    private boolean isLowOnMemory() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        activityManager.getMemoryInfo(mi);
        return mi.lowMemory;
    }

    public String getAppVersion() {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (packageInfo.versionName != null) {
                return packageInfo.versionName;
            } else {
                return null;
            }
        } catch (NameNotFoundException ignored) {
        }
        return null;
    }

    public String getCarrier() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            String ret = telephonyManager.getNetworkOperatorName();
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    public boolean isBluetoothPresent() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) {
                return bluetoothAdapter.isEnabled();
            }
        } catch (SecurityException ignored) {
        }
        return false;
    }

    public String getBluetoothVersion() {
        if (android.os.Build.VERSION.SDK_INT >= 8) {
            if (android.os.Build.VERSION.SDK_INT >= 18 &&
                    context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                return "ble";
            } else if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
                return "classic";
            }
        }
        return null;
    }

    public boolean isNFCPresent() {
        try {
            return context.getPackageManager().hasSystemFeature("android.hardware.nfc");
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean isTelephonePresent() {
        try {
            return context.getPackageManager().hasSystemFeature("android.hardware.telephony");
        } catch (Exception ignored) {
        }
        return false;
    }

    public String getPhoneBrand() {
        return android.os.Build.MANUFACTURER;
    }

    public String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    public String getOS() {
        return "Android";
    }

    public int getOSVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public String getOSRelease() { return android.os.Build.VERSION.RELEASE; }

    @SuppressLint("NewApi")
    public int getUpdateState() {
        if (android.os.Build.VERSION.SDK_INT >= 9) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                if (packageInfo.lastUpdateTime != packageInfo.firstInstallTime) {
                    return 1;
                } else {
                    return 0;
                }
            } catch (NameNotFoundException ignored) {
            }
        }
        return 0;
    }

    public DisplayMetrics getScreenDisplay() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getMetrics(displayMetrics);
        return displayMetrics;
    }

    public boolean isWifiConnected() {
        if (PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiInfo.isConnected();
        }
        return false;
    }

    public boolean isNetworkPermissionGranted() {
        return PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission(Manifest.permission.INTERNET);
    }
}
