package io.intrepid.nostalgia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    FacebookCallback<LoginResult> facebookCallback;
    public static boolean isFacebook;
    public final String PERMIT = "public_profile";

    @OnClick(R.id.skip_facebook)
    void onSkipFb() {
        isFacebook = false;
        saveDataInPreferences();
        startMainActivity();
    }

    @OnClick(R.id.login_button) void onLoginClick() {
        onFacebookLogin(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        if (intent.getBooleanExtra("settings", false)) {
            onFacebookLogin(true);
        }
    }

    public void onFacebookLogin(final boolean skipMain) {
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
                checkIntent(skipMain);
            }

            @Override
            public void onCancel() {
                checkIntent(skipMain);
            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }

    private void checkIntent(boolean skipMain) {
        if (skipMain) {
            finish();
        }
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
