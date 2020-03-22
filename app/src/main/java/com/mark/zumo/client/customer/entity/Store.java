package com.mark.zumo.client.customer.entity;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Optional;

/**
 * Created by mark on 20. 3. 8.
 */
@Entity
public class Store {

    public static final String PHARMACY = "0";
    public static final String POST = "1";
    public static final String NH = "2";

    public static final int OPENED = 1;
    public static final int CLOSED = 0;
    public static final int UNKNOWN = -1;

    public static final String PLENTY = "plenty";
    public static final String SOME = "some";
    public static final String FEW = "few";
    public static final String EMPTY = "empty";

    @PrimaryKey @NonNull
    public String code;
    public String name;
    public String addr;
    @Type public String type;
    public double lat;
    public double lng;
    public String stock_at;
    @Stock public String remain_stat;
    public String create_at;
    public String tel; //xx-xxx-xxxx 형식, 없으면 null
    @OpenStatus public int open; // 1: 영업중, 0:영업종료, -1:영업시간정보없음

    public static Store testData() {
        Store store = new Store();
        store.lat = 37.272069961567055;
        store.lng = 127.0544308796525;
        store.remain_stat = "few";
        store.name = "test store";
        store.type = "03";
        return store;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        return Optional.ofNullable(obj)
                .filter(this.getClass()::isInstance)
                .map(this.getClass()::cast)
                .map(Store::toString)
                .orElse("")
                .equals(this.toString());
    }

    @NonNull
    @Override
    public String toString() {
        return "[" + this.getClass().getSimpleName() + "]" +
                " code=" + code +
                " name=" + name +
                " addr=" + addr +
                " lat=" + lat +
                " lng=" + lng +
                " stock_at=" + stock_at +
                " remain_stat=" + remain_stat +
                " create_at=" + create_at +
                " tel=" + tel +
                " open=" + open;
    }

    @IntDef({OPENED, CLOSED, UNKNOWN})
    public @interface OpenStatus {
    }

    @StringDef({PLENTY, SOME, FEW, EMPTY})
    public @interface Stock {
    }

    @StringDef({PHARMACY, POST, NH})
    public @interface Type {
    }
}
