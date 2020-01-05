package com.bzy.game.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.bzy.game.util.LogUtil;
import com.bzy.game.util.PreferencesUtil;
import com.bzy.game.version.VersionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayerManager {
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;

    private static final long TIME_UPDATE = 300L;

    private Context context;
    private AudioFocusManager audioFocusManager;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private List<String> musicList = new ArrayList<>();
    public String currentMusic = "";
    private final List<OnPlayerEventListener> listeners = new ArrayList<>();
    private int state = STATE_IDLE;

    public boolean muted = false;//是否开启静音

    public static MediaPlayerManager get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static MediaPlayerManager instance = new MediaPlayerManager();
    }

    private MediaPlayerManager() {
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        audioFocusManager = new AudioFocusManager(context);
        mediaPlayer = new MediaPlayer();
        handler = new Handler(Looper.getMainLooper());
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
                LogUtil.d("音乐播放完成，重复播放");
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (isPreparing()) {
                    startPlayer();
                    LogUtil.d("音乐准备完成，开始播放");
                }
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                for (OnPlayerEventListener listener : listeners) {
                    listener.onBufferingUpdate(percent);
                }
                LogUtil.d("音乐缓冲完成度：" + percent);
            }
        });
    }

    public void addOnPlayEventListener(OnPlayerEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnPlayEventListener(OnPlayerEventListener listener) {
        listeners.remove(listener);
    }

    public void addAndPlay(String music) {
        if (TextUtils.isEmpty(music)) {
            return;
        }
        int position = musicList.indexOf(music);
        if (position < 0) {
            musicList.add(music);
            position = musicList.size() - 1;
        }
        play(position);
    }

    public void playLocalMusicPath(String music) {
        String urlPath = VersionManager.getResourceRootPath() + music;
        LogUtil.d("播放音乐，地址：" + urlPath);
        addAndPlay(urlPath);
    }

    public void playNetMusicPath(String musicUrl) {
        String urlPath = PreferencesUtil.getIndex() + musicUrl;
        LogUtil.d("播放音乐，地址：" + urlPath);
        addAndPlay(urlPath);
    }

//    public void playAssets(String path) {
//        AssetFileDescriptor fd = null;
//        try {
//            fd = context.getAssets().openFd("res/" + path);
//            mediaPlayer.setDataSource(fd.getFileDescriptor());
//        } catch (IOException e) {
//            LogUtil.e(e.toString());
//        }
//    }

    public void add(String music) {
        int position = musicList.indexOf(music);
        if (position < 0) {
            musicList.add(music);
            position = musicList.size() - 1;
        }
        setPlayPosition(position);
    }

    public void play(int position) {
        if (musicList.isEmpty()) {
            return;
        }

        if (position < 0) {
            position = musicList.size() - 1;
        } else if (position >= musicList.size()) {
            position = 0;
        }
        String music = musicList.get(position);
        setPlayPosition(position);

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(music);
            mediaPlayer.prepareAsync();
            state = STATE_PREPARING;
            for (OnPlayerEventListener listener : listeners) {
                listener.onChange(music);
            }
        } catch (IOException e) {
            LogUtil.e(e.toString());
        }
    }

    public void delete(int position) {
        int playPosition = getPlayPosition();
        String music = musicList.remove(position);
        if (playPosition > position) {
            setPlayPosition(playPosition - 1);
        } else if (playPosition == position) {
            if (isPlaying() || isPreparing()) {
                setPlayPosition(playPosition - 1);
                next();
            } else {
                stopPlayer();
                for (OnPlayerEventListener listener : listeners) {
                    listener.onChange(getPlayMusic());
                }
            }
        }
    }

    public void playPause() {
        if (isPreparing()) {
            stopPlayer();
        } else if (isPlaying()) {
            pausePlayer();
        } else if (isPausing()) {
            startPlayer();
        } else {
            play(getPlayPosition());
        }
    }

    public void startPlayer() {
        if (!isPreparing() && !isPausing()) {
            return;
        }

        if (audioFocusManager.requestAudioFocus()) {
            mediaPlayer.start();
            state = STATE_PLAYING;
            handler.post(mPublishRunnable);

            for (OnPlayerEventListener listener : listeners) {
                listener.onPlayerStart();
            }
        }
    }

    public void pausePlayer() {
        pausePlayer(true);
    }

    public void pausePlayer(boolean abandonAudioFocus) {
        if (!isPlaying()) {
            return;
        }

        mediaPlayer.pause();
        state = STATE_PAUSE;
        handler.removeCallbacks(mPublishRunnable);
        if (abandonAudioFocus) {
            audioFocusManager.abandonAudioFocus();
        }

        for (OnPlayerEventListener listener : listeners) {
            listener.onPlayerPause();
        }
    }

    public void stopPlayer() {
        if (isIdle()) {
            return;
        }

        pausePlayer();
        mediaPlayer.reset();
        state = STATE_IDLE;
    }

    public void rePlay() {
        addAndPlay(currentMusic);
    }

    public void next() {
        if (musicList.isEmpty()) {
            return;
        }
    }

    public void prev() {
        if (musicList.isEmpty()) {
            return;
        }
    }

    /**
     * 跳转到指定的时间位置
     *
     * @param msec 时间
     */
    public void seekTo(int msec) {
        if (isPlaying() || isPausing()) {
            mediaPlayer.seekTo(msec);
            for (OnPlayerEventListener listener : listeners) {
                listener.onPublish(msec);
            }
        }
    }

    /**
     * 开启静音
     */
    public void silentSwitchOn() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            audioManager.getStreamVolume(AudioManager.STREAM_RING);
            LogUtil.d("RINGING 已被静音");
        }
    }

    /**
     * 取消静音
     */
    public void silentSwitchOff() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.getStreamVolume(AudioManager.STREAM_RING);
            LogUtil.d("RINGING 取消静音");
        }
    }

    public void changeVoice(int voice, boolean maxVoice, boolean minVoice) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (minVoice) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
        if (maxVoice) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, voice, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying()) {
                for (OnPlayerEventListener listener : listeners) {
                    listener.onPublish(mediaPlayer.getCurrentPosition());
                }
            }
            handler.postDelayed(this, TIME_UPDATE);
        }
    };

    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    public long getAudioPosition() {
        if (isPlaying() || isPausing()) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public String getPlayMusic() {
        if (musicList.isEmpty()) {
            return null;
        }
        return musicList.get(getPlayPosition());
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public List<String> getMusicList() {
        return musicList;
    }

    public boolean isPlaying() {
        return state == STATE_PLAYING;
    }

    public boolean isPausing() {
        return state == STATE_PAUSE;
    }

    public boolean isPreparing() {
        return state == STATE_PREPARING;
    }

    public boolean isIdle() {
        return state == STATE_IDLE;
    }

    public int getPlayPosition() {
        int position = PreferencesUtil.getPlayPosition();
        if (position < 0 || position >= musicList.size()) {
            position = 0;
            PreferencesUtil.savePlayPosition(position);
        }
        return position;
    }

    private void setPlayPosition(int position) {
        PreferencesUtil.savePlayPosition(position);
    }
}
