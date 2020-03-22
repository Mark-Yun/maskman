package com.mark.zumo.client.customer.view.signin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.SignInButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mark.zumo.client.customer.ContextHolder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.bloc.MainViewBLOC;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 19. 7. 7.
 */
public class SignInActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SIGN_IN = 1010;

    private static Runnable onSignInSuccess;
    private static Runnable onSignInFailed;
    @BindView(R.id.sign_in_button) SignInButton signInButton;
    private MainViewBLOC mainViewBLOC;

    public static void startActivityWithFade(@Nullable final Activity activity,
                                             final Runnable onSignInSuccess,
                                             final Runnable onSignInFailed) {

        if (activity == null) {
            return;
        }

        Intent intent = new Intent(activity, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        SignInActivity.onSignInSuccess = onSignInSuccess;
        SignInActivity.onSignInFailed = onSignInFailed;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mainViewBLOC = ViewModelProviders.of(this).get(MainViewBLOC.class);

        inflateView();
    }

    private void inflateView() {
        final LayoutInflater layoutInflater = getSystemService(LayoutInflater.class);
        final View dialogView = layoutInflater.inflate(R.layout.fragment_google_auth,
                findViewById(R.id.content), false);
        ButterKnife.bind(this, dialogView);

        final AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .setOnCancelListener(dialog -> Optional.ofNullable(onSignInFailed).ifPresent(Runnable::run))
                .setOnDismissListener(dialog -> finish())
                .create();

        alertDialog.show();
        signInButton.setOnClickListener(v -> startSignInActivity());
    }

    private void startSignInActivity() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                REQUEST_CODE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {

            if (resultCode == AppCompatActivity.RESULT_OK) {
                Optional.ofNullable(onSignInSuccess)
                        .ifPresent(Runnable::run);
                mainViewBLOC.queryUserInformation();
                finish();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(ContextHolder.getContext(), "오류가 발생했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                Optional.ofNullable(onSignInFailed)
                        .ifPresent(Runnable::run);
            }
        }
    }
}
