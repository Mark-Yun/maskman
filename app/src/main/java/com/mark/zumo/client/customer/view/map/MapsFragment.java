package com.mark.zumo.client.customer.view.map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.mark.zumo.client.customer.ContextHolder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.bloc.MainViewBLOC;
import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.util.DateUtils;
import com.mark.zumo.client.customer.util.FilterSettingUtils;
import com.mark.zumo.client.customer.util.MapUtils;
import com.mark.zumo.client.customer.view.store.detail.StoreDetailFragment;
import com.mark.zumo.client.customer.view.store.list.StoreListActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 20. 3. 21.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_CODE = "code";

    private static final String TAG = MapsFragment.class.getSimpleName();

    private static final float DEFAULT_ZOOM = 15f;
    private static final float MIN_ZOOM = 13f;

    @BindView(R.id.list_button) MaterialButton listButton;

    private MainViewBLOC mainViewBLOC;

    private SupportMapFragment supportMapFragment;

    private CompositeDisposable compositeDisposable;

    private List<Store> currentStoreList;

    public static MapsFragment newInstance(final Bundle bundle) {
        MapsFragment fragment = new MapsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewBLOC = ViewModelProviders.of(this).get(MainViewBLOC.class);

        compositeDisposable = new CompositeDisposable();
        currentStoreList = new CopyOnWriteArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.bind(this, view);

        inflateFilterFragment();
        inflateMapFragment();
        showGuideToast();

        return view;
    }

    private void inflateFilterFragment() {
        if (getFragmentManager() == null) {
            return;
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.filter, MapFilterFragment.newInstance())
                .commit();

        FilterSettingUtils.registerOnFilterSettingChanged(getContext(), this);
    }

    private void inflateMapFragment() {
        supportMapFragment = Optional.of(getChildFragmentManager())
                .map(fragmentManager -> fragmentManager.findFragmentById(R.id.map))
                .filter(SupportMapFragment.class::isInstance)
                .map(SupportMapFragment.class::cast)
                .orElse(null);

        if (supportMapFragment == null) {
            return;
        }

        supportMapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mainViewBLOC.observeCurrentLocation()
                .subscribe();

        if (getArguments() != null && getArguments().containsKey(KEY_CODE)) {
            mainViewBLOC.observableStore(getArguments().getString(KEY_CODE))
                    .firstElement()
                    .doOnSuccess(this::focusOnStore)
                    .subscribe();
        } else {
            mainViewBLOC.maybeCurrentLocation()
                    .onErrorResumeNext(mainViewBLOC.observeCurrentLocation()
                            .firstElement())
                    .doOnSuccess(location -> onLocationLoaded(googleMap, location))
                    .subscribe();
        }

        initUiSettings(googleMap);
    }

    private void initUiSettings(final GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(() -> requestMyLocationButtonClicked(googleMap));
        googleMap.setOnCameraIdleListener(this::onCameraIdle);
        googleMap.setOnMarkerClickListener(marker -> onMarkerClicked(googleMap, marker));
    }

    private boolean onMarkerClicked(final GoogleMap googleMap, final Marker marker) {

        return focusOnStore(googleMap, marker.getTitle(), marker.getPosition());
    }

    private void focusOnStore(final Store store) {
        supportMapFragment.getMapAsync(googleMap ->
                focusOnStore(googleMap, store.code, new LatLng(store.lat, store.lng))
        );
    }

    private boolean focusOnStore(final GoogleMap googleMap, final String code, final LatLng latLng) {
        float zoom = Math.max(googleMap.getCameraPosition().zoom, DEFAULT_ZOOM);
        CameraUpdate locationUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        googleMap.animateCamera(locationUpdate);

        StoreDetailFragment storeDetailFragment = StoreDetailFragment.newInstance(code)
                .onCloseClicked(this::onCloseClicked);

        if (getView() != null) {
            getView().findViewById(R.id.store_detail).setVisibility(View.VISIBLE);
        }

        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.store_detail, storeDetailFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }

        return true;
    }

    private void onCloseClicked(final Fragment fragment) {
        if (getFragmentManager() == null || getView() == null) {
            return;
        }

        getFragmentManager().beginTransaction()
                .remove(fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .runOnCommit(() -> getView().findViewById(R.id.store_detail).setVisibility(View.GONE))
                .commit();
    }

    private void showGuideToast() {
        String todayPartition = DateUtils.getTodayPartition(getContext());
        String tomorrowPartition = DateUtils.getTomorrowPartition(getContext());

        String message = getString(R.string.purchase_today, todayPartition) + "\n"
                + getString(R.string.purchase_tomorrow, tomorrowPartition);

        if (getView() == null) {
            return;
        }

        Snackbar.make(getView().findViewById(R.id.container), message, Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setAction(android.R.string.ok, this::mark)
                .show();
    }

    private void mark(View view) {

    }

    private void onCameraIdle() {
        if (supportMapFragment == null) {
            return;
        }

        supportMapFragment.getMapAsync(googleMap -> {
            Projection projection = googleMap.getProjection();
            LatLngBounds latLngBounds = projection.getVisibleRegion().latLngBounds;

            LatLng northeast = latLngBounds.northeast;
            LatLng southwest = latLngBounds.southwest;

            Log.d(TAG, "onCameraIdle: northeast.latitude=" + northeast.latitude + " northeast.longitude=" + northeast.longitude
                    + " southwest.x=" + southwest.latitude + " southwest.longitude=" + southwest.longitude);

            final float zoom = googleMap.getCameraPosition().zoom;
            Log.d(TAG, "onCameraIdle: zoom=" + zoom);

            if (zoom < MIN_ZOOM && zoom > 6f) {
                Toast.makeText(ContextHolder.getContext(), "범위가 너무 넓습니다. 지도를 확대해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            mainViewBLOC.queryStoreList(northeast.latitude, northeast.longitude,
                    southwest.latitude, southwest.longitude)
                    .subscribe();

            mainViewBLOC.observableStoreList(northeast.latitude, northeast.longitude,
                    southwest.latitude, southwest.longitude)
                    .map(this::setStoreList)
                    .doOnNext(stores -> onLoadStoreList(googleMap, stores))
                    .subscribe();
        });
    }

    private List<Store> setStoreList(final List<Store> storeList) {
        currentStoreList.clear();
        currentStoreList.addAll(storeList);
        return currentStoreList;
    }

    private boolean requestMyLocationButtonClicked(final GoogleMap googleMap) {
        onLocationLoaded(googleMap, mainViewBLOC.getCurrentLocation());
        return true;
    }

    private void onLocationLoaded(final GoogleMap googleMap, Location location) {
        if (location == null) {
            return;
        }

        Log.d(TAG, "onLocationLoaded: location=" + location);

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate locationUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
        googleMap.moveCamera(locationUpdate);
    }

    private void onLoadStoreList(final GoogleMap googleMap, final List<Store> storeList) {
        googleMap.clear();
        compositeDisposable.clear();

        Observable.fromIterable(storeList)
                .filter(store -> FilterSettingUtils.isShownFilterSetting(getContext(), store.remain_stat))
                .map(this::createMarker)
                .toList()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(markerOptions -> {
                    markerOptions.forEach(googleMap::addMarker);
                    listButton.setText(getString(R.string.list_button_text, markerOptions.size()));
                })
                .subscribe();
    }

    @NotNull
    private MarkerOptions createMarker(final Store store) {
        return new MarkerOptions()
                .position(new LatLng(store.lat, store.lng))
                .icon(MapUtils.createCustomMarker(getActivity(), store.remain_stat, store.type))
                .title(store.code);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        supportMapFragment.getMapAsync(googleMap -> onLoadStoreList(googleMap, currentStoreList));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        compositeDisposable.clear();
        FilterSettingUtils.unRegisterOnFilterSettingChanged(getContext(), this);
    }

    @OnClick(R.id.list_button)
    public void onListButtonClicked() {
        List<Store> refinedStoreList = currentStoreList.stream()
                .filter(store -> FilterSettingUtils.isShownFilterSetting(getContext(), store.remain_stat))
                .collect(Collectors.toList());

        if (getActivity() == null) {
            return;
        }
        StoreListActivity.startActivity(getActivity(), refinedStoreList);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == StoreListActivity.REQUEST_CODE
                && resultCode == AppCompatActivity.RESULT_OK) {
            if (data == null) {
                return;
            }

            String storeCode = data.getStringExtra(StoreListActivity.KEY_CODE);
            mainViewBLOC.observableStore(storeCode)
                    .firstElement()
                    .doOnSuccess(this::focusOnStore)
                    .subscribe();
        }
    }
}
