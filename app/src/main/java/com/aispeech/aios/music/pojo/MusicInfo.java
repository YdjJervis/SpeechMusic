package com.aispeech.aios.music.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class MusicInfo extends BaseInfo implements Cloneable {

    private long id;
    private String artist;
    private long duration;
    private boolean isCloudMusic = false;
    private String cloudUrl = "";
    private String size;
    private String path;
    public String picture;
    public String lrc;

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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isCloudMusic() {
        return isCloudMusic;
    }

    public void setCloudMusic(boolean isCloudMusic) {
        this.isCloudMusic = isCloudMusic;
    }

    public String getCloudUrl() {
        return cloudUrl;
    }

    public void setCloudUrl(String cloudUrl) {
        this.cloudUrl = cloudUrl;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }



    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject item = new JSONObject();
        item.put("id", getId() + "");
        item.put("title", getName());
        item.put("artist", getArtist());
        item.put("duration", getDuration() + "");
        item.put("isCloud", isCloudMusic ? "1" : "0");
        item.put("cloudUrl", getCloudUrl() + "");
        item.put("path", getPath());
        item.put("size", getSize());
        item.put("picture", picture);
        return item;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MusicInfo clone = null;
        try {
            clone = (MusicInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }

    @Override
    public String toString() {
        return "MusicInfo{" +
                "id=" + id +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", isCloudMusic=" + isCloudMusic +
                ", cloudUrl='" + cloudUrl + '\'' +
                ", size='" + size + '\'' +
                ", path='" + path + '\'' +
                ", picture='" + picture + '\'' +
                ", lrc='" + lrc + '\'' +
                '}';
    }
}
