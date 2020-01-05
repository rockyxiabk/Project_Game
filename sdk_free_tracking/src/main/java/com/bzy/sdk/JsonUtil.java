package com.bzy.sdk;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class JsonUtil {

    public static String getMapToJsonString(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            Log.e("JsonUtil", e.toString());
        }
        return jsonObject.toString();
    }

}
