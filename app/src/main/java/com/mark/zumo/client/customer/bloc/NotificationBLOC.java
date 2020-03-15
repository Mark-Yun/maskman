package com.mark.zumo.client.customer.bloc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.model.StoreManager;
import com.mark.zumo.client.customer.view.SplashActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Created by mark on 20. 3. 12.
 */
public class NotificationBLOC {

    private static final String TAG = NotificationBLOC.class.getSimpleName();
    private static final String CHANNEL_ID = "com.mark.zumo.client.customer.CHANNEL_SUBSCRIPTION";
    private final Context context;

    private final StoreManager storeManager;

    public NotificationBLOC(final Context context) {
        this.context = context;
        storeManager = StoreManager.INSTANCE;
    }

    public void notifyNewStock(final String code) {
        storeManager.queryStore(code)
                .doOnSuccess(this::notifyNewStore)
                .doOnError(throwable -> Log.e(TAG, "notifyNewStock: ", throwable))
                .subscribe();
    }

    private void notifyNewStore(final Store store) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(createNotificationChannel());
        }

        Notification newStockNotification = createNewStockNotification(store.name, store.code);


        int notificationId = 0;
        try {
            notificationId = Integer.parseInt(store.code);
        } catch (NumberFormatException e) {
            Log.e(TAG, "notifyNewStore: ", e);
        }

        notificationManager.notify(notificationId, newStockNotification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NotNull
    private NotificationChannel createNotificationChannel() {
        return new NotificationChannel(CHANNEL_ID, "마스크 입고 알림", NotificationManager.IMPORTANCE_HIGH);
    }

    private Notification createNewStockNotification(final String name, final String code) {
        Icon icon = Icon.createWithResource(context, R.mipmap.temp_ic);
        Intent intent = new Intent(context, SplashActivity.class);
        intent.putExtra(SplashActivity.KEY_CODE, code);
        int requestCode = 0;
        try {
            requestCode = Integer.parseInt(code);
        } catch (NumberFormatException e) {
            requestCode = new Random().nextInt();
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode,
                intent, PendingIntent.FLAG_ONE_SHOT);

        final Notification.Builder builder = new Notification.Builder(context)
                .setLargeIcon(icon)
                .setSmallIcon(icon)
                .setContentTitle("입고 알림")
                .setContentText(name + " 마스크 입고 완료")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        return builder.build();
    }
}
