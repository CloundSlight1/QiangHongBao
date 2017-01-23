package com.wuyz.qianghongbao;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
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

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
//        Log2.d(TAG, "onNotificationPosted %s", sbn);
        process(sbn);
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

    private boolean process(StatusBarNotification sbn) {
        if (sbn == null)
            return false;

        String pkg = sbn.getPackageName();
        if (Utils.hasKey(pkg, Constants.TRACK_PACKAGES))
            return false;

        Notification notification = sbn.getNotification();
        if (notification == null || TextUtils.isEmpty(notification.tickerText))
            return false;
        String text = notification.tickerText.toString();
        if (NotifyManager.getInstance().checkNotification(text))
            return false;
        NotifyManager.getInstance().doNotify(this, notification);
        return true;
    }
}
