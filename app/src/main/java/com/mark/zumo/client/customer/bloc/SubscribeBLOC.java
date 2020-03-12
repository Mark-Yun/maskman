package com.mark.zumo.client.customer.bloc;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mark.zumo.client.customer.entity.Sub;
import com.mark.zumo.client.customer.model.StoreManager;

import io.reactivex.Maybe;
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

    public Maybe<Sub> subscribe(final String userId, final String code, final boolean enable) {
        if (enable) {
            return storeManager.createSub(userId, code)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(compositeDisposable::add);
        } else {
            return storeManager.removeSub(userId, code)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(compositeDisposable::add);
        }
    }
}
