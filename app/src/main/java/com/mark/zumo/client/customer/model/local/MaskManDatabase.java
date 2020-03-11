package com.mark.zumo.client.customer.model.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.model.local.dao.StoreDao;

/**
 * Created by mark on 20. 3. 8.
 */
@Database(
        entities = {
                Store.class
        }, version = 1)

public abstract class MaskManDatabase extends RoomDatabase {
    public abstract StoreDao storeDao();
}