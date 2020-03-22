package com.mark.zumo.client.customer.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.bloc.AppUpdateBLOC;
import com.mark.zumo.client.customer.bloc.MainViewBLOC;
import com.mark.zumo.client.customer.bloc.SubscribeBLOC;
import com.mark.zumo.client.customer.model.ConfigManager;
import com.mark.zumo.client.customer.util.Navigator;
import com.mark.zumo.client.customer.view.permission.PermissionActivity;
import com.mark.zumo.client.customer.view.permission.Permissions;
import com.mark.zumo.client.customer.view.signin.SignInActivity;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 20. 3. 8.
 */
public class SplashActivity extends AppCompatActivity {

    public static final String ACTION_VIEW_STORE = "com.mark.zumo.client.customer.action.VIEW_STORE";
    public static final String ACTION_VIEW_ONLINE_STORE = "com.mark.zumo.client.customer.action.VIEW_ONLINE_STORE";
    public static final String KEY_STORE_CODE = "code";
    public static final String KEY_ONLINE_STORE_URL = "store_url";

    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final int REQUEST_CODE_PERMISSION = 8121;
    private static final int REQUEST_CODE_UPDATE = 3124;

    @BindView(R.id.icon) AppCompatImageView icon;
    @BindView(R.id.app_label) AppCompatTextView appLabel;
    @BindView(R.id.progress_caption) AppCompatTextView progressCaption;
    @BindView(R.id.progress_bar) ContentLoadingProgressBar progressBar;
    @BindView(R.id.progress_bar_container) LinearLayoutCompat progressBarContainer;

    private MainViewBLOC mainViewBLOC;
    private SubscribeBLOC subscribeBLOC;
    private AppUpdateBLOC appUpdateBLOC;

    private int loading;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        mainViewBLOC = ViewModelProviders.of(this).get(MainViewBLOC.class);
        subscribeBLOC = ViewModelProviders.of(this).get(SubscribeBLOC.class);
        appUpdateBLOC = ViewModelProviders.of(this).get(AppUpdateBLOC.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        appUpdateBLOC.setActivity(this, REQUEST_CODE_UPDATE)
                .onDownloadComplete(this::popupSnackbarForCompleteUpdate)
                .updateIfPossibleOnAppUpdateType(AppUpdateType.IMMEDIATE)
                .doOnSuccess(this::checkSessionAndStartActivity)
                .doOnError(throwable -> {
                    checkSessionAndStartActivity(false);
                    Log.e(TAG, "updateIfPossibleOnAppUpdateType: ", throwable);
                })
                .subscribe();
    }

    /* Displays the snackbar notification and call to action. */
    private void popupSnackbarForCompleteUpdate() {
        Snackbar.make(findViewById(R.id.activity), R.string.app_download_complete, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.restart, view -> appUpdateBLOC.completeUpdate())
                .show();
    }

    private void checkSessionAndStartActivity(final boolean updateNeeded) {
        if (updateNeeded) {
            return;
        }

        boolean isPermissionGranted = Arrays.stream(Permissions.PERMISSIONS)
                .allMatch(this::isPermissionRequested);

        if (!isPermissionGranted) {
            PermissionActivity.start(this, REQUEST_CODE_PERMISSION);
            return;
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "checkSessionAndStartActivity: firebaseUser=" + firebaseUser);

        if (firebaseUser == null) {
            Navigator.startActivityWithFade(this, SignInActivity.class);
        } else {
            showLoadingData();

            final int LOADING_CURRENT_LOCATION = 0x1;

            mainViewBLOC.maybeCurrentLocation()
                    .doOnSubscribe(x -> loading |= LOADING_CURRENT_LOCATION)
                    .doOnSuccess(x -> loading &= ~LOADING_CURRENT_LOCATION)
                    .onErrorResumeNext(mainViewBLOC.observeCurrentLocation()
                            .firstElement())
                    .doOnSuccess(x -> startMapsActivityIfPossible())
                    .doOnError(throwable -> Log.e(TAG, "checkSessionAndStartActivity: ", throwable))
                    .subscribe();

            mainViewBLOC.queryUserInformation(firebaseUser.getUid());

            ConfigManager.INSTANCE.fetchConfig()
                    .subscribe();
        }
    }

    private void showLoadingData() {
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    private void hideLoadingData() {
        progressBarContainer.setVisibility(View.GONE);
    }

    private boolean isPermissionRequested(final String permission) {
        return ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void startMapsActivityIfPossible() {
        if (loading > 0) {
            return;
        }

        hideLoadingData();
        Navigator.startActivityWithFade(this, MainActivity.class, getIntent());
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_UPDATE && resultCode != AppCompatActivity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: update flow failed");
            checkSessionAndStartActivity(false);
        } else if (requestCode == REQUEST_CODE_PERMISSION) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                checkSessionAndStartActivity(false);
            } else {
                finish();
            }
        }
    }
}
