package com.android.yardsale.helpers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.yardsale.activities.ListActivity;
import com.android.yardsale.models.Item;
import com.android.yardsale.models.YardSale;
import com.facebook.FacebookSdk;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class YardSaleApplication extends Application {
    public static final String YARDSALE_APPLICATION_ID = "MMFnruGWoh34ACUP3e4z7MPpn4zjU7eSTtgv4t6o";
    public static final String YARDSALE_CLIENT_KEY = "1hYdGwa3TljmkHjwJnuBSvUjTlHE7UT9iByTPy7x";
    private static Context context;
    private Activity callingActivity;

    public YardSaleApplication() {
        super();
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
        // Register your parse models
        ParseObject.registerSubclass(YardSale.class);
        Parse.initialize(this, YARDSALE_APPLICATION_ID, YARDSALE_CLIENT_KEY);
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
                    getYardSales(username);
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Toast.makeText(context, "Login failed :(", Toast.LENGTH_LONG).show();
                    Log.d("DEBUG", "SIGNUP FAILED", e);
                }
            }
        });
    }

    private static void launchListActivity(String username, ArrayList<CharSequence> saleList) {
        Intent intent = new Intent(context, ListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("test_user_name", username);
        intent.putCharSequenceArrayListExtra("sale_list", saleList);
        context.startActivity(intent);
    }

    public void logout() {
        //FB.logout();
        ParseUser.logOut();
    }

    public void signUpAndLoginWithFacebook(List<String> permissions) {
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
                    getYardSales(user.getUsername());
                } else {
                    Log.d("DEBUG", "User logged in through Facebook!");
                    getYardSales(user.getUsername());
                }
            }
        });
    }

    public void createYardSale(String title, String description, Date startTime, Date endTime, ParseGeoPoint location) {
        YardSale sale = new YardSale(title, description, startTime, endTime, location, ParseUser.getCurrentUser());
        sale.saveInBackground();
    }

    public void getYardSales(final String username) {
        ParseQuery<YardSale> query = ParseQuery.getQuery(YardSale.class);
        query.findInBackground(new FindCallback<YardSale>() {
            public void done(List<YardSale> itemList, ParseException e) {
                if (e == null) {
                    //String firstItemId = itemList.get(0).getObjectId();
                    ArrayList<CharSequence> saleList = new ArrayList<>();
                    for (YardSale sale : itemList) {
                        saleList.add(sale.getObjectId());
                    }
                    launchListActivity(username, saleList);
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }

            }
        });
    }

    public List<Item> searchForItems(String query) {
        final ArrayList<Item> items = new ArrayList<>();
        ParseQuery searchQuery = new ParseQuery("Items");
        searchQuery.whereEqualTo("description", query);
        searchQuery.findInBackground(new FindCallback<Item>() {
            public void done(List<Item> objects, ParseException e) {
                if (e == null) {
                    items.addAll(objects);
                    //   objectsWereRetrievedSuccessfully(objects);
                } else {
                    //  objectRetrievalFailed();
                }
            }
        });
        return items;
    }

}
