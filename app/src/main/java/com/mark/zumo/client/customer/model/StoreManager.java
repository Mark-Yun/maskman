package com.mark.zumo.client.customer.model;

import android.content.Context;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mark.zumo.client.customer.ContextHolder;
import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.entity.Sub;
import com.mark.zumo.client.customer.entity.Token;
import com.mark.zumo.client.customer.model.local.DatabaseProvider;
import com.mark.zumo.client.customer.model.local.dao.StoreDao;
import com.mark.zumo.client.customer.model.local.dao.SubDao;
import com.mark.zumo.client.customer.model.server.AppServer;
import com.mark.zumo.client.customer.model.server.AppServerProvider;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 20. 3. 8.
 */
public enum StoreManager {
    INSTANCE;

    private final Context context;
    private final AppServer appServer;
    private final StoreDao storeDao;
    private final SubDao subDao;

    StoreManager() {
        context = ContextHolder.getContext();

        appServer = AppServerProvider.INSTANCE.appServer;

        storeDao = DatabaseProvider.INSTANCE.maskManDatabase.storeDao();
        subDao = DatabaseProvider.INSTANCE.maskManDatabase.subDao();
    }

    public Maybe<List<Store>> queryStoreList(double latitude1, double longitude1,
                                             double latitude2, double longitude2) {
        return appServer.queryStoreList(latitude1, longitude1, latitude2, longitude2)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(storeDao::insertStoreList);
    }

    public Observable<List<Store>> observableStoreList(double latitude1, double longitude1,
                                                       double latitude2, double longitude2) {

        return storeDao.flowableStoreListByGeo(latitude1, longitude1, latitude2, longitude2)
                .distinctUntilChanged()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<Sub>> querySubList(final String userId) {
        return appServer.querySubList(userId)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(subDao::insert);
    }

    public Observable<Store> observableStore(final String code) {
        return storeDao.flowableStore(code)
                .distinctUntilChanged()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> queryStore(final String code) {
        return appServer.queryStore(code)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(storeDao::insertStore);
    }

    public Observable<Boolean> observableSub(final String userId, final String code) {
        return subDao.flowableSub(userId, code)
                .distinctUntilChanged()
                .map(integer -> integer > 0)
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Sub> removeSub(final String userId, final String code) {
        return appServer.deleteSub(userId, code)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(subDao::delete);
    }

    public Maybe<Sub> createSub(final String userId, final String code) {
        return appServer.createSub(new Sub(userId, code))
                .subscribeOn(Schedulers.io())
                .doOnSuccess(subDao::insert);
    }

    public Maybe<Token> registerPushToken(final String userId, final String tokenValue) {
        return appServer.createToken(new Token(userId, tokenValue))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<String> maybeFirebaseToken() {
        return Maybe.create(emitter -> {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                String token = instanceIdResult.getToken();
                emitter.onSuccess(token);
                emitter.onComplete();
            }).addOnFailureListener(emitter::onError);
        });
    }
}
