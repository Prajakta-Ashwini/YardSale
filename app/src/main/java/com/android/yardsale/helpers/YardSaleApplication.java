package com.android.yardsale.helpers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.activities.MainActivity;
import com.facebook.FacebookSdk;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class YardSaleApplication extends Application {

    private static Context context;
    private Activity callingActivity;

    public YardSaleApplication() {
    }

    public YardSaleApplication(Activity activity) {
        this.callingActivity = activity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getBaseContext();
        FacebookSdk.sdkInitialize(getApplicationContext());
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        //Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        ParseFacebookUtils.initialize(this);

    }

    public void manualSignUp(final String userName, final String password) {
        ParseUser user = new ParseUser();
        user.setUsername(userName);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Signup success
                    Toast.makeText(context, "Signup Success", Toast.LENGTH_LONG).show();
                    login(userName, password);

                } else {
                    Toast.makeText(context, "Signup Failed", Toast.LENGTH_LONG).show();
                    Log.d("DEBUG", "SIGNUP FAILED", e);
                    // signup failed
                }
            }
        });
    }

    public void login(final String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    Toast.makeText(context, "Logged In!!!!!", Toast.LENGTH_LONG).show();
                    launchMainActivity(username);
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Toast.makeText(context, "Login failed :(", Toast.LENGTH_LONG).show();
                    Log.d("DEBUG", "SIGNUP FAILED", e);
                }
            }
        });
    }

    private static void launchMainActivity(String username) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("test_user_name", username);
        context.startActivity(intent);
    }

    public void logout() {
        //FB.logout();
        ParseUser.logOut();
    }

    public void signUpAndLoginWithFacebook(List<String> permissions)
    {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(callingActivity, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
              //TODO Progress bars
                if (user == null) {
                    Log.d("DEBUG", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("DEBUG", "User signed up and logged in through Facebook!");
                    //TODO go to the add info to profile page
                    //showUserDetailsActivity();
                    launchMainActivity(user.getUsername());
                } else {
                    Log.d("DEBUG", "User logged in through Facebook!");
                    launchMainActivity(user.getUsername());
                }
            }
        });
    }

}
