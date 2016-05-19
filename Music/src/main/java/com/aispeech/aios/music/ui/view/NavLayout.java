package com.aispeech.aios.music.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aispeech.aios.music2.R;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-04-18
 * @copyright aispeech.com
 */
public class NavLayout extends LinearLayout implements View.OnClickListener {

    private TextView mLocal;
    private TextView mClod;
    private int mColorNormal;
    private int mColorSelected;
    private OnNavClickListener mNavClickListener;

    public NavLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_nav, this);
        Resources resources = context.getResources();
        mColorNormal = resources.getColor(R.color.nav_tv_mormal);
        mColorSelected = resources.getColor(R.color.nav_tv_selected);

        mLocal = (TextView) view.findViewById(R.id.tv_music_local);
        mClod = (TextView) view.findViewById(R.id.tv_music_cloud);
        mLocal.setOnClickListener(this);
        mClod.setOnClickListener(this);
        setSelected(0);
    }

    public void setSelected(int position) {

        switch (position) {
            case 1:
                mLocal.setTextColor(mColorNormal);
                mClod.setTextColor(mColorSelected);
                break;
            default:
                mLocal.setTextColor(mColorSelected);
                mClod.setTextColor(mColorNormal);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (mNavClickListener != null) {
            switch (view.getId()) {
                case R.id.tv_music_local:
                    mNavClickListener.onClick(0);
                    break;
                case R.id.tv_music_cloud:
                    mNavClickListener.onClick(1);
                    break;
            }
        }
    }

    public void setOnNavClickListener(OnNavClickListener l) {
        mNavClickListener = l;
    }

    public interface OnNavClickListener {
        void onClick(int position);
    }

}
