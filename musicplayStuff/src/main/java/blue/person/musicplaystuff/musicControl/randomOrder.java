package blue.person.musicplaystuff.musicControl;

import java.util.Random;

/**
 * 随机顺序控制
 * 需要一个不连续出现相同数字的随机数发生器
 * Random类不合适
 * Created by getbl on 2017/2/10.
 */

public class randomOrder implements iOrderControl {
    Random random = new Random();
    @Override
    public int nextMusic(int length, int currentIndex) {
        return random.nextInt(length);
    }

    @Override
    public int lastMusic(int length, int currentIndex) {
        return random.nextInt(length);
    }
}
