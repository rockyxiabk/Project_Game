package com.bzy.tencent_x5;

import android.content.Context;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

/**
 * Description : com.bzy.tencent_x5
 *
 * @author : rocky
 * @Create Time : 2018/12/7 2:22 PM
 * @Modified Time : 2018/12/7 2:22 PM
 */
public class TencentX5 {
    public static void init(Context context) {
        Log.e("TAG", "initX5()");
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                if (arg0) {
                    Log.e("TAG", " onViewInitFinished is " + arg0 + "-----core 加载成功");
                } else {
                    Log.e("TAG", " onViewInitFinished is " + arg0 + "-----core 加载失败，调用系统内核");
                }
            }

            @Override
            public void onCoreInitFinished() {
                Log.e("TAG", " onCoreInitFinished");
            }
        };
        //x5内核初始化接口
        try {
            QbSdk.initX5Environment(context.getApplicationContext(), cb);
        }catch (Exception e){
            Log.e("tencent x5 init:",e.toString());
        }
    }
}
