package com.wuyz.qianghongbao;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification.Builder builder = new Notification.Builder(MainActivity.this);
                builder.setTicker("titcker title" + SystemClock.elapsedRealtime());
                builder.setContentTitle("content title" + SystemClock.elapsedRealtime());
                builder.setContentText("content text" + SystemClock.elapsedRealtime());
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setContentIntent(PendingIntent.getActivity(MainActivity.this, 1,
                        new Intent(MainActivity.this, OtherActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT));
                builder.setAutoCancel(true);
                Notification notification = builder.build();
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification);
            }
        });
    }
}
