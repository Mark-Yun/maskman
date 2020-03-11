package com.mark.zumo.client.customer.util;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

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

    @DrawableRes
    public static int getTypeDrawable(final String type) {
        if (TextUtils.isEmpty(type)) {
            return R.drawable.ic_local_hospital_black_24dp;
        }

        switch (type) {
            case "01":
                return R.drawable.ic_local_hospital_black_24dp;
            case "02":
                return R.drawable.ic_post;
            case "03":
                return R.drawable.ic_nh;
            default:
                return R.drawable.ic_local_hospital_black_24dp;
        }
    }

    @ColorRes
    public static int getStatusColor(final String status) {
        if (TextUtils.isEmpty(status)) {
            return R.color.empty;
        }

        switch (status) {
            case MapUtils.PLENTY:
                return R.color.plenty;
            case MapUtils.SOME:
                return R.color.some;
            case MapUtils.FEW:
                return R.color.few;
            case MapUtils.EMPTY:
            default:
                return R.color.empty;
        }
    }

    @DrawableRes
    public static int getStatusBackground(final String status) {
        if (TextUtils.isEmpty(status)) {
            return R.drawable.corner_background_empty;
        }

        switch (status) {
            case MapUtils.PLENTY:
                return R.drawable.corner_background_plenty;
            case MapUtils.SOME:
                return R.drawable.corner_background_some;
            case MapUtils.FEW:
                return R.drawable.corner_background_few;
            case MapUtils.EMPTY:
                return R.drawable.corner_background_empty;
            default:
                return R.drawable.corner_background_empty;
        }
    }

    @StringRes
    public static int getStatusLabel(final String status) {
        if (TextUtils.isEmpty(status)) {
            return R.string.empty;
        }

        switch (status) {
            case MapUtils.PLENTY:
                return R.string.plenty;
            case MapUtils.SOME:
                return R.string.some;
            case MapUtils.FEW:
                return R.string.few;
            case MapUtils.EMPTY:
                return R.string.empty;
            default:
                return R.string.empty;
        }
    }

    public static int convertStock(final String status) {
        if (TextUtils.isEmpty(status)) {
            return 0;
        }

        switch (status) {
            case MapUtils.PLENTY:
                return 100;
            case MapUtils.SOME:
                return 30;
            case MapUtils.FEW:
                return 2;
            case MapUtils.EMPTY:
                return 0;

            default:
                return 0;
        }
    }
}
