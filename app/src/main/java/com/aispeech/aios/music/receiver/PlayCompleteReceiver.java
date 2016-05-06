package com.aispeech.aios.music.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.aispeech.ailog.AILog;

/**
 * Created by Jervis on 2015/12/10.
 */
public class PlayCompleteReceiver extends BaseReceiver {

    private static final String TAG = "AIOS-PlayCompleteReceiver";

    public static final String ONE_SONG_ENDED = "com.speech.music.onesongended";

    public PlayCompleteReceiver(IntentFilter filter) {
        super(filter);
    }

    @Override
    protected void onReceiveIml(Context context, Intent intent) {
        AILog.i(TAG, mAction);

        if (mOnCompleteListener != null) {
            if (validate(ONE_SONG_ENDED)){
                AILog.i(TAG,"call service next music");
                mOnCompleteListener.onComplete();
            }
        }

    }

    private boolean validate(String action) {
        return action.equals(mAction);
    }

    @Override
    protected void addAction() {
        mFilter.addAction(ONE_SONG_ENDED);
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
