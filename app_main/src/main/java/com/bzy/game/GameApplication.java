package com.bzy.game;

import com.bzy.game.media.MediaPlayerManager;
import com.bzy.game.util.PreferencesUtil;
import com.bzy.game.util.UnCrashThread;
import com.bzy.game.version.VersionManager;
import com.bzy.sdk.BaseApplication;
import com.bzy.tencent_x5.TencentX5;
import com.tencent.bugly.Bugly;

/**
 * Description : com.bzy.game
 *
 * @author : rocky
 * @Create Time : 2018/12/7 2:12 PM
 * @Modified Time : 2018/12/7 2:12 PM
 */
public class GameApplication extends BaseApplication {
    private static GameApplication instance;

    public static GameApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initRestart();
        instance = this;
        initMedia();
        VersionManager.get().init(this);
        Constants.init(this);
        // TODO: 2019/2/20  应用宝渠道的包 集成了x5 需要注释掉自集成的x5 初始化操作
        TencentX5.init(this);
        Bugly.init(getApplicationContext(), "84a8df5d27", false);
    }

    private void initMedia() {
        PreferencesUtil.init(this);
        MediaPlayerManager.get().init(this);
    }

    private void initRestart() {
        UnCrashThread unCrashThread = new UnCrashThread(this);
        Thread.setDefaultUncaughtExceptionHandler(unCrashThread);
    }
}
