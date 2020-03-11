package com.mark.zumo.client.customer;

import android.app.Application;
import android.util.Log;

import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by mark on 20. 3. 8.
 */
public class MaskMan extends Application {

    private static final String TAG = MaskMan.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        ContextHolder.inject(this);
        RxJavaPlugins.setErrorHandler(this::onError);
    }

    private void onError(final Throwable throwable) {
        Log.e(TAG, "fatal: ", throwable);
    }
}
