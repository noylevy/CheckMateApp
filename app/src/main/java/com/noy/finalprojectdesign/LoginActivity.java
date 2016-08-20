package com.noy.finalprojectdesign;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
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
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.noy.finalprojectdesign.Model.Model;
import com.noy.finalprojectdesign.Model.User;
import com.noy.finalprojectdesign.Receivers.AlarmReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends Activity {
    private static final String TAGGED_PLACES_PERMISSION = "user_tagged_places";
    private static final String USER_BIRTHDAY_PERMISSION = "user_birthday";

    private LoginButton loginButton;
    private AccessTokenTracker accessTokenTracker;
    private CallbackManager callbackManager;
    Button enterBtn;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void getHash() {

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.noy.finalprojectdesign",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getHash();
        FacebookSdk.sdkInitialize(this);
        Model.getInstance().init(this);
        setContentView(R.layout.activity_login);

        enterBtn = (Button) findViewById(R.id.enter_button);

//        Model.getInstance().setLastSyncTime(null);

        if(AccessToken.getCurrentAccessToken() != null) {
            enterBtn.setVisibility(View.VISIBLE);
            enterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(intent);
                }
            });
            updateWithToken(AccessToken.getCurrentAccessToken());
        }
        else{
            enterBtn.setVisibility(View.GONE);
            callbackManager = CallbackManager.Factory.create();

            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(Arrays.asList(TAGGED_PLACES_PERMISSION,USER_BIRTHDAY_PERMISSION));
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Model.getInstance().setUserId(loginResult.getAccessToken().getUserId());
                    Model.getInstance().saveUserOnServer(new Model.SimpleSuccessListener() {
                        @Override
                        public void onResult(boolean result) {
                            Log.d("Login","saved user");
                        }
                    },loginResult.getAccessToken().getUserId(),loginResult.getAccessToken().getToken());
                }

                @Override
                public void onCancel() {
                    Toast.makeText(getApplicationContext(), "Facebook login canceled",
                            Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(getApplicationContext(), "There was a problem logging in",
                            Toast.LENGTH_SHORT).show();

                }
            });


            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    updateWithToken(currentAccessToken);
                }
            };
        }
    }

    //TODO: add profile tracker and change userId by it.
    private void updateWithToken(AccessToken currentAccessToken) {
        if (currentAccessToken != null) {
            //go to main.
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);

        } else {
            //go to login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }
    }
}

