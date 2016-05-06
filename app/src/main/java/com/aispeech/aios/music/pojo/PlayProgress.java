package com.aispeech.aios.music.pojo;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-05-06
 * @copyright aispeech.com
 */
public class PlayProgress {

    public String totalTime;
    public String currentTime;//02:14

    public PlayProgress(String currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public String toString() {
        return "PlayProgress{" +
                "totalTime='" + totalTime + '\'' +
                ", currentTime='" + currentTime + '\'' +
                '}';
    }
}
