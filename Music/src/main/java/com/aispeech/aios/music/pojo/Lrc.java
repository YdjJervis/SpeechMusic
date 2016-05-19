package com.aispeech.aios.music.pojo;

import java.util.List;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-04-20
 * @copyright aispeech.com
 */
public class Lrc {

    public String artist;
    public String title;

    public List<Content> contentList;

    public class Content{
        public String time;
        public String text;
        @Override
        public String toString() {
            return "Content{" +
                    "time='" + time + '\'' +
                    ", text='" + text + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Lrc{" +
                "artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", contentList=" + contentList +
                '}';
    }
}
