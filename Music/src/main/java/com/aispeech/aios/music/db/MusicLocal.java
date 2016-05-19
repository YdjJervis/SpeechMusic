package com.aispeech.aios.music.db;

import android.provider.BaseColumns;

/**
 * Created by Jervis on 2015/12/3.
 */
public class MusicLocal implements BaseColumns{
    public static final String TABLE_NAME = "music_local";
    public static final String ARTIST = "artist";
    public static final String TITLE = "title";
    public static final String ALBUM = "album";
    public static final String DURATION = "duration";
    public static final String SIZE = "_size";
    public static final String DATA = "_data";
    public static final String MIME_TYPE = "mime_type";
    public static final String ARTIST_ID = "aitist_id";
    public static final String ALBUM_ID = "album_id";
    public static final String PICTURE = "picture";
}
