package io.intrepid.nostalgia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingsActivity extends AppCompatActivity{

    public String email = "hayley@intrepid.io";
    public String subjectLine = "hayley@intrepid.io";


    @InjectView(R.id.facebook_switch)
    Switch facebookSwitch;

    @InjectView(R.id.autoplay_switch)
    Switch autoplaySwitch;

    @InjectView(R.id.feedback_setting)
    TextView feedbackSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.inject(this);
        facebookSwitch.setChecked(LoginActivity.isFacebook);
        setSettingsListeners();
    }

    public void setSettingsListeners() {

        //TODO: add functionality to switches
        facebookSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //login to facebook
                } else {
                    //logout of facebook
                }
            }
        });

        autoplaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //turn autoplay on
                } else {
                    //turn autoplay off
                }
            }
        });

        feedbackSetting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //go to feedback interface
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                intent.putExtra(Intent.EXTRA_SUBJECT, subjectLine);
                startActivity(Intent.createChooser(intent, ""));
                return false;
            }
        });
    }
}
