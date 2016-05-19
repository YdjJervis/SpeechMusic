package com.aispeech.aios.music;

import android.app.Application;
import android.content.Context;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-04-25
 * @copyright aispeech.com
 */
public class AIMusicApp extends Application {

    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
