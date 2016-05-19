package com.aispeech.aios.music.listener;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-03-17
 * @copyright aispeech.com
 */
public interface OnOperateListener {
    void onSongResult(String musicJson);

    void onPauseOperate();

    void onResumeOperate();

    void onPreOperate();

    void onNextOperate();

    void onExitOperate();

    void onRandomOperate();

    void onBtnPauseOperate();

    void onBtnRestarOperate();

}
