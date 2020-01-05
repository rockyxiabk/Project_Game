package com.bzy.game.util;

import android.content.Context;
import android.os.PowerManager;

public class ScreenUtil {
    public static boolean screenIsShow(Context context) {
        PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (manager.isInteractive()) {
                return true;
            }
        } else {
            boolean screenOn = manager.isScreenOn();
            if (screenOn) {
                return true;
            }
        }
        return false;
    }
}
