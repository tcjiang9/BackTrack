package io.intrepid.nostalgia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    CallbackManager callbackManager;
    @InjectView(R.id.skip_facebook)
    Button skipFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_login);
        setupFacebook();
    }

    FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
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

    private void setupFacebook() {
        callbackManager = CallbackManager.Factory.create();
        LoginButton facebookLogin = (LoginButton) findViewById(R.id.login_button);
        facebookLogin.setOnClickListener(this);
        facebookLogin.setReadPermissions("public_profile", "read_stream", "user_posts");
        facebookLogin.registerCallback(callbackManager, facebookCallback);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
