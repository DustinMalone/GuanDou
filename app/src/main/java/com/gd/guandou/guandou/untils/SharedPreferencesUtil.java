package com.gd.guandou.guandou.untils;

import android.content.Context;
import android.content.SharedPreferences;




/**
 * SharedPreferences工具类
 */
public class SharedPreferencesUtil {
    private static SharedPreferences mSharedPreferences;

    private static SharedPreferences getmSharedPreferences(Context context){
        if (mSharedPreferences==null){
            mSharedPreferences=context.getSharedPreferences("GuanDou",0);
        }
        return mSharedPreferences;
    }

    /**
     * @param context 上下文
     * @param key     键
     * @param value   默认值
     */
    public  static void putString(Context context, String key, String value){
        //获取Editor对象
        SharedPreferences.Editor editor=SharedPreferencesUtil.getmSharedPreferences(context).edit();
        editor.putString(key,value);
        editor.commit();
    }

    /**
     * @param context 上下文
     * @param key     键
     * @param value   默认值
     * @return 根据key获得的值
     */
    public static String getString(Context context, String key, String value) {
        return getmSharedPreferences(context).getString(key, value);
    }

    /**
     * @param context 上下文
     * @param key     键
     * @param value   默认值
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getmSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * @param context 上下文
     * @param key     键
     * @param value   默认值
     * @return 根据key获得的值
     */
    public static boolean getBoolean(Context context, String key, boolean value) {
        return getmSharedPreferences(context).getBoolean(key, value);
    }

    /**
     * @param context 上下文
     * @param key     键
     * @param value   默认值
     */
    public static void putLong(Context context, String key, Long value) {
        SharedPreferences.Editor editor = getmSharedPreferences(context).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * @param context 上下文
     * @param key     键
     * @param value   默认值
     * @return 根据key获得的值
     */
    public static Long getLong(Context context, String key, Long value) {
        return getmSharedPreferences(context).getLong(key, value);
    }

    /**
     * @param context 上下文
     * @param key     键
     */
    public static void remove(Context context, String key) {
        SharedPreferences.Editor editor = getmSharedPreferences(context).edit();
        editor.remove(key);
        editor.commit();
    }
}
