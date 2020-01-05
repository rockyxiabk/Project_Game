package com.bzy.game.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.bzy.game.http.HttpCallback;
import com.bzy.game.http.HttpMethod;
import com.bzy.game.http.Request;
import com.bzy.game.http.Response;
import com.bzy.game.ipresenter.ISplashPresenter;
import com.bzy.game.util.LogUtil;
import com.bzy.game.util.PreferencesUtil;
import com.bzy.game.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description : com.bzy.game
 *
 * @author : rocky
 * @Create Time : 2018/12/8 3:13 PM
 * @Modified Time : 2018/12/8 3:13 PM
 */
public class SplashPresenter {

    private final Context context;
    private final ISplashPresenter iSplashPresenter;

    public SplashPresenter(Context context, ISplashPresenter iSplashPresenter) {
        this.context = context;
        this.iSplashPresenter = iSplashPresenter;
    }

    public void getGameVersionInfo() {
        Request.newRequest(new Request.Builder()
                .url(BuildConfig.SERVIVEIP + "platManager/queryVersion")
                .method(HttpMethod.GET)
                .params("plat", com.bzy.view.BuildConfig.CHANNEL)
                .params("versionCode", String.valueOf(PreferencesUtil.getGameVersionCode()))
                .params("versionName", PreferencesUtil.getGameVersionName()), new HttpCallback() {
            @Override
            public void onResponse(Response response) {
                String body = response.getBody();
                if (!TextUtils.isEmpty(body)) {
                    JSONObject jsonObject = null;//json解析
                    try {
                        jsonObject = new JSONObject(body);
                        String resUrl = jsonObject.getString("resUrl");
                        String resVersion = jsonObject.getString("resVersion");
                        boolean resVersionCode = jsonObject.has("resVersionCode");
                        int versionCode = PreferencesUtil.getGameVersionCode();
                        if (resVersionCode) {
                            versionCode = jsonObject.getInt("resVersionCode");
                        }
                        iSplashPresenter.setVersionInfo(resUrl, resVersion, versionCode);
                    } catch (JSONException e) {
                        LogUtil.e(e.toString());
                        iSplashPresenter.loadVersionError("error");
                    }
                } else {
                    iSplashPresenter.loadVersionError("empty");
                }
            }

            /**
             * 999999
             * 99.99.99
             *
             *
             * 0.11.3.2.9747
             * @param msg
             * @param e
             */

            @Override
            public void onError(String msg, Exception e) {
                iSplashPresenter.loadVersionError(msg);
            }
        }).executeAsync();
    }
}
