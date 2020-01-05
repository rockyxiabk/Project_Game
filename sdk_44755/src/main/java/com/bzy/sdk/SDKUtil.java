package com.bzy.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.ledi.floatwindow.util.FloatView;
import com.ledi.util.CallbackListener;
import com.ledi.util.ChangeAccountListener;
import com.ledi.util.LoadPayCallBackLinstener;
import com.ledi.util.Operate;
import com.ledi.util.PayCallBack;

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

    private CallbackListener callback;
    private LoadPayCallBackLinstener mPayCallBackLinstener;
    private PayCallBack mpaycallBack;
    private FloatView.KeyBackListener mKeyBackListener;
    private ChangeAccountListener changeAccountListener;
    public String ssionid;
    public String uid;
    public boolean isload = true;
    private String cpOrder="";
    private String appid;


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
        this.appid = appid;
        mPayCallBackLinstener = new com.ledi.util.LoadPayCallBackLinstener() {
            @Override
            public void isloadBack(boolean arg0) {
                sdkCallBack.showToast("登录返回");
            }

            @Override
            public void isPayBack(boolean arg0) {
                sdkCallBack.showToast("充值返回");
            }
        };

        /**
         * 登录回调接口
         */
        callback = new com.ledi.util.CallbackListener() {
            @Override
            public void loginReback(String arg0, String arg1) {
                ssionid = arg0;
                uid = arg1;
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("account", uid);
                hashMap.put("pw", ssionid);
                String jsonStringLogin = JsonUtil.getMapToJsonString(hashMap);
                sdkCallBack.loginSuccess(jsonStringLogin);
                // 如果无法调用onWindowFocusChanged方法，此时调用showFloatView方法
                if (null != uid && !uid.equals("")) {
                    Operate.showFloatView(((Activity) context), 50, 100, false, mKeyBackListener, changeAccountListener);
                }
            }

            @Override
            public void loginBackKey(boolean arg0) {
                sdkCallBack.showToast("登录失败");
            }

            @Override
            public boolean init(boolean arg0) {
                isload = arg0;
                sdkCallBack.showToast("初始化成功");
                return arg0;
            }
        };

        /**
         * 充值回调接口
         */
        mpaycallBack = new PayCallBack() {
            @Override
            public void paySuccess(String arg0, int arg1) {
                sdkCallBack.logcat(arg0+"----"+arg1);
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("code", 200);
                hashMap.put("orderId",cpOrder);
                String jsonString = JsonUtil.getMapToJsonString(hashMap);
                sdkCallBack.logcat(jsonString);
                sdkCallBack.paySuccess(jsonString);
            }

            public void payFail(String msg) {
                sdkCallBack.logcat(msg);
                Map<String, Object> hashMapFailed = new HashMap<>();
                hashMapFailed.put("code", 404);
                hashMapFailed.put("msg", "支付失败");
                String jsonStringFailed = JsonUtil.getMapToJsonString(hashMapFailed);
                sdkCallBack.payFailed(jsonStringFailed);
            }
        };
        // 悬浮窗切换账号回调------根据实际情况，进行退出账号并回到登录界面
        changeAccountListener = new ChangeAccountListener() {
            @Override
            public void changeAccount(boolean arg0) {
                isload = arg0;
                if (arg0) {
                     Operate.startMain(((Activity) context));
                }
            }
        };
        Operate.init(((Activity) context), appid, context.getPackageName(), context.getPackageName()+".MainActivity", callback, mPayCallBackLinstener);
        /**
         * 点击返回键 进行悬浮窗操作
         */
        mKeyBackListener = new FloatView.KeyBackListener() {
            @Override
            public void onClickBack(boolean arg0) {
                Operate.showFloatView(((Activity) context), 50, 100, false);
            }
        };
    }

    @JavascriptInterface
    public void login() {
        sdkCallBack.logcat("login");
        Operate.startMain(((Activity) context));
    }

    @JavascriptInterface
    public void logout() {
        sdkCallBack.logcat("logout");
    }

    @JavascriptInterface
    public void pay(String json) {
        sdkCallBack.logcat("pay:json:" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            String price = jsonObject.getString("price");
            int money = Integer.valueOf(price);
            String serverName = jsonObject.getString("serverName");
            String extendstr = jsonObject.getString("extendstr");
            String serverIndex = jsonObject.getString("serverIndex");
            cpOrder = jsonObject.getString("cpOrder");
            Operate.payMoney(serverName, ((Activity) context), Integer.valueOf(serverIndex), money, extendstr, mpaycallBack);
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
            String serverName = jsonObject.getString("serverName");
            String serverID = jsonObject.getString("serverID");
            String roleID = jsonObject.getInt("roleID")+"";
            String roleName = jsonObject.getString("roleName");
            String roleLV = jsonObject.getString("roleLV");
            String vipLevel = jsonObject.getString("vipLevel");
            String balance = jsonObject.getInt("balance")+"";

            /**
             * 角色接口（玩家每次登陆后进入角色后调用一次）:
             * 游戏id(必填),
             * uid（必填），
             * 角色id（必填），
             * 角色名称（必填），
             * 角色等级（必填-只传数字），
             * 区服id（必填），
             * 区服名称（必填）
             * vip等级（只传数字），
             * 剩余元宝（必填-只传数字）
             */
            Operate.roleInfo(appid, uid, roleID, roleName, roleLV, serverID, serverName, vipLevel, balance);
        } catch (JSONException e) {
            sdkCallBack.logcat("enterGame json params error"+e.toString());
        }

    }

    public void onCreate() {
    }

    public void onStart() {
    }

    public void onRestart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onNewIntent(Intent intent) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void onDestroy() {
//        if (null != uid && !uid.equals("")) {
//            sdkCallBack.logcat(uid);
//            Operate.destoryFloatView(context.getApplicationContext());
//        }
        context = null;
    }
}
