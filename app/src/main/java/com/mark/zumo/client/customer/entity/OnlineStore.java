package com.mark.zumo.client.customer.entity;

import android.os.Bundle;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import java.util.Optional;

/**
 * Created by mark on 20. 3. 21.
 */
@Entity
public class OnlineStore {

    public static final int SOLD_OUT = 0;
    public static final int PREPARING = 1;
    public static final int ON_SALE = 2;

    @PrimaryKey @NonNull
    public String store_url;
    public String store_name;
    public String img_url;
    public String title;
    public String price;
    public String start_time;
    @SaleStatus
    public int status;
    @Expose(serialize = false, deserialize = false) @Ignore
    public boolean hide;
    @Expose(serialize = false, deserialize = false) @Ignore
    public boolean subscribe;

    public static OnlineStore fromBundle(final Bundle bundle) {
        final OnlineStore onlineStore = new OnlineStore();
        onlineStore.store_url = bundle.getString("store_url");
        onlineStore.store_name = bundle.getString("store_name");
        onlineStore.img_url = bundle.getString("img_url");
        onlineStore.title = bundle.getString("title");
        onlineStore.price = bundle.getString("price");
        onlineStore.start_time = bundle.getString("start_time");
        onlineStore.status = bundle.getInt("status");
        return onlineStore;
    }

    @NonNull
    @Override
    public String toString() {
        return "[OnlineStore]" +
                " store_name=" + store_name +
                " img_url=" + img_url +
                " store_url=" + store_url +
                " title=" + title +
                " price=" + price +
                " status=" + status +
                " start_time=" + start_time;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        return Optional.ofNullable(obj)
                .filter(this.getClass()::isInstance)
                .map(this.getClass()::cast)
                .map(OnlineStore::toString)
                .orElse("")
                .equals(this.toString());
    }

    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("store_url", store_url);
        bundle.putString("store_name", store_name);
        bundle.putString("img_url", img_url);
        bundle.putString("title", title);
        bundle.putString("price", price);
        bundle.putString("start_time", start_time);
        bundle.putInt("status", status);
        return bundle;
    }

    @IntDef(value = {SOLD_OUT, PREPARING, ON_SALE})
    public @interface SaleStatus {
    }
}
