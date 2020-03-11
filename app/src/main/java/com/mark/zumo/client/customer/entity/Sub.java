package com.mark.zumo.client.customer.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by mark on 20. 3. 8.
 */
@Entity(primaryKeys = {"user_id", "code"})
public class Sub {

    public Sub(@NonNull final String user_id, @NonNull final String code) {
        this.user_id = user_id;
        this.code = code;
    }

    @NonNull
    public String user_id;
    @NonNull
    public String code;
}
