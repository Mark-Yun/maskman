package com.mark.zumo.client.customer.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * Created by mark on 20. 3. 21.
 */
@Entity(primaryKeys = {"code", "date"})
public class StoreHistory {
    @NonNull public String code;
    @NonNull public String date;
    public String stock_at;
    public String empty_at;

    public StoreHistory setCode(@NonNull final String code) {
        this.code = code;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "[StoreHistory]" +
                " code=" + code +
                " date=" + date +
                " stock_at=" + stock_at +
                " empty_at=" + empty_at;
    }
}
