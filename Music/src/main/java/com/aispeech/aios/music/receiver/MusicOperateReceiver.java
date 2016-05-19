package com.aispeech.aios.music.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.listener.OnOperateListener;
import com.aispeech.aios.music.util.music.MusicParser;

/**
 * Created by dongjie.yao on 2015/12/28.
 */
public class MusicOperateReceiver extends BaseReceiver {

    public static final String TAG = "MusicOperateReceiver";

    public static final String ACTION_SONG_RESULT = "com.aispeech.aios.music.SONG_RESULT";
    public static final String ACTION_MUSIC_PAUSE = "com.aispeech.aios.music.ACTION_MUSIC_PAUSE";
    public static final String ACTION_MUSIC_RESUME = "com.aispeech.aios.music.ACTION_MUSIC_RESUME";
    public static final String ACTION_MUSIC_PREVIOUS = "com.aispeech.aios.music" +
            ".ACTION_MUSIC_PREVIOUS";
    public static final String ACTION_MUSIC_NEXT = "com.aispeech.aios.music.ACTION_MUSIC_NEXT";
    public static final String ACTION_MUSIC_EXIT = "com.aispeech.aios.music.ACTION_MUSIC_EXIT";
    public static final String ACTION_RANDOM = "com.aispeech.aios.adapter.ACTION_RANDOM";
    public static final String ACTION_MUSIC_PAUSE_BUTTON = "com.aispeech.aios.music" +
            ".ACTION_MUSIC_PAUSE_BUTTON";
    public static final String ACTION_MUSIC_RESTART_BUTTON = "com.aispeech.aios.music" +
            ".ACTION_MUSIC_RESTART_BUTTON";
    public static final String ACTION_ACC_OFF = "com.android.action_acc_off";

    public MusicOperateReceiver(IntentFilter filter) {
        super(filter);
    }

    @Override
    protected void onReceiveIml(Context context, Intent intent) {
        AILog.i(TAG, mAction);

        if (mOnOperateListener != null) {
            if (ACTION_SONG_RESULT.equals(mAction)) {
                String musicJson = intent.getStringExtra("musicList");
                AILog.i(TAG, "广播接收的音乐列表：" + musicJson);
                String playMusicJson = new MusicParser().parseMusicJsonArray(musicJson);
                AILog.i(TAG, "需要播发的音乐列表" + playMusicJson);
                mOnOperateListener.onSongResult(playMusicJson);
            } else if (ACTION_MUSIC_PAUSE.equals(mAction)) {
                mOnOperateListener.onPauseOperate();
            } else if (ACTION_MUSIC_RESUME.equals(mAction)) {
                mOnOperateListener.onResumeOperate();
            } else if (ACTION_MUSIC_PREVIOUS.equals(mAction)) {
                mOnOperateListener.onPreOperate();
            } else if (ACTION_MUSIC_NEXT.equals(mAction)) {
                mOnOperateListener.onNextOperate();
            } else if (ACTION_MUSIC_EXIT.equals(mAction)) {
                mOnOperateListener.onExitOperate();
            } else if (ACTION_RANDOM.equals(mAction)) {
                mOnOperateListener.onRandomOperate();
            } else if (ACTION_MUSIC_PAUSE_BUTTON.equals(mAction)) {
                mOnOperateListener.onBtnPauseOperate();
            } else if (ACTION_MUSIC_RESTART_BUTTON.equals(mAction)) {
                mOnOperateListener.onBtnRestarOperate();
            }
        }

        if (mOnAccListener != null) {
            if (ACTION_ACC_OFF.equals(mAction)) {
                mOnAccListener.onAccOff();
            }
        }


    }

    @Override
    protected void addAction() {
        //音乐操作相关
        mFilter.addAction(ACTION_SONG_RESULT);
        mFilter.addAction(ACTION_MUSIC_PAUSE);
        mFilter.addAction(ACTION_MUSIC_RESUME);
        mFilter.addAction(ACTION_MUSIC_PREVIOUS);
        mFilter.addAction(ACTION_MUSIC_NEXT);
        mFilter.addAction(ACTION_MUSIC_EXIT);
        mFilter.addAction(ACTION_RANDOM);
        mFilter.addAction(ACTION_MUSIC_PAUSE_BUTTON);
        mFilter.addAction(ACTION_MUSIC_RESTART_BUTTON);

        //Acc相关
        mFilter.addAction(ACTION_ACC_OFF);
    }

    private OnOperateListener mOnOperateListener;
    private OnAccListener mOnAccListener;

    public interface OnAccListener {
        void onAccOff();
    }

    public void setOperateListener(OnOperateListener l) {
        mOnOperateListener = l;
    }

    public void setAccListener(OnAccListener l) {
        mOnAccListener = l;
    }
}
