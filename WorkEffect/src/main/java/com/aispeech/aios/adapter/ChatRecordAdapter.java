package com.aispeech.aios.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aispeech.aios.bean.ChatRecord;
import com.aispeech.myapplication.R;

import java.util.List;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-05-11
 * @copyright aispeech.com
 */
public class ChatRecordAdapter extends BaseAdapter {

    public static final String TAG = "ChatRecordAdapter";
    public static final int ANIM_DURATION = 300;
    private List<ChatRecord> mList;
    private Context mContext;
    private int mHalfScreenWidth;
    private int mNewItemPosition = -1;
    private AnimatorSet mAnimatorSet;

    private int mLeftMargin;
    private int mRightMargin;
    private boolean mMarginMeasured;

    /**
     * @param screenWidth 列表所占据的屏幕的宽度
     */
    public ChatRecordAdapter(Context context, List<ChatRecord> list, int screenWidth) {
        mContext = context;
        mList = list;
        mHalfScreenWidth = screenWidth >> 1;

        mAnimatorSet = new AnimatorSet();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.item_chat_list, null);

            viewHolder.left = (TextView) convertView.findViewById(R.id.item_list_left);
            viewHolder.right = (TextView) convertView.findViewById(R.id.item_list_right);
            measureMargin(viewHolder.left, viewHolder.right);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChatRecord record = mList.get(position);
        if (record.isOS) {
            viewHolder.left.setVisibility(View.VISIBLE);
            viewHolder.right.setVisibility(View.GONE);
            viewHolder.left.setText(mList.get(position).record);
            viewHolder.left.setMaxWidth(mHalfScreenWidth);
            animLeft(viewHolder.left, position);
        } else {
            viewHolder.left.setVisibility(View.GONE);
            viewHolder.right.setVisibility(View.VISIBLE);
            viewHolder.right.setText(mList.get(position).record);
            viewHolder.right.setMaxWidth(mHalfScreenWidth);
            animRight(viewHolder.right, position);
        }


        return convertView;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void measureMargin(View left, View right) {
        if (!mMarginMeasured) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)left.getLayoutParams();
            mLeftMargin = params.getMarginStart();

            params = (ViewGroup.MarginLayoutParams)right.getLayoutParams();
            mRightMargin = params.getMarginStart();
            mMarginMeasured = true;
        }

    }

    private void animLeft(View view, int position) {
        if (mNewItemPosition == position) {
            view.setPivotX(mLeftMargin);
            view.setPivotY(view.getMeasuredHeight());
            initAnim(view);
            mNewItemPosition = -1;
        }
    }

    private void animRight(View view, int position) {
        if (mNewItemPosition == position) {
            view.setPivotX(view.getMeasuredWidth()+mRightMargin);
            view.setPivotY(view.getMeasuredHeight());
            initAnim(view);
            mNewItemPosition = -1;
        }
    }

    private void initAnim(View view) {
        mAnimatorSet.cancel();
        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 0, 1).setDuration(ANIM_DURATION);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 0, 1).setDuration(ANIM_DURATION);
        mAnimatorScaleX.setInterpolator(new OvershootInterpolator());
        mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY);
        mAnimatorSet.start();
    }

    public void notifyDataSetChanged(int newItemPosition) {
        mNewItemPosition = newItemPosition;
        super.notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView left;
        TextView right;
    }
}
