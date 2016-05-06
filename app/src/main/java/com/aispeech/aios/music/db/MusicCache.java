package com.aispeech.aios.music.db;

import android.provider.BaseColumns;

/**
 * Created by Jervis on 2015/12/3.
 */
public class MusicCache implements BaseColumns{
    public static final String TABLE_NAME = "musichistory";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_ARTIST = "artist";
    public static final String COLUMN_NAME_URL = "url";
}
