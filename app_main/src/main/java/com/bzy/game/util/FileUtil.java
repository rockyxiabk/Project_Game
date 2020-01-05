package com.bzy.game.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.bzy.game.GameApplication;
import com.bzy.game.version.VersionManager;
import com.bzy.view.BuildConfig;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Description : com.bzy.game.util
 *
 * @author : rocky
 * @Create Time : 2018/12/12 8:39 PM
 * @Modified Time : 2018/12/12 8:39 PM
 */
public class FileUtil {

    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (isFileExist(folderName)) {
            return false;
        }
        File folder = new File(folderName);
        LogUtil.d("makeDirs:" + folder.exists() + " path:" + folder.getPath());
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }

    public static String getFolderName(String filePath) {
        if (isFileExist(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        filePath = (filePosi == -1) ? filePath : filePath.substring(0, filePosi + 1);

        int filePosi2 = filePath.lastIndexOf("/");
        filePath = (filePosi2 == -1) ? filePath : filePath.substring(0, filePosi2 + 1);
        return filePath;
    }

    /**
     * @param pathShort map.txt
     * @return byte[]
     */
    public static byte[] getFileToByte(String pathShort) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String filePath = VersionManager.getResourceRootPath() + pathShort;
        if (isFileExist(filePath)) {
            //在sd卡里。
            try {
                File file = new File(filePath);
                FileInputStream fis = new FileInputStream(file);
                byte[] buf = new byte[1024 * 1024];
                int length;
                while ((length = fis.read(buf)) != -1) {
                    bos.write(buf, 0, length);
                }
                fis.close();
            } catch (Exception e) {
                LogUtil.e(e.toString());
            }
        } else {
            //在assets里
            AssetManager manager = GameApplication.getInstance().getAssets();
            byte[] buf = new byte[1024 * 1024];
            int length;
            try {
                InputStream is = manager.open("" + pathShort);
                while ((length = is.read(buf)) != -1) {
                    bos.write(buf, 0, length);
                }
                is.close();
            } catch (Exception e) {
                LogUtil.e(e.toString());
            }
        }
        return bos.toByteArray();
    }

    /**
     * @param filePath
     * @param dataStr
     * @return 文件是否写入成功
     */
    public static boolean writeFile(String filePath, String dataStr) {
        File file = new File(filePath);
        byte[] data = dataStr.getBytes();
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, false);
            o.write(data, 0, data.length);
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            LogUtil.e("FileNotFoundException occurred" + e.toString());
            CrashReport.postCatchedException(e);
            return false;
        } catch (IOException e) {
            LogUtil.e("IOException occurred." + e.toString());
            CrashReport.postCatchedException(e);
            return false;
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                LogUtil.e("IOException occurred." + e.toString());
                CrashReport.postCatchedException(e);
            }
        }
    }

    /**
     * 从assets目录中复制整个文件夹内容
     *
     * @param context Context context
     * @param oldPath String  原文件路径  如：/aa
     * @param newPath String  复制后路径  如：xx:/bb/cc
     */
    public static boolean copyFileFromAssets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {
                //如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFileFromAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {
                //如果是文件
                InputStream is = context.getAssets().open(oldPath);
                File file = new File(newPath);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024 * 1024];
                int byteCount ;
                //循环从输入流读取 buffer字节
                while ((byteCount = is.read(buffer)) != -1) {
                    //将读取的输入流写入到输出流
                    fos.write(buffer, 0, byteCount);
                }
                //刷新缓冲区
                fos.flush();
                is.close();
                fos.close();
                LogUtil.d("copied path：" + newPath);
                return true;
            }
        } catch (Exception e) {
            LogUtil.e(e.toString());
            CrashReport.postCatchedException(e);
            return false;
        }
        return false;
    }
}
