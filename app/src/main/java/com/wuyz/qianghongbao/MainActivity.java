package com.wuyz.qianghongbao;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private ToggleButton accessibilityButton;
    private ToggleButton notificationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accessibilityButton = (ToggleButton) findViewById(R.id.set_accessibility_button);
        notificationButton = (ToggleButton) findViewById(R.id.set_notification_button);
        accessibilityButton.setOnClickListener(this);
        notificationButton.setOnClickListener(this);
        findViewById(R.id.notify_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_accessibility_button:
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                break;
            case R.id.set_notification_button:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                }
                break;
            case R.id.notify_button:
                sendNotification();
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

    private void doCheck() {
        accessibilityButton.setChecked(MyAccessibilityService.isEnable(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            notificationButton.setChecked(MyNotificationService.isEnable(this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doCheck();
    }
}
