package com.aispeech.aios.music.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;

import com.aispeech.aios.music.pojo.MusicInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicDBHelper extends SQLiteOpenHelper {

    //数据库信息
    public static final int DATABASE_VERSION = 2;
    public static String DATABASE_NAME = "navosmusic.db";

    //方便SQL合成的常量
    private static final String TEXT = " TEXT";
    private static final String SEP = ",";

    //SQL
    //----缓存音乐的数据库
    private static final String SQL_CREATE_MUSIC_HISTORY = "CREATE TABLE "
            + MusicCache.TABLE_NAME + " (" + MusicCache.COLUMN_NAME_NAME + TEXT
            + SEP + MusicCache.COLUMN_NAME_ID + TEXT
            + SEP + MusicCache.COLUMN_NAME_ARTIST + TEXT
            + SEP + MusicCache.COLUMN_NAME_URL + TEXT
            + " )";
    private static final String SQL_DELETE_MUSIC_HISTORY = "DROP TABLE IF EXISTS " + MusicCache.TABLE_NAME;

    //----本地音乐的数据库
    public static final String SQL_CREATE_MUSIC_LOCAL = "CREATE TABLE "
            + MusicLocal.TABLE_NAME + "("
            + MusicLocal._ID + TEXT + SEP
            + MusicLocal.ARTIST + TEXT + SEP
            + MusicLocal.TITLE + TEXT + SEP
            + MusicLocal.ALBUM + TEXT + SEP
            + MusicLocal.DURATION + TEXT + SEP
            + MusicLocal.SIZE + TEXT + SEP
            + MusicLocal.DATA + TEXT + SEP
            + MusicLocal.MIME_TYPE + TEXT + SEP
            + MusicLocal.ARTIST_ID + TEXT + SEP
            + MusicLocal.ALBUM_ID + TEXT + SEP
            + MusicLocal.PICTURE + TEXT
            + ")";
    private static final String SQL_DELETE_MUSIC_LOCAL = "DROP TABLE IF EXISTS " + MusicLocal.TABLE_NAME;

    private static MusicDBHelper instance = null;
    private Context mContext;

    private MusicDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext=context;
    }

    public static synchronized MusicDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MusicDBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MUSIC_HISTORY);
        db.execSQL(SQL_CREATE_MUSIC_LOCAL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL(SQL_DELETE_MUSIC_HISTORY);
        db.execSQL(SQL_DELETE_MUSIC_LOCAL);
        onCreate(db);
    }

    public void insertMusicEntry(MusicInfo musicInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MusicCache.COLUMN_NAME_NAME, musicInfo.getName());
        values.put(MusicCache.COLUMN_NAME_ID, musicInfo.getId());
        values.put(MusicCache.COLUMN_NAME_ARTIST, musicInfo.getArtist());
        values.put(MusicCache.COLUMN_NAME_URL, musicInfo.getCloudUrl());
        db.insert(MusicCache.TABLE_NAME, null, values);

    }

    public List<MusicInfo> queryMusicEntry() {
        List<MusicInfo> musicList = new ArrayList<MusicInfo>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {MusicCache.COLUMN_NAME_ID, MusicCache.COLUMN_NAME_NAME, MusicCache.COLUMN_NAME_ARTIST, MusicCache.COLUMN_NAME_URL};
        Cursor cursor = db.query(MusicCache.TABLE_NAME, projection, null, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.setId(Long.parseLong(cursor.getString(cursor
                    .getColumnIndexOrThrow(MusicCache.COLUMN_NAME_ID))));
            musicInfo.setName(cursor.getString(cursor
                    .getColumnIndexOrThrow(MusicCache.COLUMN_NAME_NAME)));
            musicInfo.setArtist(cursor.getString(cursor
                    .getColumnIndexOrThrow(MusicCache.COLUMN_NAME_ARTIST)));
            musicInfo.setCloudUrl(cursor.getString(cursor
                    .getColumnIndexOrThrow(MusicCache.COLUMN_NAME_URL)));
            musicInfo.setCloudMusic(true);
            musicList.add(musicInfo);
        }
        Collections.reverse(musicList);
        return musicList;
    }

    public void deleteMusicEntry(MusicInfo musicInfo) {
        String id = String.valueOf(musicInfo.getId());

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MusicCache.TABLE_NAME, MusicCache.COLUMN_NAME_ID + " = ?", new String[]{id});
    }

    public void deleteMusicEntry(long idLong) {
        String id = String.valueOf(idLong);

        StringBuilder where = new StringBuilder();
        where.append(MediaStore.Audio.Media._ID + " = "+id);
        mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,where.toString(),null);
    }

}
