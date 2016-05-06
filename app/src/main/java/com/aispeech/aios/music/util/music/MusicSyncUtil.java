package com.aispeech.aios.music.util.music;

import android.content.Context;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.client.AIOSMusicDataNode;
import com.aispeech.aios.music.config.Configs;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music.util.BroadCastUtil;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by dongjie.yao on 2015/12/30.
 */
public class MusicSyncUtil {

    private static final String TAG = "AIOS-MusicSeachService";

    /**
     * 把音乐数据发送给内核进行学习，这样在没有网络的时候也能
     * 语音搜索听歌曲了。
     *
     * @param json 内核需要的音乐数据格式
     */
    public static void syncToKernel(String json) {
        AIOSMusicDataNode.getInstance().postData(json);
    }

    /**
     * 把音乐数据发送给内核进行学习，这样在没有网络的时候也能
     * 语音搜索听歌曲了。
     *
     * @param musicInfoList 内核需要的音乐数据格式
     */
    public static void syncToKernel(List<MusicInfo> musicInfoList) {
        if (musicInfoList != null) {
            for (MusicInfo info : musicInfoList) {
                info.setMatchType(null);
            }
        }
        final String musicListJson = new Gson().toJson(musicInfoList);


        AILog.i(TAG, "发给Adapter" + musicListJson);

        String kenelNeededJson = new MusicRecogParser().parseKernelNeeded(musicListJson);
        AILog.i(TAG, "同步音乐给内核：" + kenelNeededJson);
        syncToKernel(kenelNeededJson);
    }

    public static void asyncMusic(Context context) {
        AILog.i(TAG, "同步音乐");
        BroadCastUtil.getInstance(context).send("aios.intent.action.LOCAL_MUSIC_SCAN","aios.intent.extra.TEXT", Configs.getScanPath());
    }
}
