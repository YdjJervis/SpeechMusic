package com.aispeech.aios.music.util;

import com.aispeech.ailog.AILog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-04-20
 * @copyright aispeech.com
 */
public class FileUtil {

    private static final String TAG = "AIOS-FileUtil";

    /**
     *  功能：Java读取txt文件的内容
     *  步骤：1：先获得文件句柄
     *  2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     *  3：读取到输入流后，需要读取生成字节流
     *  4：一行一行的输出。readline()。
     *  备注：需要考虑的是异常情况
     * @param filePath 文件全名
     * @return String object or null
     */
    public static List<String> readTxtFile(String filePath) {
        List<String> list = new ArrayList<>();

        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在

                read = new InputStreamReader(new FileInputStream(file), "utf-8");//考虑到编码格式
                bufferedReader = new BufferedReader(read);

                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    list.add(lineTxt);
                }

            } else {
                AILog.i(TAG, "找不到指定的文件");
            }
        } catch (Exception e) {
            AILog.i(TAG, "读取文件内容出错");
            AILog.i(TAG, e);
        } finally {
            try {
                if (bufferedReader != null) {
                    read.close();
                }
                if (bufferedReader != null) {
                    read.close();
                }
            } catch (IOException e) {
                AILog.i(TAG, "文件流关闭异常");
                AILog.i(TAG, e);
            }
        }
        return list;
    }

    public static void delete(final File file) {
        if (file != null && file.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        file.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
