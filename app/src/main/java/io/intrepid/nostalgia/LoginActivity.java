package io.intrepid.nostalgia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    FacebookCallback<LoginResult> facebookCallback;
    public static boolean isFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        setupFacebook();
    }

    @OnClick(R.id.skip_facebook)
    void onSkipFb() {
        isFacebook = false;
        saveDataInPreferences();
        startMainActivity();

    }

    private void setupFacebook() {
        callbackManager = CallbackManager.Factory.create();
        facebookCallback = new FacebookCallback<LoginResult>() {
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
                        Log.e("in profile changed", "profile has changed");
                        verifyFbProfile(currentProfile);
                        this.stopTracking();
                    }
                };
                profileTracker.startTracking();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        };
        LoginButton facebookLogin = (LoginButton) findViewById(R.id.login_button);
        facebookLogin.setReadPermissions("public_profile");
        facebookLogin.registerCallback(callbackManager, facebookCallback);
    }

    private void verifyFbProfile(Profile profile) {
        isFacebook = true;
        saveDataInPreferences();
        startMainActivity();
        Toast.makeText(getApplicationContext(), "Logged in as : " + profile.getFirstName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void saveDataInPreferences() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (isFacebook) {
            editor.putString(Constants.SHARED_PREFS_ACCESS_TOKEN, AccessToken.getCurrentAccessToken().toString());
        } else {
            editor.putString(Constants.SHARED_PREFS_ACCESS_TOKEN, null);
        }
        editor.putBoolean(Constants.SHARED_PREFS_LOGIN, true);
        editor.apply();
    }

    private void startMainActivity() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

}
