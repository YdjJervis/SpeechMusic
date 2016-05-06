package com.aispeech.aios.music.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.AIMusicApp;
import com.aispeech.aios.music.adapter.BaseAdapter;
import com.aispeech.aios.music.adapter.MusicLocalAdapter;
import com.aispeech.aios.music.db.MusicLocalDaoImpl;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music.pojo.PlayProgress;
import com.aispeech.aios.music.service.MusicPlayerClient;
import com.aispeech.aios.music.util.TimeUtils;
import com.aispeech.aios.music2.R;

import java.util.List;

import de.greenrobot.event.EventBus;

public class LocalMusicFragment extends Fragment implements BaseAdapter.OnItemClickListener {

    private static final String TAG = "AIOS-Music-LocalMusicFragment";

    private RecyclerView mLocalMusicRecyclerView;
    private MusicLocalDaoImpl mLocalMusicDao;
    private List<MusicInfo> mMusicInfoList;

    private MusicLocalAdapter mMusicLocalAdapter;

    private MusicPlayerClient mPlayerClient;

    private int mCurrentPosition;

    private TimeUtils mTimeUtils;

    private OnLocalMusicFragmentListener mOnLocalMusicFragmentListener;
    private Handler mPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PlayState.READY:
                    if (mOnLocalMusicFragmentListener != null) {
                        if (mCurrentPosition <= mMusicInfoList.size() - 1 && mMusicInfoList.size() >= 0) {
                            MusicInfo musicInfo = mMusicInfoList.get(mCurrentPosition);
                            if (musicInfo.isCloudMusic()) {

                            } else {
                                //处理标题
                                mOnLocalMusicFragmentListener.onOptionPanelChanged(getCurrentMusic());
                            }
                            //处理seekbar
                            int total = (int) mPlayerClient.getDuration();
                            AILog.i(TAG, "进度条总长：" + total);

                            mOnLocalMusicFragmentListener.onProgressChanged(total, 0);
                            //处理播放按钮
                            setPlayTime();
                        }
                    }
                    break;
                case PlayState.COMPLETE:
                    onNextAction();
                    break;
                case PlayState.PROGRESS_STR:
                    setPlayTime();
                    int position = (int) mPlayerClient.getPosition();
                    int total = (int) mPlayerClient.getDuration();
                    mOnLocalMusicFragmentListener.onProgressChanged(total, position);
                    break;
            }
        }
    };
    private MusicPlayerClient.MusicPlayerNotifyer mMusicPlayerNotifyer = new MusicPlayerClient.MusicPlayerNotifyer() {
        @Override
        public void onReady() {
            mPlayHandler.sendEmptyMessage(PlayState.READY);
        }

        @Override
        public void onComplete() {
            mPlayHandler.sendEmptyMessage(PlayState.COMPLETE);
        }

        @Override
        public void onProgressStr(String prog) {
            mPlayHandler.sendEmptyMessage(PlayState.PROGRESS_STR);
        }

        @Override
        public void onLoadBegin() {
//            showProgressBar();

        }

        @Override
        public void onLoadEnd() {
//            dismissProgressBar();
        }

    };

    public LocalMusicFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, container, false);
        initData();
        initView(view);
        return view;
    }

    private void initData() {
        mLocalMusicDao = new MusicLocalDaoImpl(getActivity());
        mMusicInfoList = mLocalMusicDao.findAll();
    }

    /**
     * 只做列表刷新
     */
    public void notifyDataSetChanged() {
        mMusicInfoList.clear();
        mMusicInfoList.addAll(mLocalMusicDao.findAll());
        mMusicLocalAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新列表并且播放
     *
     * @param position 位置
     */
    public void play(int position) {
        notifyDataSetChanged();
        onItemClick(null, position);
    }

    /**
     * 设置已经播放的时间和总的时间
     */
    private void setPlayTime() {
        if (mOnLocalMusicFragmentListener != null) {
            int position = (int) mPlayerClient.getPosition();
            int duration = (int) mPlayerClient.getDuration();
            String currentTime = mTimeUtils.formateSec(position / 1000);
            String totalTime = mTimeUtils.formateSec(duration / 1000);
            mOnLocalMusicFragmentListener.onTimeChanged(totalTime, currentTime);

            AILog.i(TAG,"刷新歌词");
            EventBus.getDefault().post(new PlayProgress(currentTime));
        }
    }

    private void initView(View view) {

        mPlayerClient = new MusicPlayerClient(AIMusicApp.getContext(), mMusicPlayerNotifyer);

        mLocalMusicRecyclerView = (RecyclerView) view.findViewById(R.id.rv_local_music);
        mLocalMusicRecyclerView.setLayoutManager(new LinearLayoutManager(mLocalMusicRecyclerView.getContext()));

        mMusicLocalAdapter = new MusicLocalAdapter(mMusicInfoList);
        mMusicLocalAdapter.setOnItemClickListener(this);

        mLocalMusicRecyclerView.setAdapter(mMusicLocalAdapter);

        mTimeUtils = new TimeUtils();

    }

    public void onPlayAction() {
        onItemClick(null, mCurrentPosition);
    }

    public void onPauseAction() {
        mPlayerClient.pause();
    }

    public void onNextAction() {
        mCurrentPosition = mCurrentPosition++ % mMusicInfoList.size();
        onItemClick(null, mCurrentPosition);
    }

    @Override
    public void onItemClick(View view, int position) {
        AILog.i(TAG, "onItemClick::" + mMusicInfoList.get(position));

        if (mCurrentPosition == position) {
            if (mPlayerClient.isPlaying()) {
                AILog.i(TAG, "is playing");
                mPlayerClient.pause();
                onMusicPause();
            } else {
                AILog.i(TAG, "is not playing");
                if (mPlayerClient.isPlayingBefore()) {
                    mPlayerClient.resume();
                } else {
                    mPlayerClient.playOneSong(mMusicInfoList.get(mCurrentPosition).getPath());
                }
                onMusicResume();
            }
        } else {
            mCurrentPosition = position;
            mPlayerClient.playOneSong(mMusicInfoList.get(mCurrentPosition).getPath());
            onItemClick();
            onMusicResume();
        }

    }

    private void onItemClick() {
        if (mOnLocalMusicFragmentListener != null) {
            mOnLocalMusicFragmentListener.onClick(mMusicInfoList.get(mCurrentPosition));
        }
    }

    public MusicInfo getCurrentMusic() {
        if (mMusicInfoList == null || mMusicInfoList.size() <= mCurrentPosition) {
            return null;
        } else {
            return mMusicInfoList.get(mCurrentPosition);
        }
    }

    private void onMusicPause() {
        if (mOnLocalMusicFragmentListener != null) {
            mOnLocalMusicFragmentListener.onMusicPause();
        }
    }

    private void onMusicResume() {
        if (mOnLocalMusicFragmentListener != null) {
            mOnLocalMusicFragmentListener.onMusicResume();
        }
    }

    public void setOnLocalMusicFragmentListener(OnLocalMusicFragmentListener onLocalMusicFragmentListener) {
        mOnLocalMusicFragmentListener = onLocalMusicFragmentListener;
    }

    public interface OnLocalMusicFragmentListener {

        void onClick(MusicInfo musicInfo);

        void onMusicPause();

        void onMusicResume();

        void onOptionPanelChanged(MusicInfo musicInfo);

        void onProgressChanged(int total, int current);

        void onTimeChanged(String totalTime, String currentTime);
    }

    private static final class PlayState {
        public static final int READY = 0x001001;
        public static final int COMPLETE = 0x001002;
        public static final int PROGRESS_STR = 0x001003;
        public static final int LOAD_BEGIN = 0x001004;
        public static final int LOAD_END = 0x001005;
    }
}
