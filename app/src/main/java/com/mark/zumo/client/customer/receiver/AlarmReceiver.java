package com.mark.zumo.client.customer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mark.zumo.client.customer.ContextHolder;
import com.mark.zumo.client.customer.bloc.NotificationBLOC;
import com.mark.zumo.client.customer.entity.OnlineStore;

/**
 * Created by mark on 20. 3. 22.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final String ACTION_ONLINE_STORE_ALARM = "com.mark.zumo.client.customer.action.ONLINE_STORE_ALARM";

    private static final String TAG = "AlarmReceiver";

    private final NotificationBLOC notificationBLOC;

    public AlarmReceiver() {
        notificationBLOC = new NotificationBLOC(ContextHolder.getContext());
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final String action = intent.getAction();
        Log.d(TAG, "onReceive: action=" + action);
        if (action == null) {
            return;
        }

        switch (action) {
            case ACTION_ONLINE_STORE_ALARM:
                final Bundle extras = intent.getExtras();
                if (extras != null) {
                    notificationBLOC.notifyPreparingOnlineStore(OnlineStore.fromBundle(extras));
                }
                break;
        }
    }
}
