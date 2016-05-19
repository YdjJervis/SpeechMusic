package com.aispeech.aios.music.model;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.config.Configs;
import com.aispeech.aios.music.db.MusicDBHelper;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music.util.StorageUtil;
import com.aispeech.aios.music.util.download.FileDownLoader;
import com.aispeech.aios.music.util.music.MusicSyncUtil;

import java.io.File;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-04-25
 * @copyright aispeech.com
 */
public class DownloadData {

    private static final String TAG = "AIOS-DownloadData";
    private OnDownloadListener mListener;

    private Context mContext;

    public DownloadData(Context context) {
        AILog.i(TAG, "init DownloadData...");
        mContext = context;
    }

    public void downloadMusic(final MusicInfo musicInfo, final int position) {
        AILog.i(TAG, "downloadMusic...url=" + musicInfo.getCloudUrl());
        if (null != mListener) {
            mListener.onLoading();
        }
        StorageUtil.checkFolderExists(Configs.MUSIC_CACHE_PATH);
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在

        if (sdCardExist) {
            downloadLrc(musicInfo);
            AILog.i(TAG, "sdCard Exist...");

            final String path = Configs.MUSIC_CACHE_PATH + musicInfo.getName() + " - " + musicInfo.getArtist() + ".ogg";

            new FileDownLoader().download(musicInfo.getCloudUrl(), path, new FileDownLoader.DownLoaderListener() {
                @Override
                public void onResult(int res, String s) {
                    AILog.i(TAG, "onResult res:" + res);
                    AILog.i(TAG, "onResult s:" + s);
                    if (res == 0) {
                        // 下载成功
                        MusicDBHelper.getInstance(mContext).deleteMusicEntry(musicInfo);

                        //先把当前音乐变量转换成本地音乐对象
                        musicInfo.setCloudUrl(null);
                        musicInfo.setPath(path);
                        musicInfo.setCloudMusic(false);

                        if (null != mListener) {
                            mListener.onSuccessed(musicInfo);
                        }
                        MusicSyncUtil.asyncMusic(mContext);
                    } else {
                        // 下载歌曲失败
                        if (null != mListener) {
                            mListener.onFailed("下载歌曲失败");
                        }
                    }
                    if (null != mListener) {
                        mListener.onResult(position);
                    }
                }
            });
        } else {
            if (null != mListener) {
                mListener.onFailed("SD卡不存在");
            }
        }

    }

    public void downloadLrc(final MusicInfo musicInfo) {
        AILog.i(TAG, "downloadMusic...lrc=" + musicInfo.lrc);
        if (null != mListener) {
            mListener.onLoading();
        }
        StorageUtil.checkFolderExists(Configs.MUSIC_CACHE_PATH + "lrc");
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在

        if (sdCardExist) {
            AILog.i(TAG, "sdCard Exist...");

            final String path = Configs.MUSIC_CACHE_PATH + "lrc" + File.separator + musicInfo.getName() + " - " + musicInfo.getArtist() + ".lrc";

            new FileDownLoader().download(musicInfo.lrc, path, new FileDownLoader.DownLoaderListener() {
                @Override
                public void onResult(int res, String s) {
                    AILog.i(TAG, "onResult res:" + res);
                    AILog.i(TAG, "onResult s:" + s);
                    if (res == 0) {
                        // 下载成功
                    } else {
                        // 下载歌曲失败
                        Toast.makeText(mContext, "歌词下载失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            AILog.i(TAG, "SD卡不存在，不能存放歌词");
        }
    }

    public void setOnDownloadListener(OnDownloadListener listener) {
        mListener = listener;
    }

    public interface OnDownloadListener {
        void onLoading();

        void onSuccessed(MusicInfo musicInfo);

        void onFailed(String msg);

        void onResult(int position);
    }
}
