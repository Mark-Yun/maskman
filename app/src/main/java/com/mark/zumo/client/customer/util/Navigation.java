package com.mark.zumo.client.customer.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.mark.zumo.client.customer.ContextHolder;

/**
 * Created by mark on 20. 3. 14.
 */
public enum Navigation {
    NAVER_MAP("com.nhn.android.nmap", "navermaps://?menu=location&pinType=place&lat=%s&lng=%s&title=%s"),
    GOOGLE_MAP("com.google.android.apps.maps", "geo:%s,%s?q=%s"),
    SKT_T_MAP("com.skt.skaf.l001mtm091", "tmap://route?goaly=%s&goalx=%s&goalname=%s"),
    T_MAP("com.skt.tmap.ku", "tmap://route?goaly=%s&goalx=%s&goalname=%s"),
//    KAKAO_MAP("com.skt.tmap.ku", ""),
    ;

    private static final String TAG = "Navigation";

    private final String packageName;
    private final String uriFormatString;

    Navigation(final String packageName, final String uriFormatString) {
        this.packageName = packageName;
        this.uriFormatString = uriFormatString;
    }

    public void startNavigation(double lat, double lng, String title) {
        Uri uri = Uri.parse(String.format(uriFormatString, lng, lat, title));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            ContextHolder.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "startNavigation: ", e);
            Toast.makeText(ContextHolder.getContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isInstalled(final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "isInstalled: ", e);
            return false;
        }
    }

    public String getApplicationName(final Context context) {
        try {
            return context.getPackageManager().getApplicationLabel(
                    context.getPackageManager().getPackageInfo(packageName, 0).applicationInfo
            ).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "isInstalled: ", e);
            return "";
        }
    }

    public Drawable getPackageIcon(final Context context) {
        try {
            return context.getPackageManager()
                    .getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getPackageIcon: ", e);
            return null;
        }
    }
}