package com.it_score.admin.uscore001;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Настройки
 */

public class Settings {
    public static final String USER_ID = "user_id";
    public static final String USER_LOGIN = "user_login";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_STATUS = "user_status";
    public static final String SETTINGS = "settings";
    public static final String TEACHER_STATUS = "PGIg1vm8SrHN6YLeN0TD";
    public static final String TEACHER_HELPER_STATUS = "BpYvYudLYGkfZLspkctl";
    public static final String STUDENT_STATUS = "y1igExymzKFaV3BU8zH8";
    public static final String GROUP_NAME = "group_name";
    private static SharedPreferences sharedPreferences;

    static {
        Context context = App.context;
        sharedPreferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
    }

    public static String getGroupName(){
        return sharedPreferences.getString(GROUP_NAME, "");
    }

    public static void setGroupName(String groupName){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(GROUP_NAME, groupName);
        editor.apply();
    }

    public static String getUserId(){
        return sharedPreferences.getString(USER_ID, "");
    }

    public static void setUserId(String userId){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, userId);
        editor.apply();
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
