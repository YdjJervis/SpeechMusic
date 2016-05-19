package com.aispeech.aios.music.util;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.pojo.MusicInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CloudMusicSearchParser {

    private static final String TAG = "AIOS-CloudMusicSearchParser";

    private List<MusicInfo> list;

    public CloudMusicSearchParser(String json) {
        init();
        parse(json);
    }

    private void init() {
        list = new ArrayList<>();
    }

    public List<MusicInfo> getMusicList() {
        return list;
    }

    /**
     * @param json 解析网络音乐列表
     */
    private void parse(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray musiclistArray = root.getJSONArray("musiclist");
            AILog.i(TAG,musiclistArray+"");
            if (musiclistArray != null && musiclistArray.length() > 0) {
                list = new ArrayList<>();
                for (int i = 0; i < musiclistArray.length(); i++) {
                    JSONObject object = (JSONObject) musiclistArray.get(i);
                    MusicInfo musicInfo = new MusicInfo();
                    musicInfo.setId(Long.parseLong(object.getString("id")));
                    musicInfo.setName(object.getString("name"));
                    musicInfo.setArtist(object.getString("artist"));
                    musicInfo.setCloudUrl(object.getString("url"));
                    musicInfo.picture = object.getString("picture");
                    musicInfo.lrc = object.getString("lrcdownurl");
                    musicInfo.setCloudMusic(true);
                    list.add(musicInfo);
                }
            }
        } catch (Exception e) {
            AILog.i(TAG,e.toString());
        }
    }

}
