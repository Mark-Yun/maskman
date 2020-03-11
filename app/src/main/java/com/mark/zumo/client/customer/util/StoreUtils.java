package com.mark.zumo.client.customer.util;

import android.content.Context;

import com.mark.zumo.client.customer.R;

/**
 * Created by mark on 20. 3. 11.
 */
public class StoreUtils {
    public static String getTypeLabel(final Context context, final String type) {
        switch (type) {
            case "01":
                return context.getString(R.string.type_drugstore);
            case "02":
                return context.getString(R.string.type_post);
            case "03":
                return context.getString(R.string.type_nh);
        }
        return "";
    }

    public static String getStockLabel(final Context context, final String stock) {
        switch (stock) {
            case "plenty":
                return context.getString(R.string.stock_plenty);
            case "some":
                return context.getString(R.string.stock_some);
            case "few":
                return context.getString(R.string.stock_few);
            case "empty":
                return context.getString(R.string.stock_empty);
        }
        return "";
    }
}
