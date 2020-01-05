package com.bzy.sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.Toast;

import com.reyun.tracking.sdk.Tracking;

/**
 * Description : com.bzy.sdk
 * 当需要继承sdk的activity时添加到此处，避免主干代码冗余，需要修改时，要考虑其他sdk兼容性，再行修改
 *
 * @author : rocky
 * @Create Time : 2018/12/8 11:12 AM
 * @Modified Time : 2018/12/8 11:12 AM
 */
public abstract class BaseMainActivity extends Activity {
    protected Handler handler = new Handler();
    private long exitTime = 0l;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFlag();
        initView();
    }

    protected abstract void initView();

    protected abstract void setWindowFlag();

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                exitTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次,退出游戏！", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                showExitDialog();
            }
        }
        return false;
    }

    protected abstract void showExitDialog();

    protected void exitActivity() {
        Tracking.exitSdk();
        finish();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, 1000);
    }
}
