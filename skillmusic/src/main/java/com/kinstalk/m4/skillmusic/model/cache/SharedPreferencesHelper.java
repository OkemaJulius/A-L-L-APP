package com.kinstalk.m4.skillmusic.model.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.publicapi.CoreApplication;

import java.util.Map;

/**
 * SharedPreferences操作类
 *
 * @author jinkailong
 */
public class SharedPreferencesHelper {
    private static final String PREFERENCES_FILENAME = "sleep_sp_data";
    private static SharedPreferencesHelper sInstance;
    protected String TAG = getClass().getSimpleName();
    private Context mAppContext;

    private SharedPreferences sp;

    private SharedPreferencesHelper() {
        mAppContext = CoreApplication.getApplicationInstance();
    }

    public static SharedPreferencesHelper getInstance() {
        synchronized (SharedPreferencesHelper.class) {
            if (sInstance == null) {
                sInstance = new SharedPreferencesHelper();
            }
        }
        return sInstance;
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        QLog.d(TAG, "key:" + key + " value:" + value);
        sp = mAppContext.getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            editor.putString(key, value.toString());
        }
        editor.commit();
    }

    /**
     * 获取数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    private Object get(String key, Object defaultValue) {
        sp = mAppContext.getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE);
        if (defaultValue instanceof String || defaultValue == null) {
            return sp.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return sp.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof Float) {
            return sp.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Long) {
            return sp.getLong(key, (Long) defaultValue);
        }
        return defaultValue;
    }

    public int getInt(String key, Object defaultValue) {
        return (Integer) get(key, defaultValue);
    }

    public float getFloat(String key, Object defaultValue) {
        return (Float) get(key, defaultValue);
    }

    public Long getLong(String key, Object defaultValue) {
        return (Long) get(key, defaultValue);
    }

    public boolean getBoolean(String key, Object defaultValue) {
        return (Boolean) get(key, defaultValue);
    }

    public String getString(String key, Object defaultValue) {
        return (String) get(key, defaultValue);
    }

    /**
     * 删除某个key对应的值
     *
     * @param key
     */
    public void remove(String key) {
        Editor editor = sp.edit();
        editor.remove(key).commit();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        Editor editor = sp.edit();
        editor.clear().commit();
    }

    /**
     * 查询某个key是否存在
     */
    public boolean contains(String key) {
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> getAll(Context context) {
        return sp.getAll();
    }

}
