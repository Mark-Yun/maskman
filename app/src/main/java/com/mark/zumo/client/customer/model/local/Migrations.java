package com.mark.zumo.client.customer.model.local;

import androidx.annotation.NonNull;
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
                    database.execSQL("CREATE TABLE 'Sub' (" +
                            "'user_id' TEXT NOT NULL, " +
                            "'code' TEXT NOT NULL, " +
                            "PRIMARY KEY('user_id', 'code'))");
                }
            },
            new Migration(2, 3) {
                @Override
                public void migrate(@NotNull SupportSQLiteDatabase database) {
                    database.execSQL("ALTER TABLE 'Store' ADD COLUMN tel TEXT");
                    database.execSQL("ALTER TABLE 'Store' ADD COLUMN open INTEGER NOT NULL DEFAULT -1");
                }
            },
            new Migration(3, 4) {
                @Override
                public void migrate(@NonNull final SupportSQLiteDatabase database) {
                    database.execSQL("CREATE TABLE 'OnlineStore' (" +
                            "'store_url' TEXT NOT NULL, " +
                            "'store_name' TEXT, " +
                            "'img_url' TEXT, " +
                            "'title' TEXT, " +
                            "'price' TEXT, " +
                            "'start_time' TEXT, " +
                            "'status' INTEGER NOT NULL, " +
                            "PRIMARY KEY('store_url'))");

                    database.execSQL("CREATE TABLE 'StoreHistory' (" +
                            "'code' TEXT NOT NULL, " +
                            "'date' TEXT NOT NULL, " +
                            "'stock_at' TEXT, " +
                            "'empty_at' TEXT, " +
                            "PRIMARY KEY('code', 'date'))");

                    database.execSQL("CREATE TABLE 'PushAgreement' (" +
                            "'user_id' TEXT NOT NULL, " +
                            "'push_type' INTEGER NOT NULL, " +
                            "'value' INTEGER NOT NULL, " +
                            "PRIMARY KEY('user_id', 'push_type'))");
                }
            }
    };
}
