package com.wuyz.qianghongbao;

import android.text.TextUtils;

/**
 * Created by wuyz on 2017/1/23.
 * Utils
 */

public class Utils {
    public static boolean containKey(String text, String[] keys) {
        if (TextUtils.isEmpty(text))
            return false;
        if (keys == null || keys.length == 0)
            return true;
        for (String s : keys) {
            if (text.contains(s))
                return true;
        }
        return false;
    }

    public static boolean hasKey(String text, String[] keys) {
        if (TextUtils.isEmpty(text))
            return false;
        if (keys == null || keys.length == 0)
            return true;
        for (String s : keys) {
            if (text.equals(s))
                return true;
        }
        return false;
    }
}
