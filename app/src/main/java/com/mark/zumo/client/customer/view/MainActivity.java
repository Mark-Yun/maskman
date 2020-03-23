package com.mark.zumo.client.customer.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.install.model.AppUpdateType;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.bloc.AppUpdateBLOC;
import com.mark.zumo.client.customer.view.map.MapsFragment;
import com.mark.zumo.client.customer.view.online.OnlineStoreFragment;
import com.mark.zumo.client.customer.view.permission.PermissionFragment;
import com.mark.zumo.client.customer.view.permission.Permissions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 20. 3. 21.
 */
public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_UPDATE = 3124;

    @BindView(R.id.nav_view) BottomNavigationView navView;

    @Nullable
    private Fragment mapsFragment, onlineStoreFragment, permissionFragment;
    private Map<Class<? extends Fragment>, Fragment> fragmentMap;
    private AppUpdateBLOC appUpdateBLOC;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        appUpdateBLOC = ViewModelProviders.of(this).get(AppUpdateBLOC.class);
        fragmentMap = new HashMap<>();

        if (!appUpdateBLOC.isRejectedAppUpdate()) {
            appUpdateBLOC.setActivity(this, REQUEST_CODE_UPDATE)
                    .updateIfPossibleOnAppUpdateType(AppUpdateType.FLEXIBLE)
                    .subscribe();
        }

        final String action = getIntent().getAction();
        Log.d(TAG, "onCreate: action=" + action);
        if (TextUtils.equals(action, SplashActivity.ACTION_VIEW_STORE)) {
            transitionFragment(R.id.nav_map);
            navView.setSelectedItemId(R.id.nav_map);
        } else if (TextUtils.equals(action, SplashActivity.ACTION_VIEW_ONLINE_STORE)) {
            transitionFragment(R.id.nav_shop);
            navView.setSelectedItemId(R.id.nav_shop);
        } else {
            updateInitialMapFragment();
        }

        navView.setOnNavigationItemSelectedListener(this);
    }

    private void updateInitialMapFragment() {
        transitionFragment(R.id.nav_map);
        navView.setSelectedItemId(R.id.nav_map);
    }

    private boolean isPermissionRequested(final String permission) {
        return ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        if (itemId == navView.getSelectedItemId()) {
            return false;
        }

        transitionFragment(itemId);
        return true;
    }

    private void transitionFragment(int itemId) {

        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Class<? extends Fragment> targetFragmentClass = getaClass(itemId);
        if (targetFragmentClass == null) {
            return;
        }

        if (!fragmentMap.containsKey(targetFragmentClass)) {
            fragmentTransaction.add(R.id.content,
                    fragmentMap.computeIfAbsent(targetFragmentClass, this::createFragment));
        }

        final Fragment targetFragment = fragmentMap.get(targetFragmentClass);
        if (targetFragment != null) {
            fragmentTransaction.show(targetFragment);
        }

        fragmentMap.values().stream()
                .filter(fragment -> fragment != targetFragment)
                .forEach(fragmentTransaction::hide);

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commitAllowingStateLoss();
    }

    @Nullable
    private Class<? extends Fragment> getaClass(final int itemId) {
        if (itemId == R.id.nav_map) {
            if (!isPermissionGranted()) {
                return PermissionFragment.class;
            } else {
                return MapsFragment.class;
            }
        } else if (itemId == R.id.nav_shop) {
            return OnlineStoreFragment.class;
        } else {
            return null;
        }
    }

    private Fragment createFragment(Class<? extends Fragment> fragmentClass) {
        if (PermissionFragment.class.equals(fragmentClass)) {
            return PermissionFragment.newInstance()
                    .onSuccess(this::onSuccessGrantPermission)
                    .onFailed(this::onFailedGrantPermission);

        } else if (MapsFragment.class.equals(fragmentClass)) {
            return MapsFragment.newInstance(getIntent().getExtras());

        } else if (OnlineStoreFragment.class.equals(fragmentClass)) {
            return OnlineStoreFragment.newInstance(getIntent().getExtras());

        } else {
            throw new IllegalArgumentException();
        }
    }

    private boolean isPermissionGranted() {
        return Arrays.stream(Permissions.PERMISSIONS)
                .allMatch(this::isPermissionRequested);
    }

    private void onSuccessGrantPermission() {
        Log.d(TAG, "onSuccessGrantPermission: ");
        updateInitialMapFragment();
    }

    private void onFailedGrantPermission() {

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + " resultCode=" + resultCode);

        if (requestCode == REQUEST_CODE_UPDATE
                && resultCode != AppCompatActivity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: update flow rejected");
            appUpdateBLOC.onRejectAppUpdate();
        }

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: requestCode=" + requestCode);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
