package com.bzy.game.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.bzy.game.Constants;
import com.bzy.game.util.LogUtil;
import com.bzy.game.util.PreferencesUtil;
import com.bzy.game.version.VersionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadFileService extends Service {
    private static ExecutorService executorSingleService = Executors.newSingleThreadExecutor();

    public DownloadFileService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static void startService(Context context, String url) {//尾部路径，不包含域名地址
        Intent intent = new Intent(context, DownloadFileService.class);
        intent.putExtra("url", url);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            String url = intent.getStringExtra("url");
            LogUtil.d(url);
            DownloadThread downloadThread = new DownloadThread(this, url);
            executorSingleService.execute(downloadThread);
        } else {
            LogUtil.d("intent null");
        }
        return START_NOT_STICKY;
    }

    class DownloadThread extends Thread {

        private final Context context;
        private final String mUrl;// music/1000.mp3
        private int dataLen = 0;//字节数

        public DownloadThread(Context context, String url) {
            this.context = context;
            this.mUrl = url;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(PreferencesUtil.getIndex() + mUrl);//完整路径
                LogUtil.d("url:" + url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(60000);
                urlConnection.setRequestMethod("GET");
                InputStream inputStream = urlConnection.getInputStream();
                int lastIndexOf = mUrl.lastIndexOf("/");
                if (lastIndexOf > 0) {
                    String downloadFolderName = VersionManager.getResourceRootPath() + mUrl.substring(0, lastIndexOf + 1);
                    LogUtil.d(downloadFolderName);
                    File file = new File(downloadFolderName);
                    if (!file.isDirectory()) {
                        file.mkdirs();
                    }
                }
                String pathName = VersionManager.getResourceRootPath() + mUrl;
                File fileName = new File(pathName);
                if (fileName.exists()) {
                    fileName.delete();
                }
                byte[] buff = new byte[4096];
                int length = 0;
                dataLen = 0;
                OutputStream outputStream = new FileOutputStream(pathName);
                while ((length = inputStream.read(buff)) != -1) {
                    dataLen += length;
                    outputStream.write(buff, 0, length);
                }
                outputStream.close();
                inputStream.close();
                LogUtil.d(pathName);
                VersionManager.get().updateVersionMap(mUrl);
                sendBroadCastSuccess(mUrl, dataLen);
            } catch (IOException e) {
                LogUtil.e(e.toString());
                sendBroadCastFailed(mUrl);
            }
        }

        private void sendBroadCastSuccess(String mUrl, int length) {
            Intent intent = new Intent();
            intent.putExtra("url", mUrl);
            intent.putExtra("dataLen", length);
            intent.setAction(Constants.ACTION_DOWNLOAD_SUCCESS);
            context.sendBroadcast(intent);
        }

        private void sendBroadCastFailed(String mUrl) {
            Intent intent = new Intent();
            intent.putExtra("url", mUrl);
            intent.setAction(Constants.ACTION_DOWNLOAD_FAILED);
            context.sendBroadcast(intent);
        }
    }
}
