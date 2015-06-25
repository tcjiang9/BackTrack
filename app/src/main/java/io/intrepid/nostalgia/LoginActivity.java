package io.intrepid.nostalgia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    FacebookCallback<LoginResult> facebookCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        setupFacebook();
        runActivityOnce();
    }

    @OnClick(R.id.skip_facebook)
    void onSkipFb() {
        Constants.IS_FACEBOOK = false;
        saveDataInPreferences();
        startMainActivity();
    }

    private void runActivityOnce() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pref.getBoolean("activity_executed", false)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("activity_executed", true);
            editor.apply();
        }
    }

    private void setupFacebook() {
        callbackManager = CallbackManager.Factory.create();
        facebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    Constants.IS_FACEBOOK = true;
                    saveDataInPreferences();
                    startMainActivity();
                    Toast.makeText(getApplicationContext(), "Logged in as : " + profile.getFirstName(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        };
        LoginButton facebookLogin = (LoginButton) findViewById(R.id.login_button);
        facebookLogin.setReadPermissions("public_profile", "read_stream", "user_posts");
        facebookLogin.registerCallback(callbackManager, facebookCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void saveDataInPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.ACCESS_TOKEN, MODE_PRIVATE).edit();
        if (Constants.IS_FACEBOOK) {
            editor.putString(Constants.ACCESS_TOKEN, AccessToken.getCurrentAccessToken().getToken());
            editor.apply();
        } else {
            editor.putString(Constants.ACCESS_TOKEN, null);
        }
    }

    private void startMainActivity() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

}
