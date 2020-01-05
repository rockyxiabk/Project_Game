package com.bzy.game.javascript;

import android.webkit.JavascriptInterface;

import com.bzy.game.Constants;
import com.bzy.game.MainActivity;
import com.bzy.game.media.MediaPlayerManager;
import com.bzy.game.service.CopyIntentService;
import com.bzy.game.util.FileUtil;
import com.bzy.game.util.LogUtil;
import com.bzy.game.util.PreferencesUtil;
import com.bzy.game.version.VersionManager;
import com.bzy.game.webview.WebViewClientCallBack;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;


public class NativeScriptInterface {

    private final MainActivity activity;
    private final WebViewClientCallBack callBack;

    public NativeScriptInterface(MainActivity activity, WebViewClientCallBack callBack) {
        this.activity = activity;
        this.callBack = callBack;
    }

    public static ValueCallback<String> defaulCallback = new ValueCallback<String>() {
        @Override
        public void onReceiveValue(String value) {
            LogUtil.e("onReceiveValue: " + value);
        }
    };

    @JavascriptInterface
    public void js2javaOnGameLoaded() {
        LogUtil.d(this.getClass().getSimpleName());
        callBack.startGame();
    }

    @JavascriptInterface
    public void onBackPressed() {
        LogUtil.d(this.getClass().getSimpleName());
        activity.onBackPressed();
    }

    @JavascriptInterface
    public void js2javaOnReLoad() {
        LogUtil.d(this.getClass().getSimpleName());
        Constants.setLoadIndex(false);
        callBack.loadGameIndex();
    }

    @JavascriptInterface
    public void js2javaLog(String info) {
        LogUtil.d(this.getClass().getSimpleName());
        LogUtil.e(info);
    }

    @JavascriptInterface
    public void js2javaReturnToLogin() {
        LogUtil.d(this.getClass().getSimpleName());
        LogUtil.e("返回登录");
        Constants.setAutoLogin(true);
        Constants.setLoadIndex(false);
        callBack.loadGameIndex();
    }

    /**
     * 判断当前音乐文件本地是否存在,有则播放并返回true，没有则返回false
     *
     * @param musicPath 音乐播放地址(例如:music/1000.mp3)
     */
    @JavascriptInterface
    public boolean js2javaPlayMusic(String musicPath) {
        LogUtil.d(musicPath);
        boolean existFile = FileUtil.isFileExist(VersionManager.getResourceRootPath() + musicPath);
        if (existFile) {
            MediaPlayerManager.get().currentMusic = musicPath;
            if (!MediaPlayerManager.get().muted) {
                MediaPlayerManager.get().playLocalMusicPath(musicPath);
            }
        } else {
            MediaPlayerManager.get().currentMusic = PreferencesUtil.getIndex() + musicPath;
            CopyIntentService.startActionDownload(activity, musicPath);
            if (!MediaPlayerManager.get().muted) {
                MediaPlayerManager.get().playNetMusicPath(musicPath);
                LogUtil.d("本地音乐资源是否存在：" + existFile + ", 播放网络地址:" + MediaPlayerManager.get().currentMusic);
                return true;
            }
        }
        LogUtil.d("本地音乐资源是否存在：" + existFile);
        return existFile;
    }

    /**
     * 停止音乐播放
     */
    @JavascriptInterface
    public void js2javaStopMusic() {
        LogUtil.d(this.getClass().getSimpleName());
        MediaPlayerManager.get().stopPlayer();
    }

    /**
     * 静音操作
     */
    @JavascriptInterface
    public void js2javaMusicToSilence(boolean muted) {
        LogUtil.d(this.getClass().getSimpleName());
        LogUtil.d(muted + "");
        MediaPlayerManager.get().muted = muted;
        if (!muted) {
            if (!MediaPlayerManager.get().currentMusic.equals("")) {
                MediaPlayerManager.get().startPlayer();
            }
        } else {
            MediaPlayerManager.get().pausePlayer();
        }

    }

    public static void evaluateJavascript(WebView webView, String script, ValueCallback<String> callback) {
        if (null == webView) {
            return;
        }
        LogUtil.e(script);
        webView.evaluateJavascript(script, callback);
    }
}
