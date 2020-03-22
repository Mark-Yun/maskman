package com.mark.zumo.client.customer.bloc;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.entity.StoreHistory;
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

    public void queryUserInformation() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.d(TAG, "queryUserInformation: current user is null");
            return;
        }

        queryUserInformation(currentUser.getUid());
    }

    private void queryUserInformation(final String userID) {
        storeManager.querySubList(userID)
                .subscribe();

        storeManager.queryPushAgreement(userID)
                .subscribe();

        storeManager.maybeFirebaseToken()
                .flatMap(tokenValue -> storeManager.registerPushToken(userID, tokenValue))
                .subscribe();
    }

    public Maybe<List<Sub>> querySubList(final String userID) {
        Log.d(TAG, "querySubList: userID=" + userID);

        return storeManager.querySubList(userID)
                .observeOn(AndroidSchedulers.mainThread());
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

    public Observable<List<StoreHistory>> observableStoreHistoryList(final String code) {
        return storeManager.observableStoreHistory(code)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add);
    }

    public void queryStoreHistoryList(final String code) {
        storeManager.queryStoreHistory(code)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        locationManager.flushLocationServices();
    }
}
