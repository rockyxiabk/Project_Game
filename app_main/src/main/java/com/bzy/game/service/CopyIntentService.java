package com.bzy.game.service;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;

import com.bzy.game.Constants;
import com.bzy.game.http.DownloadAsyncTask;
import com.bzy.game.util.FileUtil;
import com.bzy.game.util.LogUtil;
import com.bzy.game.util.PreferencesUtil;
import com.bzy.game.version.VersionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class CopyIntentService extends IntentService {
    private static final String ACTION_COPY = "com.bzy.game.service.action.copy.whole";
    private static final String ACTION_BAZ = "com.bzy.game.service.action.BAZ";
    private static final String ACTION_START = "com.bzy.game.service.action.START";

    private static final String EXTRA_PARAM1 = "com.bzy.game.service.extra.PARAM1";

    public CopyIntentService() {
        super("CopyIntentService");
    }

    public static void startActionCopyWhole(Context context) {
        Intent intent = new Intent(context, CopyIntentService.class);
        intent.setAction(ACTION_COPY);
        context.startService(intent);
    }

    public static void startActionDownload(Context context, String param1) {
        Intent intent = new Intent(context, CopyIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);// music/1000.mp3
        context.startService(intent);
    }

    public static void startActionStart(Context context) {
        Intent intent = new Intent(context, CopyIntentService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_COPY.equals(action)) {
                handleActionCopyWhole();
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                handleActionBaz(param1);
            } else if (ACTION_START.equals(action)) {
                handleActionStart();
            }
        }
    }

    private void handleActionStart() {
        Map<String, Integer> updateMap = VersionManager.get().getUpdateMap();
        for (Map.Entry<String, Integer> entry : updateMap.entrySet()) {
            DownloadFileService.startService(this, entry.getKey());
        }
    }

    private void handleActionCopyWhole() {
        String[] paths = {Constants.MAP_TXT, Constants.MUSIC_FILE};
        for (int i = 0; i < paths.length; i++) {
            LogUtil.d("will copy:" + paths[i]);
            FileUtil.copyFileFromAssets(this, "" + paths[i], VersionManager.getResourceRootPath() + paths[i]);
        }
        LogUtil.d("will copy versionMap.txt");
        boolean fileCopyState = FileUtil.copyFileFromAssets(this, Constants.MAP_TXT, VersionManager.getResourceRootPath() + Constants.VERSION_MAP_TXT);
        LogUtil.d("versionMap.txt copy state:" + fileCopyState);
        if (fileCopyState) {
            sendBroadCopySuccess();
        } else {
            sendBroadCopyFailed();
        }

    }

    private void handleActionBaz(String urlPath) {
        LogUtil.d(urlPath);
        new DownloadAsyncTask().execute(urlPath);
    }

    private void sendBroadCopySuccess() {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_COPY_SUCCESS);
        sendBroadcast(intent);
    }

    private void sendBroadCopyFailed() {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_COPY_FAILED);
        sendBroadcast(intent);
    }
}
