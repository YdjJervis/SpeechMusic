package com.aispeech.aios.music.listener;

import android.os.FileObserver;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.AIMusicApp;
import com.aispeech.aios.music.db.MusicDBHelper;
import com.aispeech.aios.music.db.MusicLocalDaoImpl;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music.util.db.LocalMusicDBUtil;
import com.aispeech.aios.music.util.music.MusicSyncUtil;

import java.io.File;
import java.util.List;

/**
 * @desc SD卡监听接口
 * @auth AISPEECH
 * @date 2016-02-20
 * @copyright aispeech.com
 */
public class SDCardListener extends FileObserver {

    private static final String TAG = "AIMUSIC-SDCardListener";

    private String mPath;

    public SDCardListener(String path) {
         /*
          * 这种构造方法是默认监听所有事件的,如果使用 super(String,int)这种构造方法，
          * 则int参数是要监听的事件类型.
          */
        super(path);
        mPath = path;
    }

    @Override
    public void onEvent(int event, String path) {
        switch (event) {
            case FileObserver.ALL_EVENTS:
                AILog.i(TAG, "ALL_EVENTS path:" + path);
                break;
            case FileObserver.CREATE:
                AILog.i(TAG, "CREATE path:" + path);
                break;
            case FileObserver.DELETE:
                AILog.i(TAG, "DELETE path:" + path);
                if (mOnMusicListener != null) {
                    mOnMusicListener.onDelete(mPath + File.separator + path);
                }
                break;

            case FileObserver.MODIFY://文件被加进去
                AILog.i(TAG, "MODIFY:" + path);
                LocalMusicDBUtil.refreshLocalMusicDB(AIMusicApp.getContext());//更新数据库
                MusicSyncUtil.asyncMusic(AIMusicApp.getContext());//同步内核

                break;
            case FileObserver.MOVED_TO://文件被修改
                AILog.i(TAG, "MOVED_TO" + path);
                LocalMusicDBUtil.refreshLocalMusicDB(AIMusicApp.getContext());//更新数据库
                MusicSyncUtil.asyncMusic(AIMusicApp.getContext());//同步内核
                MusicLocalDaoImpl musicLocalDao = new MusicLocalDaoImpl(AIMusicApp.getContext());
                List<MusicInfo> localMusicList = musicLocalDao.findAll();
                List<MusicInfo> cloudMusicList = MusicDBHelper.getInstance(AIMusicApp.getContext()).queryMusicEntry();

                AILog.json(TAG, "文件被修改" + localMusicList);
                AILog.json(TAG, "文件被修改" + cloudMusicList);

                break;

        }
    }

    public OnMusicListener mOnMusicListener;

    public void setOnMusicListener(OnMusicListener onMusicListener) {
        mOnMusicListener = onMusicListener;
    }
}
