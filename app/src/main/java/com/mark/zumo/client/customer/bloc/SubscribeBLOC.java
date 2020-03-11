package com.mark.zumo.client.customer.bloc;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mark.zumo.client.customer.model.StoreManager;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 20. 3. 8.
 */
public class SubscribeBLOC extends AndroidViewModel {

    private final StoreManager storeManager;
    private final CompositeDisposable compositeDisposable;

    public SubscribeBLOC(@NonNull final Application application) {
        super(application);

        storeManager = StoreManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public Observable<Boolean> observableSub(final String userId, final String code) {
        return storeManager.observableSub(userId, code)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add);
    }

    public void subscribe(final String userId, final String code, final boolean enable) {
        if (enable) {
            storeManager.createSub(userId, code)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(compositeDisposable::add)
                    .subscribe();
        } else {
            storeManager.removeSub(userId, code)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(compositeDisposable::add)
                    .subscribe();
        }
    }
}
