package com.aispeech.aios.music.model;

import com.aispeech.aios.music.pojo.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-04-18
 * @copyright aispeech.com
 */
public class RepoMusicData {

    public List<MusicInfo> findAll(){
        List<MusicInfo> list = new ArrayList<>();
        for (int i=0;i<5;++i){
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.setArtist("陈奕迅");
            musicInfo.setName("因为爱情 - "+i);
            list.add(musicInfo);
        }
        return list;
    }
}
