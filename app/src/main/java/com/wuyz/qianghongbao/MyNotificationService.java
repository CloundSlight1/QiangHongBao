package com.wuyz.qianghongbao;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

/**
 * Created by wuyz on 1/19/2017.
 * MyNotificationService
 */

public class MyNotificationService extends NotificationListenerService {
    private static final String TAG = "MyNotificationService";
    private Vibrator vibrator;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
//        Log2.d(TAG, "onNotificationPosted %s", sbn);
        check(sbn);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationRemoved(sbn, rankingMap);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onListenerConnected() {
        Log2.d(TAG, "onListenerConnected");
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    public static boolean isEnable(Context context) {
        String listeners = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
//        Log2.d(TAG, "enabled_notification_listeners %s", listeners);
        if (!TextUtils.isEmpty(listeners)) {
            String[] arr = listeners.split(":");
            String myPackage = context.getPackageName();
            for (String name : arr) {
                ComponentName componentName = ComponentName.unflattenFromString(name);
                if (componentName != null && myPackage.equals(componentName.getPackageName()))
                    return true;
            }
        }
        return false;
    }

    private boolean check(StatusBarNotification sbn) {
        if (sbn == null)
            return false;
        String pkg = sbn.getPackageName();
        boolean match = false;
        for (String name : Constants.TRACK_PACKAGES) {
            if (name.equals(pkg)) {
                match = true;
                break;
            }
        }
        if (!match)
            return false;
        Notification notification = sbn.getNotification();
        if (notification == null || TextUtils.isEmpty(notification.tickerText))
            return false;
        String text = notification.tickerText.toString();
        Log2.d(TAG, "notification %s", text);

        if (text.contains("红包") || text.contains("qianghongbao")) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!(powerManager.isScreenOn())) {
                PowerManager.WakeLock wl = powerManager.newWakeLock(
                        PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "hongbao");
                wl.acquire();
                wl.release();
            }
            vibrator.vibrate(new long[]{500, 1000, 500, 1000}, -1);
            return true;
        }
        return false;
    }
}
