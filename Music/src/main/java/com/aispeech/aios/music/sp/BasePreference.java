package com.aispeech.aios.music.sp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jervis on 2015/12/16.
 */
public class BasePreference {

    protected Context mContext;

    public BasePreference(Context context){
        mContext = context;
    }

    protected SharedPreferences.Editor getEditor(String xmlName){
        SharedPreferences preferences = mContext.getSharedPreferences(xmlName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        return edit;
    }
}
