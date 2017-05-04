package blue.person.musicplaystuff.musicControl.OrderControls;

/**
 * Created by getbl on 2017/1/11.
 */

public interface iOrderControl {
    /**
     * 下一曲
     * @param length 歌单长度
     * @param currentIndex 现在播放的位置
     * @return 下一首的位置
     */
    int nextMusic(int length, int currentIndex);

    /**
     * 上一曲
     * @param length 歌单长度
     * @param currentIndex 现在播放的位置
     * @return 上一曲的位置
     */
    int lastMusic(int length, int currentIndex);

    /**
     * 下一个顺序
     * @return 下一种播放顺序
     */
    iOrderControl nextOrder();

    /**
     * 获得当前播放模式的名称
     * @return
     */
    String getOrderName();

}
