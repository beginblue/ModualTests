package blue.person.musicstuff.musicUtils;

import java.util.ArrayList;
import java.util.List;

import blue.person.music.Music;

/**
 * 各种数据类型的转换
 * Created by getbl on 2017/1/21.
 */

public class Convert {

    /**
     * 用给定的一系列地址生成Music列表
     * @param paths 给定的一些列地址
     * @return 生成的Music列表
     */
    public static List<Music> makeMusicList(List<String> paths){
        List<Music> musicList = new ArrayList<>();
        for (String path :
                paths) {
            try {
                musicList.add(new Music(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return musicList;
    }

    //public static List<Music> makeMusicList()


}
