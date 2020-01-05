package com.bzy.sdk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.quicksdk.QuickSdkSplashActivity;

/**
 * Description : com.bzy.sdk
 * 当需要继承sdk的闪屏页时添加到此处，避免主干代码冗余，需要修改时，要考虑其他sdk兼容性，再行修改
 *
 * @author : rocky
 * @Create Time : 2018/12/7 6:31 PM
 * @Modified Time : 2018/12/7 6:31 PM
 */
public abstract class BaseSplashActivity extends QuickSdkSplashActivity {
    protected Handler handler = new Handler();

    protected abstract void checkVersion();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public int getBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    public void onSplashStop() {
        checkVersion();
    }

    protected void exitActivity() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }
}
