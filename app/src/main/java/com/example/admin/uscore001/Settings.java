package com.example.admin.uscore001;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Настройки
 */

public class Settings {
    public static final String USER_LOGIN = "user_login";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_STATUS = "user_status";
    public static final String SETTINGS = "settings";
    private static SharedPreferences sharedPreferences;

    static {
        Context context = App.context;
        sharedPreferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
    }

    public static String getLogin() {
        return sharedPreferences.getString(USER_LOGIN, "");
    }

    public static void setLogin(String login) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_LOGIN, login);
        editor.apply();
    }

    public static String getPassword() {
        return sharedPreferences.getString(USER_PASSWORD, "");
    }

    public static void setPassword(String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_PASSWORD, password);
        editor.apply();
    }

    public static String getStatus() {
        return sharedPreferences.getString(USER_STATUS, "");
    }

    public static void setStatus(String status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_STATUS, status);
        editor.apply();
    }
}
