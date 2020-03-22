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

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by mark on 20. 3. 10.
 */
public class MapUtils {

    public static final String PLENTY = "plenty";
    public static final String SOME = "some";
    public static final String FEW = "few";
    public static final String EMPTY = "empty";

    private static final Map<String, Map<String, Bitmap>> bitmapCache = new HashMap<>();

    public static BitmapDescriptor createCustomMarker(final Activity activity, final String status,
                                                      final String type) {
        final Bitmap bitmap = bitmapCache.computeIfAbsent(status, key -> new HashMap<>())
                .computeIfAbsent(type, key -> createBitmap(activity, status, type));
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @NotNull
    private static Bitmap createBitmap(final Activity activity, final String status, final String type) {
        final LayoutInflater layoutInflater = activity.getSystemService(LayoutInflater.class);
        final View view = layoutInflater.inflate(R.layout.marker_store, null);

        final TextView text = view.findViewById(R.id.text);
        text.setText(StoreUtils.getStatusLabel(status));

        final AppCompatImageView tail = view.findViewById(R.id.tail);
        tail.setImageTintList(ColorStateList.valueOf(activity.getColor(StoreUtils.getStatusColor(status))));

        final ConstraintLayout container = view.findViewById(R.id.text_container);
        container.setBackground(activity.getDrawable(StoreUtils.getStatusBackground(status)));

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final AppCompatImageView icon = view.findViewById(R.id.icon);
        icon.setImageResource(StoreUtils.getTypeDrawable(type));

        view.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
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
