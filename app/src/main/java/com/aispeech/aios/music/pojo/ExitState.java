package com.aispeech.aios.music.pojo;

/**
 * 需要在音乐退出的时候，把音乐的各种状态记录下来，
 * 方便下次重新进入的时候回到那个状态。
 * Created by Jervis on 2015/12/8.
 */
public class ExitState {



    private int position;//音乐列表位置

    private int progress;//音乐进度条位置

    private boolean playing;//上次退出时是否在播放

    private boolean canReplay;//可以重头开始播放



    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean isCanReplay() {
        return canReplay;
    }

    public void setCanReplay(boolean canReplay) {
        this.canReplay = canReplay;
    }

    @Override
    public String toString() {
        return "ExitState{" +
                "position=" + position +
                ", progress=" + progress +
                ", playing=" + playing +
                ", canReplay=" + canReplay +
                '}';
    }
}
