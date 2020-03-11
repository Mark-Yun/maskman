package com.mark.zumo.client.customer.util;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mark on 20. 3. 11.
 */
public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();

    @Nullable
    public static Date createDate(final String dateString) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, "createDate: ", e);
        }
        return null;
    }

    public static String convertCreatedAt(final String createdDateString) {
        Log.d(TAG, "convertCreatedAt: createdDateString=" + createdDateString);
        final Date createdDate = createDate(createdDateString);
        if (createdDate == null) {
            return "";
        }

        Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime(createdDate);   // assigns calendar to given date

        Calendar currentTime = Calendar.getInstance(Locale.getDefault());
        Calendar gapCalendar = Calendar.getInstance();
        gapCalendar.setTimeInMillis(currentTime.getTimeInMillis() - calendar.getTimeInMillis());

        int gapHour = gapCalendar.get(Calendar.HOUR_OF_DAY);// gets hour in 24h format
        int gapMinute = gapCalendar.get(Calendar.MINUTE);// gets month number, NOTE this is zero based!
        int gapSeconds = gapCalendar.get(Calendar.SECOND);// gets month number, NOTE this is zero based!
        return String.format("%d시간 %d분 %d초 전", gapHour, gapMinute, gapSeconds);
    }
}
