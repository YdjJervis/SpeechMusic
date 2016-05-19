package com.aispeech.aios.music.sort;

import com.aispeech.aios.music.pojo.MusicInfo;

import java.util.Comparator;

/**
 * Created by Jervis on 2015/10/8.
 */
public class SortSearchResult implements Comparator {
    String keyword = "";

    public SortSearchResult(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public int compare(Object lhs, Object rhs) {
        MusicInfo m1 = (MusicInfo) lhs;
        MusicInfo m2 = (MusicInfo) rhs;
        if (m1.getName().equals(keyword) && m2.getName().equals(keyword)) {
            return 0;
        } else if (m1.getName().equals(keyword)) {
            return -1;
        } else if (m2.getName().equals(keyword)) {
            return 1;
        } else if (m1.getArtist().equals(keyword) && m2.getArtist().equals(keyword)) {
            return 0;
        } else if (m1.getArtist().equals(keyword)) {
            return -1;
        } else if (m2.getArtist().equals(keyword)) {
            return 1;
        } else if (m1.getName().contains(keyword)) {
            return -1;
        } else if (m2.getName().contains(keyword)) {
            return 1;
        }
        return 0;
    }

}