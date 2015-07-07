package io.intrepid.nostalgia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnTouch;

public class SettingsActivity extends AppCompatActivity{

    public static final String EMAIL = "hayley@intrepid.io";
    public static final String SUBJECT_LINE = "Nostalgia App Feedback";


    @InjectView(R.id.facebook_switch)
    Switch facebookSwitch;

    //TODO: add functionality to switches
    @OnCheckedChanged(R.id.facebook_switch) void onFacebookSwitchChanged(boolean isChecked) {
        if (isChecked) {
            //login to facebook
        } else {
            //logout of facebook
        }
    }

    @OnCheckedChanged(R.id.autoplay_switch) void onAutoplaySwitchChanged(boolean isChecked) {
        if (isChecked) {
            //autoplay on
        } else {
            //autoplay off
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
        facebookSwitch.setChecked(LoginActivity.isFacebook);
    }
}
