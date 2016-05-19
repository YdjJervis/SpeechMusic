package com.aispeech.aios.music.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.config.Configs;
import com.aispeech.aios.music.listener.AbstractOperateListener;
import com.aispeech.aios.music.listener.OnOperateListener;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music.receiver.AccReceiver;
import com.aispeech.aios.music.receiver.AudioMgrReceiver;
import com.aispeech.aios.music.receiver.MusicOperateReceiver;
import com.aispeech.aios.music.receiver.MusicReceiver;
import com.aispeech.aios.music.receiver.PlayCompleteReceiver;
import com.aispeech.aios.music.receiver.PlayerStateReceiver;
import com.aispeech.aios.music.receiver.SingleMusicReceiver;
import com.aispeech.aios.music.sp.ExitSatePreferences;
import com.aispeech.aios.music.util.BroadCastUtil;

import java.lang.ref.WeakReference;
import java.util.List;

public class MusicPlayerService extends Service {

    private static final String TAG = "Music-MusicPlayerService";

    private static final int FOCUSCHANGE = 4;
    private MediaPlayer mMediaPlayer;
    private boolean mIsInitialized = false;
    private Cursor mCursor;
    private AudioManager mAudioManager;

    private MusicInfo mMusicInfo;
    private List<MusicInfo> mMusicInfoList;

    private boolean mIsPlayingBefore;

