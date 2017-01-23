package com.wuyz.qianghongbao;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;


public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "MyAccessibilityService";

    @Override
    public void onCreate() {
        Log2.d(TAG, "onCreate");
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
        process(event);
    }

    private boolean process(AccessibilityEvent event) {
        if (event == null)
            return false;
        CharSequence className = event.getClassName();
        if (!Notification.class.getName().equals(className))
            return false;

        List<CharSequence> texts = event.getText();
        if (texts == null || texts.isEmpty())
            return false;

//        Log2.d(TAG, "onAccessibilityEvent, type: %X, package: %s, class: %s, texts: %s",
//                event.getEventType(), event.getPackageName(), className, texts);
        for (CharSequence s : texts) {
            String text = s.toString();
            if (!NotifyManager.getInstance().checkNotification(text))
                continue;
            Log2.d(TAG, "process, type: %X, text: %s", event.getEventType(), text);
            Notification notification = (Notification) event.getParcelableData();
            if (notification == null)
                continue;
            NotifyManager.getInstance().doNotify(this, notification);
            return true;
        }
        return false;
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
    }

    public static boolean isEnable(Context context) {
        String enable = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
//        Log2.d(TAG, "ACCESSIBILITY_ENABLED %s", enable);
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
