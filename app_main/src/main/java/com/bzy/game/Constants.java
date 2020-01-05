package com.bzy.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.bzy.game.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description : com.bzy.game
 *
 * @author : rocky
 * @Create Time : 2018/12/7 3:06 PM
 * @Modified Time : 2018/12/7 3:06 PM
 */
public class Constants {
    public static String MAP_TXT = "map.txt";//检查更新的文件名称
    public static String MUSIC_FILE = "music";//如果是整包，启动时把音乐文件copy到本地
    public static String VERSION_MAP_TXT = "versionMap.txt";//已经下载的文件名称
    public static String ACTION_COPY_SUCCESS = "com.bzy.game.ACTION_COPY_SUCCESS";//文件复制成功的广播
    public static String ACTION_COPY_FAILED = "com.bzy.game.ACTION_COPY_FAILED";//文件复制失败的广播
    public static String ACTION_DOWNLOAD_SUCCESS = "com.bzy.game.ACTION_DOWNLOAD_SUCCESS";//文件下载成功的广播
    public static String ACTION_DOWNLOAD_FAILED = "com.bzy.game.ACTION_DOWNLOAD_FAILED";//文件下载失败的广播
    public static String ACTION_COMPARE_SUCCESS = "com.bzy.game.ACTION_COMPARE_SUCCESS";//比对版本号的广播
    //最大缓存大小
    public static final long MAX_DISK_CACHE_SIZE = 300 * 1024 * 1024;//MB
    private static String packageName;
    private static String versionName;
    private static int versionCode;
    private static String imei;
    private static String imsi;
    private static String net;
    private static String apn;
    private static boolean autoLogin;//是否自动登录
    private static int wavPayFlag = 0;//是否播放wav 文件格式音频: 0播放 1不播放
    private static boolean loadIndex = false;
    private static String deviceBrand;
    private static String deviceOsVersion;
    private static String deviceModel;
    private static int deviceSdkInit;
    private static String netWorkType = "none";//wifi,mobile,none

    @SuppressLint("MissingPermission")
    public static void init(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        packageName = context.getPackageName();
        try {
            packInfo = packageManager.getPackageInfo(packageName, 0);
            versionName = packInfo.versionName;
            versionCode = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(e.toString());
        }

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            imei = tm.getDeviceId();
            if (null == imei) {
                imei = tm.getSimSerialNumber();
            }
            imsi = tm.getSubscriberId();
        } catch (Exception e) {
            LogUtil.e(e.toString());
        }

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            net = networkInfo.getTypeName();
            apn = networkInfo.getExtraInfo();
        } else {
            net = "";
            apn = "";
        }
        deviceBrand = android.os.Build.BRAND;
        deviceOsVersion = android.os.Build.VERSION.RELEASE;
        deviceModel = android.os.Build.MODEL;
        deviceSdkInit = Build.VERSION.SDK_INT;
        wavPayFlag = android.os.Build.VERSION.SDK_INT < 21 ? 0 : 1;
        getStartJson();
    }

    /**
     * 获取手机信息和应用版本信息
     *
     * @return
     */
    public static JSONObject getStartJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("androidVersion", Build.VERSION.RELEASE);
            obj.put("packageName", packageName);
            obj.put("appVersion", versionName);
            obj.put("versionCode", versionCode);
            obj.put("imei", imei);
            obj.put("imsi", imsi);
            obj.put("netModel", net);
            obj.put("netApn", apn);
            obj.put("phoneFactory", Build.BRAND);
            obj.put("phoneType", Build.MODEL);
        } catch (JSONException e) {
        }
        LogUtil.d("----phoneinfo:" + obj.toString());
        return obj;
    }

    public static String getPackageName() {
        return packageName;
    }

    public static void setPackageName(String packageName) {
        Constants.packageName = packageName;
    }

    public static String getVersionName() {
        return versionName;
    }

    public static void setVersionName(String versionName) {
        Constants.versionName = versionName;
    }

    public static int getVersionCode() {
        return versionCode;
    }

    public static void setVersionCode(int versionCode) {
        Constants.versionCode = versionCode;
    }

    public static String getImei() {
        return imei;
    }

    public static void setImei(String imei) {
        Constants.imei = imei;
    }

    public static String getImsi() {
        return imsi;
    }

    public static void setImsi(String imsi) {
        Constants.imsi = imsi;
    }

    public static String getNet() {
        return net;
    }

    public static void setNet(String net) {
        Constants.net = net;
    }

    public static String getApn() {
        return apn;
    }

    public static void setApn(String apn) {
        Constants.apn = apn;
    }

    public static String getDeviceBrand() {
        return deviceBrand;
    }

    public static void setDeviceBrand(String deviceBrand) {
        Constants.deviceBrand = deviceBrand;
    }

    public static String getDeviceOsVersion() {
        return deviceOsVersion;
    }

    public static void setDeviceOsVersion(String deviceOsVersion) {
        Constants.deviceOsVersion = deviceOsVersion;
    }

    public static String getDeviceModel() {
        return deviceModel;
    }

    public static void setDeviceModel(String deviceModel) {
        Constants.deviceModel = deviceModel;
    }

    public static int getDeviceSdkInit() {
        return deviceSdkInit;
    }

    public static void setDeviceSdkInit(int deviceSdkInit) {
        Constants.deviceSdkInit = deviceSdkInit;
    }

    public static String getNetWorkType() {
        return netWorkType;
    }

    public static void setNetWorkType(String netWorkType) {
        Constants.netWorkType = netWorkType;
    }

    public static boolean isAutoLogin() {
        return autoLogin;
    }

    public static void setAutoLogin(boolean autoLogin) {
        Constants.autoLogin = autoLogin;
    }

    public static int getWavPayFlag() {
        return wavPayFlag;
    }

    public static void setWavPayFlag(int wavPayFlag) {
        Constants.wavPayFlag = wavPayFlag;
    }

    public static boolean isLoadIndex() {
        return loadIndex;
    }

    public static void setLoadIndex(boolean loadIndex) {
        Constants.loadIndex = loadIndex;
    }

}
