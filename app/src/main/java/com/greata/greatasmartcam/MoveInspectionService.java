package com.greata.greatasmartcam;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

public class MoveInspectionService extends Service {
    public MoveInspectionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager barmanager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notice;
        Notification.Builder builder = new Notification.Builder(this).setTicker("移动了")
                .setSmallIcon(R.drawable.logo).setWhen(System.currentTimeMillis());
        Intent appIntent=null;
        appIntent = new Intent(this,PlayerActivity.class);
        appIntent.putExtra(PlayerActivity.PREFER_EXTENSION_DECODERS, false);
        appIntent.setData(Uri.parse("http://playertest.longtailvideo.com/adaptive/bipbop/gear4/prog_index.m3u8"));
        appIntent.setAction(PlayerActivity.ACTION_VIEW);
        //appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//关键的一步，设置启动模式
        PendingIntent contentIntent =PendingIntent.getActivity(this, 0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notice = builder.setContentIntent(contentIntent).setContentTitle("标题").setContentText("内容").build();
            //notice.flags=Notification.FLAG_AUTO_CANCEL;
            barmanager.notify(10,notice);
        }
        Log.d("Test", "onCreate service ");
        stopSelf();
    }

}
