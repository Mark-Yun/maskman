package com.mark.zumo.client.customer.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.bloc.MainViewBLOC;
import com.mark.zumo.client.customer.util.Navigator;
import com.mark.zumo.client.customer.view.permission.PermissionActivity;
import com.mark.zumo.client.customer.view.signin.SignInActivity;

/**
 * Created by mark on 20. 3. 8.
 */
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final int REQUEST_CODE_PERMISSION = 8121;
    private static final String KEY_IS_PERMISSION_REQUESTED = "is_permission_requested";

    private MainViewBLOC mainViewBLOC;

    private boolean completeLocation;
    private boolean completeSubList;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        mainViewBLOC = ViewModelProviders.of(this).get(MainViewBLOC.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkSessionAndStartActivity();
    }

    private void checkSessionAndStartActivity() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isPermissionRequested = sharedPreferences.getBoolean(KEY_IS_PERMISSION_REQUESTED, false);
        if (!isPermissionRequested) {
            sharedPreferences.edit()
                    .putBoolean(KEY_IS_PERMISSION_REQUESTED, true)
                    .apply();

            PermissionActivity.start(this, REQUEST_CODE_PERMISSION);
            return;
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "checkSessionAndStartActivity: firebaseUser=" + firebaseUser);

        if (firebaseUser == null) {
            Navigator.startActivityWithFade(this, SignInActivity.class);
        } else {
            //todo change load data
            mainViewBLOC.maybeCurrentLocation()
                    .doOnSuccess(x -> {
                        completeLocation = true;
                        startMapsActivityIfPossible();
                    })
                    .subscribe();

//            mainViewBLOC.querySubList(firebaseUser.getUid())
//                    .doOnSuccess(x -> {
//                        completeSubList = true;
//                        startMapsActivityIfPossible();
//                    })
//                    .subscribe();
        }
    }

    private void startMapsActivityIfPossible() {
//        if (!completeLocation || !completeSubList) {
//            return;
//        }

        Navigator.startActivityWithFade(this, MapsActivity.class);
    }
}
