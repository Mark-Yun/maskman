package com.mark.zumo.client.customer.model.local;

import android.content.Context;

import androidx.room.Room;

import com.mark.zumo.client.customer.ContextHolder;

/**
 * Created by mark on 20. 3. 8.
 */
public enum DatabaseProvider {
    INSTANCE;

    public final MaskManDatabase maskManDatabase;

    DatabaseProvider() {
        final Context context = ContextHolder.getContext();
        final String databaseName = context.getPackageName();

        maskManDatabase = Room.databaseBuilder(context, MaskManDatabase.class, databaseName)
                .build();

//        maskManDatabase = Room.inMemoryDatabaseBuilder(context, MaskManDatabase.class)
//                .build();
    }
}
