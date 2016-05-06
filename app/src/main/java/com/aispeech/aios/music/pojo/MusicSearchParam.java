package com.aispeech.aios.music.pojo;

import java.io.Serializable;

/**
 * Created by Jervis on 2015/9/22.
 */
public class MusicSearchParam implements Serializable {

    private String artist;
    private String title;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "MusicSearchParam{" +
                "artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
