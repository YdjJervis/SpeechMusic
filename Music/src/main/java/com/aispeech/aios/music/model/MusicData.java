package com.aispeech.aios.music.model;

import android.content.Context;
import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aimusic.model.MusicSearchParam;
import com.aispeech.aios.music.config.Configs;
import com.aispeech.aios.music.db.MusicLocalDaoImpl;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music.util.CloudMusicSearchParser;
import com.aispeech.aios.music.util.SingerUtil;
import com.aispeech.aios.music.util.UrlEncodingUtil;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jervis on 2015/9/22.
 */
public class MusicData {

    private static final String TAG = "AIOS-MusicData";

    private List<MusicInfo> mMusicInfoList;

    private List<MusicInfo> mLocalCompleteList;//本地音乐完全匹配的搜索结果
    private List<MusicInfo> mLocalFuzzyList;// 本地音乐模糊匹配的搜索结果

    private Context mContext;
    private String mKeyWord;

    private static final int TIME_OUT = 8 * 1000;//Url设置超时
    private static final int TIME_OUT_TIMES = 1;//只许一次请求，超时了也不再请求

    private MusicSearchParam mParam;

    public MusicData(Context context, MusicSearchParam param) {
        mContext = context;
        mParam = param;
    }

    /**
     * 1：当歌曲为空，歌手不为空；2歌曲不为空，歌手为空：3，都不为空。三种情况
     * 分别返回各自完全匹配加上模糊匹配的结果
     */
    public void queryLocalByTitleArtist() {

        mMusicInfoList = new ArrayList<MusicInfo>();

        initLocalVariable(mParam.getTitle(), mParam.getArtist());
        /* 如果有完全匹配就处理完全匹配 */
        mMusicInfoList.addAll(mLocalCompleteList);
        mMusicInfoList.addAll(mLocalFuzzyList);
        AILog.i(TAG, mMusicInfoList.toString());

        onCloudMusicSuccess();
    }

