package com.aispeech.aios.music.listener;

/**
 * @desc 音频文件变化接口
 * @auth AISPEECH
 * @date 2016-02-20
 * @copyright aispeech.com
 */
public interface OnMusicListener {

    /**
     * @param comPath 完整文件名
     */
    void onDelete(String comPath);
}
