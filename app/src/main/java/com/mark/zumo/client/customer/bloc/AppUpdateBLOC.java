package com.mark.zumo.client.customer.bloc;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.mark.zumo.client.customer.model.ConfigManager;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 20. 3. 17.
 */
public class AppUpdateBLOC extends AndroidViewModel implements InstallStateUpdatedListener {

    private static final String TAG = "AppUpdateBLOC";

    private static final String KEY_REJECT_UPDATE = "reject_update";

    private final AppUpdateManager appUpdateManager;
    private final CompositeDisposable compositeDisposable;
    private final ConfigManager configManager;
    private final Context context;

    private Activity activity;
    private int requestCode;
    private Runnable onDownloadComplete;

    public AppUpdateBLOC(@NonNull final Application application) {
        super(application);

        appUpdateManager = AppUpdateManagerFactory.create(application);
        compositeDisposable = new CompositeDisposable();
        configManager = ConfigManager.INSTANCE;
        context = application;
    }

    @Override
    public void onStateUpdate(final InstallState state) {
        Log.d(TAG, "onStateUpdate: state=" + state);
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            Optional.ofNullable(onDownloadComplete).ifPresent(Runnable::run);
        }
    }

    public void completeUpdate() {
        appUpdateManager.completeUpdate();
    }

    public Maybe<Boolean> updateIfPossibleOnAppUpdateType(@AppUpdateType final int appUpdateType) {
        Log.d(TAG, "updateIfPossibleOnAppUpdateType: appUpdateType=" + appUpdateType);

        return Maybe.create(
                (MaybeEmitter<Boolean> emitter) ->
                        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                            Log.d(TAG, "updateIfPossibleOnAppUpdateType: appUpdateInfo=" + appUpdateInfo);
                            Log.d(TAG, "updateIfPossibleOnAppUpdateType: isUpdateTypeAllowed="
                                    + isUpdateTypeAllowed(appUpdateInfo, appUpdateType));

                            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                    && isUpdateTypeAllowed(appUpdateInfo, appUpdateType)) {

                                try {
                                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, appUpdateType, activity, requestCode);
                                    appUpdateManager.registerListener(this);
                                    emitter.onSuccess(true);
                                } catch (IntentSender.SendIntentException e) {
                                    Log.e(TAG, "maybeUpdatable: ", e);
                                    emitter.onError(e);
                                }
                            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                                // After the update is downloaded, show a notification
                                // and request user confirmation to restart the app.
                                Optional.ofNullable(onDownloadComplete).ifPresent(Runnable::run);
                                emitter.onSuccess(true);
                            } else {
                                Log.d(TAG, "updateIfPossibleOnAppUpdateType: something else");
                                emitter.onSuccess(false);
                            }
                            emitter.onComplete();
                        }).addOnFailureListener(e -> {
                                    emitter.onError(e);
                                    emitter.onComplete();
                                }
                        ))
                .timeout(2, TimeUnit.SECONDS)
                .doOnSubscribe(compositeDisposable::add)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private boolean isUpdateTypeAllowed(final AppUpdateInfo appUpdateInfo,
                                        @AppUpdateType final int appUpdateType) {
        //210020010
        int availableVersionCode = appUpdateInfo.availableVersionCode();
        long versionCode = getVersionCode();
        Log.d(TAG, "isUpdateTypeAllowed: versionCode=" + versionCode +
                " availableVersionCode=" + availableVersionCode);

        switch (appUpdateType) {
            case AppUpdateType.IMMEDIATE:
                return availableVersionCode / 10000 > versionCode / 10000;
            case AppUpdateType.FLEXIBLE:
                return availableVersionCode / 1000000 > versionCode / 1000000;
            default:
                return false;
        }
    }

    private long getVersionCode() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0)
                        .getLongVersionCode();
            } else {
                return (long) context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0)
                        .versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getVersionCode: ", e);
            return Long.MAX_VALUE;
        }
    }

    public AppUpdateBLOC setActivity(final Activity activity, int requestCode) {
        this.activity = activity;
        this.requestCode = requestCode;
        return this;
    }

    public AppUpdateBLOC onDownloadComplete(final Runnable onDownloadComplete) {
        this.onDownloadComplete = onDownloadComplete;
        return this;
    }

    public boolean isRejectedAppUpdate() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REJECT_UPDATE, false);
    }

    public void onRejectAppUpdate() {
        Log.d(TAG, "onRejectAppUpdate");
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REJECT_UPDATE, true)
                .apply();
    }
}
