package com.aispeech.aios.music.sp;

import android.content.Context;
import android.content.SharedPreferences;

import com.aispeech.aios.music.pojo.ExitState;

/**
 * Created by Jervis on 2015/12/7.
 */
public class ExitSatePreferences extends BasePreference {

    private static final String NAME = "position";
    private static final String PROGRESS = "progress";
    private static final String PLAY = "playing";

    public ExitSatePreferences(Context context) {
        super(context);
    }

    /**
     * 保存退出时状态
     * @param state
     */
    public void save(ExitState state) {
        SharedPreferences.Editor edit = getEditor(NAME);
        edit.putInt(NAME, state.getPosition());
        edit.putInt(PROGRESS, state.getProgress());
        edit.putBoolean(PLAY,state.isPlaying());
        edit.commit();
    }

    /**
     * @param position 当前播放的位置
     */
    public void savePosition(int position){
        ExitState saved = getSaved();
        saved.setPosition(position);
        save(saved);
    }

    public void clearState(){
        save(new ExitState());
    }

    /**
     * @return 上次退出时，保存的状态
     */
    public ExitState getSaved() {
        SharedPreferences preferences = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);

        ExitState state = new ExitState();
        state.setPosition(preferences.getInt(NAME,0));
        state.setProgress(preferences.getInt(PROGRESS,0));
        state.setPlaying(preferences.getBoolean(PLAY,false));
        return state;
    }


}
