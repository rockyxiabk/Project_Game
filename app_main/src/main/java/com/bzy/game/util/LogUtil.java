package com.bzy.game.util;

import android.util.Log;

import com.bzy.game.BuildConfig;

/**
 * Description : Log工具类
 *
 * @author : Administrator
 * @Create Time : 2018/5/17 0017 16:26
 * @Modified By: Administrator
 * @Modified Time : 2018/5/17 0017 16:26
 */
public class LogUtil {
    private static String className;
    private static String methodName;
    private static int lineNumber;

    private LogUtil() {
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    private static String createLog(String log) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(").append(className).append(":").append(lineNumber).append(")");
        buffer.append(methodName);
        buffer.append("--->");
        buffer.append(log);
        return buffer.toString();
    }

    public static void d(String content) {
        if (!BuildConfig.DEBUG) return;
        getMethodNames(new Throwable().getStackTrace());
        String tag = className;

        Log.d(tag, createLog(content));
    }

    public static void d(String content, Throwable tr) {
        if (!BuildConfig.DEBUG) return;
        getMethodNames(new Throwable().getStackTrace());
        String tag = className;

        Log.d(tag, createLog(content), tr);
    }

    public static void e(String content) {
        if (!BuildConfig.DEBUG) return;
        getMethodNames(new Throwable().getStackTrace());
        String tag = className;

        Log.e(tag, createLog(content));
    }

    public static void e(String content, Throwable tr) {
        if (!BuildConfig.DEBUG) return;
        getMethodNames(new Throwable().getStackTrace());
        String tag = className;

        Log.e(tag, createLog(content), tr);
    }

    public static void i(String content) {
        if (!BuildConfig.DEBUG) return;
        getMethodNames(new Throwable().getStackTrace());
        String tag = className;

        Log.i(tag, createLog(content));
    }

    public static void i(String content, Throwable tr) {
        if (!BuildConfig.DEBUG) return;
        getMethodNames(new Throwable().getStackTrace());
        String tag = className;

        Log.i(tag, createLog(content), tr);
    }

    public static void v(String content) {
        if (!BuildConfig.DEBUG) return;
        getMethodNames(new Throwable().getStackTrace());
        String tag = className;

        Log.v(tag, createLog(content));
    }

    public static void v(String content, Throwable tr) {
        if (!BuildConfig.DEBUG) return;
        getMethodNames(new Throwable().getStackTrace());
        String tag = className;

        Log.v(tag, createLog(content), tr);
    }

    public static void w(String content) {
        if (!BuildConfig.DEBUG) return;
        getMethodNames(new Throwable().getStackTrace());
        String tag = className;

        Log.w(tag, createLog(content));
    }

    public static void w(String content, Throwable tr) {
        if (!BuildConfig.DEBUG) return;
        getMethodNames(new Throwable().getStackTrace());
        String tag = className;

        Log.w(tag, createLog(content), tr);
    }

    public static void w(Throwable tr) {
        if (!BuildConfig.DEBUG) return;
        getMethodNames(new Throwable().getStackTrace());
        String tag = className;

        Log.w(tag, tr);
    }


    public static void wtf(String content) {
        if (!BuildConfig.DEBUG) return;
        getMethodNames(new Throwable().getStackTrace());
        String tag = className;

        Log.wtf(tag, createLog(content));
    }

    public static void wtf(String content, Throwable tr) {
        if (!BuildConfig.DEBUG) return;
        getMethodNames(new Throwable().getStackTrace());
        String tag = className;

        Log.wtf(tag, createLog(content), tr);
    }

    public static void wtf(Throwable tr) {
        if (!BuildConfig.DEBUG) return;
        getMethodNames(new Throwable().getStackTrace());
        String tag = className;

        Log.wtf(tag, tr);
    }
}
