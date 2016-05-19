package com.aispeech.aios.music.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music.util.BroadCastUtil;
import com.google.gson.Gson;

/**
 * Created by dongjie.yao on 2015/12/9.
 */
public class SingleMusicReceiver extends BaseReceiver {

    private static final String TAG = "SingleMusicReceiver";

    public static final String ACTION_MUSIC_RESPONSE = "com.aispeech.vst.music.response";
    public static final String ACTION_MUSIC_REQUEST = "com.aispeech.vst.music.request";

    public SingleMusicReceiver(IntentFilter filter) {
        super(filter);
    }

    @Override
    protected void onReceiveIml(Context context, Intent intent) {

        AILog.i(TAG,mAction);
        if(mListener!=null){
            if (ACTION_MUSIC_REQUEST.equals(mAction)) {
                mListener.onRequest();
            }
        }
    }

    @Override
    protected void addAction() {
        mFilter.addAction(ACTION_MUSIC_REQUEST);
    }

    private OnRequestListener mListener;

    public interface OnRequestListener {
        void onRequest();
    }

    public void setOnRequestListener(OnRequestListener l) {
        mListener = l;
    }

    public void sendBroadcast(Context context, MusicInfo info){
        AILog.i(TAG,"发送广播："+info);
        BroadCastUtil.getInstance(context).send(ACTION_MUSIC_RESPONSE,"music",new Gson().toJson(info));
    }
}
