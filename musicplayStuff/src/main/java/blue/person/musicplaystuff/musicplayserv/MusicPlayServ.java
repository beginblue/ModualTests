package blue.person.musicplaystuff.musicplayserv;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import blue.person.music.Music;
import blue.person.musicplaystuff.iMusicControl;
import blue.person.musicplaystuff.musicControl.BroadcastReceivers;

public class MusicPlayServ extends Service
        implements iMusicControl {

    private NotificationManager mNotificationManager;
    MusicPlayer mMusicPlayer;
    private static final String TAG = "MusicPlayServ";
    private static int NOTIFICATION_ID = 33;
    private Music mCurrentMusic;




    /**
     * 启动前台机制，播放时调用
     */
    private void updateNotification(Music music) {
        Log.i(TAG, "updateNotification: updateNotification");
       // mNotificationManager.cancel(NOTIFICATION_ID);
        startForeground(NOTIFICATION_ID, SystemUtils.createNotification(this, music));
    }

    /**
     * 取消前台机制，暂停时调用
     */
    private void cancelNotification() {
        Log.i(TAG, "cancelNotification: cancelNotification");
        stopForeground(true);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }




    /**
     * 自定义Binder无需考虑ipc
     */
    public class musicBinder extends Binder {
        MusicPlayServ mMusicPlayServ;
        public musicBinder(MusicPlayServ serv) {
            mMusicPlayServ = serv;
        }
        public MusicPlayServ getService() {
            return mMusicPlayServ;
        }
    }


    public MusicPlayServ() {
        Log.d(TAG, "MusicPlayServ: constructor");

        mBinder = new musicBinder(this);
    }


    Binder mBinder;

    /**
     * 绑定成功后执行
     * @param intent intent
     * @return binder
     */
    @Override
    public IBinder onBind(Intent intent) {
        //Toast.makeText(this, "onBind", Toast.LENGTH_SHORT).show();

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        return mBinder;
    }


    /**
     * 启动服务
     *
     * @param intent 传过来的数据
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "started", Toast.LENGTH_SHORT).show();
        mMusicPlayer = new MusicPlayer(MusicPlayServ.this);
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void start(Music music) {

        mCurrentMusic=music;
        updateNotification(mCurrentMusic);
        mMusicPlayer.start(mCurrentMusic);
        sendBroadcast(new Intent(BroadcastReceivers.onStartAction));
    }

    @Override
    public void pause() {

        mMusicPlayer.pause();
        sendBroadcast(new Intent(BroadcastReceivers.onPauseAction));
    }

    @Override
    public void stop() {
        cancelNotification();
        mMusicPlayer.stop();
        sendBroadcast(new Intent(BroadcastReceivers.onStopAction));
    }

    @Override
    public void seekTo(int mills) {
        mMusicPlayer.seekTo(mills);
    }


    @Override
    public void next() {

    }

    @Override
    public void last() {

    }

    @Override
    public long getCurrentPosition() {
        return mMusicPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMusicPlayer.getDuration();
    }

}
