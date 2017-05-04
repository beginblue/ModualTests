package blue.person.musicplaystuff.musicControl.OrderControls;

/**
 * 列表单独播放
 * Created by getbl on 2017/4/17.
 */

public class listOrder implements iOrderControl {
    @Override
    public int nextMusic(int length, int currentIndex) {
        return --currentIndex;
    }

    @Override
    public int lastMusic(int length, int currentIndex) {
        return --currentIndex;
    }

    @Override
    public iOrderControl nextOrder() {
        return new normalOrder();
    }

    @Override
    public String getOrderName() {
        return "列表播放";
    }
}
