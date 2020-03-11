package com.mark.zumo.client.customer.model.local;

import android.content.Context;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mark.zumo.client.customer.ContextHolder;

/**
 * Created by mark on 20. 3. 8.
 */
public enum DatabaseProvider {
    INSTANCE;

    public final MaskManDatabase maskManDatabase;

    private final Migration migration1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE 'Sub' ('user_id' TEXT NOT NULL, 'code' TEXT NOT NULL, PRIMARY KEY('user_id', 'code'))");
        }
    };

    DatabaseProvider() {
        final Context context = ContextHolder.getContext();
        final String databaseName = context.getPackageName();

        maskManDatabase = Room.databaseBuilder(context, MaskManDatabase.class, databaseName)
                .addMigrations(migration1_2)
                .build();
    }
}
