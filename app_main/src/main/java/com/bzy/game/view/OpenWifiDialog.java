package com.bzy.game.view;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.bzy.game.R;

/**
 * Description : com.bzy.game.view
 *
 * @author : rocky
 * @Create Time : 2018/12/11 9:47 AM
 * @Modified Time : 2018/12/11 9:47 AM
 */
public class OpenWifiDialog extends BaseDialog {

    private final OpenWifiListener listener;
    private Button btnExit;
    private Button btnOpenWifi;

    public OpenWifiDialog(Context context, boolean cancelable, boolean cancelableOutside, OpenWifiListener listener) {
        super(context, cancelable, cancelableOutside);
        this.listener = listener;
    }

    @Override
    public int getLayout() {
        return R.layout.dialog_open_wifi;
    }

    @Override
    protected void initView() {
        btnExit = ((Button) findViewById(R.id.btn_exit_game));
        btnOpenWifi = ((Button) findViewById(R.id.btn_open_wifi));
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancelExitGame();
            }
        });
        btnOpenWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWifiSetting();
                dismiss();
            }
        });
    }

    private void openWifiSetting() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public interface OpenWifiListener {
        void cancelExitGame();
    }
}
