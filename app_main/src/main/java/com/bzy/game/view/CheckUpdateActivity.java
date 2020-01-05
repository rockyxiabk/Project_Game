package com.bzy.game.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bzy.game.Constants;
import com.bzy.game.MainActivity;
import com.bzy.game.R;
import com.bzy.game.ipresenter.ICheckUpdate;
import com.bzy.game.presenter.CheckUpdatePresenter;
import com.bzy.game.service.DownloadFileService;
import com.bzy.game.util.DiskManager;
import com.bzy.game.util.LogUtil;
import com.bzy.game.util.NavigationUtil;
import com.bzy.game.util.PreferencesUtil;
import com.bzy.game.util.ScreenUtil;
import com.bzy.game.version.VersionManager;

public class CheckUpdateActivity extends Activity implements ICheckUpdate {
    private LinearLayout llView;//更新进度view的root布局
    private TextView tvCheck;
    private ProgressBar progressBar;
    private TextView tvDes;
    private TextView tvSize;
    private int progress;//下载进度
    private int fileCount;//下载完成的文件数
    private int totalFile;//要下载的总的文件数
    private int totalFileSize;//要下载的总的文件大小
    private boolean isDownloadSuccess;//是否下载完成，启动游戏
    private Handler handler = new Handler();
    private CheckUpdatePresenter presenter;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                LogUtil.d(intent.getAction());
                if (intent.getAction().equalsIgnoreCase(Constants.ACTION_DOWNLOAD_SUCCESS)) {
                    String url = intent.getStringExtra("url");
                    LogUtil.d("download success:"+url);
                    int dataLen = intent.getIntExtra("dataLen", 0);
                    if (url.equalsIgnoreCase(Constants.MAP_TXT)) {
                        VersionManager.get().initMap();
                    } else {
                        updateProgress(dataLen);
                    }
                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_DOWNLOAD_FAILED)) {
                    String url = intent.getStringExtra("url");
                    LogUtil.d("download failed:"+url);
                    if (url.equalsIgnoreCase(Constants.MAP_TXT)) {
                        VersionManager.get().initMap();
                    } else {
                        updateProgress(0);
                    }
                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_COMPARE_SUCCESS)) {
                    LogUtil.d("compare success");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            presenter.initUpdate();
                        }
                    });
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_update);
        NavigationUtil.hideNavigation(getWindow());

        presenter = new CheckUpdatePresenter(this, this);

        llView = ((LinearLayout) findViewById(R.id.ll_view));
        llView.setVisibility(View.GONE);
        tvCheck = ((TextView) findViewById(R.id.tv_check));
        progressBar = ((ProgressBar) findViewById(R.id.progress));
        tvDes = ((TextView) findViewById(R.id.tv_des));
        tvSize = ((TextView) findViewById(R.id.tv_size));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_DOWNLOAD_SUCCESS);
        intentFilter.addAction(Constants.ACTION_DOWNLOAD_FAILED);
        intentFilter.addAction(Constants.ACTION_COMPARE_SUCCESS);
        registerReceiver(receiver, intentFilter);

        PreferencesUtil.setGameUpdateSuspend(true);
        DownloadFileService.startService(this, Constants.MAP_TXT);

        LogUtil.d(this.getClass().getSimpleName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(this.getClass().getSimpleName());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.d(this.getClass().getSimpleName());
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(this.getClass().getSimpleName());
        if (isDownloadSuccess) {
            startOtherActivity(MainActivity.class);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    private void startOtherActivity(final Class<?> cls, String... params) {
        if (!ScreenUtil.screenIsShow(this)) {
            isDownloadSuccess = true;
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(CheckUpdateActivity.this, cls);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    @Override
    public void setUpdateInfo(int size, int dataSize) {
        if (size == 0 && dataSize == 0) {
            tvCheck.setText("已是最新版本，无需更新！");
            PreferencesUtil.setGameUpdateSuspend(false);
            startOtherActivity(MainActivity.class);
        } else {
            tvCheck.setVisibility(View.INVISIBLE);
            llView.setVisibility(View.VISIBLE);
            totalFile = size;
            totalFileSize = dataSize;
            progressBar.setMax(totalFile);
            progressBar.setProgress(fileCount);
            progressBar.setVisibility(View.VISIBLE);
            tvDes.setVisibility(View.VISIBLE);
            tvSize.setVisibility(View.VISIBLE);
            tvDes.setText("Ver." + PreferencesUtil.getGameOldVersion() + "(" + PreferencesUtil.getGameVersionName() + ")");
            tvSize.setText("游戏更新中" + DiskManager.getFormatSize(progress) + "/" + DiskManager.getFormatSize(totalFileSize) + " (" + fileCount + "/" + totalFile + ")");
        }
    }

    @Override
    public void updateProgress(int dataLen) {
        fileCount++;
        progress += dataLen;
        LogUtil.d("progress:"+progress + "--totalFile:"+totalFile+"---totalSize:"+totalFileSize);
        if (fileCount == totalFile) {
            PreferencesUtil.setGameUpdateSuspend(false);
            progressBar.setProgress(fileCount);
            tvDes.setText("更新完成，正在重新加载资源...");
            tvSize.setText("游戏更新中" + DiskManager.getFormatSize(totalFileSize) + "/" + DiskManager.getFormatSize(totalFileSize) + " (" + fileCount + "/" + totalFile + ")");
            startOtherActivity(MainActivity.class);
        } else {
            progressBar.setProgress(fileCount);
            tvSize.setText("游戏更新中" + DiskManager.getFormatSize(progress) + "/" + DiskManager.getFormatSize(totalFileSize) + " (" + fileCount + "/" + totalFile + ")");
        }
    }
}
