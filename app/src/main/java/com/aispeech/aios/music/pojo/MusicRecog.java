package com.aispeech.aios.music.pojo;

import java.util.List;

/**
 * 发送给内核的List，便于内核语意理解，目的是，无网络也能搜本地音乐
 * Created by dongjie.yao on 2015/12/28.
 */
public class MusicRecog {

    private List<Song> songs;

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public class Song{

        private long id;

        private String artist;

        private String title;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

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
            return "MusicRecog{" +
                    "id=" + id +
                    ", artist='" + artist + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MusicRecog{" +
                "songs=" + songs +
                '}';
    }
}
