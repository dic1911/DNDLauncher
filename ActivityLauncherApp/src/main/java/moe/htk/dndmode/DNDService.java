package moe.htk.dndmode;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static moe.htk.dndmode.mNotification.CHANNEL_ID;

public class DNDService extends Service {
    private static android.app.Notification noti;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Default Notification Ch.",
                    NotificationManager.IMPORTANCE_MIN);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).deleteNotificationChannel(CHANNEL_ID);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            /*android.app.Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("030")
                    .setContentText("TEST").build();*/

            //startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        noti = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_SOUND).setVibrate(new long[]{0L})
                //.setContentTitle("TEST")
                //.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setVibrate(null)
                .setChannelId(CHANNEL_ID)
                //.setContentIntent(pendingIntent)
                .build();

        startForeground(1, noti);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO: monitor activity + timer?
                while(true) {
                    //Log.d("DNDL-mon", DNDHandler.getTopPkg());
                    try {
                        Thread.currentThread().sleep(2000); // 2 sec
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!DNDHandler.getTopPkg().contains("moe.htk")) {
                        DNDHandler.loadNotiMode();
                        break;
                    }
                }
                stopSelf();
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

}
