package com.wuyz.qianghongbao;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioAttributes;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;
import java.util.Locale;


public class MyAccessibilityService extends AccessibilityService implements TextToSpeech.OnInitListener {

    private static final String TAG = "MyAccessibilityService";

    private Vibrator vibrator;
    private AudioAttributes mAudioAttributes;
    private TextToSpeech mTts;
    private Boolean mTtsInited = false;

    @Override
    public void onCreate() {
        Log2.d(TAG, "onCreate");
//        AudioAttributes.Builder builder = new AudioAttributes.Builder();
//        builder.setUsage(AudioAttributes.USAGE_NOTIFICATION);
//        mAudioAttributes = builder.build();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mTts = new TextToSpeech(getApplicationContext(), this);
    }

    @Override
    protected void onServiceConnected() {
        Log2.d(TAG, "onServiceConnected");
        setService();
    }

    private void setService() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.packageNames = Constants.TRACK_PACKAGES;
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
//        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 100;
        info.flags = AccessibilityServiceInfo.DEFAULT;
        setServiceInfo(info);
    }

    public static final int TYPE_VIEW_CLICKED = 0x00000001;
    public static final int TYPE_VIEW_LONG_CLICKED = 0x00000002;
    public static final int TYPE_VIEW_SELECTED = 0x00000004;
    public static final int TYPE_VIEW_FOCUSED = 0x00000008;
    public static final int TYPE_VIEW_TEXT_CHANGED = 0x00000010;
    public static final int TYPE_WINDOW_STATE_CHANGED = 0x00000020;
    public static final int TYPE_NOTIFICATION_STATE_CHANGED = 0x00000040;
    public static final int TYPE_VIEW_HOVER_ENTER = 0x00000080;
    public static final int TYPE_VIEW_HOVER_EXIT = 0x00000100;
    public static final int TYPE_TOUCH_EXPLORATION_GESTURE_START = 0x00000200;
    public static final int TYPE_TOUCH_EXPLORATION_GESTURE_END = 0x00000400;
    public static final int TYPE_WINDOW_CONTENT_CHANGED = 0x00000800;
    public static final int TYPE_VIEW_SCROLLED = 0x00001000;
    public static final int TYPE_VIEW_TEXT_SELECTION_CHANGED = 0x00002000;
    public static final int TYPE_ANNOUNCEMENT = 0x00004000;
    public static final int TYPE_VIEW_ACCESSIBILITY_FOCUSED = 0x00008000;
    public static final int TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED = 0x00010000;
    public static final int TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY = 0x00020000;
    public static final int TYPE_GESTURE_DETECTION_START = 0x00040000;
    public static final int TYPE_GESTURE_DETECTION_END = 0x00080000;
    public static final int TYPE_TOUCH_INTERACTION_START = 0x00100000;
    public static final int TYPE_TOUCH_INTERACTION_END = 0x00200000;
    public static final int TYPE_WINDOWS_CHANGED = 0x00400000;
    public static final int TYPE_VIEW_CONTEXT_CLICKED = 0x00800000;
    public static final int TYPE_ASSIST_READING_CONTEXT = 0x01000000;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        List<CharSequence> texts = event.getText();

        Log2.d(TAG, "onAccessibilityEvent, type: %X, package: %s, class: %s, texts: %s",
                event.getEventType(), event.getPackageName(), className, texts);
        if (Notification.class.getName().equals(className)) {
            if (texts != null && texts.size() > 0) {
                for (CharSequence s : texts) {
                    String content = s.toString();
                    Log2.d(TAG, "onAccessibilityEvent, text: %s", content);
//                    if (content.contains("红包")) {
//                        vibrator.vibrate(new long[] {1000, 500}, 4, mAudioAttributes);
//                    }
                    if (content.contains("红包") || content.contains("qianghongbao")) {
                        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        if (!(powerManager.isScreenOn())) {
                            PowerManager.WakeLock wl = powerManager.newWakeLock(
                                    PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_DIM_WAKE_LOCK, "hongbao");
                            wl.acquire();
                            wl.release();
                        }

                        vibrator.vibrate(new long[] {500, 1000, 500, 1000}, -1);
                        if (mTtsInited) {
                            mTts.speak("red bag", TextToSpeech.QUEUE_ADD, null);
//                            mTts.speak("red bag is coming", TextToSpeech.QUEUE_ADD, null);
//                            mTts.speak("red bag is coming", TextToSpeech.QUEUE_ADD, null);
                        }
                        Notification notification = (Notification) event.getParcelableData();
                        if (notification != null) {
                            try {
                                notification.contentIntent.send();
                            } catch (PendingIntent.CanceledException e) {
                                Log2.e(TAG, e);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            mTts.setLanguage(Locale.US);
            mTtsInited = true;
        }
    }

    @Override
    public void onDestroy() {
        if (mTtsInited) {
            mTts.shutdown();
            mTts = null;
        }
    }

    public static boolean isEnable(Context context) {
        String enable = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        Log2.d(TAG, "ACCESSIBILITY_ENABLED %s", enable);
        if (!"1".equalsIgnoreCase(enable))
            return false;

        String services = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
//        Log2.d(TAG, "enabled_accessibility_services %s", services);
        if (!TextUtils.isEmpty(services)) {
            String[] arr = services.split(":");
            String myPackage = context.getPackageName();
            for (String name : arr) {
                ComponentName componentName = ComponentName.unflattenFromString(name);
                if (componentName != null && myPackage.equals(componentName.getPackageName()))
                    return true;
            }
        }
        return false;
    }
}
