package io.intrepid.nostalgia;

import android.os.Bundle;
import android.provider.UserDictionary;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.intrepid.nostalgia.constants.FacebookConstants;

public class DateFormatter {

    public static final int MILLISECOND_PER_SECOND = 1000;


    public static String makeNytDate(String year) {
        String day = new SimpleDateFormat("MMdd").format(new Date());
        String date = year + day;
        return date;
    }

    public static String makeDateText(String year) {
        String date = new SimpleDateFormat("MMM d").format(new Date());
        String day = (new SimpleDateFormat("EE", Locale.ENGLISH).format(new Date())).toUpperCase();
        return date + " " + day;
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
        if (year == 2014) {
            cal.set(Calendar.YEAR, 2015);
            cal.set(Calendar.DATE, 24);
            cal.set(Calendar.HOUR_OF_DAY, 9);
            cal.set(Calendar.MINUTE, 40);
            setUpParameters(parameters, cal, year);
        } else
        if (year == 2013) {
            cal.set(Calendar.YEAR, 2015);
            cal.set(Calendar.DATE, 24);
            cal.set(Calendar.HOUR_OF_DAY, 10);
            cal.set(Calendar.MINUTE, 50);
            setUpParameters(parameters, cal, year);
        } else
        if (year == 2012) {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            setUpParameters(parameters, cal, year);
        } else
            if(year ==2011){
                cal.set(Calendar.YEAR, 2015);
                cal.set(Calendar.DATE, 24);
                cal.set(Calendar.HOUR_OF_DAY, 12);
                cal.set(Calendar.MINUTE, 50);
                setUpParameters(parameters, cal, year);
            }
        else {
            setUpParameters(parameters, cal, year);
        }
        return parameters;

    }

    private static void setUpParameters(Bundle parameters, Calendar cal, int year) {
        long initialTime = cal.getTimeInMillis() / MILLISECOND_PER_SECOND;
        parameters.putString(FacebookConstants.SINCE, "" + initialTime);
        if (year == 2014) {
            cal.set(Calendar.HOUR_OF_DAY, 10);
        } else
        if (year == 2013) {
            cal.set(Calendar.HOUR_OF_DAY, 12);
        } else
        if (year == 2012) {
            cal.set(Calendar.HOUR_OF_DAY, 23);
        } else{
            cal.set(Calendar.HOUR_OF_DAY, 23);
        }
        long limitTime = cal.getTimeInMillis() / MILLISECOND_PER_SECOND;
        parameters.putString(FacebookConstants.UNTIL, "" + limitTime);
        parameters.putString(FacebookConstants.LIMIT, "3");
    }
}
