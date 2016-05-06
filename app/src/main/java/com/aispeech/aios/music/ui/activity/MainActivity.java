package com.aispeech.aios.music.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.AIMusicApp;
import com.aispeech.aios.music.adapter.MusicCatViewPagerAdapter;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music.service.MusicPlayerServiceConnector;
import com.aispeech.aios.music.ui.fragment.LocalMusicFragment;
import com.aispeech.aios.music.ui.fragment.RepoMusicFragment;
import com.aispeech.aios.music.ui.view.NavLayout;
import com.aispeech.aios.music.ui.view.OptionLayout;
import com.aispeech.aios.music2.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener,
        NavLayout.OnNavClickListener, View.OnClickListener,RepoMusicFragment.OnRepoMusicFragmentListener,
        LocalMusicFragment.OnLocalMusicFragmentListener,OptionLayout.OnOptionLayoutListener{

    private static final String TAG = "MainActivity";

    private NavLayout mNavLayout;

    private ViewPager mMusicViewPager;
    private MusicCatViewPagerAdapter mCatViewPagerAdapter;

    private List<Fragment> mFragmentList;

    private OptionLayout mOptionLayout;
    private LocalMusicFragment mLocalMusicFragment;
    private RepoMusicFragment mRepoMusicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initMusicService();
    }

    private void initView() {

        mNavLayout = (NavLayout) findViewById(R.id.ll_nav);
        mNavLayout.setOnNavClickListener(this);

        mMusicViewPager = (ViewPager) findViewById(R.id.music_view_pager);
        mMusicViewPager.setOnPageChangeListener(this);

        mFragmentList = new ArrayList<>();

        mLocalMusicFragment = new LocalMusicFragment();
        mRepoMusicFragment = new RepoMusicFragment();
        mFragmentList.add(mLocalMusicFragment);
        mFragmentList.add(mRepoMusicFragment);

        mRepoMusicFragment.setOnRepoMusicFragmentListener(this);
        mLocalMusicFragment.setOnLocalMusicFragmentListener(this);

        mCatViewPagerAdapter = new MusicCatViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mMusicViewPager.setAdapter(mCatViewPagerAdapter);

        mOptionLayout = (OptionLayout) findViewById(R.id.layout_music_option);
        mOptionLayout.setOnClickListener(this);
        mOptionLayout.setOnOptionLayoutListener(this);
        mOptionLayout.notifyChange(mLocalMusicFragment.getCurrentMusic());
    }

    private void initMusicService() {
        MusicPlayerServiceConnector mMusicPlayerServiceConnector = MusicPlayerServiceConnector.getInstance(AIMusicApp.getContext());
        mMusicPlayerServiceConnector.doBindService();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        AILog.i(TAG, "position:" + position + " positionOffset:" + positionOffset + " positionOffsetPixels:" + positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        AILog.i(TAG, "position:" + position);
        mNavLayout.setSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        AILog.i(TAG, "state:" + state);
    }

    @Override
    public void onClick(int position) {
        mMusicViewPager.setCurrentItem(position);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_music_option:
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                intent.putExtra("musicInfo",new Gson().toJson(mLocalMusicFragment.getCurrentMusic()));
                startActivity(intent);
                overridePendingTransition(R.anim.activity_music_second_in, R.anim.activity_music_first_out);
                break;
        }
    }

    @Override
    public void onDownload(MusicInfo musicInfo) {
        mLocalMusicFragment.play(0);
        mMusicViewPager.setCurrentItem(0);
        mOptionLayout.notifyChange(musicInfo);
    }

    @Override
    public void onClick(MusicInfo musicInfo) {
        mOptionLayout.notifyChange(musicInfo);
    }

    @Override
    public void onMusicPause() {
        mOptionLayout.pause();
    }

    @Override
    public void onMusicResume() {
        mOptionLayout.resume();
    }

    @Override
    public void onOptionPanelChanged(MusicInfo musicInfo) {
        mOptionLayout.notifyChange(musicInfo);
    }

    @Override
    public void onProgressChanged(int total, int current) {
        mOptionLayout.setProgress(total,current);
    }

    @Override
    public void onTimeChanged(String totalTime, String currentTime) {
        mOptionLayout.setTime(totalTime,currentTime);
    }

    @Override
    public void onPauseClick() {
        mLocalMusicFragment.onPauseAction();
    }

    @Override
    public void onResumeClick() {
        mLocalMusicFragment.onPlayAction();
    }

    @Override
    public void onNextClick() {
        mLocalMusicFragment.onNextAction();
    }
}
