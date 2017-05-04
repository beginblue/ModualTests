package blue.person.bulumusic;

import android.content.Context;

import java.util.List;

import blue.person.music.Music;
import blue.person.musicplaystuff.musicControl.PlayController;
import blue.person.musicplaystuff.musicControl.OrderControls.normalOrder;
import blue.person.musicstuff.DBStuff.DBController;

/**
 * universal variables
 * Created by getbl on 2017/4/4.
 */

public class Universal {

    private static Universal mUniversal;
    private Context mContext;
    private DBController mDBController;
    private PlayController mPlayController;



    public static Universal newInstance(Context context) {
        if (mUniversal == null) {
            mUniversal = new Universal(context);
        } else {
            mUniversal.mContext = context;
        }
        return mUniversal;
    }

    private Universal(Context context) {
        mContext = context;
    }

    private static boolean PREPARED = false;

    public static boolean isPREPARED() {
        return PREPARED;
    }

    public PlayController getPlayController(List<Music> musicList) {
        if (!isPREPARED()) {
            try {
                mPlayController = new PlayController(mContext);
                preparePlayer(musicList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mPlayController;
    }

    private void preparePlayer(List<Music> musicList) throws Exception {
        mPlayController
                .setMusicList(musicList)
                .changePlayMode(new normalOrder())
                .prepare();
        PREPARED = true;
    }

    public DBController getDBController() {
        if(mDBController==null) return new DBController(mContext);
        return mDBController;
    }

    public Universal prepareDBController() {
        mDBController = new DBController(mContext);
        return this;
    }

    public void release() {
        mDBController = null;
        mPlayController = null;
    }
}
