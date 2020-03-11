package com.mark.zumo.client.customer.util;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.mark.zumo.client.customer.R;

/**
 * Created by mark on 20. 3. 10.
 */
public class MapUtils {

    public static BitmapDescriptor createCustomMarker(final Activity activity, final String status,
                                                      final String type) {

        final LayoutInflater layoutInflater = activity.getSystemService(LayoutInflater.class);
        final View marker = layoutInflater.inflate(R.layout.marker_store, null);

        final TextView text = marker.findViewById(R.id.text);
        text.setText(getStatusLabel(status));

        final AppCompatImageView tail = marker.findViewById(R.id.tail);
        tail.setImageTintList(ColorStateList.valueOf(activity.getColor(getStatusColor(status))));

        final ConstraintLayout container = marker.findViewById(R.id.text_container);
        container.setBackground(activity.getDrawable(getStatusBackground(status)));

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final AppCompatImageView icon = marker.findViewById(R.id.icon);
        icon.setImageResource(getTypeDrawable(type));

        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @DrawableRes
    private static int getTypeDrawable(final String type) {
        switch (type) {
            case "01":
                return R.drawable.ic_local_hospital_black_24dp;
            case "02":
                return R.drawable.ic_post;
            case "03":
                return R.drawable.ic_nh;
        }

        return 0;
    }

    @ColorRes
    private static int getStatusColor(final String status) {
        switch (status) {
            case "plenty":
                return R.color.plenty;
            case "some":
                return R.color.some;
            case "few":
                return R.color.few;
            case "empty":
                return R.color.empty;
        }

        return 0;
    }

    @DrawableRes
    private static int getStatusBackground(final String status) {
        switch (status) {
            case "plenty":
                return R.drawable.corner_background_plenty;
            case "some":
                return R.drawable.corner_background_some;
            case "few":
                return R.drawable.corner_background_few;
            case "empty":
                return R.drawable.corner_background_empty;
        }

        return 0;
    }

    @StringRes
    private static int getStatusLabel(final String status) {
        switch (status) {
            case "plenty":
                return R.string.plenty;
            case "some":
                return R.string.some;
            case "few":
                return R.string.few;
            case "empty":
                return R.string.empty;
        }

        return 0;
    }
}
