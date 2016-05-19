package com.aispeech.aios.music.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.pojo.MusicInfo;

import java.util.List;

/**
 * 音乐播放服务连接器
 */
public class MusicPlayerServiceConnector {
    private static final String TAG = "AIOS-PlaySVCConnector";

    private static MusicPlayerServiceConnector mMusicPlayerServiceConnector;

    private Context mContext;
    private MusicPlayerService.ServiceBinder mService = null;
    private boolean mIsConnected = false;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName classname, IBinder obj) {
            mService = (MusicPlayerService.ServiceBinder) obj;
            AILog.d(TAG, "music service connected.");
            mIsConnected = true;
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
            mIsConnected = false;
            AILog.d(TAG, "music service disconnected.");
        }
    };

    private MusicPlayerServiceConnector(Context context) {
        mContext = context;
    }

    public static synchronized MusicPlayerServiceConnector getInstance(final Context context) {
        if (mMusicPlayerServiceConnector == null) {
            mMusicPlayerServiceConnector = new MusicPlayerServiceConnector(context);
        }

        return mMusicPlayerServiceConnector;
    }

    public void doBindService() {
        AILog.d(TAG, "music service do bind. isConnected:" + mIsConnected);
        Intent intent = new Intent(mContext,MusicPlayerService.class);
        mContext.getApplicationContext().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void doUnbindService() {
        AILog.d(TAG, "music service do unbind. isConnected:" + mIsConnected);
        if (!mIsConnected) {
            return;
        }
        mContext.getApplicationContext().unbindService(mServiceConnection);
    }


    /**
     * 当前是否正在播放音乐
     *
     * @return
     */
    public boolean isPlaying() {
        if (mService == null) {
            return false;
        }
        try {
            return (mService.isPlaying());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPlayingBefore() {
        if (mService == null) {
            return false;
        }
        return mService.isPlayingBefore();
    }

    public boolean openAndPlayOneSong(String url) {
        if (mService != null) {
            try {
                mService.play(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            AILog.i(TAG,"mService is null");
        }
        return true;
    }

    public void setMusicInfo(MusicInfo info){
        if (mService != null) {
            try {
                mService.setMusicInfo(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sendBroadcast();
    }

    public void setMusicInfoList(List<MusicInfo> list) {
        if (mService != null) {
            try {
                mService.setMusicInfo(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendBroadcast(){
        if (mService != null) {
            try {
                mService.sendBroadcast();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        AILog.i(TAG,"pause...");
        if (mService == null) {
            doBindService();
        } else {
            try {
                mService.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void resume() {
        AILog.i(TAG,"resume...");
        if (mService == null) {
            doBindService();
        } else {
            try {
                mService.resume();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        AILog.i(TAG,"stop...");
        if (mService == null) {
            doBindService();
        } else {
            try {
                mService.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取当前播放位置
     */
    public long getPosition() {
        long position = 0;
        try {
            position = mService.position();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return position;
    }

    /**
     * 获取歌曲时长
     */
    public long getDuration() {
        long duration = 0;
        try {
            duration = mService.duration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return duration;
    }

    public MusicInfo getMusicInfo(){
        return mService.getMusicInfo();
    }

    public void seek(long pos) {
        try {
            mService.seek(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
