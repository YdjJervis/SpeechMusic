package com.aispeech.aios.music.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AITimer extends Timer {
    private static AITimer mTimer;
    private static Map<String, TimerTask> mTaskMap = new HashMap<String, TimerTask>();

    private AITimer() {
    }

    public static Timer getInstance() {
        if (mTimer == null) {
            mTimer = new AITimer();
        }
        return mTimer;
    }

    public void startTimer(TimerTask task, String taskName, int millisec) {
        TimerTask timerTask = (TimerTask) mTaskMap.get(taskName);
        if (timerTask != null) {
            timerTask.cancel();
            mTaskMap.remove(taskName);
        }
        mTaskMap.put(taskName, task);
        try {
            mTimer.schedule(task, millisec);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void startTimer(TimerTask task, String taskName, int firstDelay, int millisecInterval) {
        TimerTask timerTask = (TimerTask) mTaskMap.get(taskName);
        if (timerTask != null) {
            timerTask.cancel();
            mTaskMap.remove(taskName);
        }
        mTaskMap.put(taskName, task);
        try {
            mTimer.schedule(task, firstDelay, millisecInterval);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void cancelTimer(String taskName) {
        TimerTask timerTask = (TimerTask) mTaskMap.get(taskName);
        if (timerTask != null) {
            timerTask.cancel();
            mTaskMap.remove(taskName);
        }
    }
}

