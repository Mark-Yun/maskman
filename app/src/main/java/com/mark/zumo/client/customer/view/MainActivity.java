package com.mark.zumo.client.customer.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.install.model.AppUpdateType;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.bloc.AppUpdateBLOC;
import com.mark.zumo.client.customer.util.DateUtils;
import com.mark.zumo.client.customer.view.map.MapsFragment;
import com.mark.zumo.client.customer.view.online.OnlineStoreFragment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private Map<Integer, Fragment> fragmentMap;
    private AppUpdateBLOC appUpdateBLOC;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fragmentMap = new ConcurrentHashMap<>();
        appUpdateBLOC = ViewModelProviders.of(this).get(AppUpdateBLOC.class);


        if (!appUpdateBLOC.isRejectedAppUpdate()) {
            appUpdateBLOC.setActivity(this, REQUEST_CODE_UPDATE)
                    .updateIfPossibleOnAppUpdateType(AppUpdateType.FLEXIBLE)
                    .subscribe();
        }

        final String action = getIntent().getAction();
        Log.d(TAG, "onCreate: action=" + action);
        if (TextUtils.equals(action, SplashActivity.ACTION_VIEW_STORE)) {
            transitionFragment(createFragment(R.id.nav_map));
            navView.setSelectedItemId(R.id.nav_map);
        } else if (TextUtils.equals(action, SplashActivity.ACTION_VIEW_ONLINE_STORE)) {
            transitionFragment(createFragment(R.id.nav_shop));
            navView.setSelectedItemId(R.id.nav_shop);
        } else {
            transitionFragment(createFragment(R.id.nav_map));
            navView.setSelectedItemId(R.id.nav_map);
            showGuideToast();
        }

        navView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        if (itemId == navView.getSelectedItemId()) {
            return false;
        }

        Fragment fragment = fragmentMap.computeIfAbsent(itemId, this::createFragment);
        transitionFragment(fragment);
        return true;
    }

    private void transitionFragment(final Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private Fragment createFragment(int itemId) {
        switch (itemId) {
            case R.id.nav_map:
                return MapsFragment.newInstance(getIntent().getExtras());
            case R.id.nav_shop:
                return OnlineStoreFragment.newInstance(getIntent().getExtras());
            default:
                throw new IllegalArgumentException("Not specified item id");
        }
    }


    private void mark(View view) {

    }

    private void showGuideToast() {
        String todayPartition = DateUtils.getTodayPartition(this);
        String tomorrowPartition = DateUtils.getTomorrowPartition(this);

        String message = getString(R.string.purchase_today, todayPartition) + "\n"
                + getString(R.string.purchase_tomorrow, tomorrowPartition);

        Snackbar.make(findViewById(R.id.container), message, Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setAction(android.R.string.ok, this::mark)
                .show();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_UPDATE
                && resultCode != AppCompatActivity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: update flow rejected");
            appUpdateBLOC.onRejectAppUpdate();
        }

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
