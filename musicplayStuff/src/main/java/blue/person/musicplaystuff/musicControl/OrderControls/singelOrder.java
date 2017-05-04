package blue.person.musicplaystuff.musicControl.OrderControls;

/**
 * Created by getbl on 2017/4/17.
 */

public class singelOrder implements iOrderControl {
    @Override
    public int nextMusic(int length, int currentIndex) {
        return currentIndex;
    }

    @Override
    public int lastMusic(int length, int currentIndex) {
        return currentIndex;
    }

    @Override
    public iOrderControl nextOrder() {
        return new listOrder();
    }

    @Override
    public String getOrderName() {
        return "单曲循环";
    }
}
