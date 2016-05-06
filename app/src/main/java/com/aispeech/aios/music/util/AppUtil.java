package com.aispeech.aios.music.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.aispeech.ailog.AILog;

/**
 * Created by hehr on 2015/9/28.
 */
public class AppUtil {

    private static final String TAG = "AIOS-Adapter-APPUtil";

    /**
     * 通过包名检测APP是否安装
     *
     * @param packageName 包名
     * @return true or false
     */
    public static boolean isInstalled(Context context, String packageName) {

        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }

        if (packageInfo == null) {
            AILog.e(TAG,packageName + " 没有安装");
            return false;
        } else {
            AILog.i(TAG,packageName + " 已经安装");
            return true;
        }

    }

    public static boolean isVST(Context context){
        return isInstalled(context,"com.aispeech.launcher");
    }

}
