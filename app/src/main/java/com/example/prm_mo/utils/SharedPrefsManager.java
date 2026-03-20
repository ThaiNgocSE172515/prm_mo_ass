package com.example.prm_mo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsManager {
    private static final String PREF_NAME = "prm_mo_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static SharedPrefsManager instance;
    private SharedPreferences sharedPreferences;

    private SharedPrefsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveAccessToken(String token) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public void saveUserRole(String role) {
        sharedPreferences.edit().putString("user_role", role).apply();
    }

    public String getUserRole() {
        return sharedPreferences.getString("user_role", "Citizen");
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
