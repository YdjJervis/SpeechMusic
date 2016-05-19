package com.aispeech.aios.music.util;

import com.aispeech.ailog.AILog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Jervis on 2015/9/15.
 */
public class UrlEncodingUtil {

    private static final String TAG = "AIOS-Adapter-UrlEncodingUtil";

    public static String getEncodedString(String url) {
        String encodedStr = null;
        try {
            encodedStr = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            AILog.i("Exception", "编码错误");
            encodedStr = url;
            e.printStackTrace();
        }
        AILog.i(TAG, encodedStr);
        return encodedStr;
    }
}
