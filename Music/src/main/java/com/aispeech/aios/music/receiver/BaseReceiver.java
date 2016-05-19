package com.aispeech.aios.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Jervis on 2015/11/11.
 */
public abstract class BaseReceiver extends BroadcastReceiver {

    protected Context mContext;
    protected String mAction;
    protected IntentFilter mFilter;

    protected BaseReceiver(IntentFilter filter) {
        mFilter = filter;

        if (mFilter != null) {
            addAction();
        }
    }

    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mAction = intent.getAction();
        onReceiveIml(context, intent);
    }

    protected abstract void onReceiveIml(Context context, Intent intent);

    protected abstract void addAction();
}
