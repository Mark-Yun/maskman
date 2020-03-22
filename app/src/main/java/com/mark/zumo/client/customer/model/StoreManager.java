package com.mark.zumo.client.customer.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mark.zumo.client.customer.ContextHolder;
import com.mark.zumo.client.customer.entity.OnlineStore;
import com.mark.zumo.client.customer.entity.PushAgreement;
import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.entity.StoreHistory;
import com.mark.zumo.client.customer.entity.Sub;
import com.mark.zumo.client.customer.entity.Token;
import com.mark.zumo.client.customer.model.local.DatabaseProvider;
import com.mark.zumo.client.customer.model.local.dao.OnlineStoreDao;
import com.mark.zumo.client.customer.model.local.dao.StoreDao;
import com.mark.zumo.client.customer.model.local.dao.SubDao;
import com.mark.zumo.client.customer.model.server.AppServer;
import com.mark.zumo.client.customer.model.server.AppServerProvider;
import com.mark.zumo.client.customer.util.DateUtils;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 20. 3. 8.
 */
public enum StoreManager {
    INSTANCE;

    public static final String KEY_HIDE = "hide_";
    public static final String KEY_SUBSCRIBE = "subscribe_";
    private final Context context;
    private final AppServer appServer;
    private final StoreDao storeDao;
    private final SubDao subDao;
    private final OnlineStoreDao onlineStoreDao;

    StoreManager() {
        context = ContextHolder.getContext();

        appServer = AppServerProvider.INSTANCE.appServer;

        storeDao = DatabaseProvider.INSTANCE.maskManDatabase.storeDao();
        subDao = DatabaseProvider.INSTANCE.maskManDatabase.subDao();
        onlineStoreDao = DatabaseProvider.INSTANCE.maskManDatabase.onlineStoreDao();
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

    public Observable<List<OnlineStore>> observableOnlineStore() {
        return onlineStoreDao.flowableOnlineStoreList()
                .flatMapSingle(onlineStores ->
                        Observable.fromIterable(onlineStores)
                                .map(this::updateSharedPreference)
//                                .filter(onlineStore -> DateUtils.isForwardedDate(onlineStore.start_time))
                                .sorted((o1, o2) -> DateUtils.isFaster(o1.start_time, o2.start_time))
                                .toList())
                .distinctUntilChanged()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    public OnlineStore updateSharedPreference(final OnlineStore onlineStore) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        onlineStore.hide = defaultSharedPreferences
                .getBoolean(KEY_HIDE + onlineStore.store_url, false);
        onlineStore.subscribe = defaultSharedPreferences
                .getBoolean(KEY_SUBSCRIBE + onlineStore.store_url, false);
        return onlineStore;
    }

    public Maybe<List<OnlineStore>> queryOnlineStore() {
        return appServer.queryOnlineStore()
                .doOnSuccess(onlineStoreDao::insertOnlineStoreList)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<StoreHistory>> observableStoreHistory(final String code) {
        return storeDao.flowableStoreHistory(code, 7)
                .distinctUntilChanged()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<StoreHistory>> queryStoreHistory(final String code) {
        return appServer.queryStoreHistory(code)
                .flatMap(storeHistoryList ->
                        Observable.fromIterable(storeHistoryList)
                                .map(storeHistory -> storeHistory.setCode(code))
                                .toList()
                                .toMaybe())
                .doOnSuccess(storeDao::insertStoreHistoryList)
                .subscribeOn(Schedulers.io());
    }

    public Observable<PushAgreement> observableSubscriptionNewOnlineStore(final String userUuid) {
        return onlineStoreDao.flowablePushAgreement(userUuid, PushAgreement.NEW_ONLINE_STORE)
                .distinctUntilChanged()
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    public Maybe<PushAgreement> subscribeNewOnlineStore(final String userUuid, final boolean enabled) {
        PushAgreement pushAgreement = new PushAgreement();
        pushAgreement.user_id = userUuid;
        pushAgreement.push_type = PushAgreement.NEW_ONLINE_STORE;
        pushAgreement.value = enabled ? PushAgreement.AGREED : PushAgreement.REJECTED;

        return appServer.postPushAgreement(pushAgreement)
                .doOnSuccess(onlineStoreDao::insertPushAgreement)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<PushAgreement>> queryPushAgreement(final String userUuid) {
        return appServer.queryPushAgreement(userUuid)
                .doOnSuccess(onlineStoreDao::insertPushAgreementList)
                .subscribeOn(Schedulers.io());
    }

    public void updateOnlineStore(final OnlineStore onlineStore) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_HIDE + onlineStore.store_url, onlineStore.hide)
                .putBoolean(KEY_SUBSCRIBE + onlineStore.store_url, onlineStore.subscribe)
                .apply();
    }
}
