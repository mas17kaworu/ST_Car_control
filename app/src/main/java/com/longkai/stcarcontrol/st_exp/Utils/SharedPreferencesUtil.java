/**
 * CountrySelectActivityVertical.java
 * YUNEEC_APP_ANDROID
 * <p>
 * Copyright @ 2016 Yuneec.
 * All rights reserved.
 */
package com.longkai.stcarcontrol.st_exp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * Class for creating util to memory some information
 *
 * @author yf.yao
 * @version 1.0 2017/3/16
 * @since 1.0
 */

public class SharedPreferencesUtil {


    public static final String FILENAME = "share_data";


    //添加一对键值对
    public static void put(Context context, String key, Object object) {

        SharedPreferences sp = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }

    //获取一对键值对
    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    //清除一对键值对
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    // 清除所有键值对
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    //查看是否含有某键值对
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    //获取所有键值对
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /** 执行editor封装 **/
    private static class SharedPreferencesCompat {
        private static final Method SApplyMethod = findApplyMethod();

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (SApplyMethod != null) {
                    SApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }
}
