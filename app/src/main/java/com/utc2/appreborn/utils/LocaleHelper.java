package com.utc2.appreborn.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

/**
 * LocaleHelper
 * ──────────────────────────────────────────────────────────────
 * Tiện ích đổi ngôn ngữ toàn ứng dụng.
 *
 * Cách dùng:
 *  1. Gọi LocaleHelper.setLocale(context, "en") để đổi sang Tiếng Anh
 *  2. Gọi LocaleHelper.setLocale(context, "vi") để đổi về Tiếng Việt
 *  3. Override attachBaseContext trong mọi Activity để locale được áp dụng:
 *
 *     @Override
 *     protected void attachBaseContext(Context base) {
 *         super.attachBaseContext(LocaleHelper.applyLocale(base));
 *     }
 *
 * Ngôn ngữ được lưu vào SharedPreferences — tồn tại giữa các lần mở app.
 */
public class LocaleHelper {

    private static final String PREF_NAME    = "AppRebornSession"; // dùng chung pref với SessionManager
    private static final String KEY_LANGUAGE = "app_language";
    private static final String LANG_VI      = "vi";
    private static final String LANG_EN      = "en";

    /** Lưu ngôn ngữ và trả về Context đã áp locale mới */
    public static Context setLocale(Context context, String language) {
        saveLanguage(context, language);
        return applyLocale(context, language);
    }

    /** Đọc locale đã lưu và áp vào Context — gọi trong attachBaseContext */
    public static Context applyLocale(Context context) {
        String language = getSavedLanguage(context);
        return applyLocale(context, language);
    }

    /** Trả về mã ngôn ngữ đang dùng — "vi" hoặc "en" */
    public static String getSavedLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, LANG_VI); // mặc định Tiếng Việt
    }

    /** Kiểm tra đang dùng Tiếng Anh không */
    public static boolean isEnglish(Context context) {
        return LANG_EN.equals(getSavedLanguage(context));
    }

    // ── Private helpers ───────────────────────────────────────

    private static void saveLanguage(Context context, String language) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LANGUAGE, language)
                .apply();
    }

    private static Context applyLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            context.getResources().updateConfiguration(config,
                    context.getResources().getDisplayMetrics());
            return context;
        }
    }
}