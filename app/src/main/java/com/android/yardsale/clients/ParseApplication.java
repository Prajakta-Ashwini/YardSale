package com.android.yardsale.clients;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Prajakta on 6/4/2015.
 */
public class ParseApplication extends Application {
    public static final String YOUR_APPLICATION_ID = "MMFnruGWoh34ACUP3e4z7MPpn4zjU7eSTtgv4t6o";
    public static final String YOUR_CLIENT_KEY = "1hYdGwa3TljmkHjwJnuBSvUjTlHE7UT9iByTPy7x";

    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);

        // Test creation of object
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }
}
