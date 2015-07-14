package io.intrepid.nostalgia.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.intrepid.nostalgia.constants.Constants;
import io.intrepid.nostalgia.R;

public class LauncherActivity extends AppCompatActivity {

    @InjectView(R.id.skip_facebook)
    TextView skipFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pref.getBoolean(Constants.SHARED_PREFS_LOGIN, false)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            skipFacebook.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
