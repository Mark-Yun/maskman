package com.mark.zumo.client.customer.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mark.zumo.client.customer.entity.OnlineStore;
import com.mark.zumo.client.customer.entity.PushAgreement;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by mark on 20. 3. 21.
 */
@Dao
public interface OnlineStoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnlineStoreList(final List<OnlineStore> onlineStoreList);

    @Query("SELECT * FROM OnlineStore")
    Flowable<List<OnlineStore>> flowableOnlineStoreList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPushAgreementList(final List<PushAgreement> pushAgreement);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPushAgreement(final PushAgreement pushAgreement);

    @Query("SELECT * FROM PushAgreement " +
            "WHERE user_id LIKE :userUuid " +
            "AND push_type LIKE :pushType")
    Flowable<PushAgreement> flowablePushAgreement(final String userUuid,
                                                  @PushAgreement.PushType final int pushType);
}
