package com.bzy.game.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.bzy.game.Constants;

/**
 * Description : com.bzy.game.util
 *
 * @author : rocky
 * @Create Time : 2018/12/8 2:56 PM
 * @Modified Time : 2018/12/8 2:56 PM
 */
public class HttpUtil {
    public static final int NETWORK_WIFI = 2;
    public static final int NETWORK_MOBILE = 1;
    public static final int NETWORK_NONE = -1;

    public static boolean isNetWorkConnected(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    public static int getNetWorkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //如果网络连接，判断该网络类型
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                Constants.setNetWorkType("wifi");
                return NETWORK_WIFI;//wifi
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                Constants.setNetWorkType("mobile");
                return NETWORK_MOBILE;//mobile
            }
        } else {
            Constants.setNetWorkType("none");
            return NETWORK_NONE;
        }
        Constants.setNetWorkType("none");
        return NETWORK_NONE;
    }
}
