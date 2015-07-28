package io.intrepid.nostalgia.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTouch;
import io.intrepid.nostalgia.R;
import io.intrepid.nostalgia.constants.Constants;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends AppCompatActivity{

    public static final String TAG = SettingsActivity.class.getSimpleName();
    public static final String EMAIL = "hayley@intrepid.io";
    public static final String SUBJECT_LINE = "Nostalgia App Feedback";
    public final String PERMIT = "public_profile";

    private boolean isOpening;
    CallbackManager callbackManager;

    @InjectView(R.id.facebook_switch)
    Switch facebookSwitch;

    @InjectView(R.id.autoplay_switch)
    Switch autoplaySwitch;

    //TODO: add functionality to switches
    @OnCheckedChanged(R.id.facebook_switch) void onFacebookSwitchChanged(boolean isChecked) {
        if (!isOpening) {
            if (isChecked) {
                onFacebookLogin();


            } else {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putString(Constants.SHARED_PREFS_ACCESS_TOKEN, null);
                editor.apply();
                LoginActivity.isFacebook = false;
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

    @OnClick(R.id.feedback_setting)
    void onFeedbackSettingTouched() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL});
        intent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT_LINE);
        startActivity(Intent.createChooser(intent, ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.inject(this);

    }

//    @Override
//    public void onBackPressed() {
//        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//        startActivity(i);
//        finish();
//    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onResume() {
        super.onResume();
        setSwitches();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void setSwitches() {
        isOpening = true;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPreferences.getString(Constants.SHARED_PREFS_ACCESS_TOKEN, null);
        facebookSwitch.setChecked(token != null);
        autoplaySwitch.setChecked(sharedPreferences.getBoolean(Constants.SHARED_PREFS_AUTOPLAY, true));
        isOpening = false;
    }

    public void onFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(PERMIT);
        LoginManager.getInstance().logInWithReadPermissions(this, permissions);
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    verifyFbProfile(profile);
                }
                ProfileTracker profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile currentProfile) {
                        Profile.setCurrentProfile(currentProfile);
                        verifyFbProfile(currentProfile);
                        this.stopTracking();
                    }
                };
                profileTracker.startTracking();
                finish();
            }

            @Override
            public void onCancel() {
                finish();
            }

            @Override
            public void onError(FacebookException e) {
                Log.d(TAG, e.toString());
            }
        });
    }

    private void verifyFbProfile(Profile profile) {
        LoginActivity.isFacebook = true;
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(Constants.SHARED_PREFS_ACCESS_TOKEN, AccessToken.getCurrentAccessToken().toString());
        editor.apply();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
        Toast.makeText(getApplicationContext(), "Logged in as : " + profile.getFirstName(), Toast.LENGTH_LONG).show();
    }


}
