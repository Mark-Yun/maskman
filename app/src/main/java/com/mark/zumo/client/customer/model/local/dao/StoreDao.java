package com.mark.zumo.client.customer.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mark.zumo.client.customer.entity.Store;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by mark on 20. 3. 8.
 */
@Dao
public interface StoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStoreList(final List<Store> storeList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStore(final Store store);

    @Query("SELECT * FROM Store WHERE lat >= :latitude2 " +
            "AND lng >= :longitude2 " +
            "AND lat <= :latitude1 " +
            "AND lng <= :longitude1")
    Flowable<List<Store>> flowableStoreListByGeo(final double latitude1, final double longitude1,
                                                 final double latitude2, final double longitude2);

    @Query("SELECT * FROM Store WHERE code LIKE :code")
    Flowable<Store> flowableStore(final String code);
}
