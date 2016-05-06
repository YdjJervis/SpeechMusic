package com.aispeech.aios.music.model;

import android.content.Context;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.config.Configs;
import com.aispeech.aios.music.db.MusicLocalDaoImpl;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music.sort.SortSearchResult;
import com.aispeech.aios.music.util.CloudMusicSearchParser;
import com.aispeech.aios.music.util.UrlEncodingUtil;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Jervis on 2015/10/8.
 */
public class MusicKeywordData {

    private static final String TAG = "AIOS-MusicKeywordData";

    private Context mContext;
    private List<MusicInfo> mCloudList;
    private List<MusicInfo> mLocalList;
    private String mKeyword;

    public MusicKeywordData(Context context) {
        this.mContext = context;
    }

    /**
     * 初始化本地模糊搜索的列表List<MusicInfo>，不可能为null值
     *
     * @param keyword 根据关键字模糊搜索，这个关键字可能是歌手名，也可能是歌曲名
     */
    public void initLocalList(String keyword) {

        mLocalList = new ArrayList<>();

        List<MusicInfo> localMusicList = new MusicLocalDaoImpl(mContext).findAll();
        for (MusicInfo musicInfo : localMusicList) {
            // 首先搜索歌名
            String name = musicInfo.getName();
            if (name.contains(keyword)) {
                mLocalList.add(musicInfo);
                continue;
            }
            // 然后搜索歌手名
            String artist = musicInfo.getArtist();
            if (artist.contains(keyword)) {
                mLocalList.add(musicInfo);
            }
        }
    }

    /**
     * @param keyword 根据关键字模糊搜索，这个关键字可能是歌手名，也可能是歌曲名
     * @return List<MusicInfo>，但不可能为null值
     */
    public void queryCloudByKeyword(final String keyword) {

        mKeyword = keyword;
        initLocalList(keyword);

        mCloudList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = Configs.MusicAPI.URL_SEARCH + "?keyword=" + UrlEncodingUtil.getEncodedString(keyword) + "&m=musics&a=musiclist&order=hit&sort=desc";
        AILog.i(TAG,mKeyword);
        AILog.i(TAG, url);

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                AILog.i(TAG, jsonObject.toString());

                try {
                    CloudMusicSearchParser parser = new CloudMusicSearchParser(jsonObject.toString());
                    List<MusicInfo> cloudMusicList = parser.getMusicList();

                    for (MusicInfo cloud:cloudMusicList){
                        if(cloud.getArtist().contains(keyword)||cloud.getName().contains(keyword)){
                            mCloudList.add(cloud);
                        }
                    }

                    onCloudMusicSuccess();
                } catch (Exception e) {
                    onCloudMusicFailure();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onCloudMusicFailure();
            }
        });
        queue.add(request);
    }

    public interface OnMusicListener {
        void onSearchSuccess(List<MusicInfo> list, String keyword);

        void onSearchFailure(List<MusicInfo> list, String keyword);
    }

    private OnMusicListener mListener;

    public void setListener(OnMusicListener mListener) {
        this.mListener = mListener;
    }

    public void onCloudMusicSuccess() {
        AILog.i(TAG, "onCloudMusicSuccess()");
        Collections.sort(mCloudList, new SortSearchResult(mKeyword));

        combineLocalCloud();

        if (mListener != null) {
            mListener.onSearchSuccess(mCloudList, mKeyword);
        }
    }

    public void onCloudMusicFailure() {
        AILog.i(TAG, "onCloudMusicFailure()");

        if (mListener != null) {
            mListener.onSearchFailure(mLocalList, mKeyword);
        }
    }

    /**
     * 合并本地列表和云端列表，并且过滤掉云端与本地相同的结果
     */
    private void combineLocalCloud() {
        Map<String, MusicInfo> map = new LinkedTreeMap<String, MusicInfo>();

        for (MusicInfo local : mLocalList) {
            map.put(local.getName() + local.getArtist(), local);
        }
        for (MusicInfo local : mCloudList) {
            if(!map.containsKey(local.getName()+local.getArtist())){//如果直接put，后面相同的元素会覆盖已有元素
                map.put(local.getName() + local.getArtist(), local);
            }
        }

        mCloudList.clear();
        mCloudList.addAll(map.values());
        map.clear();
    }

}
