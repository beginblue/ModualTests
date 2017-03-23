package blue.person.musicplaystuff.musicplayserv;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import blue.person.music.Music;

/**
 * Created by getbl on 2017/2/12.
 */
public class SystemUtils {
    /**
     * 音乐控制通知.
     *
     *
     * http://blog.csdn.net/qiu592198740/article/details/16116385
     * @param context
     * @param music
     * @return
     */
    public static Notification createNotification(Context context, Music music) {


        Intent notificationIntent = new Intent("blue.activity.test.MAIN");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(music.getTitle())
                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setContentText("playing")
                .setContentIntent(pendingIntent);


        return builder.build();
    }
}