    /**
     * 1：当歌曲为空，歌手不为空；2歌曲不为空，歌手为空：3，都不为空。三种情况
     * 分别返回各自完全匹配加上模糊匹配的结果
     * @return 本地音乐列表，完全匹配加模糊匹配,可能返回空列表，但不会返回null
     */
    public void queryCloudByTitleArtist() {

        final String musicTitle = mParam.getTitle();
        final String artist = mParam.getArtist();
        AILog.i(TAG,"歌手名："+artist+"  歌曲名："+musicTitle);

        mMusicInfoList = new ArrayList<MusicInfo>();

        RequestQueue queue = Volley.newRequestQueue(mContext);

        String mKeywordEncoded;
        if (TextUtils.isEmpty(musicTitle) && TextUtils.isEmpty(artist)) { //都为空则搜索失败
            onCloudMusicFailure();
            return;
        } else if (TextUtils.isEmpty(musicTitle) && !TextUtils.isEmpty(artist)) { //关键词为歌手

            if (SingerUtil.existEnglishName(artist)) {
                mKeywordEncoded = SingerUtil.getName(artist);
                mKeyWord = mKeywordEncoded;//英文不用编码就能搜索
            } else {
                mKeywordEncoded = UrlEncodingUtil.getEncodedString(artist);
                mKeyWord = artist;
            }
        } else { //关键词为歌曲
            mKeywordEncoded = UrlEncodingUtil.getEncodedString(musicTitle);
            mKeyWord = musicTitle;
        }

        String url = Configs.MusicAPI.URL_SEARCH + "?keyword=" + mKeywordEncoded + "&m=musics&a=musiclist&order=hit&sort=desc";
        AILog.i(TAG, url);

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                AILog.json(TAG, jsonObject.toString());
                try {

                    CloudMusicSearchParser parser = new CloudMusicSearchParser(jsonObject.toString());
                    List<MusicInfo> cloudMusicList = parser.getMusicList();
                    AILog.json(TAG, cloudMusicList + "");

                    if (TextUtils.isEmpty(artist)) { //歌手为空，直接把所有根据歌名搜索的歌曲放入List

                        AILog.i(TAG, "歌手为空");
                        for (MusicInfo mi : cloudMusicList) {//过滤掉搜索出来的，name中不包含关键字的对象
                            if (mi.getName().contains(musicTitle)) {
                                mMusicInfoList.add(mi);
                            }
                        }
                    } else { //歌手不为空
                        AILog.i(TAG, "歌手不为空");
                        for (MusicInfo musicInfo : cloudMusicList) {//根据歌手过滤相同歌名的列表

                            boolean existEnName = SingerUtil.existEnglishName(artist);
                            if ((existEnName && musicInfo.getArtist().equals(SingerUtil.getName(artist))) || (!existEnName && musicInfo.getArtist().equals(artist))) {
                                if (!TextUtils.isEmpty(musicTitle)) {
                                    AILog.i(TAG, "歌名不为空");
                                    if (musicInfo.getName().contains(musicTitle)) {
                                        mMusicInfoList.add(musicInfo);
                                    }
                                } else {
                                    AILog.i(TAG, "歌名为空");
                                    mMusicInfoList.add(musicInfo);
                                }
                            }
                        }
                    }

                    initLocalVariable(musicTitle, artist);
                    combineLocalCloud();

                    onCloudMusicSuccess();
                } catch (Exception e) {
                    AILog.e(TAG, e.toString());
                    onCloudMusicFailure();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                AILog.i(TAG, volleyError.toString());
                onCloudMusicFailure();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, TIME_OUT_TIMES, 1.0f));
        queue.add(request);
    }

    /**
     * 合并本地列表和云端列表，并且过滤掉云端与本地相同的结果
     */
    private void combineLocalCloud() {
        Map<String, MusicInfo> map = new LinkedTreeMap<String, MusicInfo>();

        for (MusicInfo local : mLocalCompleteList) {
            map.put(local.getName() + local.getArtist(), local);
        }
        for (MusicInfo local : mLocalFuzzyList) {
            if (!map.containsKey(local.getName() + local.getArtist())) {//如果直接put，后面相同的元素会覆盖已有元素
                map.put(local.getName() + local.getArtist(), local);
            }
        }
        for (MusicInfo cloud : mMusicInfoList) {
            if (!map.containsKey(cloud.getName() + cloud.getArtist())) {
                map.put(cloud.getName() + cloud.getArtist(), cloud);
            }
        }

        mMusicInfoList.clear();
        mMusicInfoList.addAll(map.values());
        map.clear();
    }

    /**
     * 初始化本地音乐完全匹配和模糊匹配的变量，方便本地搜索和网络搜索时用到
     *
     * @param musicTitle 歌曲名，可以为空
     * @param artist     歌手名，可以为空
     */
    private void initLocalVariable(String musicTitle, String artist) {

        List<MusicInfo> localMusicList = new MusicLocalDaoImpl(mContext).findAll();
        mLocalCompleteList = new ArrayList<>(); // 完全匹配的搜索结果
        mLocalFuzzyList = new ArrayList<>(); // 模糊匹配的搜索结果

        for (MusicInfo musicInfo : localMusicList) {
            if (TextUtils.isEmpty(musicTitle) && !TextUtils.isEmpty(artist)) {//歌曲名为空，歌手不为空

                if (musicInfo.getArtist().equals(artist)) {
                    mLocalCompleteList.add(musicInfo);
                } else if (musicInfo.getArtist().contains(artist)) {
                    mLocalFuzzyList.add(musicInfo);
                }

            } else if (!TextUtils.isEmpty(musicTitle) && TextUtils.isEmpty(artist)) {//歌曲名不为空，歌手为空

                if (musicInfo.getName().equals(musicTitle)) {
                    mLocalCompleteList.add(musicInfo);
                } else if (musicInfo.getName().contains(musicTitle)) {
                    mLocalFuzzyList.add(musicInfo);
                }

            } else if (!TextUtils.isEmpty(musicTitle) && !TextUtils.isEmpty(artist)) {//歌曲名，歌手都不为空

                if (musicInfo.getName().equals(musicTitle) && musicInfo.getArtist().equals(artist)) {
                    mLocalCompleteList.add(musicInfo);
                } else if (musicInfo.getName().contains(musicTitle) && musicInfo.getArtist().contains(artist)) {
                    mLocalFuzzyList.add(musicInfo);
                }

            }
            //都不满足，则返回一个空的音乐列表
        }
        AILog.i(TAG, "本地完全匹配结果：" + mLocalCompleteList);
        AILog.i(TAG, "本地模糊匹配结果：" + mLocalFuzzyList);
    }

    public interface OnMusicListener {
        void onSearchSuccess(List<MusicInfo> musicInfoList,String keyword);

        void onSearchFailure();
    }

    private OnMusicListener mListener;

    public void setListener(OnMusicListener mListener) {
        this.mListener = mListener;
    }

    public void onCloudMusicSuccess() {
        AILog.i(TAG, "onCloudMusicSuccess()");
        AILog.i(TAG, mMusicInfoList.toString());
        if (mListener != null) {
            mListener.onSearchSuccess(mMusicInfoList,mKeyWord);
        }
    }

    public void onCloudMusicFailure() {
        AILog.i(TAG, "onCloudMusicFailure()");
        if (mListener != null) {
            mListener.onSearchFailure();
        }
    }
}
