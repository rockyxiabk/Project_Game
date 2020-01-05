package com.bzy.game.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.bzy.game.BuildConfig;

public class PreferencesUtil {
    private static final String PLAY_POSITION = "play_position";
    private static final String IS_FIRST_START = "is_first_start";
    private static final String IS_COPY = "is_copy";
    private static final String GAME_INDEX_IP = "index";
    private static final String GAME_VERSIONNAME = "game_version_name";
    private static final String GAME_VERSIONCODE = "game_version_code";
    private static final String GAME_OLD_VERSION = "game_old_version";
    private static final String GAME_UPDATE_SUSPEND = "game_update_suspend";

    private static Context sContext;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static int getPlayPosition() {
        return getInt(PLAY_POSITION, 0);
    }

    public static void savePlayPosition(int position) {
        saveInt(PLAY_POSITION, position);
    }

    public static boolean getIsCopy() {
        return getBoolean(IS_COPY, false);
    }

    public static void setIsCopy(boolean value) {
        saveBoolean(IS_COPY, value);
    }

    public static boolean getIsFirstStart() {
        return getBoolean(IS_FIRST_START, true);
    }

    public static void setIsFirstStart(boolean value) {
        saveBoolean(IS_FIRST_START, value);
    }

    public static String getIndex() {
        return getString(GAME_INDEX_IP, "");
    }

    public static void setGameIndexIp(String indexIp) {
        saveString(GAME_INDEX_IP, indexIp);
    }

    public static String getGameOldVersion() {
        return getString(GAME_OLD_VERSION, "1.0.0");
    }

    public static String getGameVersionName() {
        return getString(GAME_VERSIONNAME, com.bzy.view.BuildConfig.GAMEVERSION);
    }

    public static void setGameOldVersion(String gameVersion) {
        saveString(GAME_OLD_VERSION, gameVersion);
    }

    public static void setGameVersionName(String gameVersion) {
        setGameOldVersion(getGameVersionName());
        saveString(GAME_VERSIONNAME, gameVersion);
    }

    public static int getGameVersionCode() {
        return getInt(GAME_VERSIONCODE, com.bzy.view.BuildConfig.GAMEVERSIONCODE);
    }

    public static void setGameVersionCode(int gameVersionCode) {
        saveInt(GAME_VERSIONCODE, gameVersionCode);
    }

    public static boolean getGameUpdateSuspend() {
        return getBoolean(GAME_UPDATE_SUSPEND, false);
    }

    public static void setGameUpdateSuspend(boolean value) {
        saveBoolean(GAME_UPDATE_SUSPEND, value);
    }

    private static boolean getBoolean(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    private static void saveBoolean(String key, boolean value) {
        getPreferences().edit().putBoolean(key, value).apply();
    }

    private static int getInt(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    private static void saveInt(String key, int value) {
        getPreferences().edit().putInt(key, value).apply();
    }

    private static long getLong(String key, long defValue) {
        return getPreferences().getLong(key, defValue);
    }

    private static void saveLong(String key, long value) {
        getPreferences().edit().putLong(key, value).apply();
    }

    private static String getString(String key, @Nullable String defValue) {
        return getPreferences().getString(key, defValue);
    }

    private static void saveString(String key, @Nullable String value) {
        getPreferences().edit().putString(key, value).apply();
    }

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(sContext);
    }
}
