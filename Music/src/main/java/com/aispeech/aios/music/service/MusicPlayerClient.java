package com.aispeech.aios.music.service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music.receiver.PlayerStateReceiver;
import com.aispeech.aios.music.util.AITimer;
import com.aispeech.aios.music.util.TimeUtils;

import java.util.List;
import java.util.TimerTask;

public class MusicPlayerClient {
    private static final String TAG = "AIOS-MusicPlayerClient";
    private static final int UPDATE_PROG_INTERVAL = 1000;

    private MusicPlayerServiceConnector mMusicPlayerServiceConnector;
    private Context mContext;
    private AITimer mAITime;
    private TimeUtils mTimeUtils;
    private int mPlayDuration;
    private String mPlayDurationStr;
    private MusicPlayerNotifyer mMusicPlayerNotifyer;
    private boolean mIsRegisterStatusChanged;
    /**
     * 播放状态监听器，主要监听播放歌曲的改变，以及播放状态的改变
     */
    private PlayerStateReceiver mPlayerStateReceiver;
    private PlayerStateReceiver.OnStateChangeListener mOnStateChangeListener = new PlayerStateReceiver.OnStateChangeListener() {
        @Override
        public void onReady() {
            mMusicPlayerNotifyer.onReady();
        }

        @Override
        public void onComplete() {
            mMusicPlayerNotifyer.onComplete();
        }

        @Override
        public void onPlayStateChanger() {
            boolean isPlaying = isPlaying();

            if (isPlaying) {
                AILog.d(TAG, "isPlaying");
                mPlayDuration = (int) getDuration();
                AILog.d(TAG, "mPlayDuration:" + mPlayDuration + ", localPlayer onReady");
                mPlayDurationStr = mTimeUtils.formateSec(mPlayDuration / 1000);
                startProgTimer();
                mMusicPlayerNotifyer.onReady();
            }
        }

        @Override
        public void onLoadBegin() {
            mMusicPlayerNotifyer.onLoadBegin();
        }

        @Override
        public void onLoadEnd() {
            mMusicPlayerNotifyer.onLoadEnd();
        }
    };

    public MusicPlayerClient(Context context, MusicPlayerNotifyer notifyer) {
        mMusicPlayerServiceConnector = MusicPlayerServiceConnector.getInstance(context);
        mContext = context;
        mMusicPlayerNotifyer = notifyer;
        mAITime = (AITimer) AITimer.getInstance();
        mTimeUtils = new TimeUtils();
        registerStatusChangedListener();
    }

    private void registerStatusChangedListener() {
        if (!mIsRegisterStatusChanged) {
            AILog.d(TAG, "registerStatusChangedListener()");
            IntentFilter filter = new IntentFilter();
            mPlayerStateReceiver = new PlayerStateReceiver(filter);
            mPlayerStateReceiver.setStateChangeListener(mOnStateChangeListener);
            mContext.registerReceiver(mPlayerStateReceiver, filter);
            mIsRegisterStatusChanged = true;
        }
    }

    public void unregisterStatusChangedListener() {
        if (mIsRegisterStatusChanged) {
            AILog.d(TAG, "unregisterStatusChangedListener()");
            mContext.unregisterReceiver(mPlayerStateReceiver);
            mPlayerStateReceiver = null;
            mIsRegisterStatusChanged = false;
        }
    }

    private void startProgTimer() {
        mAITime.startTimer(new UpdateProgTimer(), UpdateProgTimer.class.getName(), 0,
                UPDATE_PROG_INTERVAL);
    }

    public void cancelProgTimer() {
        mAITime.cancelTimer(UpdateProgTimer.class.getName());
    }

    public MusicInfo getMusicInfo(){
        return mMusicPlayerServiceConnector.getMusicInfo();
    }

    public long getDuration() {
        if (mMusicPlayerServiceConnector==null) {
            return 0;
        }
        return mMusicPlayerServiceConnector.getDuration();
    }

    public long getPosition() {
        return mMusicPlayerServiceConnector.getPosition();
    }

    public void seek(long pos) {
        mMusicPlayerServiceConnector.seek(pos);
    }

    public boolean playOneSong(String url) {
        return mMusicPlayerServiceConnector.openAndPlayOneSong(url);
    }

    public void setMusicInfo(MusicInfo info) {
        mMusicPlayerServiceConnector.setMusicInfo(info);
    }

    public void setMusicInfoList(List<MusicInfo> list) {
        mMusicPlayerServiceConnector.setMusicInfoList(list);
    }

    public void pause() {
        if (isPlaying()) {// 如果正在播放
            AILog.d(TAG, "pause()");
            mMusicPlayerServiceConnector.pause();// 暂停
        }
    }

    public void resume() {
        if (!isPlaying()) {// 如果正在播放
            AILog.d(TAG, "resume()");
            mMusicPlayerServiceConnector.resume();
        }
    }

    public void stop() {
        mMusicPlayerServiceConnector.stop();
        cancelProgTimer();
    }

    /**
     * 当前是否正在播放音乐
     *
     * @return
     */
    public boolean isPlaying() {
        return mMusicPlayerServiceConnector.isPlaying();
    }

    public boolean isPlayingBefore(){
        return mMusicPlayerServiceConnector.isPlayingBefore();
    }

    private void notifyChange(String what) {
        Intent i = new Intent(what);
        mContext.sendBroadcast(i);
    }

    public interface MusicPlayerNotifyer {

        void onReady();

        void onComplete();

        void onProgressStr(String prog);

        void onLoadBegin();

        void onLoadEnd();

    }

    private class UpdateProgTimer extends TimerTask {
        @Override
        public void run() {
            if (isPlaying()) {
                int position = (int) getPosition();
                if (mPlayDuration > 0) {
                    String pos = mTimeUtils.formateSec(position / 1000);
                    mMusicPlayerNotifyer.onProgressStr(pos + "/" + mPlayDurationStr);
                }
            }
        }
    }

}
