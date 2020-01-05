package com.bzy.game.util;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.widget.Toast;

import com.bzy.game.BuildConfig;
import com.bzy.game.Constants;
import com.bzy.game.GameApplication;
import com.bzy.game.http.HttpCallback;
import com.bzy.game.http.HttpMethod;
import com.bzy.game.http.Request;
import com.bzy.game.http.Response;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Description : 处理异常崩溃后重新起动应用
 *
 * @author : rocky
 * @Create Time : 2018/12/12 10:25 AM
 * @Modified By: rocky
 * @Modified Time : 2018/12/12 10:25 AM
 */
public class UnCrashThread implements Thread.UncaughtExceptionHandler {

    private final Thread.UncaughtExceptionHandler mDefaultHandler;
    private GameApplication application;

    public UnCrashThread(GameApplication application) {
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.application = application;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            CrashReport.postCatchedException(ex);
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LogUtil.e(e.toString());
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    /**
     * 自定义错误处理,收集错误信息
     * 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息，否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        uploadCrashInfo(ex);
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(application.getApplicationContext(), "很抱歉,程序出现异常,即将退出", Toast.LENGTH_SHORT).show();
//                Toast.makeText(application.getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        return true;
    }

    /**
     * 上传错误信息到服务器
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String uploadCrashInfo(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        Request.newRequest(new Request.Builder()
                .url(BuildConfig.SERVIVEIP + "dwsgplatservice/log/androidLogs.jsp")
                .method(HttpMethod.POST)
                .params("errors", result)
                .params("plat", com.bzy.view.BuildConfig.CHANNEL)
                .params("device_id", Constants.getImei())
                .params("phone_brand", Constants.getDeviceBrand())
                .params("phone_model", Constants.getDeviceModel())
                .params("network_type", Constants.getNetWorkType())
                .params("system_version", Constants.getDeviceOsVersion())
                .params("sdk", Constants.getDeviceSdkInit() + "")
                .params("package_version", Constants.getVersionCode() + "")
                .params("package_description", Constants.getVersionName())
                .params("package", Constants.getPackageName())
                .params("add_info", ""), new HttpCallback() {
            @Override
            public void onResponse(Response response) {
                LogUtil.e("递交成功:" + response.getBody());
            }

            @Override
            public void onError(String msg, Exception e) {
                LogUtil.e("递交失败:" + msg);
            }
        }).executeAsync();
        return null;
    }
}
