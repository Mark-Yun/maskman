package com.mark.zumo.client.customer.model;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.mark.zumo.client.customer.ContextHolder;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

/**
 * Created by mark on 20. 3. 8.
 */
public enum LocationManager {
    INSTANCE;

    private static final String TAG = LocationManager.class.getSimpleName();

    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final Observable<Location> currentLocationObservable;
    private Location currentLocation;
    private ObservableEmitter<Location> currentLocationEmitter;
    private LocationCallback locationCallback;

    LocationManager() {
        context = ContextHolder.getContext();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        currentLocationObservable = Observable.create(this::requestCurrentLocation);

    }

    public Observable<Location> observeCurrentLocation() {
        return currentLocationObservable;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public Maybe<Location> maybeCurrentLocation() {
        return Maybe.create(emitter -> fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    currentLocation = location;
                    emitter.onSuccess(location);
                    emitter.onComplete();
                }).addOnFailureListener(emitter::onError));
    }

    private void requestCurrentLocation(final ObservableEmitter<Location> observableEmitter) {
        fusedLocationClient.requestLocationUpdates(
                createLocationRequest(),
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Log.d(TAG, "onLocationResult: locationResult=" + locationResult);
                        if (!locationResult.getLocations().isEmpty()) {
                            Location location = locationResult.getLocations().get(0);
                            currentLocation = location;
                            observableEmitter.onNext(location);
                        }
                    }

                    @Override
                    public void onLocationAvailability(LocationAvailability locationAvailability) {
                        Log.d(TAG, "onLocationAvailability: locationAvailability=" + locationAvailability);
                    }
                },
                null
        ).addOnFailureListener(observableEmitter::onError);
    }

    @NotNull
    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setInterval(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    public void flushLocationServices() {
        fusedLocationClient.flushLocations();
    }
}
