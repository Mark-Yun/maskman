package com.mark.zumo.client.customer.service;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mark.zumo.client.customer.bloc.NotificationBLOC;
import com.mark.zumo.client.customer.entity.Token;
import com.mark.zumo.client.customer.model.StoreManager;

import java.util.Map;

import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 19. 6. 30.
 */
public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    private StoreManager storeManager;
    private NotificationBLOC notificationBLOC;

    @Override
    public void onCreate() {
        super.onCreate();

        storeManager = StoreManager.INSTANCE;
        notificationBLOC = new NotificationBLOC(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            Log.d(TAG, "Message data payload: " + data);
            handleData(data);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void handleData(final Map<String, String> data) {
        final String type = data.get("notification_type");
        Log.d(TAG, "handleData: notification_type=" + type);

        if (TextUtils.isEmpty(type)) {
            return;
        }

        switch (type) {
            case "new_stock":
                final String code = data.get("code");
                if (TextUtils.isEmpty(code)) {
                    return;
                }
                notificationBLOC.notifyNewStock(code);
        }
    }

    @Override
    public void onNewToken(@NonNull final String s) {
        Log.d(TAG, "onNewToken: s=" + s);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            return;
        }

        String uid = currentUser.getUid();
        storeManager.registerPushToken(uid, s)
                .observeOn(Schedulers.io())
                .doOnSuccess(this::onRegisterSucceed)
                .subscribe();
    }

    private void onRegisterSucceed(final Token token) {
        Log.d(TAG, "onRegisterSucceed: userId=" + token.user_id + " token=" + token.token_value);
    }
}
