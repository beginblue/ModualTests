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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static final String MUSIC_LIST = "musicLists";
    private int size;

    /**
     * 构造
     *
     * @param context 务必使用Application的Context
     *                //why? 2017.4.22
     *                because DBController 是一个活在Application中的对象
     *
     */
    public DBController(Context context) {
        mContext = context;
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
            removeMusicList("localMusic");
            scanMusic(handler);
        } catch (Exception e) {
            handler.sendEmptyMessage(muscScan.MUSIC_SEARCH_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * 扫描本地音乐
     * 如果已经扫描过了向{@code handler}发送{@code muscScan.MUSIC_ALREADY_SEARCHED }
     * 如果没有开始扫面，如果成功则向 {@code handler} 发送{@code muscScan.SEARCH_MUSIC_SUCCESS}
     * 最后在handler中向musicList表中添加localMusic记录。
     *
     * @param handler 处理机
     */
    public void scanMusic(final Handler handler) {
        try {
            db.execSQL("create table localMusic (name varchar(20),object blob);");
            muscScan.scanLocalMusic(handler);
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(muscScan.MUSIC_ALREADY_SEARCHED);

        }


    }

    @Override
    public void addNewMusicList(String listName, int count) throws Exception {
        try {
            db.execSQL("create table if not exists " + listName + " (name varchar(20) primary key,object blob);");
            db.execSQL("insert into musicLists values ('" + listName + "'," + count + ");");
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void removeMusicList(String listName) {
        db.execSQL("drop table \"" + listName + "\"");
        db.delete(MUSIC_LIST, "name=\"" + listName + "\"", null);
    }

    @Override
    public void addMusicToList(String listName, Music music, int currentSize) {
        ContentValues values = new ContentValues();
        values.put("name", music.getTitle());
        values.put("object", DBController.writeObject(music));
        db.insert(listName, null, values);

        ContentValues values1 = new ContentValues();
        values1.put("amount", currentSize + 1);
        db.update(MUSIC_LIST, values1, "name = '" + listName + "'", null);

    }

    @Override
    public void removeMusicFromList(String listName, String musicName, int size) {
        db.delete(listName, "name = \"" + musicName + "\"", null);
        ContentValues values = new ContentValues();
        values.put("amount", size - 1);
        db.update(MUSIC_LIST, values, "name=\"" + listName + "\"", null);
        //db.execSQL("delete from " + listName + " where name = " + musicName + ";");
    }

    @Override
    public List<Music> getMusicList(String listName) {
        Cursor songs
                = db.query( //select
                listName,  //from listName
                new String[]{"object"}, // select object
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


    public Map<String, Integer> getMusicLists() {
        Cursor lists = db.query(false,
                MUSIC_LIST, //select * from MUSIC_LIST
                null, null, null, null, null, null, null);
        Map<String, Integer> res = new HashMap<>();
        while (lists.moveToNext()) {
            String name = lists.getString(0);
            Integer count = lists.getInt(1);
            res.put(name, count);
        }
        return res;
    }

    @Override
    public void addMusicListToTable(String listName, List<Music> musicList, int currentSize) {
        int size = currentSize;
        for (Music muic :
                musicList) {
            insertIntoList(listName, muic, size++);
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
     * @param listName    歌单名称
     * @param music       歌曲对象
     * @param currentSize 当前歌单长度
     */
    public void insertIntoList(String listName, Music music, int currentSize) {
        addMusicToList(listName, music, currentSize);
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

    public void setContext(Context context) {
        mContext = context;
    }


    public int getSizeOfMusicList(String listName) {
        int currentSize = 0;
        Cursor cursor = db.query(MUSIC_LIST, new String[]{"amount"}, "name = '" + listName + "'", null, null, null, null);
        cursor.moveToNext();
        currentSize = cursor.getInt(0);

        return currentSize;
    }

    public Music getSpecificMusicFromSpecificList(String musicName, String listName) {
        Cursor songs
                = db.query( //select
                listName,  //from listName
                new String[]{"name,object"}, // select object
                null, null, null, null, null);
        while (songs.moveToNext()) {
            if (songs.getString(0).equals(musicName)) {
                byte[] blob = songs.getBlob(1);
                Music musicRec = DBController.readObject(blob);
                songs.close();
                return musicRec;
            }
        }
        songs.close();
        return null;
    }
}
