package blue.person.bulumusic;

import android.app.Application;
import android.support.annotation.Nullable;

import java.util.List;

import blue.person.music.Music;
import blue.person.musicplaystuff.musicControl.OrderControls.normalOrder;
import blue.person.musicplaystuff.musicControl.PlayController;
import blue.person.musicstuff.DBStuff.DBController;

/**
 * Created by getbl on 2017/5/12.
 */

public class ShareDataApplication extends Application {
    private DBController mDBController;
    private PlayController mPlayController;
    private boolean PREPARED = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public DBController getDBController() {
        if (mDBController == null) {
            mDBController = new DBController(this);
        }
        return mDBController;
    }

    public PlayController getPlayController(@Nullable List<Music> musicList) {
        if (!isPREPARED()) {

            try {
                mPlayController = new PlayController(this);
                preparePlayer(musicList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return mPlayController;
    }

    public boolean isPREPARED() {
        return PREPARED;
    }

    private void preparePlayer(List<Music> musicList) throws Exception {
        mPlayController
                .setMusicList(musicList)
                .changePlayMode(new normalOrder())
                .prepare();
        PREPARED = true;
    }
}
