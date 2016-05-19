package com.aispeech.aios.music.config;

import android.media.AudioManager;
import android.os.Environment;

import com.aispeech.aios.music.pojo.MusicInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jervis on 2015/9/14.
 */
public class Configs {

    public static final class MusicAPI {
        /**
         * 获取网络音乐的URL
         */
        public static final String URL_SEARCH = "http://www.dorylist.com/api.php";
    }

    public static final class Channel {

        public static final int SDS_AUDIO_CHANNEL = AudioManager.STREAM_SYSTEM;

        public static final int NAVI_AUDIO_CHANNEL = AudioManager.STREAM_MUSIC;

        public static final int MUSIC_AUDIO_CHANNEL = AudioManager.STREAM_MUSIC;

        public static final int CALL_AUDIO_CHANNEL = AudioManager.STREAM_ALARM;
    }


    public static String getScanPath() {

        JSONArray pathArr = new JSONArray();
        JSONObject pathObj = new JSONObject();

        String sdDir;

        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);  //判断sd卡是否存在

        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();//获取跟目录
            try {
                pathObj.put("folder", sdDir + File.separator + "Music");
                pathArr.put(pathObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return pathArr.toString();
    }

    public static final String SD_CARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    public final static String MUSIC_CACHE_PATH = SD_CARD_DIR + "/AIOS-MUSIC/";

    public static boolean isUIExit = false;
    public static List<MusicInfo> mMusicInfoList = new ArrayList<>();
    public static int musicPlayingPosition = 0;
}
