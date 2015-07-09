package io.intrepid.nostalgia;

import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.intrepid.nostalgia.facebook.FacebookConstants;

public class DateFormatter {

    public static final int MILLISECOND_PER_SECOND = 1000;


    public static String makeNytDate(String year) {
        String day = new SimpleDateFormat("MMdd").format(new Date());
        String date = year + day;
        return date;
    }

    public static String makeDateText(String year) {
        String date = new SimpleDateFormat("MMMM d").format(new Date());
        return date + ", " + year;
    }

    public static Bundle makeFacebookDate(int year) {
        Bundle parameters = new Bundle();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        long initialTime = cal.getTimeInMillis() / MILLISECOND_PER_SECOND;
        parameters.putString(FacebookConstants.SINCE, "" + initialTime);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        long limitTime = cal.getTimeInMillis() / MILLISECOND_PER_SECOND;
        parameters.putString(FacebookConstants.UNTIL, "" + limitTime);
        parameters.putString(FacebookConstants.LIMIT, "3");
        return parameters;
    }
}
