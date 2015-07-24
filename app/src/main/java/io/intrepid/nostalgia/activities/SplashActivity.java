package io.intrepid.nostalgia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

import io.intrepid.nostalgia.R;

public class SplashActivity extends Activity {
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash_screen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), LauncherActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
