package com.aispeech.aios.music.common;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.pojo.MusicInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jervis on 2015/12/7.
 */
public class MusicPathScanner {

    private static final String TAG = "MusicPathScanner";

    private String mPath;

    /**
     * 扫描目录下的MP3和OGG文件，扫描成功执行回调
     * @param path 要扫描的路径
     */
    public MusicPathScanner(String path) {
        mPath = path;
    }


    public List<MusicInfo> getList(){

        List<MusicInfo> list = new ArrayList<MusicInfo>();

        File dirFile = new File(mPath);
        if (!dirFile.exists() || dirFile.listFiles()==null) {
            return list;
        }

        for (File file : dirFile.listFiles()) {

            String name = file.getName();
            if(endWith(name,".mp3")||endWith(name,".ogg")){

                String title;
                String artist;

                int index_ogg = getIndexOfPoint(name);

                if(name.contains("-")) {
                    int index_ = name.indexOf("-");

                    title = name.substring(0, index_).trim();
                    artist = name.substring(index_ + 1, index_ogg).trim();

                }else{

                    title = name.substring(0, index_ogg).trim();
                    artist = "";
                }

                MusicInfo info = new MusicInfo();
                initMusicInfo(info, artist, title, file.getAbsolutePath());
                list.add(info);
            }
        }
        AILog.i(TAG,"指定目录后扫描到的歌曲如下");
        for (MusicInfo info : list) {
            AILog.i(TAG,info+"");
        }

        return list;
    }

    private boolean endWith(String src, String fostFix){
        return src.toLowerCase().endsWith(fostFix);
    }

    private void initMusicInfo(MusicInfo info, String artist, String title, String path) {
        info.setArtist(artist);
        info.setName(title);
        info.setPath(path);
    }

    /**
     * @return Mp3,Ogg前.的index
     */
    private int getIndexOfPoint(String name){
        int index_ogg = name.indexOf(".ogg");

        if (endWith(name, ".mp3")) {
            index_ogg = name.indexOf(".mp3");
        }
        return  index_ogg;
    }
}
