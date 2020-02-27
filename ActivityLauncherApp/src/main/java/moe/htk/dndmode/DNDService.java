package moe.htk.dndmode;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import moe.htk.dndlauncher.MainActivity;
import moe.htk.dndlauncher.R;

import static moe.htk.dndmode.DNDHandler.mNotificationManager;
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
                    NotificationManager.IMPORTANCE_LOW);

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
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        noti = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_SOUND).setVibrate(new long[]{0L})
                .setContentTitle("TEST")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setVibrate(null)
                .setColorized(true)
                .setChannelId(CHANNEL_ID)
                //.setContentIntent(pendingIntent)
                .build();

        startForeground(1, noti);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO: monitor activity + timer?
                //Calendar time = Calendar.getInstance();
                int t = -1;
                while (true) {
                    //time.getTime();
                    //if (t != Calendar.getInstance().get(Calendar.SECOND)) {
                        //t = Calendar.getInstance().get(Calendar.SECOND);
                        noti = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setDefaults(Notification.DEFAULT_SOUND).setVibrate(new long[]{0L})
                                .setContentTitle(DNDHandler.getTopPkg())
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                //.setContentIntent(pendingIntent)
                                .build();
                        mNotificationManager.notify(1, noti);
                        if (!DNDHandler.getTopPkg().contains("moe.htk")) {
                            DNDHandler.loadNotiMode();
                            break;
                        }
                        //Toast.makeText(getApplicationContext(), DNDHandler.getTopPkg(), Toast.LENGTH_LONG).show();
                        //Log.d("DNDL", DNDHandler.getTopPkg());
                    //}
                }
                stopSelf();
            }
        });


        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
        /*new Thread() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(100);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.run();*/
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

}
