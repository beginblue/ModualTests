package blue.person.musicplaystuff.musicplayserv;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.util.Log;

import java.io.IOException;

import blue.person.music.Music;
import blue.person.musicplaystuff.iMusicControl;

/**
 * 音乐播放
 * 与顺序无关 只接受一首歌 给啥播啥
 * 播放顺序控制和曲目控制都由类执行
 * Created by getbl on 2017/1/8.
 */

public class MusicPlayer implements iMusicControl, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "MusicPlayer";

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private Context mContext;
    // private iOrderControl mOrderController;

    public MusicPlayer(Context context) {
        mContext = context;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnErrorListener(new onErrorListener());
        mMediaPlayer.setOnPreparedListener(new onPrepareFinishedListener());
        mMediaPlayer.setOnSeekCompleteListener(new onSeekCompleteListener());
        mMediaPlayer.setOnCompletionListener(new onCompletionListener());

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); // 控制播放焦点

    }

    @Override
    public void start(Music music) {
        String songPath = music.getUri();
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        Log.e(TAG, "start: starting " + songPath);
        try {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(songPath);//Initialed
                mMediaPlayer.prepareAsync();//Preparing
            } else {
                mMediaPlayer.reset();//Idle
                mMediaPlayer.setDataSource(songPath);
                mMediaPlayer.prepareAsync();
            }
        } catch (IOException e) {
            Log.e(TAG, "start: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void pause() {
        Log.i(TAG, "pause: pause tapped" + mMediaPlayer.isPlaying());
        if (mMediaPlayer.isPlaying()) {
            mAudioManager.abandonAudioFocus(this);
            mMediaPlayer.pause();
        } else {
            Log.i(TAG, "pause: start???");
            mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            mMediaPlayer.start();
        }
    }

    @Override
    public void stop() {
        if (mMediaPlayer.isPlaying()) {
            mAudioManager.abandonAudioFocus(this);
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();
    }

    @Override
    public void seekTo(int mills) {
        mMediaPlayer.seekTo(mills);
    }


    @Override
    public void next() {

    }

    @Override
    public void last() {

    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public double getPlayedPercent()  {

            double playedPercent = getCurrentPosition() / getDuration();
            return playedPercent;

    }

    /**
     * 播放焦点改变的回调
     *
     * @param focusChange 焦点改变
     */
    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mMediaPlayer.isPlaying()) {
                    pause();
                }
                break;
        }
    }
//
//    @Override
//    public int changeCycleMode() {
//        return (CURRENT_PLAY_MODE++) % 4;
//    }


    private class onErrorListener implements OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            Log.e(TAG, "onError: " + what + ":" + extra);
            return false;
        }
    }

    private class onSeekCompleteListener implements OnSeekCompleteListener {

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            mAudioManager.requestAudioFocus(MusicPlayer.this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            mp.start();
        }
    }

    private class onPrepareFinishedListener implements OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
        }
    }


    /**
     * 播放完成的监听器
     */
    private class onCompletionListener implements OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.i(TAG, "onCompletion: Finished,going to send.");
            Intent intent = new Intent("blue.broadcast.NEXT_MUSIC");
            mContext.sendBroadcast(intent);
        }
    }
}
