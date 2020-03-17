package com.mark.zumo.client.customer.model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.StringDef;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.mark.zumo.client.customer.ContextHolder;

import io.reactivex.Maybe;

/**
 * Created by mark on 20. 3. 17.
 */
public enum ConfigManager {
    INSTANCE;

    public static final String PHONE_NUMBER = "phone_number_enabled";
    public static final String OPEN_STATUS = "open_status_enabled";
    public static final String UPDATE_IMMEDIATE = "update_immediate_enabled";

    private static final String TAG = "ConfigManager";

    private final Context context;
    private final FirebaseRemoteConfig firebaseRemoteConfig;

    ConfigManager() {
        context = ContextHolder.getContext();

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setConfigSettingsAsync(
                new FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(3600)
                        .build()
        );

//        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
    }

    public Maybe<Boolean> fetchConfig() {
        return Maybe.create(emitter -> firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        emitter.onError(new Exception("remote config loading is failed"));
                    } else {
                        Boolean result = task.getResult();
                        Log.d(TAG, "fetchConfig: result=" + result);
                        emitter.onSuccess(result);
                    }
                    emitter.onComplete();
                }));
    }

    public boolean isEnabled(@Config final String config) {
        return firebaseRemoteConfig.getBoolean(config);
    }

    @StringDef({PHONE_NUMBER, OPEN_STATUS, UPDATE_IMMEDIATE})
    public @interface Config {
    }
}
