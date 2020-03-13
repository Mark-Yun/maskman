package com.mark.zumo.client.customer.model.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.entity.Sub;
import com.mark.zumo.client.customer.model.local.dao.StoreDao;
import com.mark.zumo.client.customer.model.local.dao.SubDao;

/**
 * Created by mark on 20. 3. 8.
 */
@Database(
        entities = {
                Store.class, Sub.class
        }, version = 3)

public abstract class MaskManDatabase extends RoomDatabase {
    public abstract StoreDao storeDao();
    public abstract SubDao subDao();
}