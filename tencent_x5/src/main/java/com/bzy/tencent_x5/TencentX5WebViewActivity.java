package com.bzy.tencent_x5;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.RelativeLayout;

import com.tencent.smtt.sdk.WebView;

/**
 * Description : com.bzy.tencent_x5
 *
 * @author : rocky
 * @Create Time : 2018/12/7 2:17 PM
 * @Modified Time : 2018/12/7 2:17 PM
 */
public abstract class TencentX5WebViewActivity extends Activity {

    private RelativeLayout rootView;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(layoutParams);
        rootView.setGravity(Gravity.CENTER);
        webView = new WebView(this);
        webView.setLayoutParams(layoutParams);


    }

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
}
