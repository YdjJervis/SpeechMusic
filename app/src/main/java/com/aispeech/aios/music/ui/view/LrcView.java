package com.aispeech.aios.music.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.pojo.Lrc;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-04-21
 * @copyright aispeech.com
 */
public class LrcView extends TextView {

    private static final String TAG = "AIOS-Music-LrcView";

    private float mWidth;        //歌词视图宽度
    private float mHeight;       //歌词视图高度
    private Paint currentPaint; //当前画笔对象
    private Paint notCurrentPaint;  //非当前画笔对象
    private float textHeight = 25;  //文本高度
    private float textSize = 18;        //文本大小
    private int mIndex = 0;      //list集合下标

    private Lrc mLrc;

    public LrcView(Context context) {
        this(context, null);
    }

    public LrcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setLrc(Lrc lrc) {
        mLrc = lrc;
    }

    private void init() {
        setFocusable(true);     //设置可对焦

        //高亮部分
        currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currentPaint.setTextAlign(Paint.Align.CENTER);//设置文本对齐方式
        currentPaint.setColor(Color.argb(210, 251, 248, 29));
        currentPaint.setTextSize(24);
        currentPaint.setTypeface(Typeface.SERIF);

        //非高亮部分
        notCurrentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
        notCurrentPaint.setColor(Color.argb(140, 255, 255, 255));
        notCurrentPaint.setTextSize(textSize);
        notCurrentPaint.setTypeface(Typeface.DEFAULT);
    }

    /**
     * 绘画歌词
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) {
            return;
        }

        try {
            setText("");
            canvas.drawText(mLrc.contentList.get(mIndex).text, mWidth / 2, mHeight / 2, currentPaint);

            float tempY = mHeight / 2;
            //画出本句之前的句子
            for (int i = mIndex - 1; i >= 0; i--) {
                //向上推移
                tempY = tempY - textHeight;
                canvas.drawText(mLrc.contentList.get(i).text, mWidth / 2, tempY, notCurrentPaint);
            }
            tempY = mHeight / 2;
            //画出本句之后的句子
            for (int i = mIndex + 1; i < mLrc.contentList.size(); i++) {
                //往下推移
                tempY = tempY + textHeight;
                canvas.drawText(mLrc.contentList.get(i).text, mWidth / 2, tempY, notCurrentPaint);
            }
        } catch (Exception e) {
            canvas.drawText("此歌曲没有歌词", mWidth / 2, mHeight / 2, currentPaint);
        }
    }

    /**
     * 当view大小改变的时候调用的方法
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    public void setIndex(int index) {
        mIndex = index;
        invalidate();
    }

    /**
     * @param time 02:14这样的格式
     */
    public void setIndex(String time) {
        mIndex = getIndex(time);
        invalidate();
    }

    private int getIndex(String timeValidate) {
        for (int index = 0; index < mLrc.contentList.size(); index++) {
            String timeNow = mLrc.contentList.get(index).time;
            String timeNext;
            if (index >= mLrc.contentList.size() + 1) {
                timeNext = mLrc.contentList.get(index).time;
            } else {
                timeNext = mLrc.contentList.get(index + 1).time;
            }
            AILog.i(TAG, "timeNow=" + timeNow + " timeNext=" + timeNext);

            int timeNowMin = Integer.valueOf(timeNow.substring(0, 2));
            int timeNowSec = Integer.valueOf(timeNow.substring(3, 5));

            int timeNextMin = Integer.valueOf(timeNext.substring(0, 2));
            int timeNextSec = Integer.valueOf(timeNext.substring(3, 5));

            int timeVaMin = Integer.valueOf(timeValidate.substring(0, 2));
            int timeVaSec = Integer.valueOf(timeValidate.substring(3, 5));

            if ((timeVaMin >= timeNowMin && timeVaMin <= timeNextMin) && (timeVaSec >= timeNowSec && timeVaSec <= timeNextSec)) {
                return index;
            }
        }
        return 0;
    }
}
