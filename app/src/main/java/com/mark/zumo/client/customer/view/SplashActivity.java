package com.mark.zumo.client.customer.view;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mark.zumo.client.customer.R;
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

    public static final String KEY_CODE = "code";

    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final int REQUEST_CODE_PERMISSION = 8121;
    private static final String KEY_IS_PERMISSION_REQUESTED = "is_permission_requested";

    @BindView(R.id.icon) AppCompatImageView icon;
    @BindView(R.id.app_label) AppCompatTextView appLabel;
    @BindView(R.id.progress_caption) AppCompatTextView progressCaption;
    @BindView(R.id.progress_bar) ContentLoadingProgressBar progressBar;
    @BindView(R.id.progress_bar_container) LinearLayoutCompat progressBarContainer;

    private MainViewBLOC mainViewBLOC;
    private SubscribeBLOC subscribeBLOC;

    private boolean completeLocation;
    private boolean completeSubList;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        mainViewBLOC = ViewModelProviders.of(this).get(MainViewBLOC.class);
        subscribeBLOC = ViewModelProviders.of(this).get(SubscribeBLOC.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkSessionAndStartActivity();
    }

    private void checkSessionAndStartActivity() {
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
            subscribeBLOC.registerToken(firebaseUser.getUid())
                    .doOnSuccess(token -> Log.d(TAG, "registerToken: user_id=" + token.user_id + " token=" + token.token_value))
                    .doOnError(throwable -> Log.e(TAG, "checkSessionAndStartActivity: ", throwable))
                    .subscribe();

            mainViewBLOC.maybeCurrentLocation()
                    .onErrorResumeNext(
                            mainViewBLOC.observeCurrentLocation()
                                    .firstElement()
                    )
                    .doOnSuccess(x -> {
                        completeLocation = true;
                        startMapsActivityIfPossible();
                    }).doOnError(throwable -> Log.e(TAG, "checkSessionAndStartActivity: ", throwable))
                    .subscribe();

            mainViewBLOC.querySubList(firebaseUser.getUid())
                    .doOnSuccess(x -> {
                        completeSubList = true;
                        startMapsActivityIfPossible();
                    })
                    .subscribe();

            ConfigManager.INSTANCE
                    .fetchConfig()
                    .subscribe();
        }
    }

    private void showLoadingData() {
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    private void hidLoadingData() {
        progressBarContainer.setVisibility(View.GONE);
    }

    private boolean isPermissionRequested(final String permission) {
        return ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void startMapsActivityIfPossible() {
        if (!completeLocation || !completeSubList) {
            return;
        }

        hidLoadingData();
        Navigator.startActivityWithFade(this, MapsActivity.class, getIntent().getExtras());
    }
}
