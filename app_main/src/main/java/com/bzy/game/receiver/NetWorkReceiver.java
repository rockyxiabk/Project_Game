package com.bzy.game.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.bzy.game.Constants;
import com.bzy.game.MainActivity;
import com.bzy.game.util.HttpUtil;
import com.bzy.game.util.LogUtil;

public class NetWorkReceiver extends BroadcastReceiver {

    private final NetWorkChangeListener listener;

    public NetWorkReceiver(NetWorkChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果相等的话就说明网络状态发生了变化
        LogUtil.d(intent.getAction());
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = HttpUtil.getNetWorkState(context);
            if (null != listener) {
                listener.onNetWorkChangeListener(netWorkState);
            } else {
                LogUtil.e("listener null");
                Constants.setNetWorkType("none");
            }
        } else {
            Constants.setNetWorkType("none");
            LogUtil.e("not equals");
        }
    }
}
