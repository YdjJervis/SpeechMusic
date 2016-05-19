package com.aispeech.aios.music.util.db;

import android.content.Context;
import android.os.Environment;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.common.MusicPathScanner;
import com.aispeech.aios.music.config.Configs;
import com.aispeech.aios.music.db.MusicLocalDaoImpl;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music.util.AppUtil;
import com.aispeech.aios.music.util.StorageUtil;

import java.io.File;
import java.util.List;

/**
 * Created by Jervis on 2015/12/16.
 */
public class LocalMusicDBUtil {

    private static final String TAG="LocalMusicDBUtil";

    public static final void refreshLocalMusicDB(Context context){
            //根据路径查音乐，然后更新本地音乐数据库
        List<MusicInfo> pathMusicInfoList = new MusicPathScanner(Configs.MUSIC_CACHE_PATH).getList();
        refreshLocalMusicDB(context, pathMusicInfoList);
    }

    public static void refreshLocalMusicDB(Context context, List<MusicInfo> pathMusicInfoList) {
        MusicLocalDaoImpl dao = new MusicLocalDaoImpl(context);
        dao.updateDatabase(pathMusicInfoList);
        pathMusicInfoList =  dao.findAll();

        if (AppUtil.isVST(context) && StorageUtil.externalMemoryAvailable()) {//针对VST中TF卡Music目录下的音乐也刷新进数据库
            File file = Environment.getExternalStorageDirectory().getParentFile().getParentFile();

            String tfCardMusicPath = file.getAbsolutePath()+ File.separator+"external"+ File.separator+"Music";
            File tfCardMusicFile = new File(tfCardMusicPath);

            if(!tfCardMusicFile.exists()){
                AILog.i(TAG,"Music目录不存在，自动生成");
                boolean result = tfCardMusicFile.mkdirs();
                AILog.i(TAG,"生成结果："+result);
            }else{
                AILog.i(TAG,"Music目录存在");
            }

            AILog.d(TAG,pathMusicInfoList+"");
            StorageUtil.guaranteeSpace(context, Configs.MUSIC_CACHE_PATH,500,pathMusicInfoList);//Music目录下只能有500M音乐

            List<MusicInfo> sdcardMusicInfoList = new MusicPathScanner(tfCardMusicFile.getAbsolutePath()).getList();
            pathMusicInfoList.addAll(sdcardMusicInfoList);
            dao.updateDatabase(pathMusicInfoList);
        }else{
            AILog.i(TAG,"VST Luancher没有安装");
        }
    }
}
