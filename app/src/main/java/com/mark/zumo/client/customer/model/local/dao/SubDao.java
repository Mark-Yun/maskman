package com.mark.zumo.client.customer.model.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mark.zumo.client.customer.entity.Sub;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by mark on 20. 3. 11.
 */
@Dao
public interface SubDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Sub sub);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Sub> sub);

    @Delete
    void delete(Sub sub);

    @Delete
    void delete(List<Sub> sub);

    @Query("SELECT COUNT(*) FROM Sub WHERE user_id LIKE :user_id AND code LIKE :code")
    Flowable<Integer> flowableSub(String user_id, String code);
}
