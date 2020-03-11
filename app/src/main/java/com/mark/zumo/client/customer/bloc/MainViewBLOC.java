package com.mark.zumo.client.customer.bloc;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.entity.Sub;
import com.mark.zumo.client.customer.model.LocationManager;
import com.mark.zumo.client.customer.model.StoreManager;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by mark on 20. 3. 8.
 */
public class MainViewBLOC extends AndroidViewModel {

    private static final String TAG = MainViewBLOC.class.getSimpleName();

    private final LocationManager locationManager;
    private final StoreManager storeManager;
    private final CompositeDisposable compositeDisposable;

    private Disposable storeListDisposable;

    public MainViewBLOC(@NonNull final Application application) {
        super(application);

        locationManager = LocationManager.INSTANCE;
        storeManager = StoreManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public Observable<Location> observeCurrentLocation() {
        return locationManager.observeCurrentLocation()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add);
    }

    public Maybe<Location> maybeCurrentLocation() {
        return locationManager.maybeCurrentLocation()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add);
    }

    public Observable<Store> observableStore(final String code) {
        return storeManager.observableStore(code)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add);
    }

    public Maybe<List<Store>> queryStoreList(double latitude1, double longitude1,
                                             double latitude2, double longitude2) {

        Log.d(TAG, "queryStoreList: la1=" + latitude1 + " lo1" + longitude1
                + " la2=" + latitude2 + " lo2=" + longitude2);
        return storeManager.queryStoreList(latitude1, longitude1, latitude2, longitude2)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add);
    }

    public Maybe<List<Sub>> querySubList(final String userID) {
        Log.d(TAG, "querySubList: userID=" + userID);

        return storeManager.querySubList(userID)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add);
    }

    public Observable<List<Store>> observableStoreList(double latitude1, double longitude1,
                                                       double latitude2, double longitude2) {
        clearStoreListDisposable();

        return storeManager.observableStoreList(latitude1, longitude1, latitude2, longitude2)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::setStoreListDisposable);
    }

    public Location getCurrentLocation() {
        return locationManager.getCurrentLocation();
    }

    private void setStoreListDisposable(final Disposable disposable) {
        storeListDisposable = disposable;
    }

    private void clearStoreListDisposable() {
        if (storeListDisposable == null) {
            return;
        }

        storeListDisposable.dispose();
        storeListDisposable = null;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        locationManager.flushLocationServices();
    }
}
