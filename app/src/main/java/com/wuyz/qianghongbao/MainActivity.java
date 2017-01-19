package com.wuyz.qianghongbao;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.set_accessibility_button).setOnClickListener(this);
        findViewById(R.id.set_notification_button).setOnClickListener(this);
        findViewById(R.id.notify_button).setOnClickListener(this);

//        boolean ret = bindService(new Intent(this, MyAccessibilityService.class), new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                Log2.d(TAG, "onServiceConnected");
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                Log2.d(TAG, "onServiceDisconnected");
//            }
//        }, BIND_AUTO_CREATE);
//        Log2.d(TAG, "bindService %b", ret);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_accessibility_button:
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                break;
            case R.id.set_notification_button:
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                break;
            case R.id.notify_button:
                sendNotification();
//                test();
                break;
        }
    }

    private void sendNotification() {
        Notification.Builder builder = new Notification.Builder(MainActivity.this);
        builder.setTicker("qianghongbao" + SystemClock.elapsedRealtime());
        builder.setContentTitle("qianghongbao title" + SystemClock.elapsedRealtime());
        builder.setContentText("qianghongbao text" + SystemClock.elapsedRealtime());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(PendingIntent.getActivity(MainActivity.this, 1,
                new Intent(MainActivity.this, OtherActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setAutoCancel(true);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    private void test() {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> services = packageManager.queryIntentServices(
                new Intent(AccessibilityService.SERVICE_INTERFACE),
                PackageManager.GET_META_DATA
                        | PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS
                        | PackageManager.MATCH_DIRECT_BOOT_AWARE
                        | PackageManager.MATCH_DIRECT_BOOT_UNAWARE);
        if (services != null) {
            for (ResolveInfo resolveInfo : services) {
                Log2.d(TAG, "services: %s %s", resolveInfo.serviceInfo.name, resolveInfo.serviceInfo.permission);
            }
        }
    }

    private void doCheck() {
        if (!MyAccessibilityService.isEnable(this)) {
            Toast.makeText(this, "请开启抢红包", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!MyNotificationService.isEnable(this)) {
                Toast.makeText(this, "请开启抢红包", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doCheck();
    }
}
