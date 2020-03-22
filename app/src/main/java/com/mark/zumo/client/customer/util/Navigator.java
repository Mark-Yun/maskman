package com.mark.zumo.client.customer.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

/**
 * Created by mark on 20. 3. 8.
 */
public class Navigator {

    private Navigator() {
    }

    public static void startActivityWithFade(@Nullable final Activity activity,
                                             @NonNull final Class clazz) {

        startActivityWithFade(activity, clazz, new Bundle());
    }

    public static void startActivityWithFade(@Nullable final Activity activity,
                                             @NonNull final Class clazz,
                                             @Nullable final Bundle extras) {

        if (activity == null) {
            return;
        }

        Intent intent = new Intent(activity, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Optional.ofNullable(extras)
                .ifPresent(intent::putExtras);

        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void startActivityWithFade(@Nullable final Activity activity,
                                             @NonNull final Class clazz,
                                             @NonNull final Intent intent) {

        if (activity == null) {
            return;
        }

        Intent newIntent = new Intent(activity, clazz);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Optional.ofNullable(intent.getExtras())
                .ifPresent(newIntent::putExtras);
        Optional.ofNullable(intent.getAction())
                .ifPresent(newIntent::setAction);

        activity.startActivity(newIntent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