    private Handler mMediaplayerHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FOCUSCHANGE:
                    switch (msg.arg1) {
                        case AudioManager.AUDIOFOCUS_LOSS:
                            if (isPlaying()) {
                                isPlayingBefore = true;
                                pause();
                            } else {
                                isPlayingBefore = false;
                            }
                            AILog.i(TAG, "<><><>---AIOS音乐 焦点监听：AUDIOFOCUS_LOSS isPlayingBefore:" + isPlayingBefore);
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:  //可以共同使用的
                            AILog.i(TAG, "<><><>---AIOS音乐 焦点监听：AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                            setVolume(0.05f);
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:   //一个暂停，不释放资源,eg:导航
                            AILog.i(TAG, "<><><>---AIOS音乐 焦点监听：AUDIOFOCUS_LOSS_TRANSIENT");
                            setVolume(0.05f);
                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                            AILog.i(TAG, "<><><>---AIOS音乐 焦点监听：AUDIOFOCUS_GAIN isPlayingBefore:" + isPlayingBefore);
                            if (isPlayingBefore) {
                                resume();
                            } else {
                                setVolume(1.0f);
                            }
                            break;
                        default:
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private int mIntentCnt = 0;
    private boolean isPlayingBefore = false;
    private boolean isAIOSMusicPlay = false;

    public MusicPlayerService getService() {
        return MusicPlayerService.this;
    }

    private AudioMgrReceiver mAudioManagerReceiver;
    private AudioMgrReceiver.OnReceiveListener mOnReceiveListener = new AudioMgrReceiver.OnReceiveListener() {
        @Override
        public void volumeUp() {
            if (mIntentCnt > 0) {
                mIntentCnt--;
            } else {
                AILog.i(TAG, "<><><>---AIOS音乐 requestAudioFocus：AUDIOFOCUS_GAIN");
                mAudioManager.requestAudioFocus(mAudioFocusListener,
                        Configs.Channel.MUSIC_AUDIO_CHANNEL, AudioManager.AUDIOFOCUS_GAIN);
            }
            setVolume(1f);
        }

        @Override
        public void volumeDown() {
            setVolume(0.05f);
            mIntentCnt++;
        }

        @Override
        public void pauseMusic() {
            if (isPlaying()) {
                isPlayingBefore = true;
                pause();
            } else {
                isPlayingBefore = false;
            }
        }

        @Override
        public void resumeMusic() {
            if (isPlayingBefore) {
                resume();
            }
        }
    };

    /**
     * 注册音乐广播
     */
    public void registerAudioManagerReceiver() {
        if (mAudioManagerReceiver == null) {
            AILog.i(TAG, "注册音乐广播");
            IntentFilter filter = new IntentFilter();
            mAudioManagerReceiver = new AudioMgrReceiver(filter);
            mAudioManagerReceiver.setOnReceiveListener(mOnReceiveListener);
            registerReceiver(mAudioManagerReceiver, filter);
        }
    }

    /**
     * 解绑音乐广播
     */
    public void unregisterAudioManagerReceiver() {
        if (mAudioManagerReceiver != null) {
            AILog.i(TAG, "取消音乐广播");
            unregisterReceiver(mAudioManagerReceiver);
            mAudioManagerReceiver = null;
        }
    }

    private AccReceiver mAccReceiver;

    /**
     * 注册Acc广播
     */
    private void registerAccReceiver() {
        if (mAccReceiver == null) {
            AILog.i(TAG, "注册Acc广播");
            mAccReceiver = new AccReceiver();
            mAccReceiver.setListener(new AccReceiver.OnAccListener() {
                @Override
                public void onAccOff() {
                    pause();
                }
            });
            IntentFilter filter = new IntentFilter();
            filter.addAction(AccReceiver.ACC_OFF);
            registerReceiver(mAccReceiver, filter);
        }
    }

    /**
     * 解绑Acc广播
     */
    public void unregisterAccReceiver() {
        if (mAccReceiver != null) {
            AILog.i(TAG, "取消Acc广播");
            unregisterReceiver(mAccReceiver);
            mAccReceiver = null;
        }
    }

    private MusicReceiver mMusicReceiver;

    /**
     * 注册音乐广播
     */
    private void registerMusicReceiver() {
        if (mMusicReceiver == null) {
            AILog.i(TAG, "注册音乐广播");
            mMusicReceiver = new MusicReceiver();
            mMusicReceiver.setListener(new MusicReceiver.OnMusicListener() {
                @Override
                public void onExit() {
                    pause();
                }
            });
            IntentFilter filter = new IntentFilter();
            filter.addAction(MusicReceiver.EXIT);
            registerReceiver(mMusicReceiver, filter);
        }
    }

    /**
     * 解绑音乐广播
     */
    public void unregisterMusicReceiver() {
        if (mMusicReceiver != null) {
            AILog.i(TAG, "解绑音乐广播");
            unregisterReceiver(mMusicReceiver);
            mMusicReceiver = null;
        }
    }

    private SingleMusicReceiver mSingleMusicReceiver;

    /**
     * 注册外界获取单首音乐信息的广播
     */
    private void registerSingleMusicReceiver() {
        if (mSingleMusicReceiver == null) {
            AILog.i(TAG, "注册外界获取单首音乐信息的广播");
            IntentFilter filter = new IntentFilter();
            mSingleMusicReceiver = new SingleMusicReceiver(filter);
            mSingleMusicReceiver.setOnRequestListener(new SingleMusicReceiver.OnRequestListener() {
                @Override
                public void onRequest() {
                    if (isAIOSMusicPlay) {
                        sendBroadcast();
                    } else {
                        sendBroadcastEmpty();
                    }
                }
            });
            registerReceiver(mSingleMusicReceiver, filter);
        }
    }

    /**
     * 解绑外界获取单首音乐信息的广播
     */
    public void unregisterSingleMusicReceiver() {
        if (mSingleMusicReceiver != null) {
            AILog.i(TAG, "解绑外界获取单首音乐信息的广播");
            unregisterReceiver(mSingleMusicReceiver);
            mSingleMusicReceiver = null;
        }
    }

    private PlayCompleteReceiver mPlayerStateReceiver;
    private PlayCompleteReceiver.OnCompleteListener mOnCompleteListener = new PlayCompleteReceiver.OnCompleteListener() {
        @Override
        public void onComplete() {
            AILog.d(TAG, "onComplete");

            if (Configs.isUIExit) {//UI退出的情况下自动播放下一首
                playNextMusic();
            }
        }
    };

    private void playPreviousMusic() {
        int position = preLocalMusicPosition();
        saveStateAndPlay(position);
    }

    private void playNextMusic() {
        int position = nextLocalMusicPosition();
        saveStateAndPlay(position);
    }

    private void saveStateAndPlay(int position) {
        new ExitSatePreferences(MusicPlayerService.this).savePosition(position);
        for (MusicInfo info : Configs.mMusicInfoList) {
            AILog.d(TAG, info + "");
        }
        AILog.i(TAG, position + "");
        mMusicInfo = mMusicInfoList.get(position);
        sendBroadcast();
        play(mMusicInfo.getPath());
    }

    /**
     * 注册音乐状态改变的广播
     */
    private void registerPlayerStateReceiver() {
        if (mPlayerStateReceiver == null) {
            AILog.i(TAG, "注册音乐状态改变的广播");
            IntentFilter filter = new IntentFilter();
            mPlayerStateReceiver = new PlayCompleteReceiver(filter);
            mPlayerStateReceiver.setOnCompleteListener(mOnCompleteListener);
            registerReceiver(mPlayerStateReceiver, filter);
        }
    }

    /**
     * 解绑音乐状态改变的广播
     */
    public void unregisterPlayerStateReceiver() {
        if (mPlayerStateReceiver != null) {
            AILog.i(TAG, "解绑音乐状态改变的广播");
            unregisterReceiver(mPlayerStateReceiver);
            mPlayerStateReceiver = null;
        }
    }

    private MusicOperateReceiver mMusicOperateReceiver;

    /**
     * 注册音乐广播
     */
    public void registerMusicOperateReceiver() {
        if (/*!PrefUtil.isMusicApp(mContext) && */mMusicOperateReceiver == null) {//如果aios指向adapter)
            AILog.i(TAG, "注册音乐操作广播");
            IntentFilter filter = new IntentFilter();
            mMusicOperateReceiver = new MusicOperateReceiver(filter);
            mMusicOperateReceiver.setOperateListener(mOnOperateListener);
            registerReceiver(mMusicOperateReceiver, filter);
        }
    }

    /**
     * 解绑音乐广播
     */
    public void unregisterMusicOperateReceiver() {
        if (/*!PrefUtil.isMusicApp(mContext) && */mMusicOperateReceiver != null) {//如果aios指向adapter
            AILog.i(TAG, "取消音乐操作广播");
            unregisterReceiver(mMusicOperateReceiver);
            mMusicOperateReceiver = null;
        }
    }

    private OnOperateListener mOnOperateListener = new AbstractOperateListener() {

        @Override
        public void onPauseOperate() {
            if (Configs.isUIExit) {
                pause();
            }
        }

        @Override
        public void onPreOperate() {
            if (Configs.isUIExit) {
                playPreviousMusic();
            }
        }

        @Override
        public void onNextOperate() {
            if (Configs.isUIExit) {
                playNextMusic();
            }
        }
    };

    private int nextLocalMusicPosition() {

        Configs.musicPlayingPosition++;
        if (Configs.musicPlayingPosition == Configs.mMusicInfoList.size()) {
            Configs.musicPlayingPosition = 0;
        }
        String path = Configs.mMusicInfoList.get(Configs.musicPlayingPosition).getPath();
        if (TextUtils.isEmpty(path)) {
            return nextLocalMusicPosition();
        } else {
            return Configs.musicPlayingPosition;
        }
    }

    private int preLocalMusicPosition() {

        Configs.musicPlayingPosition--;
        if (Configs.musicPlayingPosition < 0) {
            Configs.musicPlayingPosition = Configs.mMusicInfoList.size() - 1;
        }
        String path = Configs.mMusicInfoList.get(Configs.musicPlayingPosition).getPath();
        if (TextUtils.isEmpty(path)) {
            return preLocalMusicPosition();
        } else {
            return Configs.musicPlayingPosition;
        }
    }

    public void sendBroadcast() {
        mSingleMusicReceiver.sendBroadcast(this, mMusicInfo);
    }

    public void sendBroadcastEmpty() {
        mSingleMusicReceiver.sendBroadcast(this, new MusicInfo());
    }

    private OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            AILog.i(TAG, "focusChange state : " + focusChange);
            mMediaplayerHandler.obtainMessage(FOCUSCHANGE, focusChange, 0).sendToTarget();
        }
    };
    private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer player) {
            notifyChange(PlayerStateReceiver.ONE_SONG_ENDED);
        }
    };
    private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer player) {
            AILog.i(TAG, "<><><>---AIOS音乐 requestAudioFocus：AUDIOFOCUS_GAIN");
            mAudioManager.requestAudioFocus(mAudioFocusListener,
                    Configs.Channel.MUSIC_AUDIO_CHANNEL, AudioManager.AUDIOFOCUS_GAIN);
            if (!isAIOSMusicPlay) {
                AILog.i(TAG, "<><><>---AIOS音乐 AIOS_MUSIC_PLAY");
                BroadCastUtil.getInstance(MusicPlayerService.this).send("audiomanager.intent.action.AIOS_MUSIC_PLAY", null, null);
                isAIOSMusicPlay = true;
            }

            mIsInitialized = true;
            player.start();
            notifyChange(PlayerStateReceiver.LOAD_END);
            notifyChange(PlayerStateReceiver.META_CHANGED);
            notifyChange(PlayerStateReceiver.PLAYSTATE_CHANGED);
        }

    };
    private Binder mBinder = new ServiceBinder(this);

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(Configs.Channel.MUSIC_AUDIO_CHANNEL);
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

        registerAudioManagerReceiver();
        registerAccReceiver();
        registerMusicReceiver();
        registerSingleMusicReceiver();
        registerPlayerStateReceiver();
        registerMusicOperateReceiver();
    }

    @Override
    public void onDestroy() {
        AILog.i(TAG, "<><><>---AIOS音乐 abandonAudioFocus");
        mAudioManager.abandonAudioFocus(mAudioFocusListener);

        if (isAIOSMusicPlay) {
            AILog.i(TAG, "<><><>---AIOS音乐 AIOS_MUSIC_PAUSE");
            BroadCastUtil.getInstance(MusicPlayerService.this).send("audiomanager.intent.action.AIOS_MUSIC_PAUSE", null, null);
            isAIOSMusicPlay = false;
        }

        mMediaplayerHandler.removeCallbacksAndMessages(null);
        unregisterAudioManagerReceiver();
        unregisterAccReceiver();
        unregisterMusicReceiver();
        unregisterSingleMusicReceiver();
        unregisterPlayerStateReceiver();
        unregisterMusicOperateReceiver();

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void play(String path) {
        try {
            if (mMediaPlayer != null && isInitialized()) {
                AILog.i(TAG, "mMediaPlayer not null ");
                mMediaPlayer.stop();
                mIsInitialized = false;
                mMediaPlayer.reset();
            }
            if (path.startsWith("content://")) {
                AILog.i(TAG, "content--setDataSource::" + path);
                mMediaPlayer.setDataSource(MusicPlayerService.this, Uri.parse(path));
            } else {
                AILog.i(TAG, "url--setDataSource::" + path);
                mIsPlayingBefore = true;
                mMediaPlayer.setDataSource(path);
            }
            mMediaPlayer.prepareAsync();
            notifyChange(PlayerStateReceiver.LOAD_BEGIN);

        } catch (Exception e) {
            mIsInitialized = false;
            AILog.e(TAG, e.toString());
            mMediaPlayer = null;
            mMediaPlayer = new MediaPlayer();
        }
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    private void notifyChange(String what) {

        Intent i = new Intent(what);
        sendBroadcast(i);
    }

    public long seek(long pos) {
        if (isInitialized()) {
            if (pos < 0)
                pos = 0;
            if (pos > mMediaPlayer.getDuration())
                pos = mMediaPlayer.getDuration();
            mMediaPlayer.seekTo((int) pos);
            return pos;
        }
        return -1;
    }

    public long duration() {
        if (isInitialized()) {
            return mMediaPlayer.getDuration();
        }
        return -1;
    }

    /**
     * Returns the current playback position in milliseconds
     */
    public long position() {
        if (isInitialized()) {
            return mMediaPlayer.getCurrentPosition();
        }
        return -1;
    }

    public void pause() {
        if (isAIOSMusicPlay) {
            AILog.i(TAG, "<><><>---AIOS音乐 AIOS_MUSIC_PAUSE");
            BroadCastUtil.getInstance(MusicPlayerService.this).send("audiomanager.intent.action.AIOS_MUSIC_PAUSE", null, null);
            isAIOSMusicPlay = false;
        }
        synchronized (this) {
            if (isPlaying() && isInitialized()) {
                mMediaPlayer.pause();
                notifyChange(PlayerStateReceiver.PLAYSTATE_CHANGED);
                Intent i = new Intent();
                i.setAction("com.aispeech.aios.music.ACTION_MUSIC_PAUSE_BUTTON");
                this.sendBroadcast(i);
            }
        }

        //让VST不能获取到我们当前播放的歌曲，因为已经暂停了
        sendBroadcastEmpty();
    }

    public void resume() {
        AILog.i(TAG, "<><><>---AIOS音乐 requestAudioFocus：AUDIOFOCUS_GAIN");
        mAudioManager.requestAudioFocus(mAudioFocusListener,
                Configs.Channel.MUSIC_AUDIO_CHANNEL, AudioManager.AUDIOFOCUS_GAIN);
        if (!isAIOSMusicPlay) {
            AILog.i(TAG, "<><><>---AIOS音乐 AIOS_MUSIC_PLAY");
            BroadCastUtil.getInstance(MusicPlayerService.this).send("audiomanager.intent.action.AIOS_MUSIC_PLAY", null, null);
            isAIOSMusicPlay = true;
        }

        synchronized (this) {
            if (isInitialized()) {
                mMediaPlayer.start();
                notifyChange(PlayerStateReceiver.PLAYSTATE_CHANGED);
                Intent i = new Intent();
                i.setAction("com.aispeech.aios.music.ACTION_MUSIC_RESTART_BUTTON");
                this.sendBroadcast(i);
            }
        }
    }

    public void setVolume(float vol) {
        if (mIntentCnt <= 0) {
            mMediaPlayer.setVolume(vol, vol);
        }
    }

    // 停止
    public void stop() {
        if (isAIOSMusicPlay) {
            AILog.i(TAG, "<><><>---AIOS音乐 AIOS_MUSIC_PAUSE");
            BroadCastUtil.getInstance(MusicPlayerService.this).send("audiomanager.intent.action.AIOS_MUSIC_PAUSE", null, null);
            isAIOSMusicPlay = false;
        }

        if (mMediaPlayer != null && isInitialized()) {
            mMediaPlayer.stop();
            mIsInitialized = false;
        }

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        sendBroadcastEmpty();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public boolean isPlayingBefore(){
        return mIsPlayingBefore;
    }

    public class ServiceBinder extends Binder {

        WeakReference<MusicPlayerService> mService;

        ServiceBinder(MusicPlayerService service) {
            mService = new WeakReference<MusicPlayerService>(service);
        }

        public boolean isPlaying() {
            return mService.get().isPlaying();
        }

        public boolean isPlayingBefore() {
            return mService.get().isPlayingBefore();
        }

        public void stop() {
            AILog.i(TAG, "<><><>---AIOS音乐 abandonAudioFocus");
            mAudioManager.abandonAudioFocus(mAudioFocusListener);
            mService.get().stop();
        }

        public void pause() {
            AILog.i(TAG, "<><><>---AIOS音乐 abandonAudioFocus");
            mAudioManager.abandonAudioFocus(mAudioFocusListener);
            mService.get().pause();
        }

        public void resume() {
            mService.get().resume();
        }

        public void play(String path) {
            mService.get().play(path);
        }

        public void setMusicInfo(MusicInfo info) {
            mMusicInfo = info;
        }

        public void setMusicInfo(List<MusicInfo> list) {
            mMusicInfoList = list;
        }

        public MusicInfo getMusicInfo() {
            return mMusicInfo;
        }

        public void sendBroadcast() {
            mService.get().sendBroadcast();
        }

        public long position() {
            return mService.get().position();
        }

        public long duration() {
            return mService.get().duration();
        }

        public long seek(long pos) {
            return mService.get().seek(pos);
        }

    }
}
