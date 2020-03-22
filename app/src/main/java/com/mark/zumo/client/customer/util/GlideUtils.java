package com.mark.zumo.client.customer.util;

import android.util.TypedValue;

import com.mark.zumo.client.customer.ContextHolder;

/**
 * Created by mark on 19. 8. 18.
 */
public final class GlideUtils {

    private GlideUtils() {
    }

    public static int dpToPx(final int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                ContextHolder.getContext().getResources().getDisplayMetrics());
    }
}
