package com.mark.zumo.client.customer.model.local;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.jetbrains.annotations.NotNull;

/**
 * Created by mark on 20. 3. 13.
 */
class Migrations {
    static final Migration[] LIST = {
            new Migration(1, 2) {
                @Override
                public void migrate(@NotNull SupportSQLiteDatabase database) {
                    database.execSQL("CREATE TABLE 'Sub' ('user_id' TEXT NOT NULL, 'code' TEXT NOT NULL, PRIMARY KEY('user_id', 'code'))");
                }
            },
            new Migration(2, 3) {
                @Override
                public void migrate(@NotNull SupportSQLiteDatabase database) {
                    database.execSQL("ALTER TABLE 'Store' ADD COLUMN tel TEXT");
                    database.execSQL("ALTER TABLE 'Store' ADD COLUMN open INTEGER NOT NULL DEFAULT -1");
                }
            }
    };
}
