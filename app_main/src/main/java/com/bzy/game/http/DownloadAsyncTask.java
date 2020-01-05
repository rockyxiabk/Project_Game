package com.bzy.game.http;

import android.os.AsyncTask;

import com.bzy.game.util.LogUtil;
import com.bzy.game.util.PreferencesUtil;
import com.bzy.game.version.VersionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Description : com.bzy.game.http
 *
 * @author : rocky
 * @Create Time : 2018/12/13 3:47 PM
 * @Modified Time : 2018/12/13 3:47 PM
 */
public class DownloadAsyncTask extends AsyncTask<String, Integer, String> {

    private int hasRead;

    public DownloadAsyncTask() {
        hasRead = 0;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String urlStrPath = params[0];// music/1000.mp3
            URL url = new URL(PreferencesUtil.getIndex() + urlStrPath);//完整路径
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            int lastIndexOf = urlStrPath.lastIndexOf("/");
            if (lastIndexOf > 0) {
                String downloadFolderName = VersionManager.getResourceRootPath() + urlStrPath.substring(0, lastIndexOf + 1);
                LogUtil.d(downloadFolderName);
                File file = new File(downloadFolderName);
                if (!file.isDirectory()) {
                    file.mkdirs();
                }
            }
            String pathName = VersionManager.getResourceRootPath() + urlStrPath;
            File fileName = new File(pathName);
            if (fileName.exists()) {
                fileName.delete();
            }
            byte[] buff = new byte[4096];
            int length = 0;
            OutputStream outputStream = new FileOutputStream(pathName);
            while ((length = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, length);
                hasRead++;
                publishProgress(hasRead);
            }
            outputStream.close();
            inputStream.close();
            return pathName;
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String path) {
        super.onPostExecute(path);
        LogUtil.d(path);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //更新进度
        LogUtil.d("下载进度:" + values[0]);
    }
}