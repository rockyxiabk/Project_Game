package com.bzy.game.ipresenter;

/**
 * Description : com.bzy.game.ipresenter
 *
 * @author : rocky
 * @Create Time : 2018/12/8 3:14 PM
 * @Modified Time : 2018/12/8 3:14 PM
 */
public interface ISplashPresenter {
    void setVersionInfo(String resourceIP, String gameVersion, int versionCode);

    void loadVersionError(String msg);
}
