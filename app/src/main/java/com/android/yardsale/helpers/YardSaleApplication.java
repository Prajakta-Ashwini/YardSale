package com.android.yardsale.helpers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.yardsale.activities.AddItemActivity;
import com.android.yardsale.activities.ItemDetailActivity;
import com.android.yardsale.activities.ListActivity;
import com.android.yardsale.activities.ProgressDialog;
import com.android.yardsale.activities.SearchActivity;
import com.android.yardsale.activities.YardSaleDetailActivity;
import com.android.yardsale.fragments.SaleMapFragment;
import com.android.yardsale.fragments.YouDoNotOwnThisAlertDialog;
import com.android.yardsale.interfaces.OnAsyncTaskCompleted;
import com.android.yardsale.models.Item;
import com.android.yardsale.models.YardSale;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class YardSaleApplication extends Application {
    public static final String YARDSALE_APPLICATION_ID = "MMFnruGWoh34ACUP3e4z7MPpn4zjU7eSTtgv4t6o";
    public static final String YARDSALE_CLIENT_KEY = "1hYdGwa3TljmkHjwJnuBSvUjTlHE7UT9iByTPy7x";
    public static final int MAP_ZOOM = 13;
    private static Context context;
    private Activity callingActivity;
    FragmentManager fm;


    public YardSaleApplication() {
        super();
    }

    public YardSaleApplication(Activity activity) {
        this.callingActivity = activity;
    }


    @Override
    public void onCreate() {
        context = getBaseContext();
        FacebookSdk.sdkInitialize(getApplicationContext());
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        ParseObject.registerSubclass(YardSale.class);
        ParseObject.registerSubclass(Item.class);
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(this, YARDSALE_APPLICATION_ID, YARDSALE_CLIENT_KEY);
        ParseUser.enableAutomaticUser();
        PushService.setDefaultPushCallback(context, ListActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseFacebookUtils.initialize(this);

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
        super.onCreate();
    }

    public void manualSignUp(final FragmentManager manager, final String userName, final String password) {
        ParseUser user = new ParseUser();
        user.setUsername(userName);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Signup success
                    Toast.makeText(context, "Signup Success", Toast.LENGTH_LONG).show();
                    login(manager, userName, password);

                } else {
                    Log.d("DEBUG", "SIGNUP FAILED", e);
                    FragmentManager fm = manager;
                    YouDoNotOwnThisAlertDialog dialog = YouDoNotOwnThisAlertDialog.newInstance("Signup failed, Try again!!!");
                    dialog.show(fm, "signup_failed");
                }
            }
        });
    }

    public void login(final FragmentManager manager, final String username, String password) {
        fm = manager;
        new LoginOperation().execute(username, password);
    }

    private class LoginOperation extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //show dialog
            dialog = ProgressDialog.newInstance();
            dialog.show(fm, "");
        }

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            ParseUser.logInInBackground(username, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        // Hooray! The user is logged in.
                        Toast.makeText(context, "Logged In!!!!!", Toast.LENGTH_LONG).show();
                        getYardSales(fm, false, false);
                    } else {
                        // Signup failed. Look at the ParseException to see what happened.
                        Log.d("DEBUG", "LOGIN FAILED", e);

                        YouDoNotOwnThisAlertDialog dialog = YouDoNotOwnThisAlertDialog.newInstance("Login failed, Try again!!!");
                        dialog.show(fm, "login_failed");
                    }
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //remove dialog
            dialog.dismiss();
        }
    }


    private void launchListActivity(ArrayList<CharSequence> saleList) {
        Intent intent = new Intent(context, ListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putCharSequenceArrayListExtra("sale_list", saleList);
        callingActivity.startActivity(intent);
    }

    public void logout() {
        ParseUser.logOut();
        LoginManager.getInstance().logOut();
    }

    public void signUpAndLoginWithFacebook(FragmentManager fragmentManager, List<String> permissions) {
        //TODO check why this is not working
        fm = fragmentManager;
        new LoginWithFbOperation().execute(permissions);
    }

    private class LoginWithFbOperation extends AsyncTask<List<String>, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //show dialog
            dialog = ProgressDialog.newInstance();
            dialog.show(fm, "");
        }

        @Override
        protected String doInBackground(List<String>... params) {
            List<String> permissions = params[0];
//            try {
//                Thread.sleep(3000);
//            }catch (InterruptedException e){
//                e.printStackTrace();
//            }
            ParseFacebookUtils.logInWithReadPermissionsInBackground(callingActivity, permissions, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException err) {
                    if (user == null) {
                        Toast.makeText(callingActivity, "signUpAndLoginWithFB called user is null", Toast.LENGTH_SHORT).show();
                        Log.e("DEBUG", "Uh oh. The user cancelled the Facebook login.");

                    } else if (user.isNew()) {
                        Toast.makeText(callingActivity, "User signed up and logged in through Facebook", Toast.LENGTH_SHORT).show();
                        Log.e("DEBUG", "User signed up and logged in through Facebook!");
                        //TODO go to the add info to profile page
                        getYardSales(fm, false, true);
                    } else {
                        Log.e("DEBUG", "User logged in through Facebook!");
                        Toast.makeText(callingActivity, "signUpAndLoginWithFB called already exisiting user", Toast.LENGTH_SHORT).show();
                        getYardSales(fm, false, true);
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //remove dialog
            dialog.dismiss();
        }
    }

    public void createYardSale(final Activity activity, final String title, final String description, Date startTime, Date endTime, String address) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String toastMessage = "title: " + title + " description: " + description + " startTime: " + dateFormat.format(startTime) + " endTime: " + dateFormat.format(endTime) + " address: " + address;
        Toast.makeText(callingActivity, toastMessage, Toast.LENGTH_LONG).show();
        //TODO get location from the given address

        final YardSale yardSale = new YardSale();
        yardSale.setTitle(title);
        yardSale.setDescription(description);
        yardSale.setAddress(address);
        yardSale.setCreatedAt(new Date());
        if (yardSale.getAddress() != null) {
            LatLng lat = GeopointUtils.getLocationFromAddress(context, yardSale.getAddress());
            if (lat != null)
                yardSale.setLocation(new ParseGeoPoint(lat.latitude, lat.longitude));
        }
        yardSale.setSeller(ParseUser.getCurrentUser());
        yardSale.setStartTime(startTime);
        yardSale.setEndTime(startTime);
        yardSale.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(activity, AddItemActivity.class);
                    intent.putExtra("fromAddingYS", true);
                    intent.putExtra("yard_sale_id", yardSale.getObjectId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(context, "error while saving item!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void updateYardSale(String id, final String title, final String description, final Date startTime, final Date endTime, final String address) {
        ParseQuery<YardSale> query = YardSale.getQuery();
        query.getInBackground(id, new GetCallback<YardSale>() {
            @Override
            public void done(YardSale yardSale, ParseException e) {
                if (e == null) {
                    yardSale.setTitle(title);
                    yardSale.setDescription(description);
                    yardSale.setStartTime(startTime);
                    yardSale.setEndTime(endTime);
                    yardSale.setAddress(address);
                    if (yardSale.getAddress() != null) {
                        LatLng lat = GeopointUtils.getLocationFromAddress(context, yardSale.getAddress());
                        if (lat != null)
                            yardSale.setLocation(new ParseGeoPoint(lat.latitude, lat.longitude));
                    }
                    yardSale.saveInBackground();
                }
            }
        });
    }

    public void getYardSales(FragmentManager fm, boolean getAllSales, boolean isFBUSer) {
        Boolean[] params = new Boolean[2];
        params[0] = getAllSales;
        params[1] = isFBUSer;
        this.fm = fm;
        new GetYardSales().execute(params);
    }

    private class GetYardSales extends AsyncTask<Boolean, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //show dialog
            dialog = ProgressDialog.newInstance();
            dialog.show(fm, "");
        }

        @Override
        protected String doInBackground(Boolean... params) {
            final boolean getAllSales = params[0];
            final boolean isFBUser = params[1];
            //            try {
            //                Thread.sleep(3000);
            //            }catch (InterruptedException e){
            //                e.printStackTrace();
            //            }
            if (isFBUser) {
                makeMeRequest(fm);
            }
            ParseQuery<YardSale> query = ParseQuery.getQuery(YardSale.class);
            query.include("seller");
            if (!getAllSales) {
                //get only sales that dont belong to me
                query.whereNotEqualTo("seller", getCurrentUser());
            }
            query.orderByDescending("createdAt");
            query.findInBackground(new FindCallback<YardSale>() {
                public void done(List<YardSale> itemList, ParseException e) {
                    if (e == null) {
                        //String firstItemId = itemList.get(0).getObjectId();
                        final ArrayList<CharSequence> saleList = new ArrayList<>();
                        if (itemList != null) {
                            for (int i = itemList.size() - 1; i >= 0; i--) {
                                YardSale sale = itemList.get(i);
                                if (sale.getLocation() == null) {
                                    LatLng lat = GeopointUtils.getLocationFromAddress(context, sale.getAddress());
                                    if (lat != null) {
                                        sale.setLocation(new ParseGeoPoint(lat.latitude, lat.longitude));
                                        sale.saveInBackground();
                                    }
                                }
                                saleList.add(sale.getObjectId());
                            }

                        }
                        launchListActivity(saleList);
                    } else {
                        Log.d("item", "Error: " + e.getMessage());
                    }

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //remove dialog
            dialog.dismiss();
        }
    }

    private boolean getAllSales;

    public void addYardSalesToMap(FragmentActivity activity, final SaleMapFragment mapFragment, boolean getAllSales) {
        this.fm = activity.getSupportFragmentManager();
        this.getAllSales = getAllSales;
        new AddYardSalesToMap().execute(mapFragment);
    }

    private class AddYardSalesToMap extends AsyncTask<SaleMapFragment, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //show dialog
            dialog = ProgressDialog.newInstance();
            dialog.show(fm, "");
        }

        @Override
        protected String doInBackground(SaleMapFragment... params) {
            final SaleMapFragment mapFragment = params[0];
            //            try {
            //                Thread.sleep(3000);
            //            }catch (InterruptedException e){
            //                e.printStackTrace();
            //            }
            ParseQuery<YardSale> query = ParseQuery.getQuery(YardSale.class);
            query.include("seller");
            if (!getAllSales) {
                //get only sales that dont belong to me
                query.whereNotEqualTo("seller", getCurrentUser());
            }
            query.findInBackground(new FindCallback<YardSale>() {
                public void done(List<YardSale> itemList, ParseException e) {
                    if (e == null) {
                        for (YardSale ys : itemList)
                            mapFragment.addSaleToList(ys);
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //remove dialog
            dialog.dismiss();
        }
    }

    public void searchForItems(FragmentManager fm, Activity activity, String query) {
        this.fm = fm;
        this.callingActivity = activity;
        new SearchItems().execute(query);
    }

    private class SearchItems extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //show dialog
            dialog = ProgressDialog.newInstance();
            dialog.show(fm, "");
        }

        @Override
        protected String doInBackground(String... params) {
            final String query = params[0];
            ParseQuery<Item> searchQuery = ParseQuery.getQuery(Item.class);
            searchQuery.whereMatches("description", "(" + query + ")", "i");
            searchQuery.findInBackground(new FindCallback<Item>() {
                public void done(List<Item> results, ParseException e) {
                    if (e == null) {
                        launchSearchActivity(callingActivity, results);
                        //   objectsWereRetrievedSuccessfully(objects);
                    } else {
                        //  objectRetrievalFailed();
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //remove dialog
            dialog.dismiss();
        }
    }

    private boolean isFromYSCreation;
    YardSale yardSale;

    public void createItem(FragmentManager fm, final boolean isFromYSCreation, final Context context, final String description, final Number price, ParseFile photo, final YardSale yardSale) {
        final Item item = new Item(description, price, photo, yardSale);
        this.isFromYSCreation = isFromYSCreation;
        this.yardSale = yardSale;
        this.fm = fm;
        new CreateItems().execute(item);
    }

    private class CreateItems extends AsyncTask<Item, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //show dialog
            dialog = ProgressDialog.newInstance();
            dialog.show(fm, "");
        }

        @Override
        protected String doInBackground(Item... params) {
            final Item item = params[0];
            item.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {

                        if (isFromYSCreation) {
                            item.getYardSale().getItemsRelation().add(item);
                            item.getYardSale().saveInBackground();
                        }
                        Intent data = new Intent();
                        Log.e("PUSH ", "Start PUSH");
                        data.putExtra("desc", String.valueOf(item.getDescription()));
                        data.putExtra("price", String.valueOf(item.getPrice()));
                        Log.e("PUSH", "Set extras done" + yardSale.getObjectId());

                        Log.e("PUSH", yardSale.getObjectId() + yardSale.getSeller().getUsername());
                        ParsePush push = new ParsePush();
                        push.setChannel("yardsale_" + yardSale.getObjectId());
                        push.setMessage("New Item has been added to the yardsale " + yardSale.getTitle());
                        push.sendInBackground();
                        ((Activity) context).setResult(143, data);
                        ((Activity) context).finish();

                    } else {
                        Log.e("CREATE_ITEM_ERR", "blah", e);
                        // when something went wrong in saving the item you do not want to display this to the user.
                    }
                }
            });
            if (yardSale.getCoverPic() == null)
                setPicForYardSale(yardSale, item.getPhoto());
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //remove dialog
            dialog.dismiss();
        }
    }

    private Number price;
    private ParseFile photo;

    public void updateItem(FragmentManager fm, final String id, final String description, final Number price, final ParseFile photo, final YardSale yardSale) {
        this.fm = fm;
        this.price = price;
        this.photo = photo;
        this.yardSale = yardSale;
        new UpdateItem().execute(id, description);
    }

    private class UpdateItem extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //show dialog
            dialog = ProgressDialog.newInstance();
            dialog.show(fm, "");
        }

        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            final String description = params[1];
            ParseQuery<Item> query = Item.getQuery();
            query.getInBackground(id, new GetCallback<Item>() {
                @Override
                public void done(Item item, ParseException e) {
                    if (e == null) {
                        item.setDescription(description);
                        item.setPrice(price);
                        item.setPhoto(photo);
                        if (!yardSale.getCoverPic().equals(photo))
                            setPicForYardSale(yardSale, photo);
                        item.saveInBackground();
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //remove dialog
            dialog.dismiss();
        }
    }

    public Item getItem(final String id) {
        ParseQuery<Item> query = Item.getQuery();
        try {
            return query.get(id);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPicForYardSale(YardSale sale, ParseFile photo) {
        sale.setCoverPic(photo);
        sale.saveInBackground();
    }

    private ImageView ivCoverPic;

    public void getItemsForYardSale(FragmentManager fm, final Context context, final YardSale yardSale, final ImageView ivCoverPic) {
        this.ivCoverPic = ivCoverPic;
        this.fm = fm;
        this.context = context;
        new GetItemsForSale().execute(yardSale);
    }

    private class GetItemsForSale extends AsyncTask<YardSale, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //show dialog
            dialog = ProgressDialog.newInstance();
            dialog.show(fm, "");
        }

        @Override
        protected String doInBackground(YardSale... params) {
            final YardSale yardSale = params[0];
            // Define the class we would btn_like to query
            // Define the class we would btn_like to query
            ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
            query.whereEqualTo("yardsale_id", yardSale);
            query.findInBackground(new FindCallback<Item>() {
                public void done(List<Item> itemList, ParseException e) {
                    if (e == null) {
                        launchYardSaleDetailActivity(context, yardSale.getObjectId(), itemList, ivCoverPic);
                    } else {
                        Log.d("item", "Error: " + e.getMessage());
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //remove dialog
            dialog.dismiss();
        }
    }

    public void getItemsForYardSaleFromMap(FragmentManager fm, final Context context, final String yardSale_id) {
        this.fm = fm;
        new GetItemsForSaleFromMap().execute(yardSale_id);
    }

    private class GetItemsForSaleFromMap extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //show dialog
            dialog = ProgressDialog.newInstance();
            dialog.show(fm, "");
        }

        @Override
        protected String doInBackground(String... params) {
            final String yardSale_id = params[0];
            ParseQuery<YardSale> query = ParseQuery.getQuery(YardSale.class);
// Specify the object id
            query.getInBackground(yardSale_id, new GetCallback<YardSale>() {
                public void done(YardSale sale, ParseException e) {
                    if (e == null) {
                        getItemsForYardSale(fm, context, sale, null);
                    } else {
                        Log.d("item", "Error: " + e.getMessage());
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //remove dialog
            dialog.dismiss();
        }
    }

    private static void launchYardSaleDetailActivity(Context context, String yardsale_id, List<Item> items, ImageView ivCoverPic) {

        Intent intent = new Intent(context, YardSaleDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ArrayList<CharSequence> itemObjList = new ArrayList<>();
        for (Item item : items) {
            itemObjList.add(item.getObjectId());
        }
        intent.putCharSequenceArrayListExtra("item_list", itemObjList);
        intent.putExtra("yardsale", yardsale_id);
        ActivityOptionsCompat options = null;
        if (ivCoverPic != null) {
            options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) context, ivCoverPic, "itemDetail");
            context.startActivity(intent, options.toBundle());
        } else {
            context.startActivity(intent);
        }
    }

    //TODO may be generalize this
    private static void launchSearchActivity(Activity activity, List<Item> items) {
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle data = new Bundle();
        ArrayList<String> seachItems = new ArrayList<>();
        for (Item item : items) {
            seachItems.add(item.getObjectId());
        }
        data.putStringArrayList("search", seachItems);
        intent.putExtras(data);
        activity.startActivity(intent);
    }

    public void deleteSale(FragmentManager fm, YardSale sale) {
        this.fm = fm;
        new DeleteSale().execute(sale);

//        Naturally we can also delete in an offline manner with:
//
//        todoItem.deleteEventually();
    }

    private class DeleteSale extends AsyncTask<YardSale, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //show dialog
            dialog = ProgressDialog.newInstance();
            dialog.show(fm, "");
        }

        @Override
        protected String doInBackground(YardSale... params) {
            final YardSale sale = params[0];
            sale.deleteInBackground();

            ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
            query.whereEqualTo("yardsale_id", sale);
            query.findInBackground(new FindCallback<Item>() {
                public void done(List<Item> itemList, ParseException e) {
                    if (e == null) {
                        for (Item item : itemList)
                            item.deleteInBackground();
                    } else {
                        Log.d("item", "Error: " + e.getMessage());
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //remove dialog
            dialog.dismiss();
        }
    }

    public void deleteItem(FragmentManager fm, Item item) {
        this.fm = fm;
        new DeleteItem().execute(item);
    }

    private class DeleteItem extends AsyncTask<Item, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //show dialog
            dialog = ProgressDialog.newInstance();
            dialog.show(fm, "");
        }

        @Override
        protected String doInBackground(Item... params) {
            final Item item = params[0];
            ParsePush push = new ParsePush();
            push.setChannel("yardsale_" + item.getYardSale().getObjectId());
            push.setMessage(item.getDescription() + " was deleted");
            push.sendInBackground();
            item.deleteInBackground();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //remove dialog
            dialog.dismiss();
        }
    }

    // Can be triggered by a view event such as a button press
    public void shareSale(FragmentManager fm, Context context, YardSale sale) {
        this.context = context;
        this.fm = fm;
        new ShareSale().execute(sale);
    }

    private class ShareSale extends AsyncTask<YardSale, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //show dialog
            dialog = ProgressDialog.newInstance();
            dialog.show(fm, "");
        }

        @Override
        protected String doInBackground(YardSale... params) {
            YardSale sale = params[0];
            try {
                ParseFile file = sale.getCoverPic();
                if (file == null) {
                    Toast.makeText(context, "cover pic not found!", Toast.LENGTH_SHORT).show();
                    return "";
                }
                // Get access to the URI for the bitmap
                Uri bmpUri = getLocalBitmapUri(file);

                if (bmpUri != null) {
//                // Construct a ShareIntent with link to image
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
//                shareIntent.setType("image/*");
//                // Launch sharing dialog for image
//                context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
                    List<Intent> targetedShareIntents = new ArrayList<Intent>();
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("image/*");

                    List<ResolveInfo> resInfo = context.getPackageManager()
                            .queryIntentActivities(share, 0);
                    if (!resInfo.isEmpty()) {
                        for (ResolveInfo info : resInfo) {
                            Intent targetedShare = new Intent(
                                    android.content.Intent.ACTION_SEND);
                            targetedShare.setType("image/*"); // put here your mime
                            // type
//                        if (info.activityInfo.packageName.toLowerCase().contains(
//                                nameApp)
//                                || info.activityInfo.name.toLowerCase().contains(
//                                nameApp)) {
                            String subject = "Yardsale alert! :) " + sale.getTitle() + " at " + sale.getAddress();
                            targetedShare.putExtra(Intent.EXTRA_SUBJECT, subject);
                            targetedShare.putExtra(Intent.EXTRA_TEXT, subject);
                            targetedShare.putExtra(Intent.EXTRA_STREAM,
                                    bmpUri);
                            targetedShare.setPackage(info.activityInfo.packageName);
                            targetedShareIntents.add(targetedShare);

                        }
                        Intent chooserIntent = Intent.createChooser(
                                targetedShareIntents.remove(0), "Select app to share");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                                targetedShareIntents.toArray(new Parcelable[]{}));
                        context.startActivity(chooserIntent);
                    }
                } else {
                    // ...sharing failed, handle error
                    Toast.makeText(context, "Error while sharing image!", Toast.LENGTH_SHORT)
                            .show();
                }
            } catch (ParseException e) {
                Toast.makeText(context, "Error while sharing image!", Toast.LENGTH_SHORT)
                        .show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //remove dialog
            dialog.dismiss();
        }
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ParseFile image) throws ParseException {
        // Extract Bitmap from ImageView drawable
        Bitmap bmp = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);

        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    //todo fix this
    public static void launchItemDetailActivity(Context context, Item item) {
        Intent i = new Intent(context, ItemDetailActivity.class);
        i.putExtra("selected_item", item.getObjectId());
//        ActivityOptionsCompat options = ActivityOptionsCompat.
//                makeSceneTransitionAnimation((Activity) context, (View) ivItemPic, "itemDetail");
//        context.startActivity(i, options.toBundle());
    }

    public static ParseUser getCurrentUser() {
        return ParseUser.getCurrentUser();
    }

    public void makeMeRequest(FragmentManager fm) {
        this.fm = fm;
        new MakeMeRequest().execute();
    }

    private class MakeMeRequest extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //show dialog
            dialog = ProgressDialog.newInstance();
            dialog.show(fm, "");
        }

        @Override
        protected String doInBackground(Void... params) {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                            if (jsonObject != null) {
                                JSONObject userProfile = new JSONObject();
                                System.out.println("response: " + jsonObject.toString());
                                try {

                                    String url = "https://graph.facebook.com/" + jsonObject.getLong("id") + "/picture?type=large";
                                    // Save the user profile info in a user property
                                    ParseUser currentUser = ParseUser.getCurrentUser();
                                    currentUser.put("profile", userProfile);
                                    currentUser.setUsername(jsonObject.getString("name"));
                                    currentUser.setEmail(jsonObject.getString("email"));
                                    currentUser.put("profile_pic_url", url);
                                    currentUser.saveInBackground();

                                } catch (JSONException e) {
                                    Log.d("TAG",
                                            "Error parsing returned user data. " + e);
                                }
                            } else if (graphResponse.getError() != null) {
                                switch (graphResponse.getError().getCategory()) {
                                    case LOGIN_RECOVERABLE:
                                        Log.d("TAG",
                                                "Authentication error: " + graphResponse.getError());
                                        break;

                                    case TRANSIENT:
                                        Log.d("TAG",
                                                "Transient error. Try again. " + graphResponse.getError());
                                        break;

                                    case OTHER:
                                        Log.d("TAG",
                                                "Some other error: " + graphResponse.getError());
                                        break;
                                }
                            }
                        }
                    });

            request.executeAsync();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //remove dialog
            dialog.dismiss();
        }
    }

    private ImageButton btLike;
    private boolean toggleLike;
    private OnAsyncTaskCompleted listener;

    public void setLikeForSale(FragmentManager fm, final YardSale sale, OnAsyncTaskCompleted listener) {
        this.fm = fm;
        this.listener = listener;
        new SetLikeForSale().execute(sale);
    }

    private class SetLikeForSale extends AsyncTask<YardSale, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //show dialog
            dialog = ProgressDialog.newInstance();
            dialog.show(fm, "");
        }

        //return list of sales that user likes
        @Override
        protected String doInBackground(YardSale... params) {
            final YardSale sale = params[0];

            sale.getLikesRelation().getQuery().findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> results, ParseException e) {
                    if (e == null) {
                        if (!results.contains(getCurrentUser())) {
                            sale.addLikeForUser(getCurrentUser());

                        } else {
                            sale.removeLikeForUser(getCurrentUser());

                        }

                        listener.onTaskCompleted();
                    } else {
                        Log.d("ff", e.toString());
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //remove dialog

            dialog.dismiss();
        }
    }
}
