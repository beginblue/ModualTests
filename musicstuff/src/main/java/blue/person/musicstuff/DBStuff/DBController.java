package blue.person.musicstuff.DBStuff;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import blue.person.music.Music;
import blue.person.musicstuff.R;
import blue.person.musicstuff.musicUtils.muscScan;

/**
 * Created by getbl on 2017/1/21.
 */

public class DBController implements iDBControl {
    private SQLiteOpenHelper mSQLiteOpenHelper;
    private SQLiteDatabase db;
    private Context mContext;

    /**
     * 构造
     *
     * @param context 务必使用Application的Context
     */
    public DBController(Context context) {
        mContext = context;
        //Looper.prepare();
        mSQLiteOpenHelper = new DBOpenHelper(mContext, "tables.db", null, 1);
        db = mSQLiteOpenHelper.getWritableDatabase();
        db.execSQL(mContext.getString(R.string.sql_create_lists_table));

    }


    /**
     * 强行重新扫描本地音乐
     *
     * @param handler 处理机
     */
    public void forceScanMusic(Handler handler) {
        try {
            db.execSQL("create table if not exist localMusic (name varchar(20),object blob); ");
            db.execSQL("drop table localMusic;");
            db.execSQL("create table if not exist localMusic (name varchar(20),object blob); ");
            muscScan.scanLocalMusic(handler);
        } catch (Exception e) {
            handler.sendEmptyMessage(muscScan.MUSIC_SEARCH_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * 扫描本地音乐
     * 如果已经扫描过了向{@code handler}发送{@code muscScan.MUSIC_ALREADY_SEARCHED }
     * 如果没有则向 {@code handler} 发送{@code muscScan.SEARCH_MUSIC_SUCCESS}
     *
     * @param handler 处理机
     */
    public void scanMusic(final Handler handler) {
        try {
            db.execSQL("create table localMusic (name varchar(20),object blob);");
            muscScan.scanLocalMusic(handler);
        } catch (Exception e) {

//            Log.e("scanTest", "scanMusic:  begin drop" );
            e.printStackTrace();
            handler.sendEmptyMessage(muscScan.MUSIC_ALREADY_SEARCHED);

        }


    }

    @Override
    public void addNewMusicList(String listName) {
        db.execSQL("create table if not exists " + listName + " (name varchar(20),object blob);");
        db.execSQL("insert into musiclists values (" + listName + " ," + System.currentTimeMillis() + ")");

    }

    @Override
    public void removeMusicList(String listName) {
        db.execSQL("drop table" + listName);
    }

    @Override
    public void addMusicToList(String listName, Music music) {
        ContentValues values = new ContentValues();
        values.put("name", music.getTitle());
        values.put("object", DBController.writeObject(music));
        db.insert(listName, null, values);
    }

    @Override
    public void removeMusicFromList(String listName, String musicName) {
        db.execSQL("delete from " + listName + " where name = " + musicName + ";");
    }

    @Override
    public List<Music> getMusicList(String listName) {
        Cursor songs
                = db.query(listName,
                new String[]{"object"},
                null, null, null, null, null);
        List<Music> musicList = new ArrayList<>();
        while (songs.moveToNext()) {
            byte[] blob = songs.getBlob(0);
            Music musicRec = DBController.readObject(blob);
            musicList.add(musicRec);
        }
        songs.close();
        return musicList;

    }


    @Override
    public void addMusicListToTable(String listName, List<Music> musicList) {
        for (Music muic :
                musicList) {
            insertIntoList(listName, muic);
        }
    }

    @Override
    public boolean isLocalMusicSearched() {
        SharedPreferences status = mContext.getSharedPreferences("status", Context.MODE_APPEND);
        return status.getBoolean(
                mContext.getString(R.string.sp_isLocalMusicSearched)
                , false);
    }


    public static byte[] writeObject(Music music) {
        byte[] res = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(music);
            res = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static Music readObject(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Music music = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            music = (Music) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return music;
    }


    /**
     * 将歌曲对象存入歌单
     *
     * @param listName 歌单名称
     * @param music    歌曲对象
     */
    public void insertIntoList(String listName, Music music) {
        addMusicToList(listName, music);
    }

    /**
     * 读取歌单中的全部歌曲
     *
     * @param listName 歌单名称
     * @return 歌曲对象列表
     */
    public List<Music> getMusicFromList(String listName) {
        return getMusicList(listName);
    }
}
