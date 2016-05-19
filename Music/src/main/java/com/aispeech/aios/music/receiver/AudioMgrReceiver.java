package com.aispeech.aios.music.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.aispeech.ailog.AILog;

/**
 * Created by Jervis on 2015/12/10.
 */
public class AudioMgrReceiver extends BaseReceiver {

    private static final String TAG = "AIOS-MusicPlayerBroadcastReceiver";

    private static final String ACTION_INTERRUPT_START = "audiomanager.intent.action.INTERRUPT_START";
    private static final String ACTION_INTERRUPT_STOP = "audiomanager.intent.action.INTERRUPT_STOP";
    private static final String ACTION_AIOS_START = "audiomanager.intent.action.AIOS_START";
    private static final String ACTION_AIOS_STOP = "audiomanager.intent.action.AIOS_STOP";
    private static final String ACTION_INSERT_START = "audiomanager.intent.action.INSERT_START";
    private static final String ACTION_INSERT_STOP = "audiomanager.intent.action.INSERT_STOP";

    private static final String ACTION_BT_MUSIC_PLAY = "audiomanager.intent.action.BT_MUSIC_PLAY";
    private static final String ACTION_BT_MUSIC_PAUSE = "audiomanager.intent.action.BT_MUSIC_PAUSE";
    private static final String ACTION_MUSIC_PLAY = "audiomanager.intent.action.MUSIC_PLAY";
    private static final String ACTION_MUSIC_PAUSE = "audiomanager.intent.action.MUSIC_PAUSE";
    private static final String ACTION_VIDEO_PLAY = "audiomanager.intent.action.VIDEO_PLAY";
    private static final String ACTION_VIDEO_PAUSE = "audiomanager.intent.action.VIDEO_PAUSE";

    public AudioMgrReceiver(IntentFilter filter) {
        super(filter);
    }

    @Override
    protected void onReceiveIml(Context context, Intent intent) {
        AILog.i(TAG, mAction);

        if (mListener != null) {
            if (validate(ACTION_INTERRUPT_START) || validate(ACTION_AIOS_START) || validate(ACTION_INSERT_START)) {
                mListener.volumeDown();
            } else if (validate(ACTION_INTERRUPT_STOP) || validate(ACTION_AIOS_STOP) || validate(ACTION_INSERT_STOP)) {
                mListener.volumeUp();
            } else if (validate(ACTION_BT_MUSIC_PLAY) || validate(ACTION_MUSIC_PLAY) || validate(ACTION_VIDEO_PLAY)) {
                mListener.pauseMusic();
            } else if (validate(ACTION_BT_MUSIC_PAUSE) || validate(ACTION_MUSIC_PAUSE) || validate(ACTION_VIDEO_PAUSE)) {
                mListener.resumeMusic();
            }
        }
    }

    private boolean validate(String action) {
        return action.equals(mAction);
    }

    @Override
    protected void addAction() {
        mFilter.addAction(ACTION_INTERRUPT_START);
        mFilter.addAction(ACTION_INTERRUPT_STOP);
        mFilter.addAction(ACTION_AIOS_START);
        mFilter.addAction(ACTION_AIOS_STOP);
        mFilter.addAction(ACTION_INSERT_START);
        mFilter.addAction(ACTION_INSERT_STOP);

        mFilter.addAction(ACTION_BT_MUSIC_PLAY);
        mFilter.addAction(ACTION_BT_MUSIC_PAUSE);
        mFilter.addAction(ACTION_MUSIC_PLAY);
        mFilter.addAction(ACTION_MUSIC_PAUSE);
        mFilter.addAction(ACTION_VIDEO_PLAY);
        mFilter.addAction(ACTION_VIDEO_PAUSE);
    }

    private OnReceiveListener mListener;

    public interface OnReceiveListener {
        void volumeUp();

        void volumeDown();

        void pauseMusic();

        void resumeMusic();
    }

    public void setOnReceiveListener(OnReceiveListener listener) {
        this.mListener = listener;
    }

}
