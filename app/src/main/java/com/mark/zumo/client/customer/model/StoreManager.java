package com.mark.zumo.client.customer.model;

import android.content.Context;

import com.mark.zumo.client.customer.ContextHolder;
import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.model.local.DatabaseProvider;
import com.mark.zumo.client.customer.model.local.dao.StoreDao;
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
    private final StoreDao storeDao;
    private final AppServer appServer;

    StoreManager() {
        context = ContextHolder.getContext();
        storeDao = DatabaseProvider.INSTANCE.maskManDatabase.storeDao();
        appServer = AppServerProvider.INSTANCE.appServer;
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

    public Observable<Store> observableStore(final String code) {
        return storeDao.flowableStore(code)
                .distinctUntilChanged()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }
}
