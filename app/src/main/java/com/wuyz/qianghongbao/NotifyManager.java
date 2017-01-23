package com.wuyz.qianghongbao;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.TextUtils;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by wuyz on 2017/1/23.
 * NotifyManager
 */

public class NotifyManager {
    private static final String TAG = "NotifyManager";
    private static NotifyManager instance;

    private String lastText = "";
    private long lastTime = 0L;
    private SoundPool soundPool;
    private boolean soundPoolInit = false;
    private Vibrator vibrator;
    private int soundId;

    public NotifyManager() {
        init(App.getInstance());
    }

    public synchronized static NotifyManager getInstance() {
        if (instance == null) {
            instance = new NotifyManager();
        }
        return instance;
    }

    private void init(Context context) {
        if (vibrator == null)
            vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (soundPool == null) {
            soundPoolInit = false;
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            soundId = soundPool.load(context, R.raw.sirius, 1);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool sound, int sampleId, int status) {
                    if (status == 0)
                        soundPoolInit = true;
                }
            });
        }
    }

    public synchronized boolean checkNotification(String text) {
        if (TextUtils.isEmpty(text) || !Utils.containKey(text, Constants.HONG_BAO_WORD))
            return false;
        long time = System.currentTimeMillis();
        if (lastText.equals(text) && (time - lastTime) < 1500)
            return false;
        lastText = text;
        lastTime = time;
        return true;
    }

    public synchronized void doNotify(Context context, Notification notification) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!(powerManager.isScreenOn())) {
            PowerManager.WakeLock wl = powerManager.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "hongbao");
            wl.acquire();
            wl.release();
        }

        try {
            notification.contentIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Log2.e(TAG, e);
        }

        if (vibrator != null)
            vibrator.vibrate(new long[]{500, 1000, 500, 1000}, -1);
        if (soundPoolInit && soundPool != null) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            float volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            soundPool.play(soundId,
                    volume,
                    volume,
                    1, // 优先级
                    0,// 循环播放次数
                    1);// 回放速度，该值在0.5-2.0之间 1为正常速度
        }
    }
}
