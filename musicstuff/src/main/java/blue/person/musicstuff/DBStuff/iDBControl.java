package blue.person.musicstuff.DBStuff;

import java.util.List;

import blue.person.music.Music;

/**
 * 数据库基本功能定义
 * Created by getbl on 2017/1/21.
 */

public interface iDBControl {

    /**
     * 添加新歌单
     *
     * @param listName 歌单名称
     * @param count    歌曲数量
     */
    void addNewMusicList(String listName, int count) throws Exception;

    /**
     * 删除歌单
     *
     * @param listName 歌单名称
     */
    void removeMusicList(String listName);

    /**
     * 给某个歌单添加特定歌曲
     *
     * @param listName 歌单名称
     * @param music    要添加的音乐
     */
    void addMusicToList(String listName, Music music, int currentSize);


    /**
     * 从特定歌单中删除特定歌曲
     *
     * @param listName  歌单名称
     * @param musicName 要删除的音乐名称
     * @param size      目前歌单长度
     */
    void removeMusicFromList(String listName, String musicName, int size);


    /**
     * 将歌单转换成Music列表
     *
     * @param listName 歌单名称
     * @return Music列表
     */
    List<Music> getMusicList(String listName);

    /**
     * 将一串音乐列表添加到歌单中
     *
     * @param musicList 要添加的MusicList
     * @param listName  歌单名称
     */
    void addMusicListToTable(String listName, List<Music> musicList, int currentSize);

    /**
     * 是否已经扫描过本地内容
     *
     * @return 是否扫描过
     */
    boolean isLocalMusicSearched();
}
