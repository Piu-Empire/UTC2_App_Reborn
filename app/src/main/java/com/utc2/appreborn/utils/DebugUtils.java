package com.utc2.appreborn.utils;

import android.util.Log;
import android.view.View;

public class DebugUtils {
    private static final String TAG = "ViewUtilsDebug";
    public static boolean IS_DEBUG = false; // Bật/tắt log toàn app tại đây

    public static void log(View view, String method, String message) {
        if (!IS_DEBUG) return;

        String viewName = "null";
        if (view != null) {
            try {
                if (view.getId() != View.NO_ID) {
                    viewName = view.getContext().getResources().getResourceEntryName(view.getId());
                } else {
                    viewName = view.getClass().getSimpleName();
                }
            } catch (Exception e) {
                viewName = "unknown_view";
            }
        }

        Log.d(TAG, String.format("[%s] -> View(%s): %s", method, viewName, message));
    }
}