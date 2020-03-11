package com.mark.zumo.client.customer.view.permission;

import android.Manifest;

import com.mark.zumo.client.customer.R;


/**
 * Created by mark on 19. 7. 14.
 */
public final class Permissions {

    public static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    static final int[] PERMISSION_NAMES = {
            R.string.permission_name_location,
    };

    static final int[] PERMISSION_DESCRIPTIONS = {
            R.string.permission_description_location,
    };
    static final int[] PERMISSION_ICONS = {
            R.drawable.ic_gps_fixed_black_48dp,
    };

    private Permissions() {
        //do nothing
    }
}
