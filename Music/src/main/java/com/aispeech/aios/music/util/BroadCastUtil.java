package com.aispeech.aios.music.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.aispeech.ailog.AILog;

/**
 * Created by hehr on 2015/10/8.
 * 广播发送通用类
 */
public class BroadCastUtil {

    private static final String TAG = "BroadCastUtil";

    private Context context;
    private static BroadCastUtil mUtil;
    private Intent it;

    private BroadCastUtil(Context context) {

        this.context = context;
        this.it = new Intent();

    }

    public static synchronized BroadCastUtil getInstance(Context context) {

        if (null == mUtil) {
            mUtil = new BroadCastUtil(context);
        }
        return mUtil;
    }

    /**
     * 广播发送方法
     *
     * @param action 广播action
     * @param name   数据 key
     * @param data   携带 数据
     */
    public void send(String action, String name, String data) {

        if (null != it) {
            it.setAction(action);
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(data)) {//如果携带有数据

                it.putExtra(name, data);

            }
            if (context != null) {
                context.sendBroadcast(it);
            }else{
                AILog.i(TAG,"Context为空，不能发送广播");
            }
        }else{
            AILog.i(TAG,"intent为空，不能发送广播");
        }
    }
}
