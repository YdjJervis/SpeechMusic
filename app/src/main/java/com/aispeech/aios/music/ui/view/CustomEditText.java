package com.aispeech.aios.music.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.aispeech.ailog.AILog;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-04-19
 * @copyright aispeech.com
 */
public class CustomEditText extends EditText {

    private static final String TAG = "AIOS-Music-CustomEditText";

    private Drawable mSeachImg;
    private OnClearListener mOnClearListener;

    public CustomEditText(Context context) {
        this(context, null);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void init(Context context) {
        mSeachImg = getCompoundDrawables()[1];
        AILog.i(TAG, mSeachImg);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mOnClearListener != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (event.getRawX() > getRight() - mSeachImg.getBounds().width()) {
                        mOnClearListener.onClear();
                    }
                    break;
            }
        }
        return true;
    }

    public void setOnClearListener(OnClearListener onClearListener) {
        mOnClearListener = onClearListener;
    }

    public interface OnClearListener {
        void onClear();
    }


}
