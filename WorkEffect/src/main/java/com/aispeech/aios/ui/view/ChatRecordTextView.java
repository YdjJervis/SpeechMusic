package com.aispeech.aios.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-05-13
 * @copyright aispeech.com
 */
public class ChatRecordTextView extends TextView {

    private Paint mPaint;

    public ChatRecordTextView(Context context) {
        this(context, null);
    }

    public ChatRecordTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatRecordTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);

        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }
}
