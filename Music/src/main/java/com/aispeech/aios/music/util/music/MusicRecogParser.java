package com.aispeech.aios.music.util.music;


import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music.pojo.MusicRecog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * 把通过广播搜索的音乐列表转换成内核语意理解需要的字段类型
 * Created by dongjie.yao on 2015/12/28.
 */
public class MusicRecogParser {

    private static final String TAG = "AIOS-Adapter-MusicRecogParser";

    public String parseKernelNeeded(String musicInfoList){
        AILog.i(TAG,"转换之前的列表："+musicInfoList);
        List<MusicInfo> musicInfos = new Gson().fromJson(musicInfoList,new TypeToken<List<MusicInfo>>(){}.getType());

        MusicRecog musicRecog = new MusicRecog();
        List<MusicRecog.Song> songs = new ArrayList<MusicRecog.Song>();
        for (MusicInfo musicInfo : musicInfos) {
            MusicRecog.Song recog = musicRecog.new Song();
            recog.setId(musicInfo.getId());
            recog.setArtist(musicInfo.getArtist());
            recog.setTitle(musicInfo.getName());
            songs.add(recog);
        }
        musicRecog.setSongs(songs);

        String result = new Gson().toJson(musicRecog);
        AILog.i(TAG,"转换之后的列表："+result);
        return result;

    }
}
