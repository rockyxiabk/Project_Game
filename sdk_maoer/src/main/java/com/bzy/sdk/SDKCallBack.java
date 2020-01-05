package com.bzy.sdk;

/**
 * Description : com.bzy.sdk
 *
 * @author : rocky
 * @Create Time : 2018/12/7 3:56 PM
 * @Modified Time : 2018/12/7 3:56 PM
 */
public interface SDKCallBack {
    void loginSuccess(String... params);

    void loginFailed(String... params);

    void logoutSuccess(String... params);

    void logoutFailed(String... params);

    void switchAccountSuccess(String... params);

    void switchAccountFailed(String... params);

    void paySuccess(String... params);

    void payFailed(String... params);

    void showToast(String msg);

    void logcat(String msg);

    void exitSuccess();

    void exitFailed();
}
