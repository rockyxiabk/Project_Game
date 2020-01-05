package com.bzy.game;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bzy.game.javascript.NativeScriptInterface;
import com.bzy.game.media.MediaPlayerManager;
import com.bzy.game.receiver.NetWorkChangeListener;
import com.bzy.game.receiver.NetWorkReceiver;
import com.bzy.game.receiver.ScreenListener;
import com.bzy.game.util.AndroidBug5497Workaround;
import com.bzy.game.util.HttpUtil;
import com.bzy.game.util.LogUtil;
import com.bzy.game.util.NavigationUtil;
import com.bzy.game.view.ExitGameDialog;
import com.bzy.game.view.OpenWifiDialog;
import com.bzy.game.webview.TencentX5WebViewClient;
import com.bzy.game.webview.WebViewClientCallBack;
import com.bzy.sdk.BaseMainActivity;
import com.bzy.sdk.JsonUtil;
import com.bzy.sdk.SDKCallBack;
import com.bzy.sdk.SDKUtil;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseMainActivity implements WebViewClientCallBack,
        SDKCallBack, NetWorkChangeListener, ExitGameDialog.ExitGameListener,
        OpenWifiDialog.OpenWifiListener {

    private WebView webView;
    private NetWorkReceiver netWorkReceiver;
    public static NetWorkChangeListener netWorkListener;
    private ExitGameDialog exitGameDialog;
    private OpenWifiDialog openWifiDialog;
    private ScreenListener screenListener;

    @Override
    protected void setWindowFlag() {
        final Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        NavigationUtil.hideNavigation(window);
        window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                NavigationUtil.hideNavigation(window);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(this.getClass().getSimpleName());
        SDKUtil.getInstance(this).initSDK(com.bzy.view.BuildConfig.APPID, com.bzy.view.BuildConfig.APPKEY);
        SDKUtil.getInstance(this).onCreate();
        SDKUtil.getInstance(this).setSdkCallBack(this);

        //采用动态注册的方式
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        netWorkReceiver = new NetWorkReceiver(this);
        registerReceiver(netWorkReceiver, filter);

        AndroidBug5497Workaround.assistActivity(this);

        screenListener = new ScreenListener(MainActivity.this);
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                LogUtil.d("screen on");
            }

            @Override
            public void onScreenOff() {
                LogUtil.d("screen off");
            }

            @Override
            public void onUserPresent() {
                LogUtil.d("screen present");
                LogUtil.d("不缓存资源代表测试环境 测试环境解锁后刷新游戏");
                if (!Constants.isLoadIndex()) {
                    loadGameIndex();
                }
            }
        });
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        webView = ((WebView) findViewById(R.id.web_view));

        webView.setBackgroundColor(getResources().getColor(android.R.color.black));
        webView.setWebContentsDebuggingEnabled(true);//开启浏览器调试
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setJavaScriptEnabled(true);
        NativeScriptInterface nativeJs = new NativeScriptInterface(this, this);
        webView.addJavascriptInterface(nativeJs, "native");
        webView.addJavascriptInterface(SDKUtil.getInstance(this), "sdk");
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setAppCachePath(GameApplication.getInstance().getCacheDir().getAbsolutePath());
        settings.setAppCacheMaxSize(Constants.MAX_DISK_CACHE_SIZE);
        settings.setAppCacheEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setGeolocationEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setWebViewClient(new TencentX5WebViewClient(this));
        webView.setWebChromeClient(new WebChromeClient());

        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (manager.isInteractive()) {
                LogUtil.d("interactive load game index");
                Constants.setLoadIndex(false);
                loadGameIndex();
            }
        } else {
            boolean screenOn = manager.isScreenOn();
            if (screenOn) {
                LogUtil.d("ScreenON load game index");
                Constants.setLoadIndex(false);
                loadGameIndex();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(this.getClass().getSimpleName());
        SDKUtil.getInstance(this).onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.d(this.getClass().getSimpleName());
        SDKUtil.getInstance(this).onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(this.getClass().getSimpleName());
        if (webView != null) {
            webView.resumeTimers();
            webView.onResume();
        }
        SDKUtil.getInstance(this).onResume();
        if (!MediaPlayerManager.get().muted) {
            MediaPlayerManager.get().startPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(this.getClass().getSimpleName());
        SDKUtil.getInstance(this).onPause();
        if (webView != null) {
            webView.pauseTimers();
            webView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(this.getClass().getSimpleName());
        SDKUtil.getInstance(this).onStop();
        if (MediaPlayerManager.get().isPlaying()) {
            MediaPlayerManager.get().playPause();
        }
        if (Constants.isLoadIndex()) {
            final String script = "javascript:onStop()";
            executeJavaScript(script);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SDKUtil.getInstance(this).onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SDKUtil.getInstance(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(this.getClass().getSimpleName());
        SDKUtil.getInstance(this).onDestroy();
        unregisterReceiver(netWorkReceiver);
        screenListener.unregisterListener();
        netWorkListener = null;
        if (null != webView) {
            webView.loadUrl(null);
            webView.destroy();
        }
        System.gc();
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
    protected void showExitDialog() {
        if (null == exitGameDialog) {
            exitGameDialog = new ExitGameDialog(this, false, false, this);
        }
        if (!exitGameDialog.isShowing()) {
            exitGameDialog.show();
        }
    }

    @Override
    public void loadGameIndex() {
        if (Constants.isLoadIndex()) {
            final String script = "javascript:onStart()";
            executeJavaScript(script);
        } else {
            LogUtil.e("loadGameIndex");
            Constants.setLoadIndex(true);
            webView.loadUrl(BuildConfig.INDEX + com.bzy.view.BuildConfig.CHANNEL + "&rand=" + System.currentTimeMillis());
        }
    }

    @Override
    public void loadMain() {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("gameNameID", com.bzy.view.BuildConfig.GAMENAMEID);
        hashMap.put("gameName", com.bzy.view.BuildConfig.GAMENAME);
        String jsonString = JsonUtil.getMapToJsonString(hashMap);
        final String script = "javascript:loadMain('" + jsonString + "')";
        executeJavaScript(script);
    }

    @Override
    public void startGame() {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("pf", com.bzy.view.BuildConfig.CHANNEL);
        hashMap.put("subsdk", com.bzy.view.BuildConfig.SDK);
        String jsonString = JsonUtil.getMapToJsonString(hashMap);
        final String script = "javascript:start('" + jsonString + "')";
        executeJavaScript(script);
    }

    @Override
    public void loginSuccess(String... params) {
        String script = "javascript:updateChannelInfo('" + params[0] + "')";
        executeJavaScript(script);
    }

    @Override
    public void loginFailed(String... params) {
        String script = "javascript:onLoginFailure('" + params[0] + "')";
        executeJavaScript(script);
    }

    @Override
    public void logoutSuccess(String... params) {
        new NativeScriptInterface(this, this).js2javaReturnToLogin();
    }

    @Override
    public void logoutFailed(String... params) {
        showToast("账号退出失败！");
    }

    @Override
    public void switchAccountSuccess(String... params) {
        new NativeScriptInterface(this, this).js2javaReturnToLogin();
    }

    @Override
    public void switchAccountFailed(String... params) {
        //暂时不做处理
        String script = "javascript:onSwitchAccountFailed('" + params[0] + "')";
        executeJavaScript(script);
    }

    @Override
    public void paySuccess(String... params) {
        String script = "javascript:onPayResult('" + params[0] + "')";
        executeJavaScript(script);
    }

    @Override
    public void payFailed(String... params) {
        String script = "javascript:onPayResult('" + params[0] + "')";
        executeJavaScript(script);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void logcat(String msg) {
        LogUtil.e(msg);
    }

    @Override
    public void exitSuccess() {
        exitActivity();
    }

    @Override
    public void exitFailed() {
        exitActivity();
    }

    @Override
    public void onNetWorkChangeListener(int status) {
        LogUtil.d(status + "");
        switch (status) {
            case HttpUtil.NETWORK_MOBILE:
            case HttpUtil.NETWORK_WIFI:
                LogUtil.d("网络已经连接");
                if (null != openWifiDialog && openWifiDialog.isShowing()) {
                    openWifiDialog.dismiss();
                    if (Constants.isLoadIndex()) {
                        final String script = "javascript:onStart()";
                        executeJavaScript(script);
                    }
                }
                break;
            case HttpUtil.NETWORK_NONE:
                LogUtil.d("网络已断开");
                if (Constants.isLoadIndex()) {
                    final String script = "javascript:onStop()";
                    executeJavaScript(script);
                }
                showWifiDialog();
                break;
        }
    }

    @Override
    public void confirmExitGame() {
        if (null != exitGameDialog && exitGameDialog.isShowing()) {
            exitGameDialog.dismiss();
        }
        exitActivity();
    }

    @Override
    public void cancelExitGame() {
        if (null != exitGameDialog && exitGameDialog.isShowing()) {
            exitGameDialog.dismiss();
        }
        exitActivity();
    }

    private void executeJavaScript(final String script) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NativeScriptInterface.evaluateJavascript(webView, script, NativeScriptInterface.defaulCallback);
            }
        });
    }
}
