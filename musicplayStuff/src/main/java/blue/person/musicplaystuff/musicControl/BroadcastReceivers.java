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

    public static void sendBroadcast(Context context, String  broadcast, @Nullable Music music){
        Intent intent = new Intent(broadcast);
        if(music != null){
//            intent.putExtra("Title",music.getTitle());
//            intent.putExtra("Author",music.getArtist());
//            intent.putExtra("Image",music.getCover());
            intent.putExtra("Music",music);
        }
        context.sendBroadcast(intent);
    }

    public static void sendBroadcast(Context context,int index){
        Intent intent = new Intent(onStartActionIndex);
        intent.putExtra("Index",index);
        context.sendBroadcast(intent);
    }

    public static abstract class BroadcastResolver {

        protected Intent mIntent ;
        public BroadcastResolver(){

        }

        public void setIntent(Intent intent){
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


    public void setOnStartListener(BroadcastResolver onStartListener) {
        onCommonBroadcastReceiver ocbr = new onCommonBroadcastReceiver(onStartListener);
        mContext.registerReceiver(ocbr,
                new IntentFilter(onStartAction));

    }

    public void setOnStopListener(BroadcastResolver onStopListener) {
        onCommonBroadcastReceiver ocbr = new onCommonBroadcastReceiver(onStopListener);
        mContext.registerReceiver(ocbr,
                new IntentFilter(onStopAction));
    }


    public void setOnPauseListener(BroadcastResolver onPauseListener) {
        onCommonBroadcastReceiver ocbr = new onCommonBroadcastReceiver(onPauseListener);
        mContext.registerReceiver(ocbr,
                new IntentFilter(onPauseAction));
    }


    public void setOnNextListener(BroadcastResolver onNextListener){
        onCommonBroadcastReceiver ocbr = new onCommonBroadcastReceiver(onNextListener);
        mContext.registerReceiver(ocbr,
                new IntentFilter(onNextAction));
    }


    public void setOnStartActionIndexListener(BroadcastResolver onStartActionIndexListener){
        onCommonBroadcastReceiver ocbr = new onCommonBroadcastReceiver(onStartActionIndexListener);
        mContext.registerReceiver(ocbr,
                new IntentFilter(onStartActionIndex));
    }

}
