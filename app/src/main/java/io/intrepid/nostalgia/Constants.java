package io.intrepid.nostalgia;

import java.util.Calendar;

public class Constants {
    // name of token can be accessed from other classes for SharedPrefs
    public static final String SHARED_PREFS_ACCESS_TOKEN = "accessToken";
    public static final String SHARED_PREFS_LOGIN = "activity_executed";

    public static final int MIN_YEAR = 1979;
    /**
     * the default year to display, one less than the present year
     */
    public static final int NUMBER_OF_YEARS = Calendar.getInstance().get(Calendar.YEAR) - MIN_YEAR;

}
