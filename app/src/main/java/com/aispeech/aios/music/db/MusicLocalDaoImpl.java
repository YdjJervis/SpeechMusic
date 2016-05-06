package com.aispeech.aios.music.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.pojo.MusicInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by Jervis on 2015/12/3.
 */
public class MusicLocalDaoImpl implements MusicLocalDao {

    private final String TAG = "AIOS-MusicLocalDaoImpl";

    SQLiteDatabase mDb;

    public MusicLocalDaoImpl(Context context) {
        if (mDb == null) {
            mDb = MusicDBHelper.getInstance(context).getWritableDatabase();
        }
    }

    /**
     * Insert a row in table music_local if the row not exist
     *
     * @param o MusicInfo object
     */
    @Override
    public void add(Object o) {

        if (!isExist(o)) {
            MusicInfo info = ((MusicInfo) o);

            ContentValues values = new ContentValues();
            values.put(MusicLocal._ID, info.getId());
            values.put(MusicLocal.ARTIST, info.getArtist());
            values.put(MusicLocal.TITLE, info.getName());
            values.put(MusicLocal.DURATION, info.getDuration());
            values.put(MusicLocal.SIZE, info.getSize());
            values.put(MusicLocal.DATA, info.getPath());
            values.put(MusicLocal.PICTURE, info.picture);
            values.put(MusicLocal.MIME_TYPE, "application/ogg");

            mDb.insert(MusicLocal.TABLE_NAME, null, values);
        }

    }

    /**
     * Delete a row in table music_local if the row has existed
     *
     * @param o MusicInfo object
     */
    @Override
    public void delete(Object o) {
        if (isExist(o)) {
            String where = "_id = ?";
            mDb.delete(MusicLocal.TABLE_NAME, where, new String[]{String.valueOf(((MusicInfo) o).getId())});
        }
    }

    /**
     * Delete a row by file absolute path in table music_local if the row has existed
     *
     * @param comPath music file path
     */
    public void deleteByPath(String comPath) {
        List<MusicInfo> all = findAll();
        for (MusicInfo mi : all) {
            if (mi.getPath().toLowerCase().equals(comPath.toLowerCase())) {
                delete(mi);
            }
        }
    }

    /**
     * @param id colomn -id in db
     * @return MusicInfo object or null
     */
    @Override
    public Object findById(long id) {

        MusicInfo info = null;

        String selection = " _id = ? ";
        Cursor cursor = mDb.query(MusicLocal.TABLE_NAME, new String[]{MusicLocal._ID, MusicLocal.ARTIST, MusicLocal.TITLE, MusicLocal.DURATION, MusicLocal.DATA, MusicLocal.SIZE}, selection, new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            info = new MusicInfo();
            initMusicInfo(cursor, info);
        }

        return info;
    }

    /**
     * @return MusicInfo List or Empty List
     */
    @Override
    public List findAll() {

        List<MusicInfo> list = new ArrayList<MusicInfo>();

        Cursor cursor = mDb.query(MusicLocal.TABLE_NAME, null, null, null, null, null, null);

        MusicInfo info;
        while (cursor != null && cursor.moveToNext()) {
            info = new MusicInfo();
            initMusicInfo(cursor, info);
            list.add(info);
        }

        Collections.reverse(list);
        return list;
    }

    @Override
    public boolean isExist(Object o) {
        MusicInfo info = ((MusicInfo) o);

        String selection = "_id = ?";
        Cursor query = mDb.query(MusicLocal.TABLE_NAME, new String[]{MusicLocal._ID}, selection, new String[]{String.valueOf(info.getId())}, null, null, null);
        if (query != null && query.moveToNext()) {
            return true;
        }

        return false;
    }

    public List<Long> getIDs() {

        List<Long> ids = new ArrayList<>();

        Cursor cursor = mDb.query(MusicLocal.TABLE_NAME, new String[]{MusicLocal._ID}, null, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            Long id = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal._ID)));
            ids.add(id);
        }

        return ids;
    }

    /**
     * 根据当前游标位置指向的数据库记录，设置需要初始化的MusicInfo对象
     *
     * @param cursor 当前游标
     * @param info   已经实例化的MusicInfo对象
     */
    public void initMusicInfo(Cursor cursor, MusicInfo info) {
        info.setId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal._ID))));
        info.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.ARTIST)));
        info.setName(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.TITLE)));
        info.setDuration(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.DURATION))));
        info.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.DATA)));
        info.setSize(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.SIZE)));
        info.setCloudMusic(false);
        info.picture = cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.PICTURE));
    }

    public void updateDatabase(List<MusicInfo> list) {
        for (MusicInfo info : list) {
            if (!isExistByObject(info)) {
                info.setId(generateID());
                add(info);
            }
        }
    }

    private boolean isExistByObject(Object o) {
        MusicInfo info = ((MusicInfo) o);

        String selection = "artist = ? and title = ?";
        Cursor query = mDb.query(MusicLocal.TABLE_NAME, new String[]{MusicLocal.ARTIST, MusicLocal.TITLE}, selection, new String[]{info.getArtist(), info.getName()}, null, null, null);
        if (query != null && query.moveToNext()) {
            return true;
        }

        return false;
    }

    private long generateID() {
        Set<Long> ids = new HashSet<>();

        Cursor cursor = mDb.query(MusicLocal.TABLE_NAME, new String[]{MusicLocal._ID}, null, null, null, null, null);


        while (cursor != null && cursor.moveToNext()) {
            Long id = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal._ID)));
            ids.add(id);
        }


        return getRandomID(ids);
    }

    private long getRandomID(Set<Long> ids) {

        Long id = (long) Math.abs(new Random().nextInt()) % 20000;
        if (ids.contains(id)) {
            AILog.i(TAG, "ID已经存在：" + id);
            return getRandomID(ids);
        } else {
            return id;
        }
    }
}
