package com.mark.zumo.client.customer.bloc;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mark.zumo.client.customer.entity.OnlineStore;
import com.mark.zumo.client.customer.entity.PushAgreement;
import com.mark.zumo.client.customer.model.StoreManager;
import com.mark.zumo.client.customer.receiver.AlarmReceiver;
import com.mark.zumo.client.customer.util.DateUtils;

import java.util.Date;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 20. 3. 21.
 */
public class OnlineStoreBLOC extends AndroidViewModel {

    private static final String TAG = "OnlineStoreBLOC";

    private final StoreManager storeManager;
    private final CompositeDisposable compositeDisposable;

    public OnlineStoreBLOC(@NonNull final Application application) {
        super(application);

        storeManager = StoreManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public Observable<List<OnlineStore>> observeOnlineStoreList() {
        return storeManager.observableOnlineStore()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add);
    }

    public void queryOnlineStoreList() {
        storeManager.queryOnlineStore()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
    }

    public void setHideOnlineStore(final OnlineStore onlineStore, final boolean hide) {
        onlineStore.hide = hide;
        storeManager.updateOnlineStore(onlineStore);
    }

    public void onSubscribeOnlineStore(final OnlineStore onlineStore, final boolean subscribe) {
        onlineStore.subscribe = subscribe;
        storeManager.updateOnlineStore(onlineStore);

        final AlarmManager alarmManager = getApplication().getSystemService(AlarmManager.class);
        final Intent intent = new Intent(getApplication(), AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ACTION_ONLINE_STORE_ALARM);
        intent.putExtras(onlineStore.toBundle());
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplication(),
                onlineStore.store_url.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);

        if (subscribe) {
            final Date date = DateUtils.createDateOnlineStore(onlineStore.start_time);
            final long exactTime = date.getTime() - 5 * 60 * 1000;

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, exactTime, pendingIntent);
            Log.d(TAG, "onSubscribeOnlineStore: subscribe=" + subscribe + " when=" + exactTime +
                    " pendingIntent=" + pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "onSubscribeOnlineStore: subscribe=" + subscribe + " pendingIntent=" + pendingIntent);
        }
    }

    public Observable<Boolean> observableSubscriptionNewOnlineStore(final String userUuid) {
        return storeManager.observableSubscriptionNewOnlineStore(userUuid)
                .map(pushAgreement -> pushAgreement.value == PushAgreement.AGREED)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add);
    }

    public void onClick(final OnlineStore onlineStore) {
        Log.d(TAG, "onClick: store_url=" + onlineStore.store_url);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(onlineStore.store_url));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(browserIntent);
    }

    public Maybe<Boolean> subscribeNewOnlineStore(final String userUuid, final boolean subscribe) {
        return storeManager.subscribeNewOnlineStore(userUuid, subscribe)
                .map(pushAgreement -> pushAgreement.value == PushAgreement.AGREED)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add);
    }
}
