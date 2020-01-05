package com.bzy.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.quicksdk.Extend;
import com.quicksdk.QuickSDK;
import com.quicksdk.entity.GameRoleInfo;
import com.quicksdk.entity.OrderInfo;
import com.quicksdk.entity.UserInfo;
import com.quicksdk.notifier.ExitNotifier;
import com.quicksdk.notifier.InitNotifier;
import com.quicksdk.notifier.LoginNotifier;
import com.quicksdk.notifier.LogoutNotifier;
import com.quicksdk.notifier.PayNotifier;
import com.quicksdk.notifier.SwitchAccountNotifier;

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
        // 设置横竖屏，游戏横屏为true，游戏竖屏为false(必接)
        QuickSDK.getInstance().setIsLandScape(true);
        // 设置通知，用于监听初始化，登录，注销，支付及退出功能的返回值(必接)
        QuickSDK.getInstance()
                .setInitNotifier(new InitNotifier() {// 1.设置初始化通知(必接)

                    @Override
                    public void onSuccess() {
                        sdkCallBack.showToast("初始化成功");
                    }

                    @Override
                    public void onFailed(String message, String trace) {
                        sdkCallBack.showToast("初始化失败");
                    }
                })
                .setLoginNotifier(new LoginNotifier() {// 2.设置登录通知(必接)

                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        if (userInfo != null) {
                            Map<String, Object> hashMap = new HashMap<>();
                            hashMap.put("account", userInfo.getUID());
                            hashMap.put("pw", userInfo.getToken());
                            hashMap.put("channelType",Extend.getInstance().getChannelType());
                            String jsonStringLogin = JsonUtil.getMapToJsonString(hashMap);
                            sdkCallBack.loginSuccess(jsonStringLogin);
                        } else {
                            sdkCallBack.showToast("登录失败");
                            sdkCallBack.loginFailed("{\"code\":404,\"msg\":\"登录失败\"}");
                        }
                    }

                    @Override
                    public void onCancel() {
                        sdkCallBack.showToast("登录取消");
                        sdkCallBack.loginFailed("{\"code\":404,\"msg\":\"登录取消\"}");
                    }

                    @Override
                    public void onFailed(final String message, String trace) {
                        sdkCallBack.showToast("登录失败");
                        sdkCallBack.loginFailed("{\"code\":404,\"msg\":\"登录失败\"}");
                    }

                })
                .setLogoutNotifier(new LogoutNotifier() {// 3.设置注销通知(必接)

                    @Override
                    public void onSuccess() {
                        sdkCallBack.showToast("注销成功");
                    }

                    @Override
                    public void onFailed(String message, String trace) {
                        sdkCallBack.showToast("注销失败:" + message);
                    }
                })
                .setSwitchAccountNotifier(new SwitchAccountNotifier() {// 4.设置切换账号通知(必接)
                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        if (userInfo != null) {
                            sdkCallBack.switchAccountSuccess();
                        }
                    }

                    @Override
                    public void onFailed(String message, String trace) {
                        sdkCallBack.switchAccountFailed();
                    }

                    @Override
                    public void onCancel() {
                        sdkCallBack.switchAccountFailed();
                    }
                })
                .setPayNotifier(new PayNotifier() {// 5.设置支付通知(必接)

                    @Override
                    public void onSuccess(String sdkOrderID, final String cpOrderID, String extrasParams) {
                        if (!TextUtils.isEmpty(cpOrderID)) {
                            Map<String, Object> hashMap = new HashMap<>();
                            hashMap.put("code", 200);
                            hashMap.put("orderId", cpOrderID);
                            String jsonString = JsonUtil.getMapToJsonString(hashMap);
                            sdkCallBack.paySuccess(jsonString);
                        } else {
                            Map<String, Object> hashMapCancel = new HashMap<>();
                            hashMapCancel.put("code", 404);
                            hashMapCancel.put("msg", "支付失败");
                            String jsonStringCancel = JsonUtil.getMapToJsonString(hashMapCancel);
                            sdkCallBack.payFailed(jsonStringCancel);
                        }
                    }

                    @Override
                    public void onCancel(String cpOrderID) {
                        Map<String, Object> hashMapCancel = new HashMap<>();
                        hashMapCancel.put("code", 404);
                        hashMapCancel.put("msg", "支付取消");
                        String jsonStringCancel = JsonUtil.getMapToJsonString(hashMapCancel);
                        sdkCallBack.payFailed(jsonStringCancel);
                    }

                    @Override
                    public void onFailed(String cpOrderID, String message, String trace) {
                        Map<String, Object> hashMapCancel = new HashMap<>();
                        hashMapCancel.put("code", 404);
                        hashMapCancel.put("msg", "支付失败");
                        String jsonStringCancel = JsonUtil.getMapToJsonString(hashMapCancel);
                        sdkCallBack.payFailed(jsonStringCancel);
                    }
                })
                .setExitNotifier(new ExitNotifier() {// 6.设置退出通知(必接)

                    @Override
                    public void onSuccess() {
                        sdkCallBack.payFailed("退出成功");
                        sdkCallBack.exitSuccess();
                    }

                    @Override
                    public void onFailed(String message, String trace) {
                        sdkCallBack.payFailed("退出失败");
                        sdkCallBack.exitFailed();
                    }
                });
        // 请将下面语句中的第二与第三个参数，替换成QuickSDK后台申请的productCode和productKey值
        com.quicksdk.Sdk.getInstance().init(((Activity) context), appid, appkey);
    }

    @JavascriptInterface
    public void login() {
        sdkCallBack.logcat("login");
        com.quicksdk.User.getInstance().login(((Activity) context));
    }

    @JavascriptInterface
    public void logout() {
        sdkCallBack.logcat("logout");
        com.quicksdk.User.getInstance().logout(((Activity) context));
    }

    @JavascriptInterface
    public void pay(String json) {
        sdkCallBack.logcat(json);
        try {
            JSONObject jsonObject = new JSONObject(json);

            GameRoleInfo roleInfo = new GameRoleInfo();
            roleInfo.setServerID(jsonObject.getString("serverIndex"));// 服务器ID，其值必须为数字字符串
            roleInfo.setServerName(jsonObject.getString("serverName"));// 服务器名称
            roleInfo.setGameRoleName(jsonObject.getString("roleName"));// 角色名称
            roleInfo.setGameRoleID(jsonObject.getString("roleID"));// 角色ID
            roleInfo.setGameUserLevel(jsonObject.getString("roleLv"));// 等级
            roleInfo.setVipLevel("");// VIP等级
            roleInfo.setGameBalance("");// 角色现有金额
            roleInfo.setPartyName("");// 公会名字

            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setCpOrderID(jsonObject.getString("cpOrder"));// 游戏订单号
            orderInfo.setGoodsName(jsonObject.getString("goodsName"));// 产品名称
            orderInfo.setCount(1);// 购买数量，如购买"月卡"则传1
            orderInfo.setAmount(Integer.valueOf(jsonObject.getString("price"))); // 总金额（单位为元）
            orderInfo.setGoodsID(jsonObject.getInt("goodsID") + ""); // 产品ID，用来识别购买的产品
            orderInfo.setExtrasParams(jsonObject.getString("extendstr")); // 透传参数

            com.quicksdk.Payment.getInstance().pay(((Activity) context), orderInfo, roleInfo);
        } catch (Exception e) {
            sdkCallBack.logcat(e.toString());
        }
    }

    @JavascriptInterface
    public void auth() {
        sdkCallBack.logcat("auth");
    }

    @JavascriptInterface
    public void enterGame(String json) {
        sdkCallBack.logcat("enterGame:" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);

            GameRoleInfo roleInfo = new GameRoleInfo();
            roleInfo.setServerID(jsonObject.has("serverID")?jsonObject.getString("serverID"):"nullServerID");// 服务器ID
            roleInfo.setServerName(jsonObject.has("serverName")?jsonObject.getString("serverName"):"nullServerName");// 服务器名称
            roleInfo.setGameRoleName(jsonObject.has("roleName")?jsonObject.getString("roleName"):"nullName");// 角色名称
            roleInfo.setGameRoleID(jsonObject.has("roleID")?jsonObject.getString("roleID"):"nullRoleID");// 角色ID
            roleInfo.setGameUserLevel(jsonObject.has("roleLV")?jsonObject.getString("roleLV"):"nullLevel");// 等级
            roleInfo.setVipLevel(jsonObject.has("vipLevel")?jsonObject.getString("vipLevel"):"nullVipLevel"); // 设置当前用户vip等级，必须为整型字符串
            roleInfo.setGameBalance(jsonObject.has("balance")?jsonObject.getString("balance"):"nullBalance"); // 角色现有金额
            roleInfo.setPartyName(jsonObject.has("partName")?jsonObject.getString("partName"):"nullPartName"); // 设置帮派，公会名称
            roleInfo.setRoleCreateTime(System.currentTimeMillis() / 1000 + ""); // UC与1881渠道必传，值为10位数时间戳
            roleInfo.setPartyId(jsonObject.has("partId")?jsonObject.getString("partId"):"nullPartID"); // 360渠道参数，设置帮派id，必须为整型字符串
            roleInfo.setGameRoleGender("男"); // 360渠道参数
            roleInfo.setGameRolePower("10"); // 360渠道参数，设置角色战力，必须为整型字符串
            roleInfo.setPartyRoleId("1"); // 360渠道参数，设置角色在帮派中的id
            roleInfo.setPartyRoleName("无"); // 360渠道参数，设置角色在帮派中的名称
            roleInfo.setProfessionId("1"); // 360渠道参数，设置角色职业id，必须为整型字符串
            roleInfo.setProfession("无"); // 360渠道参数，设置角色职业名称
            roleInfo.setFriendlist("无"); // 360渠道参数，设置好友关系列表，格式请参考：http://open.quicksdk.net/help/detail/aid/190
            com.quicksdk.User.getInstance().setGameRoleInfo(((Activity) context), roleInfo, jsonObject.has("isNew")&&jsonObject.getBoolean("isNew"));
            sdkCallBack.logcat("enterGame update channel success");
        } catch (JSONException e) {
            sdkCallBack.logcat("enterGame json params error");
        }
    }

    public void onCreate() {
        // 生命周期接口调用(必接)
        com.quicksdk.Sdk.getInstance().onCreate(((Activity) context));
    }

    public void onStart() {
        // 生命周期接口调用(必接)
        com.quicksdk.Sdk.getInstance().onStart(((Activity) context));
    }

    public void onRestart() {
        // 生命周期接口调用(必接)
        com.quicksdk.Sdk.getInstance().onRestart(((Activity) context));
    }

    public void onResume() {
        // 生命周期接口调用(必接)
        com.quicksdk.Sdk.getInstance().onResume(((Activity) context));
    }

    public void onPause() {
        // 生命周期接口调用(必接)
        com.quicksdk.Sdk.getInstance().onPause(((Activity) context));
    }

    public void onStop() {
        // 生命周期接口调用(必接)
        com.quicksdk.Sdk.getInstance().onPause(((Activity) context));
    }

    public void onNewIntent(Intent intent) {
        // 生命周期接口调用(必接)
        com.quicksdk.Sdk.getInstance().onNewIntent(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 生命周期接口调用(必接)
        com.quicksdk.Sdk.getInstance().onActivityResult(((Activity) context), requestCode, resultCode, data);
    }

    public void onDestroy() {
        // 生命周期接口调用(必接)
        com.quicksdk.Sdk.getInstance().onDestroy(((Activity) context));
        context = null;
    }
}
