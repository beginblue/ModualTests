package blue.person.musicplaystuff.musicControl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.List;

import blue.person.music.Music;
import blue.person.musicplaystuff.iMusicControl;
import blue.person.musicplaystuff.musicplayserv.MusicPlayServ;
import blue.person.musicplaystuff.musicplayserv.musicServiceConnection;

/**
 * PlayController
 * 专门负责 控制播放内容 和播放顺序
 * 只有它和musicPlayService交流(.)
 * Created by getbl on 2017/1/11.
 * First test finished on 2017.2.10.
 */

public class PlayController implements iMusicControl {

    /**
     * 一首歌完成后会发送广播
     * 接受这个广播并播放下一首
     */
    public class onCompletionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Received", Toast.LENGTH_SHORT).show();
            int nextIndex = mOrderControl.nextMusic(mMusicList.size(), currentIndex);
            play(nextIndex);
        }
    }


    /**
     * 来电暂停.
     * 挂电话继续.
     */
    public class onNoisyAudioReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mMusicServiceConnection.pause();
        }

    }


    /**
     * 耳机线控
     */
    public class RemoteControlReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event == null || event.getAction() != KeyEvent.ACTION_UP) {
                return;
            }
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                case KeyEvent.KEYCODE_HEADSETHOOK:
                   mMusicServiceConnection.pause();
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    mMusicServiceConnection.next();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                   mMusicServiceConnection.last();
                    break;
            }
        }
    }

    public static final String TAG = "Play controller";
private static final String CALL_STATE = "callState";
    private Context mContext;
    private List<Music> mMusicList;
    private iOrderControl mOrderControl;
    private musicServiceConnection mMusicServiceConnection;
    private int currentIndex;

    public PlayController(Context context) {
        mContext = context;
        mContext.registerReceiver(
                new onCompletionBroadcastReceiver(),
                new IntentFilter("blue.broadcast.NEXT_MUSIC")
        );
    }


    public PlayController setMusicList(List<Music> musicList) {
        if (mMusicList != null) {
            stop();
        }
        mMusicList = musicList;
        return this;
    }


    /**
     * 构建完成后启动音乐播放服务并绑定
     * 对数据进行检查
     */
    public PlayController prepare() throws Exception {
        if (mOrderControl == null) throw new Exception("order is null");
        if (mMusicList == null) throw new Exception("list is null");
        mMusicServiceConnection = new musicServiceConnection();
        Intent intent = new Intent(mContext, MusicPlayServ.class);
        mContext.startService(intent);
        // Thread.sleep(1000);
        mContext.bindService(intent, mMusicServiceConnection, 0);
        mContext.registerReceiver(
                new RemoteControlReceiver(),
                new IntentFilter("android.intent.action.MEDIA_BUTTON")
        );
        return this;
    }


    @Override
    public void start(Music music) {
        Log.i(TAG, "start: ???");
        mMusicServiceConnection.start(music);
        mContext.registerReceiver(
                new onNoisyAudioReceiver(),
                new IntentFilter("android.intent.action.PHONE_STATE")
        );
        mContext.registerReceiver(
                new onNoisyAudioReceiver(),
                new IntentFilter("android.intent.action.NEW_OUTGOING_CALL")
        );

    }

    @Override
    public void pause() {
        mMusicServiceConnection.pause();
    }

    @Override
    public void stop() {
        mMusicServiceConnection.stop();
        try {
            mContext.unregisterReceiver(new onNoisyAudioReceiver());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void seekTo(int mills) {
        mMusicServiceConnection.seekTo(mills);
    }


    public PlayController changePlayMode(iOrderControl controller) {
        mOrderControl = controller;
        return this;
    }

    @Override
    public void next() {
        // mOrderControl.nextMusic(currentIndex,mMusicList.size());
        int next = mOrderControl.nextMusic(mMusicList.size(), currentIndex); //得到下一首曲子的uri
        currentIndex = next;//标记下一首为当前
        start(mMusicList.get(next));//播放之
    }

    @Override
    public void last() {
        int last = mOrderControl.lastMusic(mMusicList.size(), currentIndex);
        currentIndex = last;
        start(mMusicList.get(last));
    }


    /**
     * 播放选中的列表
     * 默认从歌单第一首开始播
     */
    public void play(int index) {
        currentIndex = index;
        start(mMusicList.get(index));
    }
}
