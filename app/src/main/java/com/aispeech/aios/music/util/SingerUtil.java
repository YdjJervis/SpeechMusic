package com.aispeech.aios.music.util;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jervis on 2015/11/12.
 */
public class SingerUtil {

    private static final Map<String, String> chToEn = new HashMap<String, String>() {
        {
            put("林肯公园", "Linkin Park");
            put("艾薇儿", "Avril Lavigne");
            put("席琳狄翁", "Celine Dion");
            put("麦当娜", "Madonna");
            put("辣妹组合", "Wannabe");
        }

    };

    /**
     * 根据外国歌手中文名返回外国歌手英文名
     *
     * @param chName 外国歌手的中文名
     * @return 英文名
     */
    public static String getName(String chName) {
        String enName = chToEn.get(chName);
        if (TextUtils.isEmpty(enName)) {
            enName = chName;
        }
        return enName;
    }

    public static boolean existEnglishName(String chName) {
        return chToEn.get(chName) != null;
    }
}
