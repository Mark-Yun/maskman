package com.mark.zumo.client.customer.view.signin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.SignInButton;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.util.Navigator;
import com.mark.zumo.client.customer.view.SplashActivity;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 19. 7. 6.
 */
public class GoogleSignInFragment extends Fragment {

    public static final String TAG = "GoogleSignInFragment";
    private static final int REQUEST_CODE_SIGN_IN = 1010;

    @BindView(R.id.sign_in_button) SignInButton signInButton;

    public static GoogleSignInFragment newInstance() {

        Bundle args = new Bundle();

        GoogleSignInFragment fragment = new GoogleSignInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_google_auth, container, false);
        ButterKnife.bind(this, view);
        inflateView();
        return view;
    }

    private void inflateView() {
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
                Navigator.startActivityWithFade(getActivity(), SplashActivity.class);
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}
