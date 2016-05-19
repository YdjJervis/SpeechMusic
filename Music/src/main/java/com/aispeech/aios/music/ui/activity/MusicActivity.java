package com.aispeech.aios.music.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.config.Configs;
import com.aispeech.aios.music.helper.VolleyHelper;
import com.aispeech.aios.music.pojo.Lrc;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music.pojo.PlayProgress;
import com.aispeech.aios.music.ui.view.LrcView;
import com.aispeech.aios.music.util.LrcHelper;
import com.aispeech.aios.music2.R;
import com.google.gson.Gson;

import java.io.File;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-04-20
 * @copyright aispeech.com
 */
public class MusicActivity extends Activity {

    private static final String TAG = "AIOS-Music-MusicActivity";

    private LrcView mLrcView;

    private Lrc mLrc;

    private MusicInfo mMusicInfo;
    private CircleImageView mHeadImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        initData();
        initView();
        EventBus.getDefault().register(this);
    }

    private void initData() {
        mMusicInfo = new Gson().fromJson(getIntent().getStringExtra("musicInfo"), MusicInfo.class);
    }

    private void initView() {
        mLrcView = (LrcView) findViewById(R.id.lv_lyric);

        String lrcPath = new StringBuffer(Configs.MUSIC_CACHE_PATH).append("lrc").append(File.separator)
                .append(mMusicInfo.getName()).append(" - ").append(mMusicInfo.getArtist()).append(".lrc").toString();
        if (new File(lrcPath).exists()) {
            mLrc = new LrcHelper().getLrc(lrcPath, mMusicInfo.getArtist(), mMusicInfo.getName());
            mLrcView.setLrc(mLrc);
            mLrcView.setIndex(0);
        }

        mHeadImageView = (CircleImageView) findViewById(R.id.detailed_ci_head);
        VolleyHelper.getInstance().getImage(mHeadImageView, mMusicInfo.picture);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_music_first_in, R.anim.activity_music_second_out);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(PlayProgress progress) {
        AILog.i(TAG, "onEventMainThread.." + progress);
        mLrcView.setIndex(progress.currentTime);
    }
}
