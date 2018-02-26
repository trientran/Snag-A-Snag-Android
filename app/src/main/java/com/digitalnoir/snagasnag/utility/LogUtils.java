package com.digitalnoir.snagasnag.utility;

import android.util.Log;
import com.digitalnoir.snagasnag.BuildConfig;
/**
 * This utility is very important as it helps to avoid calling Log.d when in release mode
 */

public class LogUtils {
    public static void debug(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }
}