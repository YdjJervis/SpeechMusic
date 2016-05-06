package com.aispeech.aios.music.util;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.pojo.Lrc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-04-20
 * @copyright aispeech.com
 */
public class LrcHelper {

    private static final String TAG = "AIOS-LrcHelper";

    /**
     * 根据指定lrc文件生成
     *
     * @param path   文件全名
     * @param artist 歌手
     * @param title  歌名
     * @return Lrc object or null
     */
    public Lrc getLrc(String path, String artist, String title) {
        List<String> list = FileUtil.readTxtFile(path);
        if (list.size() == 0) {
            return null;
        } else {
            for (String line : list) {
                AILog.i(TAG, line);
            }
        }

        Lrc lrc = new Lrc();
        lrc.artist = artist;
        lrc.title = title;

        lrc.contentList = new ArrayList<>();
        Lrc.Content content;
        for (String line : list) {

            int inLeft = line.indexOf("[");//每一行第一个“[”符号的下标
            int inRight = line.lastIndexOf("]");//每一行最后一个“]”符号的下标
            AILog.i(TAG, "inLeft:" + inLeft + " inRight:" + inRight);

            if ((inRight - inLeft + 1) % 10 == 0) {//如果是"[01:06.60][03:01.61]早就已经发生过"这样的字符串

                int count = (inRight - inLeft + 1) / 10;
                for (int i = 0; i < count; i++) {
                    content = lrc.new Content();

                    content.time = line.substring(10 * i + 1, 10 * i + 9);
                    content.text = line.substring(inRight + 1, line.length());
                    AILog.i(TAG, content);
                    lrc.contentList.add(content);
                }
            } else {
                AILog.i(TAG, "此行不用解析");
            }
        }

        Collections.sort(lrc.contentList,new LycSort());

        return lrc;
    }

    private class LycSort implements Comparator<Lrc.Content> {

        SimpleDateFormat mFormat = new SimpleDateFormat("mm:ss");

        @Override
        public int compare(Lrc.Content left, Lrc.Content right) {

            Date dateLeft;
            Date dateRight;
            try {
                dateLeft = mFormat.parse(left.time);
                dateRight = mFormat.parse(right.time);
            } catch (ParseException e) {
                AILog.i(TAG, e);
                return 0;
            }

            if(dateLeft.getTime()>dateRight.getTime()){
                return 1;
            }else{
                return -1;
            }

        }
    }
}
