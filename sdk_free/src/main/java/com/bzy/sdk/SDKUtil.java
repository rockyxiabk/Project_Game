package com.bzy.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.allugame.freesdk.callback.FreeCallback;
import com.allugame.freesdk.callback.FreeCallbackCode;
import com.allugame.freesdk.entities.FreeOrder;
import com.allugame.freesdk.port.FreePlatform;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Description : com.bzy.sdk
 *
 * @author : rocky
 * @Create Time : 2018/12/7 4:02 PM
 * @Modified Time : 2018/12/7 4:02 PM
 */
public class SDKUtil {
    private static SDKUtil instance;
    private static Context context;
    private static SDKCallBack sdkCallBack;
    private FreePlatform freePlatform;
    private FreeOrder freeOrder;

    private SDKUtil() {
    }

    public static SDKUtil getInstance(Context context) {
        if (null == instance) {
            SDKUtil.context = context;
            instance = new SDKUtil();
        }
        if (null == SDKUtil.context) {
            SDKUtil.context = context;
        }
        return instance;
    }

    public void setSdkCallBack(SDKCallBack sdkCallBack) {
        SDKUtil.sdkCallBack = sdkCallBack;
    }

    public void initSDK(String appid, String appkey) {
        freePlatform = FreePlatform.getInstance();
        freePlatform.init(((Activity) context), new FreeCallback() {
            @Override
            public void onResult(int code, String msg) {
                sdkCallBack.logcat("code:" + code + " result:" + msg);
                switch (code) {
                    case FreeCallbackCode.INIT_SUCCESS:
                        sdkCallBack.showToast("初始化成功");
                        break;
                    case FreeCallbackCode.INIT_FAILURE:
                        sdkCallBack.showToast("初始化失败");
                        break;
                    case FreeCallbackCode.LOGIN_CANCEL:
                        sdkCallBack.showToast("登录取消");
                        sdkCallBack.loginFailed("404");
                        break;
                    case FreeCallbackCode.LOGIN_FAILURE:
                        sdkCallBack.showToast("登录失败");
                        sdkCallBack.loginFailed("404");
                        break;
                    case FreeCallbackCode.LOGIN_SUCCESS:
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(msg);
                            String token = jsonObject.getString("token");//帐号验证需要的token
                            String account_id = jsonObject.getString("account");//用户id
                            Map<String, Object> hashMap = new HashMap<>();
                            hashMap.put("account", account_id);
                            hashMap.put("pw", token);
                            String jsonStringLogin = JsonUtil.getMapToJsonString(hashMap);
                            sdkCallBack.loginSuccess(jsonStringLogin);
                        } catch (JSONException e) {
                            sdkCallBack.logcat(e.toString());
                            sdkCallBack.showToast("登录失败");
                        }
                        break;
                    case FreeCallbackCode.PAY_CANCEL:
                        Map<String, Object> hashMapCancel = new HashMap<>();
                        hashMapCancel.put("code", 404);
                        hashMapCancel.put("msg", "支付取消");
                        String jsonStringCancel = JsonUtil.getMapToJsonString(hashMapCancel);
                        sdkCallBack.payFailed(jsonStringCancel);
                        break;
                    case FreeCallbackCode.PAY_FAIL:
                        Map<String, Object> hashMapFailed = new HashMap<>();
                        hashMapFailed.put("code", 404);
                        hashMapFailed.put("msg", "支付失败");
                        String jsonStringFailed = JsonUtil.getMapToJsonString(hashMapFailed);
                        sdkCallBack.payFailed(jsonStringFailed);
                        break;
                    case FreeCallbackCode.PAY_PROGRESS:
                        sdkCallBack.showToast("支付中...");
                        break;
                    case FreeCallbackCode.PAY_SUCCESS:
                        if (null != freeOrder) {
                            Map<String, Object> hashMap = new HashMap<>();
                            hashMap.put("code", 200);
                            hashMap.put("orderId", freeOrder.getCpOrderId());
                            String jsonString = JsonUtil.getMapToJsonString(hashMap);
                            sdkCallBack.paySuccess(jsonString);
                        }
                        break;
                }
            }
        });
    }

    @JavascriptInterface
    public void login() {
        sdkCallBack.logcat("login");
        freePlatform.login(((Activity) context));
    }

    @JavascriptInterface
    public void logout() {
        sdkCallBack.logcat("logout");
        freePlatform.logout();
    }

    @JavascriptInterface
    public void pay(String price, String serverName,
                    String serverIndex, String roleName,
                    String roleID, String roleLv,
                    String goodsName, int goodsID,
                    String cpOrder, String extendstr) {
        try {
            sdkCallBack.logcat(price + "-" + serverIndex + "-" + serverName + "-" + goodsName + "-" + goodsID + "-" + cpOrder);
            float money = Float.valueOf(price);
            freeOrder = new FreeOrder(goodsName, cpOrder, money, serverIndex + "," + goodsID);
            freePlatform.pay((Activity) context, freeOrder);
        } catch (Exception e) {
            sdkCallBack.logcat("支付失败：" + e.toString());
        }
    }

    @JavascriptInterface
    public void pay(String json) {
        sdkCallBack.logcat("pay:json:" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            String price = jsonObject.getString("price");
            float money = Float.valueOf(price);
            String goodsName = jsonObject.getString("goodsName");
            String cpOrder = jsonObject.getString("cpOrder");
            String serverIndex = jsonObject.getString("serverIndex");
            int goodsID = jsonObject.getInt("goodsID");
            freeOrder = new FreeOrder(goodsName, cpOrder, money, serverIndex + "," + goodsID);
            freePlatform.pay((Activity) context, freeOrder);
        } catch (JSONException e) {
            sdkCallBack.logcat("支付失败:" + e.toString());
        }
    }

    @JavascriptInterface
    public void auth() {
        sdkCallBack.logcat("auth");
        freePlatform.auth();
    }

    @JavascriptInterface
    public void enterGame(String serverID, String serverName,
                          String roleID, String roleName,
                          String roleLV, String payLevel) {
        sdkCallBack.logcat("enterGame");
    }

    @JavascriptInterface
    public void enterGame(String json) {
        sdkCallBack.logcat("enterGame:json:" + json);
    }

    public void onCreate() {
        freePlatform.onCreate();
    }

    public void onStart() {
        freePlatform.onStart();
    }

    public void onRestart() {
    }

    public void onResume() {
        freePlatform.onResume();
    }

    public void onPause() {
        freePlatform.onPause();
    }

    public void onStop() {
        freePlatform.onStop();
    }

    public void onNewIntent(Intent intent) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void onDestroy() {
        freePlatform.onDestory();
        context = null;
    }
}
