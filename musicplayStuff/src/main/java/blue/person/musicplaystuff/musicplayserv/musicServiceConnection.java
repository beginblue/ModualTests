package blue.person.musicplaystuff.musicplayserv;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import blue.person.music.Music;
import blue.person.musicplaystuff.iMusicControl;

import static android.content.ContentValues.TAG;

/**
 * Created by getbl on 2017/1/9.
 */

public class musicServiceConnection implements ServiceConnection, iMusicControl {
    iMusicControl _service;
    private boolean connected = false;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicPlayServ.musicBinder binder = (MusicPlayServ.musicBinder) service;
        _service = binder.getService();
        connected = true;
        Log.e(TAG, "onServiceConnected: ??");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        connected = false;
        Log.i(TAG, "onServiceDisconnected: disconnected");
    }

    @Override
    public void start(Music music) {
        if (connected) _service.start(music);
        else Log.d(TAG, "start: connected is false");
    }

    @Override
    public void stop() {
        if (connected) _service.stop();
    }

    @Override
    public void pause() {
        if (connected) _service.pause();
    }

    @Override
    public void seekTo(int m) {
        if(connected) _service.seekTo(m);
    }


    @Override
    public void next() {

    }

    @Override
    public void last() {

    }

    @Override
    public long getCurrentPosition() {
        return _service.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return _service.getDuration();
    }


}
