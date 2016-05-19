package com.aispeech.aios.lrc;

import android.test.AndroidTestCase;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.pojo.Lrc;
import com.aispeech.aios.music.util.LrcHelper;

import java.io.File;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-04-20
 * @copyright aispeech.com
 */
public class LrcHelperTest extends AndroidTestCase {

    private static final String TAG = "AIOS-LrcHelperTest";

    public void testAnalyseLycFile() {
        Lrc lrc = new LrcHelper().getLrc("sdcard/lyc/333.lrc", "周杰伦", "算什么男人");

        for (Lrc.Content content : lrc.contentList) {
            AILog.i(TAG, content.time + " " + content.text);
        }

    }

    public void testFile() {

        for (File file : new File("sdcard/lyc").listFiles()) {
            AILog.i(TAG, file.getAbsolutePath());
        }
    }

    public void testIndex() {
        String string = "[00:07.00]词曲：伍佰";
        AILog.i(TAG, string.indexOf("[") + " " + string.lastIndexOf("]"));
        AILog.i(TAG, string.substring(0, 9));
    }
}
