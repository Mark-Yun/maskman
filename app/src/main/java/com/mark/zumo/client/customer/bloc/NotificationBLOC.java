package com.mark.zumo.client.customer.bloc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.mark.zumo.client.customer.ContextHolder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.entity.OnlineStore;
import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.model.StoreManager;
import com.mark.zumo.client.customer.view.SplashActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 20. 3. 12.
 */
public class NotificationBLOC {

    private static final String TAG = NotificationBLOC.class.getSimpleName();

    private static final String NEW_STORE_CHANNEL_ID = "com.mark.zumo.client.customer.notification.channel.NEW_STORE";
    private static final String NEW_ONLINE_STORE_CHANNEL_ID = "com.mark.zumo.client.customer.notification.channel.NEW_ONLINE_STORE";

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

    public void notifyNewOnlineStore(final Map<String, String> data) {
        if (data == null) {
            return;
        }
        Log.d(TAG, "notifyNewOnlineStore: data=" + data);

        final OnlineStore onlineStore = new OnlineStore();
        onlineStore.store_url = data.getOrDefault("store_url", "");
        onlineStore.start_time = data.getOrDefault("start_time", "");
        onlineStore.img_url = data.getOrDefault("img_url", "");
        onlineStore.store_name = data.getOrDefault("store_name", "");
        onlineStore.price = data.getOrDefault("price", "");
        onlineStore.title = data.getOrDefault("title", "");
        onlineStore.status = Integer.parseInt(data.getOrDefault("status", "0"));

        notifyNewOnlineStore(onlineStore);
    }

    public void notifyNewOnlineStore(final OnlineStore onlineStore) {
        Log.d(TAG, "notifyNewOnlineStore: onlineStore=" + onlineStore);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(createNewOnlineStoreNotificationChannel());
        }

        Maybe.just(onlineStore)
                .map(this::createNewOnlineStoreNotification)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(notification -> notificationManager.notify(onlineStore.store_url.hashCode(), notification))
                .subscribe();
    }

    public void notifyPreparingOnlineStore(final OnlineStore onlineStore) {
        Log.d(TAG, "notifyNewOnlineStore: onlineStore=" + onlineStore);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(createNewOnlineStoreNotificationChannel());
        }

        Maybe.just(onlineStore)
                .map(this::createPreparingStoreNotification)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(notification -> notificationManager.notify(onlineStore.store_url.hashCode(), notification))
                .subscribe();
    }

    private void notifyNewStore(final Store store) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(createNewStoreNotificationChannel());
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
    private NotificationChannel createNewStoreNotificationChannel() {
        return new NotificationChannel(NEW_STORE_CHANNEL_ID, "마스크 입고 알림", NotificationManager.IMPORTANCE_HIGH);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NotNull
    private NotificationChannel createNewOnlineStoreNotificationChannel() {
        return new NotificationChannel(NEW_ONLINE_STORE_CHANNEL_ID, "새로운 온라인 마스크 판매 알림", NotificationManager.IMPORTANCE_HIGH);
    }

    private PendingIntent createNewOnlinePendingIntent(final int status, final String storeUrl) {
        final Intent intent = new Intent();
        if (status == OnlineStore.ON_SALE) {
            intent.setData(Uri.parse(storeUrl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
        } else {
            intent.setClass(ContextHolder.getContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(SplashActivity.ACTION_VIEW_ONLINE_STORE);
            intent.putExtra(SplashActivity.KEY_ONLINE_STORE_URL, storeUrl);
        }

        return PendingIntent.getActivity(context, storeUrl.hashCode(),
                intent, PendingIntent.FLAG_ONE_SHOT);
    }

    private Notification createPreparingStoreNotification(final OnlineStore onlineStore) {
        final Bitmap bitmap = prepareBitmap(ContextHolder.getContext(), onlineStore.img_url);
        String subText = onlineStore.status == OnlineStore.ON_SALE
                ? ContextHolder.getContext().getString(R.string.on_sale)
                : ContextHolder.getContext().getString(R.string.preparing_sale);
        subText += " " + onlineStore.price;

        PendingIntent pendingIntent = createNewOnlinePendingIntent(onlineStore.status,/*OnlineStore.ON_SALE*/ onlineStore.store_url);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NEW_ONLINE_STORE_CHANNEL_ID)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.mipmap.temp_ic)
                .setWhen(Calendar.getInstance(Locale.getDefault()).getTimeInMillis())
                .setShowWhen(true)
                .setContentTitle("마스크 판매 시작 5분전")
                .setContentText(onlineStore.title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(onlineStore.title))
                .setSubText(subText)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        return builder.build();
    }
    private Notification createNewOnlineStoreNotification(final OnlineStore onlineStore) {
        final Bitmap bitmap = prepareBitmap(ContextHolder.getContext(), onlineStore.img_url);
        String subText = onlineStore.status == OnlineStore.ON_SALE
                ? ContextHolder.getContext().getString(R.string.on_sale)
                : ContextHolder.getContext().getString(R.string.preparing_sale);
        subText += " " + onlineStore.price;

        PendingIntent pendingIntent = createNewOnlinePendingIntent(onlineStore.status, onlineStore.store_url);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NEW_ONLINE_STORE_CHANNEL_ID)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.mipmap.temp_ic)
                .setWhen(Calendar.getInstance(Locale.getDefault()).getTimeInMillis())
                .setShowWhen(true)
                .setContentTitle("새로운 온라인 마스크 판매점 알림")
                .setContentText(onlineStore.title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(onlineStore.title))
                .setSubText(subText)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        return builder.build();
    }

    private Notification createNewStockNotification(final String name, final String code) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.putExtra(SplashActivity.KEY_STORE_CODE, code);
        intent.setAction(SplashActivity.ACTION_VIEW_STORE);
        int requestCode = 0;
        try {
            requestCode = Integer.parseInt(code);
        } catch (NumberFormatException e) {
            requestCode = new Random().nextInt();
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode,
                intent, PendingIntent.FLAG_ONE_SHOT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NEW_STORE_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.temp_ic))
                .setSmallIcon(R.mipmap.temp_ic)
                .setWhen(Calendar.getInstance(Locale.getDefault()).getTimeInMillis())
                .setShowWhen(true)
                .setContentTitle("입고 알림")
                .setContentText(name + " 마스크 입고 완료")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        return builder.build();
    }

    private Bitmap prepareBitmap(final Context context, final String imageUrl) {
        try {
            return Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get();
        } catch (Exception e) {
            Log.e(TAG, "prepareBitmap: ", e);
            return null;
        }
    }
}
