package com.mark.zumo.client.customer;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * Created by mark on 20. 3. 8.
 */
public class ContextHolder {
    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    private ContextHolder() {
    }

    static void inject(final Context context) {
        sContext = context;
    }

    public static Context getContext() {
        return sContext;
    }
}
