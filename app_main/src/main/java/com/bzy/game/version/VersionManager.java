package com.bzy.game.version;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.bzy.game.Constants;
import com.bzy.game.util.FileUtil;
import com.bzy.game.util.LogUtil;
import com.bzy.view.BuildConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Description : com.bzy.game.version
 *
 * @author : rocky
 * @Create Time : 2018/12/12 2:42 PM
 * @Modified Time : 2018/12/12 2:42 PM
 */
public class VersionManager {

    private Context context;
    public static String resourceRootPath;
    private HashMap<String, String> mapTxt;
    private HashMap<String, Integer> mapSizeTxt;
    private HashMap<String, String> versionMapTxt;
    private Map<String, Integer> updateMap;
    private boolean isCompared;

    private VersionManager() {
    }

    private static class SingletonHolder {
        private static VersionManager instance = new VersionManager();
    }

    public static VersionManager get() {
        return VersionManager.SingletonHolder.instance;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        resourceRootPath = context.getFilesDir().getPath() + File.separator;
        LogUtil.d(resourceRootPath);
    }

    public static String getResourceRootPath() {
        return resourceRootPath;
    }

    public Map<String, Integer> getUpdateMap() {
        return updateMap;
    }

    public void initMap() {
        mapTxt = new HashMap<>();
        mapSizeTxt = new HashMap<>();
        versionMapTxt = new HashMap<>();
        updateMap = new HashMap<>();
        //初始化 远程最新版本信息
        String mapStr = new String(FileUtil.getFileToByte(Constants.MAP_TXT));
        readMapTxtVersion(mapStr, mapTxt, mapSizeTxt);
        //初始化 本地最新版本信息
        String versionMapTxtStr = new String(FileUtil.getFileToByte(Constants.VERSION_MAP_TXT));
        readVersionMapTxt(versionMapTxtStr, versionMapTxt);

        compareVersion(mapTxt, versionMapTxt);
    }

    private void readVersionMapTxt(String data, HashMap<String, String> map) {
        if (TextUtils.isEmpty(data)) {
            LogUtil.d("versionMap.txt 为空");
            return;
        }
        String[] list = data.split("\\n");
        String[] versionInfo;
        for (int i = 0; i < list.length; i++) {
            //预判断得到的版本信息是否为空以避免调用空对象方法
            if (list[i] != null && list[i] != "") {
                versionInfo = list[i].split("\t");
                //预判断得到的版本信息是否符合规则以避免超出数组范围
                if (versionInfo.length >= 2) {
//                    LogUtil.d("path:" + versionInfo[0] + "  md5:" + versionInfo[1]);
                    map.put(versionInfo[0], versionInfo[1]);
                }
            }
        }
    }

    private void readMapTxtVersion(String data, HashMap<String, String> map, HashMap<String, Integer> mapSizeTxt) {
        if (TextUtils.isEmpty(data)) {
            LogUtil.d("map.txt 为空");
            return;
        }
        String[] list = data.split("\\n");
        String[] versionInfo;
        for (int i = 0; i < list.length; i++) {
            //预判断得到的版本信息是否为空以避免调用空对象方法
            if (list[i] != null && list[i] != "") {
                versionInfo = list[i].split("\t");
                //预判断得到的版本信息是否符合规则以避免超出数组范围
                if (versionInfo.length > 2) {
//                    LogUtil.d("path:" + versionInfo[0] + "  md5:" + versionInfo[1] + "  size:" + versionInfo[2]);
                    map.put(versionInfo[0], versionInfo[1]);
                    mapSizeTxt.put(versionInfo[0], Integer.valueOf(versionInfo[2]));
                }
            }
        }
    }

    public byte[] getLocalRes(String url) {
        return FileUtil.getFileToByte(url);
    }

    private void compareVersion(HashMap<String, String> mapTxt, HashMap<String, String> versionMapTxt) {
        if (null == mapTxt && mapTxt.size() <= 0) {
            return;
        }
        for (Map.Entry<String, String> mapSet : mapTxt.entrySet()) {
            String key = mapSet.getKey();
            if (versionMapTxt.containsKey(key)) {
                if (!mapSet.getValue().equalsIgnoreCase(versionMapTxt.get(key))) {
                    if (!mapSet.getKey().equals(Constants.MAP_TXT)) {
                        updateMap.put(key, mapSizeTxt.get(key));
                    }
                }
            } else {
                updateMap.put(key, mapSizeTxt.get(key));
            }
        }
        isCompared = true;
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_COMPARE_SUCCESS);
        context.sendBroadcast(intent);
    }

    public void updateVersionMap(String mUrl) {
        if (isCompared) {
            if (mapTxt.containsKey(mUrl)) {
                versionMapTxt.put(mUrl, mapTxt.get(mUrl));
            } else {
                versionMapTxt.put(mUrl, mapTxt.get(mUrl));
            }
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> mapSet : versionMapTxt.entrySet()) {
                sb.append(mapSet.getKey() + "\t" + mapSet.getValue() + "\n");
            }
            FileUtil.writeFile(VersionManager.getResourceRootPath() + Constants.VERSION_MAP_TXT, sb.toString());
        }
    }
}
