package io.intrepid.nostalgia;

import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.intrepid.nostalgia.constants.FacebookConstants;

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

    public static String makeRibbonDateText() {
        return new SimpleDateFormat("MMM d").format(new Date());
    }

    public static int getDay() {
        String date = new SimpleDateFormat("d").format(new Date());
        return Integer.parseInt(date);
    }

    public static Bundle makeFacebookDate(int year) {
        Bundle parameters = new Bundle();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        //cal.set(Calendar.DATE, cal.get(Calendar.DATE)-1);
        cal.set(Calendar.DATE, 7);
        cal.set(Calendar.HOUR_OF_DAY, 20);
        cal.set(Calendar.MINUTE, 0);
        long initialTime = cal.getTimeInMillis() / MILLISECOND_PER_SECOND;
        parameters.putString(FacebookConstants.SINCE, "" + initialTime);
        Log.e("time", ""+initialTime);
        //cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
        cal.set(Calendar.DATE, 9);
        cal.set(Calendar.HOUR_OF_DAY, 20);
        long limitTime = cal.getTimeInMillis() / MILLISECOND_PER_SECOND;
        parameters.putString(FacebookConstants.UNTIL, "" + limitTime);
        parameters.putString(FacebookConstants.LIMIT, "3");
        return parameters;
    }
}
