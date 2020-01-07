package cn.modificator.launcher.model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import cn.modificator.launcher.Launcher;
import cn.modificator.launcher.R;

public class HomeEntranceService extends Service {

    public static final String HomeEntranceChannelId = "Back to Launcher";
    public static final String HomeEntranceChannelName = "Back to Launcher Notification";

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT>=26){
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                nm.createNotificationChannel(new NotificationChannel(HomeEntranceChannelId,HomeEntranceChannelName,NotificationManager.IMPORTANCE_LOW));
            }
        }

        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this,HomeEntranceChannelId);
        }else{
            builder = new Notification.Builder(this);
        }

        startForeground(1, builder
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.notification_click_back_launcher))
                .setContentIntent(PendingIntent.getActivity(this,10,new Intent(this, Launcher.class),Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED))
                .setSmallIcon(R.mipmap.ic_launcher)
                .getNotification()
        );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Intent thisService = new Intent(this,HomeEntranceService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(thisService);
        }else{
            startService(thisService);
        }
        super.onDestroy();
    }
}
