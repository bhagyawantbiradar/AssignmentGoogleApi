package com.example.solo.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class StoreData {

    private static final String PREF_NAME = "ExiomsAssignment";
    private static SharedPreferences sharedPreferences;

    public static void putBoolean(Context context, String key, boolean value) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static void putBooleanArray(Context context, String arrayName, boolean[] array) {

        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(arrayName +"_size", array.length);

        for(int i=0;i<array.length;i++)
            editor.putBoolean(arrayName + "_" + i, array[i]);

        editor.apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void putString(Context context, String key, String value) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }


    public static void putInt(Context context, String key, int value) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static int getInt(Context context, String key, int value) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, value);
    }

    public static void clearSharePreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    public static void putLong(Context context, String key, long value){
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public static long getLong(Context context, String key, long defValue) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defValue);
    }



}
