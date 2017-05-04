package com.blue.modualtests;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import blue.person.music.Music;
import blue.person.musicplaystuff.musicControl.PlayController;
import blue.person.musicplaystuff.musicControl.OrderControls.normalOrder;
import blue.person.musicplaystuff.musicControl.OrderControls.randomOrder;
import blue.person.musicstuff.DBStuff.DBController;
import blue.person.musicstuff.musicUtils.muscScan;

public class MainActivity extends AppCompatActivity {


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == muscScan.SEARCH_MUSIC_SUCCESS) {
                musicList = muscScan.getLocalMusicList();
                DBController.addMusicListToTable("localMusic", musicList);
            } else if (msg.what == muscScan.MUSIC_ALREADY_SEARCHED) {
                musicList = DBController.getMusicList("localMusic");
            }else if(msg.what == muscScan.MUSIC_SEARCH_ERROR){
                Toast.makeText(MainActivity.this, "something wrong", Toast.LENGTH_SHORT).show();
                return;
            }
            //  List<Music> testList = DBController.getMusicList("localMusic");
            // Log.i("Handler", "handleMessage: " + testList.size());
            mListAdapter.setMusicList(musicList);
            try {
                Universal.controller = new PlayController(getApplicationContext())
                        .changePlayMode(new normalOrder())
                        .setMusicList(musicList)
                        .prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mLoadingDialog.cancel();
        }
    };


    private static AlertDialog mLoadingDialog;
    private static List<Music> musicList;
    private DBController DBController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        musicList = new ArrayList<>();
//        播放器网络音乐播放测试
//        Music music1 = new Music();
//        music1.setUri("http://192.168.21.66:8080/doc/a.mp3");
//        Music music2 = new Music();
//        music2.setUri("http://192.168.21.66:8080/doc/b.mp3");
//
//        musicList.add(music1);
//        musicList.add(music2);


        mLoadingDialog =
                new AlertDialog.Builder(this)
                        .setTitle("Loading")
                        .show();

        DBController = new DBController(getApplicationContext());
        DBController.scanMusic(mHandler);

        ListView listView = (ListView) findViewById(R.id.lv_musicList);
        mListAdapter = new listAdapter();
        assert listView != null;
        listView.setAdapter(mListAdapter);

    }

    listAdapter mListAdapter;

    public void click(View view) {
        Universal.controller.next();

    }

    public void onPrevClick(View view) {
        Universal.controller.pause();
    }

    public void onFuncClicked(View view) {
        Universal.controller.changePlayMode(new randomOrder());
    }
}
