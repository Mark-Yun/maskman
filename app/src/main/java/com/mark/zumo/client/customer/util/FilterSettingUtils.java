package com.mark.zumo.client.customer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.Nullable;

/**
 * Created by mark on 20. 3. 11.
 */
public class FilterSettingUtils {

    private static final String FILTER_SHARED_PREF = "filter";

    public static boolean getFilterSetting(@Nullable final Context context, final String key) {
        if (context == null || TextUtils.isEmpty(key)) {
            return false;
        }

        return context.getSharedPreferences(FILTER_SHARED_PREF, Context.MODE_PRIVATE)
                .getBoolean(key, true);
    }

    public static void setFilterSetting(@Nullable final Context context, final String key, final boolean show) {
        if (context == null || TextUtils.isEmpty(key)) {
            return;
        }

        context.getSharedPreferences(FILTER_SHARED_PREF, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(key, show)
                .apply();
    }

    public static void registerOnFilterSettingChanged(@Nullable final Context context,
                                                      final SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if (context == null) {
            return;
        }

        context.getSharedPreferences(FILTER_SHARED_PREF, Context.MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unRegisterOnFilterSettingChanged(@Nullable final Context context,
                                                        final SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if (context == null) {
            return;
        }

        context.getSharedPreferences(FILTER_SHARED_PREF, Context.MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(listener);
    }
}
