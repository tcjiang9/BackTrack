package io.intrepid.nostalgia;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BacktrackApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/ProximaNova-Regular.otf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}
