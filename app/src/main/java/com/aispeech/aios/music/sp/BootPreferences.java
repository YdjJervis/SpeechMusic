package com.aispeech.aios.music.sp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jervis on 2015/12/16.
 */
public class BootPreferences extends BasePreference {

    private static final String TAG = "BootPreferences";

    private static final String BOOT_FIRST_TIME = "BOOT_FIRST_TIME";

    private static final String NAME = "BOOT_PARAMS";

    public BootPreferences(Context context) {
        super(context);
    }

    public void changToBootedBefore(){
        SharedPreferences.Editor editor = getEditor(NAME);
        editor.putBoolean(BOOT_FIRST_TIME,false);
    }

    public boolean isFirstBoot(){
        SharedPreferences preferences = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(BOOT_FIRST_TIME, true);
    }
}
