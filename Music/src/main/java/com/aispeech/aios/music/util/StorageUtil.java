package com.aispeech.aios.music.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.os.StatFs;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.config.Configs;
import com.aispeech.aios.music.db.MusicLocalDaoImpl;
import com.aispeech.aios.music.pojo.MusicInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods about storage management
 */
public class StorageUtil {

    private static final String TAG = "AISph-StorageUtil";

    /**
     * 检查文件夹是否存在，不存在则创建它
     */
    public static void checkFolderExists(String uri) {
        String dataDir = Environment.getDataDirectory().getAbsolutePath();
        if (dataDir.equals(uri.substring(0, 5))) {
            File file = new File(uri);
            if (!file.exists()) {
                file.mkdir();
            }
        } else {
            String sdcardDir = Configs.SD_CARD_DIR;
            if (!new File(sdcardDir).canRead()) {
                return;
            }
            File file = new File(uri);
            if (!file.exists()) {
                file.mkdir();
            }
        }
    }

    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
        } finally {
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap decodeBitmapWithConfig(String path, Config cf) {
        Options op = new Options();
        op.inPreferredConfig = cf;
        try {
            return BitmapFactory.decodeFile(path, op);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取system根目录
     *
     * @return
     * @see
     */
    public static String getSystemDirectory() {
        return "/system";
    }

    public static boolean deletefile(String delpath) {
        File file = new File(delpath);
        // 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
        if (!file.isDirectory()) {
            file.delete();
        } else if (file.isDirectory()) {
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                File delfile = new File(delpath + "/" + filelist[i]);
                if (!delfile.isDirectory()) {
                    delfile.delete();
                } else if (delfile.isDirectory()) {
                    deletefile(delpath + "/" + filelist[i]);
                }
            }
            file.delete();
        }
        return true;
    }

    private static final int ERROR = -1;

    /**
     * SDCARD是否存
     */
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取SDCARD剩余存储空间，单位是Byte
     *
     * @return
     */
    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    /**
     * 获取SDCARD总的存储空间
     *
     * @return
     */
    public static long getTotalExternalMemorySize(String path) {
        if (externalMemoryAvailable()) {
            try {
                StatFs stat = new StatFs(path);
                long blockSize = stat.getBlockSize();
                long totalBlocks = stat.getBlockCount();
                return totalBlocks * blockSize;
            } catch (Exception e) {
                AILog.e(TAG, e.toString());
                return ERROR;
            }
        } else {
            return ERROR;
        }
    }


    /**
     * 某个音乐目录下，音乐总体积超过多少兆后，就删除老的超出兆数的歌曲
     *
     * @param context   上下文
     * @param rootDir   音乐根目录
     * @param allowSize 允许的总歌曲大小(单位：M)
     * @param list      列表
     */
    public static void guaranteeSpace(Context context, String rootDir, int allowSize, List<MusicInfo> list) {
        long currentTotalSize = getSize(rootDir);
        AILog.i(TAG, "当前空间大小为：" + currentTotalSize);

        if (currentTotalSize != -1) {//內部空间存在
            long maxAllowSize = allowSize * 1024 * 1024;//允许的空间大小
            AILog.i(TAG, "歌曲允许的最大空间：" + maxAllowSize);

            if (currentTotalSize > maxAllowSize) {

                long needDelete = currentTotalSize - maxAllowSize;//应该删除多少兆歌曲
                AILog.i(TAG, "应该删除多少兆歌曲：" + needDelete);

                long currentNeedDelete = 0;
                int index = list.size() - 1;//需要从列表哪个位置删除

                File music;
                for (int i = list.size() - 1; i > 0; --i) {
                    music = new File(list.get(i).getPath());
                    currentNeedDelete += music.length();
                    if (currentNeedDelete > needDelete) {
                        index = i;
                        break;
                    }
                }
                AILog.i(TAG, "确定要删除的歌曲总大小：" + currentNeedDelete);
                AILog.i(TAG, "应该还留歌曲数目" + index);
                delete(context, list, index);//删除掉老的歌曲
            }
        } else {
            AILog.e(TAG, "內部空间不存在");
        }
    }

    /**
     * @param list  删除数据库中太老的数据，并且删除数据对应的本地记录
     * @param index 从哪里开始删除
     */
    private static void delete(Context context, List<MusicInfo> list, int index) {
        MusicLocalDaoImpl dao = new MusicLocalDaoImpl(context);
        int size = list.size();
        AILog.i(TAG, "列表长度：" + size);
        for (int i = index; i < size; ++i) {//删除数据库记录和本地文件
            MusicInfo info = list.get(i);
            if (!info.isCloudMusic()) {
                FileUtil.delete(new File(info.getPath()));
                list.remove(info);
                dao.delete(info);
            }
        }

        /*if (size > index) {
            List<MusicInfo> subList = list.subList(0, index - 1);
            list.clear();
            list.addAll(subList);
        }*/
    }

    private static long getSize(String path) {
        long totalSize = 0;

        List<String> fileList = new ArrayList<String>();
        initFileList(fileList, path);

        for (String filePath : fileList) {
            totalSize += new File(filePath).length();
        }

        return totalSize;
    }

    private static void initFileList(List<String> fileList, String path) {
        try {
            File[] files = new File(path).listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    initFileList(fileList, file.getAbsolutePath());
                } else {
                    fileList.add(file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            AILog.e(TAG, e.toString());
        }
    }
}
