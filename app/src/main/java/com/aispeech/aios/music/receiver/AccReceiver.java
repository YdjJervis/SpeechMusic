package com.aispeech.aios.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Jervis on 2015/11/9.
 */
public class AccReceiver extends BroadcastReceiver {

    public static final String ACC_OFF = "com.android.action_acc_off";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (mListener != null) {
            if (ACC_OFF.equals(action)) {
                mListener.onAccOff();
            }
        }
    }

    static OnAccListener mListener;

    public interface OnAccListener {
        void onAccOff();
    }

    public static void setListener(OnAccListener l) {
        AccReceiver.mListener = l;
    }
}
