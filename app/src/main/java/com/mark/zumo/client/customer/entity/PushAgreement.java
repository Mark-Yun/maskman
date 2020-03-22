package com.mark.zumo.client.customer.entity;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;

import java.util.Optional;

/**
 * Created by mark on 20. 3. 22.
 */
@Entity(primaryKeys = {"user_id", "push_type"})
public class PushAgreement {

    public static final int NEW_ONLINE_STORE = 0;

    public static final int AGREED = 1;
    public static final int REJECTED = 0;

    @NonNull public String user_id;
    @NonNull @PushType public int push_type;
    @AgreeType public int value;

    @NonNull
    @Override
    public String toString() {
        return "[PushAgreement]" +
                " user_id=" + user_id +
                " push_type=" + push_type +
                " value=" + value;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        return Optional.ofNullable(obj)
                .filter(this.getClass()::isInstance)
                .map(this.getClass()::cast)
                .map(PushAgreement::toString)
                .orElse("")
                .equals(this.toString());
    }

    @IntDef({NEW_ONLINE_STORE})
    public @interface PushType {
    }

    @IntDef({AGREED, REJECTED})
    public @interface AgreeType {
    }
}
