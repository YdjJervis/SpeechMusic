package com.aispeech.aios;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ListView;

import com.aispeech.aios.adapter.ChatRecordAdapter;
import com.aispeech.aios.bean.ChatRecord;
import com.aispeech.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {


    private ListView mListViewChat;
    private ChatRecordAdapter mChatRecordAdapter;
    private String[] str = {"有什么可以帮您", "查看今天深圳的限行情况", "稍等",
            "给我讲个笑话", "正在查询，哥喝酒过河堤额定功耗对更黑如果俄日会给如果和俄日换个可如果和扩大的高；了几个人供热",
            "滚", "主任再见", "你今年多大了", "我今年24了", "你有小孩了吗？", "论家还没结婚了"};
    private List<ChatRecord> mList;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i("aaa", "1133");
            mChatRecordAdapter.notifyDataSetChanged(mList.size() - 1);
            Log.i("aaa", "1144");
            mListViewChat.smoothScrollToPosition(mList.size() - 1);
            Log.i("aaa", "1155");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListViewChat = (ListView) findViewById(R.id.lv_chat_list);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        initList();
        mChatRecordAdapter = new ChatRecordAdapter(this, mList, metric.widthPixels);
        mListViewChat.setAdapter(mChatRecordAdapter);
        mListViewChat.setSelection(mList.size() - 1);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ChatRecord record = new ChatRecord();
                record.record = "从来不骑的小毛驴";
//                record.isOS = true;
                Log.i("aaa", "11");
                mList.add(record);
                Log.i("aaa", "1122");
                mHandler.sendEmptyMessage(0);
            }
        }, 2000);
    }

    private void initList() {
        mList = new ArrayList<ChatRecord>();
        for (int i = 0; i < 10; i++) {
            ChatRecord record = new ChatRecord();
            record.isOS = false;
            if (i % 2 == 0) {
                record.isOS = true;
            }
            record.record = str[i];
            mList.add(record);
        }
    }
}
