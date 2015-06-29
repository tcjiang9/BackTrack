package io.intrepid.nostalgia;

import java.util.Calendar;

public class Constants {
    // name of token can be accessed from other classes for SharedPrefs
    public static String SHARED_PREFS_ACCESS_TOKEN = "accessToken";
    public static String SHARED_PREFS_LOGIN = "activity_executed";

    /**
     * the default year to display, one less than the present year
     */
    public static final int DEFAULT_YEAR = Calendar.getInstance().get(Calendar.YEAR) - 1852;

    public static int currentYear;
}
