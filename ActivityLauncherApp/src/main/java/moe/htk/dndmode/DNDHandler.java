package moe.htk.dndmode;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class DNDHandler {

    public static Context mContext;

    public static NotificationManager mNotificationManager;
    public static int savedNotiMode;

    public static ActivityManager am;

    public static boolean checkPermission(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return mNotificationManager.isNotificationPolicyAccessGranted();
    }

    public static void initMonitor(Context context) {
        am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        Log.d("DNDL", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        Log.d("DNDL", componentInfo.getPackageName());
    }

    public static String getTopPkg() {
        if (am == null) am = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        return taskInfo.get(0).topActivity.getPackageName();
    }

    public static void enableDND(){
        Log.d("DNDL", "Attempt to enable DND");
        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS);
        mContext.startService(new Intent(mContext, DNDService.class));
    }

    public static void saveNotiMode() {
        savedNotiMode = mNotificationManager.getCurrentInterruptionFilter();
    }

    public static void loadNotiMode() {
        Log.d("DNDL", "Attempt to restore notification mode");
        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        //mNotificationManager.setInterruptionFilter(savedNotiMode);
    }
}
