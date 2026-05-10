package com.utc2.appreborn.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "AppRebornSession";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_LOGIN_TYPE = "login_type";
    private static final String KEY_STUDENT_ID = "student_id";

    private static SessionManager instance;
    private final SharedPreferences sharedPreferences;

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void createLoginSession(String token, String type, String studentId) {
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_TOKEN, token)
                .putString(KEY_LOGIN_TYPE, type)
                .putString(KEY_STUDENT_ID, studentId)
                .apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getLoginType() {
        return sharedPreferences.getString(KEY_LOGIN_TYPE, "");
    }

    public void logout() {
        sharedPreferences.edit().clear().apply();
    }
}