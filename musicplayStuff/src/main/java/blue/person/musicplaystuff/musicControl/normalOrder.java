package blue.person.musicplaystuff.musicControl;

/**
 * 顺序播放控制器
 */
public class normalOrder implements iOrderControl {

    @Override
    public int nextMusic(int length, int currentIndex) {
        return (++currentIndex) % length;
    }

    @Override
    public int lastMusic(int length, int currentIndex) {
        int tempOrder = --currentIndex;
        if (tempOrder < 0) {
            tempOrder += length;
        }
        return tempOrder;
    }
}