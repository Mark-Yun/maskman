package com.mark.zumo.client.customer.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.mark.zumo.client.customer.R;

import java.util.Locale;

/**
 * Created by mark on 20. 3. 10.
 */
public class MapUtils {

    public static final String PLENTY = "plenty";
    public static final String SOME = "some";
    public static final String FEW = "few";
    public static final String EMPTY = "empty";

    public static BitmapDescriptor createCustomMarker(final Activity activity, final String status,
                                                      final String type) {

        final LayoutInflater layoutInflater = activity.getSystemService(LayoutInflater.class);
        final View marker = layoutInflater.inflate(R.layout.marker_store, null);

        final TextView text = marker.findViewById(R.id.text);
        text.setText(StoreUtils.getStatusLabel(status));

        final AppCompatImageView tail = marker.findViewById(R.id.tail);
        tail.setImageTintList(ColorStateList.valueOf(activity.getColor(StoreUtils.getStatusColor(status))));

        final ConstraintLayout container = marker.findViewById(R.id.text_container);
        container.setBackground(activity.getDrawable(StoreUtils.getStatusBackground(status)));

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final AppCompatImageView icon = marker.findViewById(R.id.icon);
        icon.setImageResource(StoreUtils.getTypeDrawable(type));

        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static Location locationFrom(double latitude, double longitude) {
        Location location = new Location("Place Point");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    public static String convertDistance(final Context context, float distance) {
        if (distance < 100) {
            return context.getString(R.string.distance_description_under_100m);
        } else {
            String distKm = String.format(Locale.getDefault(), "%.1f", distance / 1000);
            return context.getString(R.string.distance_format_kilo_meter, distKm);
        }
    }

}
