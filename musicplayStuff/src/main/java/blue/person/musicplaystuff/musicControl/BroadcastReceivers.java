package blue.person.musicplaystuff.musicControl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;

import blue.person.music.Music;

/**
 * 集中处理广播的收发
 * Created by getbl on 2017/4/14.
 */

public class BroadcastReceivers {


    private Context mContext;

    public BroadcastReceivers(Context context) {
        mContext = context;
    }

    /*定义事件的Action*/
    public static final String onStartAction = "blue.broadcast.START";
    public static final String onPauseAction = "blue.broadcast.PAUSE";
    public static final String onStopAction = "blue.broadcast.STOP";
    public static final String onNextAction = "blue.broadcast.NEXT_MUSIC";
    public static final String onStartActionIndex = "blue.broadcast.START.INDEX";
    public static final  String onStartOrderAction = "blue.broadcast.START_ORDER";

    public static void sendBroadcast(Context context, String broadcast, @Nullable Music music) {
        Intent intent = new Intent(broadcast);
        if (music != null) {
//            intent.putExtra("Title",music.getTitle());
//            intent.putExtra("Author",music.getArtist());
//            intent.putExtra("Image",music.getCover());
            intent.putExtra("Music", music);
        }
        context.sendBroadcast(intent);
    }

    public static void sendBroadcast(Context context, int index) {
        Intent intent = new Intent(onStartActionIndex);
        intent.putExtra("Index", index);
        context.sendBroadcast(intent);
    }

    public void unregisterAllBroadcastReceivers() {
        if (ocbrStart != null) mContext.unregisterReceiver(ocbrStart);
        if (ocbrPause != null) mContext.unregisterReceiver(ocbrPause);
        if (ocbrStop != null) mContext.unregisterReceiver(ocbrStop);
        if (ocbrNext != null) mContext.unregisterReceiver(ocbrNext);
        if (ocbrStartActionIndex != null) mContext.unregisterReceiver(ocbrStartActionIndex);
    }

    public static abstract class BroadcastResolver {

        protected Intent mIntent;

        public BroadcastResolver() {

        }

        public void setIntent(Intent intent) {
            mIntent = intent;
        }

        public abstract void run();
    }

    /**
     * 广播接收器。
     * 执行定义时刻应该执行的动作。
     */
    public class onCommonBroadcastReceiver extends BroadcastReceiver {

        BroadcastResolver mOnStartListener;

        public onCommonBroadcastReceiver(BroadcastResolver listener) {
            mOnStartListener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            mOnStartListener.setIntent(intent);
            mOnStartListener.run();
        }
    }

    private onCommonBroadcastReceiver ocbrStart;

    public void setOnStartListener(BroadcastResolver onStartListener) {

        ocbrStart = new onCommonBroadcastReceiver(onStartListener);
        mContext.registerReceiver(ocbrStart,
                new IntentFilter(onStartAction));

    }

    private onCommonBroadcastReceiver ocbrStop;

    public void setOnStopListener(BroadcastResolver onStopListener) {

        ocbrStop = new onCommonBroadcastReceiver(onStopListener);
        mContext.registerReceiver(ocbrStop,
                new IntentFilter(onStopAction));
    }

    private onCommonBroadcastReceiver ocbrPause;

    public void setOnPauseListener(BroadcastResolver onPauseListener) {

        ocbrPause = new onCommonBroadcastReceiver(onPauseListener);
        mContext.registerReceiver(ocbrPause,
                new IntentFilter(onPauseAction));
    }

    private onCommonBroadcastReceiver ocbrNext;

    public void setOnNextListener(BroadcastResolver onNextListener) {

        ocbrNext = new onCommonBroadcastReceiver(onNextListener);
        mContext.registerReceiver(ocbrNext,
                new IntentFilter(onNextAction));
    }

    private onCommonBroadcastReceiver ocbrStartActionIndex;

    public void setOnStartActionIndexListener(BroadcastResolver onStartActionIndexListener) {

        ocbrStartActionIndex = new onCommonBroadcastReceiver(onStartActionIndexListener);
        mContext.registerReceiver(ocbrStartActionIndex,
                new IntentFilter(onStartActionIndex));
    }

}
