package com.mark.zumo.client.customer.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by mark on 20. 3. 8.
 */
@Entity
public class Store {
    @PrimaryKey @NonNull
    public String code;
    public String name;
    public String addr;
    public String type;
    public double lat;
    public double lng;
    public String stock_at;
    public String remain_stat;
    public String create_at;

    public static Store testData() {
        Store store = new Store();
        store.lat = 37.272069961567055;
        store.lng = 127.0544308796525;
        store.remain_stat = "few";
        store.name = "test store";
        store.type = "03";
        return store;
    }
}
