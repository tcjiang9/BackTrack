package io.intrepid.nostalgia.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Switch;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnTouch;
import io.intrepid.nostalgia.R;
import io.intrepid.nostalgia.constants.Constants;

public class SettingsActivity extends AppCompatActivity{

    public static final String EMAIL = "hayley@intrepid.io";
    public static final String SUBJECT_LINE = "Nostalgia App Feedback";

    private boolean isOpening;

    @InjectView(R.id.facebook_switch)
    Switch facebookSwitch;

    @InjectView(R.id.autoplay_switch)
    Switch autoplaySwitch;

    //TODO: add functionality to switches
    @OnCheckedChanged(R.id.facebook_switch) void onFacebookSwitchChanged(boolean isChecked) {
        if (!isOpening) {
            if (isChecked) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("settings", true);
                startActivity(intent);
            } else {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putString(Constants.SHARED_PREFS_ACCESS_TOKEN, null);
                editor.apply();
            }
        }
    }

    @OnCheckedChanged(R.id.autoplay_switch) void onAutoplaySwitchChanged(boolean isChecked) {
        if (!isOpening) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean(Constants.SHARED_PREFS_AUTOPLAY, isChecked);
            editor.apply();
        }
    }

    @OnTouch(R.id.feedback_setting) boolean onFeedbackSettingTouched() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL});
        intent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT_LINE);
        startActivity(Intent.createChooser(intent, ""));
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setSwitches();
    }

    public void setSwitches() {
        isOpening = true;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPreferences.getString(Constants.SHARED_PREFS_ACCESS_TOKEN, null);
        facebookSwitch.setChecked(token != null);
        autoplaySwitch.setChecked(sharedPreferences.getBoolean(Constants.SHARED_PREFS_AUTOPLAY, true));
        isOpening = false;
    }
}
