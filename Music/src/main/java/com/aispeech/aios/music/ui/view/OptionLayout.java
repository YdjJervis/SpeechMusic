package com.aispeech.aios.music.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aispeech.aios.music.helper.VolleyHelper;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music2.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-04-18
 * @copyright aispeech.com
 */
public class OptionLayout extends RelativeLayout implements View.OnClickListener {

    private CircleImageView mCircleImageView;
    private TextView mTipTextView;
    private TextView mTimeTextView;
    private ImageView mPlayPauseIV;
    private ImageView mPlayNextIV;
    private SeekBar mSeekBar;
    private OnOptionLayoutListener mOnOptionLayoutListener;

    private boolean mIsPlaying;

    public OptionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_option, this);
        mCircleImageView = (CircleImageView) view.findViewById(R.id.option_iv_head);
        mTipTextView = (TextView) view.findViewById(R.id.option_tv_tip);
        mTimeTextView= (TextView) view.findViewById(R.id.option_tv_time);
        mPlayPauseIV = (ImageView) view.findViewById(R.id.option_play_pause);
        mPlayNextIV = (ImageView) view.findViewById(R.id.option_play_next);
        mSeekBar = (SeekBar) view.findViewById(R.id.option_sb_progress);
        mPlayPauseIV.setOnClickListener(this);
        mPlayNextIV.setOnClickListener(this);
        pause();
    }

    public void notifyChange(MusicInfo musicInfo) {
        if(musicInfo!=null) {
            VolleyHelper.getInstance().getImage(mCircleImageView, musicInfo.picture);
            mTipTextView.setText(new StringBuilder(musicInfo.getName()).append(" - ").append(musicInfo.getArtist()).toString());
        }
    }

    public void pause() {
        mPlayPauseIV.setImageResource(R.mipmap.btn_playback_play);
    }

    public void resume() {
        mPlayPauseIV.setImageResource(R.mipmap.btn_playback_pause);
    }

    @Override
    public void onClick(View view) {
        if (mOnOptionLayoutListener != null) {
            switch (view.getId()) {
                case R.id.option_play_pause:
                    if (mIsPlaying) {
                        pause();
                        mOnOptionLayoutListener.onPauseClick();
                    }else{
                        resume();
                        mOnOptionLayoutListener.onResumeClick();
                    }
                    mIsPlaying = !mIsPlaying;
                    break;
                case R.id.option_play_next:
                    mOnOptionLayoutListener.onNextClick();
                    break;
            }
        }
    }

    public void setProgress(int maxProgress,int currentProgress){
        mSeekBar.setMax(maxProgress);
        mSeekBar.setProgress(currentProgress);
    }

    public void setTime(String maxTime,String currentTime){
        mTimeTextView.setText(new StringBuffer(currentTime).append(" | ").append(maxTime));
    }

    public void setOnOptionLayoutListener(OnOptionLayoutListener onOptionLayoutListener) {
        mOnOptionLayoutListener = onOptionLayoutListener;
    }

    public interface OnOptionLayoutListener {
        void onPauseClick();

        void onResumeClick();

        void onNextClick();
    }
}
