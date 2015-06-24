package io.intrepid.nostalgia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    public  static boolean isFacebook = false;
    public  static  String accessToken = "access";
    CallbackManager callbackManager;
    FacebookCallback<LoginResult> facebookCallback;
    Button skipFacebook;
    @InjectView(R.id.skip_facebook)
    Button getSkipFacebook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        setupFacebook();
    }

    @OnClick(R.id.skip_facebook) void onSkipFb(){

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
                    isFacebook = true;
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

    private void saveDataInPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences(accessToken, MODE_PRIVATE).edit();
        if (isFacebook) {
            editor.putString(accessToken, AccessToken.getCurrentAccessToken().getToken());
            editor.apply();
        } else {
            editor.putString(accessToken, null);
        }
    }

    private void startMainActivity() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
