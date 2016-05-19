package com.aispeech.aios.music.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.aispeech.ailog.AILog;

/**
 * Created by Jervis on 2015/12/10.
 */
public class PlayerStateReceiver extends BaseReceiver {

    private static final String TAG = "AIOS-PlayerStateReceiver";

    public static final String LOAD_BEGIN = "com.speech.music.loadbegin";
    public static final String LOAD_END = "com.speech.music.loadend";
    public static final String PLAYSTATE_CHANGED = "com.speech.music.playstatechanged";
    public static final String META_CHANGED = "com.speech.music.metachanged";
    public static final String ONE_SONG_ENDED = "com.speech.music.onesongended";

    public PlayerStateReceiver(IntentFilter filter) {
        super(filter);
    }

    @Override
    protected void onReceiveIml(Context context, Intent intent) {
        AILog.i(TAG, mAction);

        if (mListener != null) {
            if (validate(LOAD_BEGIN)) {
                mListener.onLoadBegin();
            } else if (validate(LOAD_END)) {
                mListener.onLoadEnd();
            } else if (validate(PLAYSTATE_CHANGED)) {
                mListener.onPlayStateChanger();
            } else if (validate(META_CHANGED)) {
                mListener.onReady();
            } else if (validate(ONE_SONG_ENDED)) {
                mListener.onComplete();
                if (mOnCompleteListener != null) {
                    AILog.i(TAG,"call service next music");
                    mOnCompleteListener.onComplete();
                }else{
                    AILog.i(TAG,"listener is null in service");
                }
            }
        }

    }

    private boolean validate(String action) {
        return action.equals(mAction);
    }

    @Override
    protected void addAction() {
        mFilter.addAction(LOAD_BEGIN);
        mFilter.addAction(LOAD_END);
        mFilter.addAction(PLAYSTATE_CHANGED);
        mFilter.addAction(META_CHANGED);
        mFilter.addAction(ONE_SONG_ENDED);
    }


    private OnStateChangeListener mListener;
    public void setStateChangeListener(OnStateChangeListener listener) {
        this.mListener = listener;
    }
    /**
     * 用于界面
     */
    public interface OnStateChangeListener {
        void onReady();

        void onComplete();

        void onPlayStateChanger();

        void onLoadBegin();

        void onLoadEnd();
    }


    private OnCompleteListener mOnCompleteListener;
    /**
     * 用于服务
     */
    public interface OnCompleteListener {
        void onComplete();
    }
    public void setOnCompleteListener(OnCompleteListener listener) {
        this.mOnCompleteListener = listener;
    }
}
