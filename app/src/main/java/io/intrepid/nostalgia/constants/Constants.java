package io.intrepid.nostalgia.constants;

import java.util.Calendar;

public class Constants {
    // name of token can be accessed from other classes for SharedPrefs
    public static final String SHARED_PREFS_ACCESS_TOKEN = "accessToken";
    public static final String SHARED_PREFS_LOGIN = "activity_executed";
    public static final String SHARED_PREFS_AUTOPLAY = "autoPlay";

    public static final int MIN_YEAR = 1979;
    /**
     * the default year to display, one less than the present year
     */
    public static final int NUMBER_OF_YEARS = Calendar.getInstance().get(Calendar.YEAR) - MIN_YEAR;
    public static final String COUNTRY = "US";
    public static final String SONG = "song";

    public static final String IMAGE_WIDTH = "244";
    public static final String IMAGE_HEIGHT = "244";
}
