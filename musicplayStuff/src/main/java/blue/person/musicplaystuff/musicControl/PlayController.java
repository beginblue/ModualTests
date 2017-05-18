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
import blue.person.musicplaystuff.musicControl.OrderControls.iOrderControl;
import blue.person.musicplaystuff.musicplayserv.MusicPlayServ;
import blue.person.musicplaystuff.musicplayserv.musicServiceConnection;

/**
 * PlayController
 * 专门负责 控制播放内容 和播放顺序
 * 只有它和musicPlayService交流(.)
 * Created by getbl on 2017/1/11.
 * First test finished on 2017.2.10.
 */

public  class PlayController implements iMusicControl {

    public void setContext(Context context) {
        mContext = context;
    }

    public List<Music> getMusicList() {
        return mMusicList;
    }

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
            Log.i("bulu latest", "onReceive: receive broadcast");
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
    private onNoisyAudioReceiver mReceiver;
    private RemoteControlReceiver mrcReceiver;
    private onCompletionBroadcastReceiver mcbReceiver;
    private BroadcastReceivers mBroadcastReceivers;

    public PlayController(Context context) {
        mContext = context;

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
        mReceiver = new onNoisyAudioReceiver();
        mrcReceiver = new RemoteControlReceiver();
        mcbReceiver = new onCompletionBroadcastReceiver();
        mContext.registerReceiver(
                mrcReceiver,
                new IntentFilter("android.intent.action.MEDIA_BUTTON")
        );
        mContext.registerReceiver(
                mReceiver,
                new IntentFilter("android.intent.action.PHONE_STATE")
        );
        mContext.registerReceiver(
                mReceiver,
                new IntentFilter("android.intent.action.NEW_OUTGOING_CALL")
        );
        mContext.registerReceiver(
                mcbReceiver,
                new IntentFilter(BroadcastReceivers.onNextAction)
        );
        return this;
    }


    @Override
    public void start(Music music) {
        Log.i(TAG, "start: ???");
        mMusicServiceConnection.start(music);
       // BroadcastReceivers.sendBroadcast(mContext,
       //        BroadcastReceivers.onStartAction,
       /////         music);

    }

    @Override
    public void pause() {
        mMusicServiceConnection.pause();
//        BroadcastReceivers.sendBroadcast(mContext,
//                BroadcastReceivers.onPauseAction
//                , null);
    }

    @Override
    public void stop() {
        mMusicServiceConnection.stop();
//        BroadcastReceivers.sendBroadcast(mContext,
//                BroadcastReceivers.onStopAction,
//                null);
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
        if (currentIndex >= mMusicList.size()) {
            stop();
            currentIndex = 0;
            Toast.makeText(mContext, "播放结束", Toast.LENGTH_SHORT).show();
            return;
        }

        play(next);//播放之
    }

    @Override
    public void last() {
        int last = mOrderControl.lastMusic(mMusicList.size(), currentIndex);
        currentIndex = last;
        play(last);
    }

    @Override
    public long getCurrentPosition() {
        return mMusicServiceConnection.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMusicServiceConnection.getDuration();
    }


    /**
     * 播放选中的列表
     * 默认从歌单第一首开始播
     */
    public void play(int index) {
        currentIndex = index;
        start(mMusicList.get(index));
       // BroadcastReceivers.sendBroadcast(mContext,index);
    }



    public void changeOrder() {
        mOrderControl = mOrderControl.nextOrder();
        String orderName = mOrderControl.getOrderName();
        Toast.makeText(mContext, orderName, Toast.LENGTH_SHORT).show();
    }

    public void release() {
        stop();
        mContext.unregisterReceiver(mcbReceiver);
        mContext.unregisterReceiver(mrcReceiver);
        mContext.unregisterReceiver(mReceiver);
        mContext.unbindService(mMusicServiceConnection);
    }

    public int getCurrentIndex(){
        return currentIndex;
    }
}
