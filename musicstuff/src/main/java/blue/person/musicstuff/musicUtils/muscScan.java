package blue.person.musicstuff.musicUtils;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import blue.person.music.Music;


/**
 * 扫描本地音乐
 * Created by getbl on 2017/1/14.
 */

public class muscScan {
    public static final int SEARCH_MUSIC_SUCCESS = 1;      //扫描成功
    public static final int MUSIC_ALREADY_SEARCHED = 0;    //列表已存在
    public static final int MUSIC_SEARCH_ERROR = 2;        //扫描出现错误
    private static List<String> list = new ArrayList<>();  //歌曲地址
    private static Handler sHandler;//处理器
    private static String TAG = "musicScanmm";

    /**
     * 类入口
     *
     * @param handler 外部处理Handler
     */
    public static void scanLocalMusic(Handler handler) {
        Log.d(TAG, "scanLocalMusic: started");
        sHandler = handler;
        scanMusic();
        //return localMusicList.size();
    }

    /**
     * 获得扫描结果
     * *在Handler中使用*
     *
     * @return 扫描结果
     */
    public static List<Music> getLocalMusicList() {
//        List<Music> musicList = new ArrayList<>();
//        for (String path :
//                list) {
//            Music music = new Music();
//            music.setUri(path);
//            musicList.add(music);
//        }
//        return musicList;
        return Convert.makeMusicList(list);
    }


    /**
     * 设置扫描属性 开启扫描线程
     */
    private static void scanMusic() {
        new Thread() {
            @Override
            public void run() {
                Log.d(TAG, "run: started");
                try {
                    String[] ext = {".mp3"};

                    File file = Environment.getExternalStorageDirectory();
                    //File file = Environment.getDataDirectory();
                    searchMp3Infos(file, ext);
                    sHandler.sendEmptyMessage(SEARCH_MUSIC_SUCCESS);
                } catch (Exception e) {
                }
            }
        }.start();

    }


    /**
     * 递归扫描本地文件夹
     *
     * @param file 扫描根节点
     * @param ext  扫面选项
     */
    private static void searchMp3Infos(File file, String[] ext) {
        if (file != null) {
            if (file.isDirectory()) {
                File[] listFile = file.listFiles();
                if (listFile != null) {
                    for (int i = 0; i < listFile.length; i++) {
                        searchMp3Infos(listFile[i], ext);
                    }
                }
            } else {
                String filename = file.getAbsolutePath();
                if (!filename.contains("cloudmusic")) return; //只扫描网易云音乐
                for (int i = 0; i < ext.length; i++) {
                    if (filename.endsWith(ext[i])) {
                        list.add(filename);
                        break;
                    }
                }
            }
        }
    }
}
