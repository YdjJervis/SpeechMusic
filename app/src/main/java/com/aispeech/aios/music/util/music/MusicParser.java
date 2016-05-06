package com.aispeech.aios.music.util.music;

import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-01-19
 * @copyright aispeech.com
 */
public class MusicParser {

    private final String TAG = "AIOS-MusicParser";

    /**
     * 把广播接收到的数据转成播发需要的
     *
     * @param musicListJson AIOS内核结构
     * @return
     */
    public String parseMusicJsonArray(String musicListJson) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(musicListJson);
        } catch (JSONException e) {
            AILog.e(TAG, e);
        }

        ArrayList<MusicInfo> musicList = new ArrayList<MusicInfo>();

        if (jsonArray != null) {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    MusicInfo musicInfo = new MusicInfo();
                    musicInfo.setId(Long.parseLong(item.optString("id")));
                    musicInfo.setName(new String(item.optString("title").getBytes(), "UTF-8"));
                    musicInfo.setArtist(new String(item.optString("artist").getBytes(), "UTF-8"));
                    musicInfo.setDuration(Long.parseLong(item.optString("duration")));
                    musicInfo.setCloudMusic(TextUtils.isEmpty(item.optString("url")) ? false :
                            true);
                    musicInfo.setCloudUrl(item.optString("url"));
                    musicInfo.setSize(item.optString("size"));
                    musicList.add(musicInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return new Gson().toJson(musicList);
    }
}
