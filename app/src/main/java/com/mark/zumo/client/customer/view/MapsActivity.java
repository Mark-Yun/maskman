package com.mark.zumo.client.customer.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
import com.mark.zumo.client.customer.ContextHolder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.bloc.MainViewBLOC;
import com.mark.zumo.client.customer.entity.Store;
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
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_CODE = "code";

    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final float DEFAULT_ZOOM = 15f;
    private static final float MIN_ZOOM = 13f;
    @BindView(R.id.list_button) MaterialButton listButton;

    private MainViewBLOC mainViewBLOC;
    private SupportMapFragment supportMapFragment;

    private CompositeDisposable compositeDisposable;

    private List<Store> currentStoreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        mainViewBLOC = ViewModelProviders.of(this).get(MainViewBLOC.class);
        compositeDisposable = new CompositeDisposable();
        currentStoreList = new CopyOnWriteArrayList<>();

        inflateFilterFragment();
        inflateMapFragment();
    }

    private void inflateFilterFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.filter, MapFilterFragment.newInstance())
                .commit();

        FilterSettingUtils.registerOnFilterSettingChanged(this, this);
    }

    private void inflateMapFragment() {
        supportMapFragment = Optional.ofNullable(getSupportFragmentManager().findFragmentById(R.id.map))
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

        if (getIntent().hasExtra(KEY_CODE)) {
            mainViewBLOC.observableStore(getIntent().getStringExtra(KEY_CODE))
                    .firstElement()
                    .doOnSuccess(this::focusOnStore)
                    .subscribe();
        } else {
            onLocationLoaded(googleMap, mainViewBLOC.getCurrentLocation());
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

        findViewById(R.id.store_detail).setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.store_detail, storeDetailFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        return true;
    }

    private void onCloseClicked(final Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .remove(fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .commit();

        findViewById(R.id.store_detail).setVisibility(View.GONE);
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

            if (googleMap.getCameraPosition().zoom < MIN_ZOOM) {
                Toast.makeText(ContextHolder.getContext(), "범위가 너무 넓습니다. 지도를 확대해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "onCameraIdle: zoom=" + googleMap.getCameraPosition().zoom);

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

    private void addMarker(final GoogleMap googleMap, LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng);

        googleMap.addMarker(markerOptions);
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

        int count = 0;

        for (Store store : storeList) {
            if (!FilterSettingUtils.getFilterSetting(this, store.remain_stat)) {
                continue;
            }
            count++;
            createMarker(store)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess(googleMap::addMarker)
                    .doOnSubscribe(compositeDisposable::add)
                    .subscribe();
        }

        listButton.setText(getString(R.string.list_button_text, count));
    }

    @NotNull
    private Maybe<MarkerOptions> createMarker(final Store store) {
        return Maybe.create(emitter -> {
            emitter.onSuccess(
                    new MarkerOptions()
                            .position(new LatLng(store.lat, store.lng))
                            .icon(MapUtils.createCustomMarker(this, store.remain_stat, store.type))
                            .title(store.code)
            );
            emitter.onComplete();
        });
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        supportMapFragment.getMapAsync(googleMap -> onLoadStoreList(googleMap, currentStoreList));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FilterSettingUtils.unRegisterOnFilterSettingChanged(this, this);
    }

    @OnClick(R.id.list_button)
    public void onListButtonClicked() {
        List<Store> refinedStoreList = currentStoreList.stream()
                .filter(store -> FilterSettingUtils.getFilterSetting(this, store.remain_stat))
                .collect(Collectors.toList());

        StoreListActivity.startActivity(this, refinedStoreList);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
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
