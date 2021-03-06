package blue.person.musicplaystuff;

import blue.person.music.Music;

/**
 * Created by getbl on 2017/1/8.
 */

public interface iMusicControl {

    /**
     *  播放音乐
     * @param music 音乐对象
     */
    void start(Music music);

    /**
     * 暂停音乐
     */
    void pause();

    /**
     * 停止音乐
     */
    void stop();

    /**
     * 跳转音乐
     * @param mills 跳转位置
     */
    void seekTo(int mills);

    /**
     * 改变播放模式
     * @param controller 播放控制器
     */
    //PlayController changePlayMode(iOrderControl controller);

    /**
     * 下一曲
     */
    void next();


    /**
     * 上一曲
     */
    void last();


    /**
     * 获取当前播放位置
     * @return 当前播放位置的毫秒数
     */
    long getCurrentPosition();

    /**
     * 获取歌曲长度
     */
    long getDuration();


    /**
     * 获得当前播放进度
     * @return 当前播放进度
     */
    double getPlayedPercent();
}
