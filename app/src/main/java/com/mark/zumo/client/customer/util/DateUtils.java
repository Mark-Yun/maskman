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

    public static String convertTimeStamp(final String createdDateString) {
        Log.d(TAG, "convertTimeStamp: createdDateString=" + createdDateString);
        final Date createdDate = createDate(createdDateString);
        if (createdDate == null) {
            return "";
        }

        Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime(createdDate);   // assigns calendar to given date

        Calendar currentTime = Calendar.getInstance(Locale.getDefault());
        Calendar gapCalendar = Calendar.getInstance();
        gapCalendar.setTimeInMillis(currentTime.getTimeInMillis() - calendar.getTimeInMillis());

        int gapDay = gapCalendar.get(Calendar.DAY_OF_YEAR);
        int gapHour = gapCalendar.get(Calendar.HOUR_OF_DAY);// gets hour in 24h format
        int gapMinute = gapCalendar.get(Calendar.MINUTE);// gets month number, NOTE this is zero based!
        int gapSeconds = gapCalendar.get(Calendar.SECOND);// gets month number, NOTE this is zero based!

        if (gapDay > 1) {
            return String.format("%d일 전", gapDay-1);
        }

        if (gapHour > 0) {
            return String.format("%d시간 %d분 전", gapHour, gapMinute);
        }

        if (gapMinute > 0) {
            return String.format("%d분 전", gapMinute);
        }

        return String.format("%d초 전", gapSeconds);
    }
}
