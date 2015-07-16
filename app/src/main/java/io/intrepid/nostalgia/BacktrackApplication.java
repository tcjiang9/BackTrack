package io.intrepid.nostalgia;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BacktrackApplication extends Application{
    public static final String DEFAULT = "fonts/ProximaNova-Regular.otf";

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(DEFAULT)
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}
