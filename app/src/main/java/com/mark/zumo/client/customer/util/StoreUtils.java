package com.mark.zumo.client.customer.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.entity.Store;

/**
 * Created by mark on 20. 3. 11.
 */
public class StoreUtils {

    public static String getOpenStatusLabel(final Context context,
                                            @Store.OpenStatus final int status) {
        switch (status) {
            case Store.OPENED:
                return context.getString(R.string.opened);
            case Store.CLOSED:
                return context.getString(R.string.closed);
            case Store.UNKNOWN:
                return context.getString(R.string.unknown);
            default:
                return context.getString(R.string.unknown);
        }
    }

    public static Drawable getOpenStatusIcon(final Context context,
                                             @Store.OpenStatus final int status) {
        switch (status) {
            case Store.OPENED:
                return context.getDrawable(R.drawable.open_ic_opened);
            case Store.CLOSED:
                return context.getDrawable(R.drawable.open_ic_closed);
            case Store.UNKNOWN:
                return context.getDrawable(R.drawable.open_ic_unknown);
            default:
                return context.getDrawable(R.drawable.open_ic_unknown);
        }
    }

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

    public static String getStockLabel(final Context context, @Store.Stock final String stock) {
        switch (stock) {
            case Store.PLENTY:
                return context.getString(R.string.stock_plenty);
            case Store.SOME:
                return context.getString(R.string.stock_some);
            case Store.FEW:
                return context.getString(R.string.stock_few);
            case Store.EMPTY:
                return context.getString(R.string.stock_empty);
        }
        return "";
    }

    @DrawableRes
    public static int getTypeDrawable(@Store.Type final String type) {
        if (TextUtils.isEmpty(type)) {
            return R.drawable.ic_local_hospital_black_24dp;
        }

        switch (type) {
            case Store.PHARMACY:
                return R.drawable.ic_local_hospital_black_24dp;
            case Store.POST:
                return R.drawable.ic_post;
            case Store.NH:
                return R.drawable.ic_nh;
            default:
                return R.drawable.ic_local_hospital_black_24dp;
        }
    }

    @ColorRes
    public static int getStatusColor(@Store.Stock final String stock) {
        if (TextUtils.isEmpty(stock)) {
            return R.color.empty;
        }

        switch (stock) {
            case Store.PLENTY:
                return R.color.plenty;
            case Store.SOME:
                return R.color.some;
            case Store.FEW:
                return R.color.few;
            case Store.EMPTY:
            default:
                return R.color.empty;
        }
    }

    @DrawableRes
    public static int getStatusBackground(@Store.Stock final String stock) {
        if (TextUtils.isEmpty(stock)) {
            return R.drawable.corner_background_empty;
        }

        switch (stock) {
            case Store.PLENTY:
                return R.drawable.corner_background_plenty;
            case Store.SOME:
                return R.drawable.corner_background_some;
            case Store.FEW:
                return R.drawable.corner_background_few;
            case Store.EMPTY:
                return R.drawable.corner_background_empty;
            default:
                return R.drawable.corner_background_empty;
        }
    }

    @StringRes
    public static int getStatusLabel(@Store.Stock final String stock) {
        if (TextUtils.isEmpty(stock)) {
            return R.string.empty;
        }

        switch (stock) {
            case Store.PLENTY:
                return R.string.plenty;
            case Store.SOME:
                return R.string.some;
            case Store.FEW:
                return R.string.few;
            case Store.EMPTY:
                return R.string.empty;
            default:
                return R.string.empty;
        }
    }

    public static int convertStock(@Store.Stock final String stock) {
        if (TextUtils.isEmpty(stock)) {
            return 0;
        }

        switch (stock) {
            case Store.PLENTY:
                return 100;
            case Store.SOME:
                return 30;
            case Store.FEW:
                return 2;
            case Store.EMPTY:
                return 0;

            default:
                return 0;
        }
    }
}
