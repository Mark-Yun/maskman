package com.mark.zumo.client.customer.view.permission;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mark.zumo.client.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 20. 3. 22.
 */
public class PermissionFragment extends Fragment {

    public static final String TAG = "PermissionActivity";

    private static final int REQUEST_CODE = 2412;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private Runnable onSuccess;
    private Runnable onFailed;

    public static PermissionFragment newInstance() {

        Bundle args = new Bundle();

        PermissionFragment fragment = new PermissionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PermissionFragment onSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }

    public PermissionFragment onFailed(Runnable onFailed) {
        this.onFailed = onFailed;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_permission, container, false);
        ButterKnife.bind(this, view);
        inflateView();
        return view;
    }

    private void inflateView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new PermissionAdapter());
    }

    @OnClick(R.id.request)
    public void onRequestedClicked() {
        ActivityCompat.requestPermissions(getActivity(), Permissions.PERMISSIONS, REQUEST_CODE);
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
                    Log.d(TAG, "onRequestPermissionsResult: success");
                    onSuccess.run();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "onRequestPermissionsResult: failed");
                    onFailed.run();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
