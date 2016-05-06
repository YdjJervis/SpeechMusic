package com.aispeech.aios.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Jervis on 2015/11/10.
 */
public class MusicReceiver extends BroadcastReceiver {

    public static final String EXIT = "com.aispeech.aios.music.SERVICE_ACTION_MUSIC_EXIT";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(mListener!=null){
            if(EXIT.equals(action)){
                mListener.onExit();
            }
        }
    }


    static OnMusicListener mListener;

    public interface OnMusicListener {
        void onExit();
    }

    public static void setListener(OnMusicListener l) {
        mListener = l;
    }
}
