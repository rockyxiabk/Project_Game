package com.bzy.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.mrcn.common.CommonMrSdk;
import com.mrcn.sdk.callback.MrCallback;
import com.mrcn.sdk.entity.MrError;
import com.mrcn.sdk.entity.MrInitEntity;
import com.mrcn.sdk.entity.MrPayEntity;
import com.mrcn.sdk.entity.MrRoleEntity;
import com.mrcn.sdk.entity.response.ResponseLoginData;

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
        MrInitEntity initEntity = new MrInitEntity();
        initEntity.setDebug(false);

        CommonMrSdk.getInstance().init(((Activity) context), initEntity, new MrCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sdkCallBack.logcat("init success maoer");
            }

            @Override
            public void onFail(MrError mrError) {
                sdkCallBack.logcat("init failed maoer:"+mrError.toString());
            }
        });
        CommonMrSdk.getInstance().registerLogout(context, new MrCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sdkCallBack.logoutSuccess();
            }

            @Override
            public void onFail(MrError mrError) {
                sdkCallBack.loginFailed();
            }
        });
    }

    @JavascriptInterface
    public void login() {
        sdkCallBack.logcat("login");
        CommonMrSdk.getInstance().loginWithUI(((Activity) context), new MrCallback<ResponseLoginData>() {
            @Override
            public void onSuccess(ResponseLoginData loginData) {
                String uid = loginData.getUid();
                String time = loginData.getTime();
                String vsign = loginData.getVsign();
                Map<String, Object> hashMap = new HashMap<>();
                if (!TextUtils.isEmpty(uid)) {
                    hashMap.put("account", uid);
                    hashMap.put("pw", vsign);
                    hashMap.put("time", time);
                    String jsonStringLogin = JsonUtil.getMapToJsonString(hashMap);
                    sdkCallBack.loginSuccess(jsonStringLogin);
                } else {
                    hashMap.put("code", 201);
                    hashMap.put("msg", "登录失败");
                    String jsonStringLogin = JsonUtil.getMapToJsonString(hashMap);
                    sdkCallBack.loginFailed(jsonStringLogin);
                    sdkCallBack.showToast("登录失败");
                }
            }

            @Override
            public void onFail(MrError error) {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("code", 201);
                hashMap.put("msg", "登录失败");
                String jsonStringLogin = JsonUtil.getMapToJsonString(hashMap);
                sdkCallBack.loginFailed(jsonStringLogin);
                sdkCallBack.showToast("登录失败");
            }
        });
    }

    @JavascriptInterface
    public void logout() {
        sdkCallBack.logcat("logout");
        CommonMrSdk.getInstance().logOut(((Activity) context), new MrCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sdkCallBack.logoutSuccess("");
            }

            @Override
            public void onFail(MrError mrError) {
                sdkCallBack.logoutFailed("");
            }
        });
    }

    @JavascriptInterface
    public void pay(String json) {
        sdkCallBack.logcat("pay:json:" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);

            final String gamecno = jsonObject.getString("cpOrder"); //游戏内的订单号
            final MrPayEntity r2PayEntity = new MrPayEntity();
            r2PayEntity.setProductid(jsonObject.getInt("goodsID") + "");
            r2PayEntity.setUid(jsonObject.getString("roleID"));
            r2PayEntity.setRoleid(jsonObject.getString("roleID"));
            r2PayEntity.setRolename(jsonObject.getString("roleName"));
            r2PayEntity.setRolelevel(jsonObject.getString("roleLv"));
            r2PayEntity.setExtradata(jsonObject.getString("extendstr"));
            r2PayEntity.setServerid(jsonObject.getString("serverIndex"));
            r2PayEntity.setGamecno(jsonObject.getString("cpOrder"));
            r2PayEntity.setNotifyurl(jsonObject.getString("notifyUrl"));
            r2PayEntity.setChannel("1");//支付渠道，传 1 即可
            CommonMrSdk.getInstance().pay(((Activity) context), r2PayEntity, new MrCallback<Void>() {
                @Override
                public void onSuccess(Void t) {
                    Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("code", 200);
                    hashMap.put("orderId", gamecno);
                    String jsonString = JsonUtil.getMapToJsonString(hashMap);
                    sdkCallBack.paySuccess(jsonString);
                }

                @Override
                public void onFail(MrError error) {
                    Map<String, Object> hashMapFailed = new HashMap<>();
                    hashMapFailed.put("code", 404);
                    hashMapFailed.put("msg", "支付失败");
                    String jsonStringFailed = JsonUtil.getMapToJsonString(hashMapFailed);
                    sdkCallBack.payFailed(jsonStringFailed);
                }
            });
        } catch (JSONException e) {
            sdkCallBack.logcat("支付失败:" + e.toString());
        }
    }

    @JavascriptInterface
    public void auth() {
        sdkCallBack.logcat("auth");
    }

    @JavascriptInterface
    public void enterGame(String json) {
        sdkCallBack.logcat("enterGame:json:" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);

            final MrRoleEntity mrRoleEntity = new MrRoleEntity();
            mrRoleEntity.setRoleid(jsonObject.has("roleID") ? jsonObject.getString("roleID") : "nullRoleID");
            mrRoleEntity.setServerId(jsonObject.has("serverID") ? jsonObject.getString("serverID") : "nullServerID");
            mrRoleEntity.setRoleLevel(jsonObject.has("roleLV") ? jsonObject.getString("roleLV") : "nullLevel");
            mrRoleEntity.setRoleName(jsonObject.has("roleName") ? jsonObject.getString("roleName") : "nullName");
            mrRoleEntity.setVipLevel(jsonObject.has("vipLevel") ? jsonObject.getString("vipLevel") : "nullVipLevel");
            if (jsonObject.has("isNew")) {
                if (jsonObject.getBoolean("isNew")) {
                    sdkCallBack.logcat("create");
                    CommonMrSdk.getInstance().sendRoleCreateData(((Activity) context), mrRoleEntity);
                } else {
                    sdkCallBack.logcat("login");
                    CommonMrSdk.getInstance().sendRoleLoginData(((Activity) context), mrRoleEntity);
                }
            } else {
                sdkCallBack.logcat("login");
                CommonMrSdk.getInstance().sendRoleLoginData(((Activity) context), mrRoleEntity);
            }

            sdkCallBack.logcat("enterGame update channel success");
        } catch (Exception e) {
            sdkCallBack.logcat(e.toString());
        }
    }

    public void onCreate() {
    }

    public void onStart() {
    }

    public void onRestart() {
        CommonMrSdk.getInstance().onRestart(((Activity) context));
    }

    public void onResume() {
        CommonMrSdk.getInstance().onResume(((Activity) context));
    }

    public void onPause() {
        CommonMrSdk.getInstance().onPause(((Activity) context));
    }

    public void onStop() {
        CommonMrSdk.getInstance().onStop(((Activity) context));
    }

    public void onNewIntent(Intent intent) {
        CommonMrSdk.getInstance().handleIntent(intent, ((Activity) context));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CommonMrSdk.getInstance().onActivityResult(requestCode, resultCode, data, ((Activity) context));
    }

    public void onDestroy() {
        CommonMrSdk.getInstance().onDestroy(((Activity) context));
        context = null;
    }
}
