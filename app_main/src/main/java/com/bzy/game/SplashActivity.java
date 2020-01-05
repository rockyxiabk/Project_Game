package com.bzy.game;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.bzy.game.ipresenter.ISplashPresenter;
import com.bzy.game.presenter.SplashPresenter;
import com.bzy.game.service.CopyIntentService;
import com.bzy.game.util.HttpUtil;
import com.bzy.game.util.LogUtil;
import com.bzy.game.util.NavigationUtil;
import com.bzy.game.util.PreferencesUtil;
import com.bzy.game.util.ScreenUtil;
import com.bzy.game.version.VersionManager;
import com.bzy.game.view.CheckUpdateActivity;
import com.bzy.game.view.OpenWifiDialog;
import com.bzy.sdk.BaseSplashActivity;
import com.bzy.game.BuildConfig;

import java.io.File;

public class SplashActivity extends BaseSplashActivity implements ISplashPresenter,
        OpenWifiDialog.OpenWifiListener {

    private SplashPresenter presenter;
    private OpenWifiDialog openWifiDialog;
    private boolean isNeedStart;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                LogUtil.d(intent.getAction());
                if (intent.getAction().equalsIgnoreCase(Constants.ACTION_COPY_SUCCESS)) {
                    if (isNeedStart) {
                        isNeedStart = false;
                        presenter.getGameVersionInfo();
                    }
                }else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_COPY_FAILED)){
                    CopyIntentService.startActionCopyWhole(SplashActivity.this);
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NavigationUtil.hideNavigation(getWindow());

        initPresenter();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_COPY_SUCCESS);
        intentFilter.addAction(Constants.ACTION_COPY_FAILED);
        registerReceiver(receiver, intentFilter);

        LogUtil.d(this.getClass().getSimpleName());
    }

    private void initPresenter() {
        presenter = new SplashPresenter(this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(this.getClass().getSimpleName());
        File file = new File(VersionManager.getResourceRootPath());
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.d(this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(this.getClass().getSimpleName());
        if (com.bzy.view.BuildConfig.ISWHOLEPKG) {
            boolean isCopy = PreferencesUtil.getIsCopy();
            if (!isCopy) {
                CopyIntentService.startActionCopyWhole(this);
                PreferencesUtil.setIsCopy(true);
            }else {
                if (isNeedStart) {
                    isNeedStart = false;
                    presenter.getGameVersionInfo();
                }else {
                    presenter.getGameVersionInfo();
                }
            }
        }else {
            if (isNeedStart) {
                isNeedStart = false;
                presenter.getGameVersionInfo();
            }else {
                presenter.getGameVersionInfo();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(this.getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(this.getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(this.getClass().getSimpleName());
        unregisterReceiver(receiver);
    }

    @Override
    protected void checkVersion() {
        boolean netWorkConnected = HttpUtil.isNetWorkConnected(this);
        if (netWorkConnected) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    presenter.getGameVersionInfo();
                }
            }, 2000);
        } else {
            showWifiDialog();
        }
    }

    private void showWifiDialog() {
        if (null == openWifiDialog) {
            openWifiDialog = new OpenWifiDialog(this, false, false, this);
        }
        if (!openWifiDialog.isShowing()) {
            openWifiDialog.show();
        }
    }

    @Override
    public void setVersionInfo(String resourceIP, String gameVersion, int versionCode) {
        LogUtil.d(resourceIP + "----" + gameVersion);
        PreferencesUtil.setGameIndexIp(resourceIP);
        if (com.bzy.view.BuildConfig.ISWHOLEPKG) {
            if (PreferencesUtil.getIsFirstStart()) {
                if (versionCode > com.bzy.view.BuildConfig.GAMEVERSIONCODE) {
                    PreferencesUtil.setGameVersionName(gameVersion);
                    PreferencesUtil.setGameVersionCode(versionCode);
                    startOtherActivity(CheckUpdateActivity.class);
                } else {
                    startOtherActivity(MainActivity.class);
                }
                PreferencesUtil.setIsFirstStart(false);
            } else {
                if (versionCode > PreferencesUtil.getGameVersionCode()) {
                    PreferencesUtil.setGameVersionName(gameVersion);
                    PreferencesUtil.setGameVersionCode(versionCode);
                    startOtherActivity(CheckUpdateActivity.class);
                } else {
                    if (PreferencesUtil.getGameUpdateSuspend()) {
                        startOtherActivity(CheckUpdateActivity.class);
                    } else {
                        startOtherActivity(MainActivity.class);
                    }
                }
            }
        } else {
            PreferencesUtil.setGameVersionName(gameVersion);
            startOtherActivity(MainActivity.class);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.d(this.getClass().getSimpleName());
        return false;
    }

    @Override
    public void loadVersionError(String msg) {
        LogUtil.e(msg);
        exitActivity();
    }

    private void startOtherActivity(final Class<?> cls, String... params) {
        // TODO: 2018/12/29  
        LogUtil.d("start:" + ScreenUtil.screenIsShow(this));
        if (!ScreenUtil.screenIsShow(this)) {
            isNeedStart = true;
            return;
        }
        Intent intent = new Intent(SplashActivity.this, cls);
        startActivity(intent);
        exitActivity();
    }

    @Override
    public void cancelExitGame() {
        exitActivity();
    }
}
