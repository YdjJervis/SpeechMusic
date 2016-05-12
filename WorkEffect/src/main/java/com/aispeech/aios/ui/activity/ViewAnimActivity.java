package com.aispeech.aios.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.aispeech.aios.ui.view.RobotView;
import com.aispeech.myapplication.R;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-05-12
 * @copyright aispeech.com
 */
public class ViewAnimActivity extends Activity {

    private RobotView mRobotView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_anim);
        mRobotView = (RobotView) findViewById(R.id.r_robot_view);
    }

    public void startListening(View view){
        mRobotView.startListening();
    }

    public void stopListening(View view){
        mRobotView.stopListening();
    }

    public void startRecog(View view){
        mRobotView.startRecognition();
    }

    public void stopRecog(View view){
        mRobotView.stopRecognition();
    }
}
