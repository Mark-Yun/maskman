package com.mark.zumo.client.customer.view.permission;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mark.zumo.client.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 19. 7. 14.
 */
public class PermissionActivity extends AppCompatActivity {

    public static final String TAG = "PermissionActivity";

    private static final int REQUEST_CODE = 2412;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    public static void start(final Activity activity, final int resultCode) {
        Intent intent = new Intent(activity, PermissionActivity.class);
        activity.startActivityForResult(intent, resultCode);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        ButterKnife.bind(this);
        inflateView();
    }

    private void inflateView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PermissionAdapter());
    }

    @OnClick(R.id.request)
    public void onRequestedClicked() {
        ActivityCompat.requestPermissions(this, Permissions.PERMISSIONS, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final String[] permissions, final int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                setResult(RESULT_OK);
                finish();
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
