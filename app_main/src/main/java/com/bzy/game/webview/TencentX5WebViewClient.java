package com.bzy.game.webview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;

import com.bzy.game.util.LogUtil;
import com.bzy.game.version.VersionManager;
import com.bzy.view.BuildConfig;
import com.tencent.smtt.export.external.interfaces.ClientCertRequest;
import com.tencent.smtt.export.external.interfaces.HttpAuthHandler;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

public class TencentX5WebViewClient extends WebViewClient {

    private final WebViewClientCallBack callBack;

    public TencentX5WebViewClient(WebViewClientCallBack callBack) {
        super();
        this.callBack = callBack;
    }

    @Override
    public void onLoadResource(WebView webView, String url) {
        super.onLoadResource(webView, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        return super.shouldOverrideUrlLoading(webView, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
        return super.shouldOverrideUrlLoading(webView, webResourceRequest);
    }

    @Override
    public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
        super.onPageStarted(webView, url, bitmap);
        LogUtil.d("onPageStarted:" + url);
    }

    @Override
    public void onPageFinished(WebView webView, String url) {
        super.onPageFinished(webView, url);
        LogUtil.d("onPageFinished:" + url);
        callBack.loadMain();
    }

    @Override
    public void onReceivedError(WebView webView, int i, String s, String s1) {
        super.onReceivedError(webView, i, s, s1);
        LogUtil.e("i:" + i + "----s:" + s + "----s1:" + s1);
    }

    @Override
    public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
        super.onReceivedError(webView, webResourceRequest, webResourceError);
        LogUtil.e(webView.getUrl());
    }

    @Override
    public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
        super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
        LogUtil.e(webView.getUrl());
    }

    /**
     * 从API 11开始引入，API 21弃用
     *
     * @param webView
     * @param url
     * @return
     */
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView webView, String url) {
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        if (scheme.equals("http")) {
            String urlPath = uri.getPath();
            urlPath = urlPath.indexOf("/") == 0 ? urlPath.replaceFirst("/", "") : urlPath;
            byte[] data = null;
            //是否是整包
            if (BuildConfig.ISWHOLEPKG) {
                data = VersionManager.get().getLocalRes(urlPath);
                WebResourceResponse response = null;
                //检查 文件是否存在
                if (data == null || data.length == 0) {
                    LogUtil.e("加载net资源" + urlPath);
                    return super.shouldInterceptRequest(webView, url);
                } else {
                    LogUtil.e("加载本地资源" + url);
                    ByteArrayInputStream byteSteam = new ByteArrayInputStream(data);
                    response = new WebResourceResponse("text/html", "UTF-8", byteSteam);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Access-Control-Allow-Origin", "*");
                    map.put("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    map.put("Access-Control-Allow-Headers", "Content-Type");
                    response.setResponseHeaders(map);
                }
                return response;
            }
        }
        return super.shouldInterceptRequest(webView, url);
    }

    /**
     * 从API 21开始引入
     *
     * @param view
     * @param request
     * @return
     */
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        Uri uri = request.getUrl();
        String scheme = uri.getScheme();
        if (request.getMethod().equals("POST")) {
            LogUtil.e("POST:" + request.getUrl().toString());
            return super.shouldInterceptRequest(view, request);
        }
        if (scheme.equals("http")) {
            String urlPath = uri.getPath();
            urlPath = urlPath.indexOf("/") == 0 ? urlPath.replaceFirst("/", "") : urlPath;
            byte[] data = null;
            //是否是整包
            if (BuildConfig.ISWHOLEPKG) {
                data = VersionManager.get().getLocalRes(urlPath);
                WebResourceResponse response = null;
                //检查 文件是否存在
                if (data == null || data.length == 0) {
                    LogUtil.e("加载net资源" + urlPath);
                    return super.shouldInterceptRequest(view, request);
                } else {
                    LogUtil.e("加载本地资源" + urlPath);
                    ByteArrayInputStream byteSteam = new ByteArrayInputStream(data);
                    response = new WebResourceResponse("text/html", "UTF-8", byteSteam);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Access-Control-Allow-Origin", "*");
                    map.put("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    map.put("Access-Control-Allow-Headers", "Content-Type");
                    response.setResponseHeaders(map);
                }
                return response;
            }
        }
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest, Bundle bundle) {
        return super.shouldInterceptRequest(webView, webResourceRequest, bundle);
    }

    @Override
    public void doUpdateVisitedHistory(WebView webView, String s, boolean b) {
        super.doUpdateVisitedHistory(webView, s, b);
    }

    @Override
    public void onFormResubmission(WebView webView, Message message, Message message1) {
        super.onFormResubmission(webView, message, message1);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView webView, HttpAuthHandler httpAuthHandler, String s, String s1) {
        super.onReceivedHttpAuthRequest(webView, httpAuthHandler, s, s1);
    }

    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
        super.onReceivedSslError(webView, sslErrorHandler, sslError);
    }

    @Override
    public void onReceivedClientCertRequest(WebView webView, ClientCertRequest clientCertRequest) {
        super.onReceivedClientCertRequest(webView, clientCertRequest);
    }

    @Override
    public void onScaleChanged(WebView webView, float v, float v1) {
        super.onScaleChanged(webView, v, v1);
    }

    @Override
    public void onUnhandledKeyEvent(WebView webView, KeyEvent keyEvent) {
        super.onUnhandledKeyEvent(webView, keyEvent);
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView webView, KeyEvent keyEvent) {
        return super.shouldOverrideKeyEvent(webView, keyEvent);
    }

    @Override
    public void onTooManyRedirects(WebView webView, Message message, Message message1) {
        super.onTooManyRedirects(webView, message, message1);
    }

    @Override
    public void onReceivedLoginRequest(WebView webView, String s, String s1, String s2) {
        super.onReceivedLoginRequest(webView, s, s1, s2);
    }

    @Override
    public void onDetectedBlankScreen(String s, int i) {
        super.onDetectedBlankScreen(s, i);
    }
}
