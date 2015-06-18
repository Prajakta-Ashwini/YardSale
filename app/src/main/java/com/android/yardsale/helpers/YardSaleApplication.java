package com.android.yardsale.helpers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.yardsale.activities.ItemDetailActivity;
import com.android.yardsale.activities.ListActivity;
import com.android.yardsale.activities.SearchActivity;
import com.android.yardsale.activities.YardSaleDetailActivity;
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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
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


    public YardSaleApplication() {
        super();
    }

    public YardSaleApplication(Activity activity) {
        this.callingActivity = activity;
    }

    public enum TYPE_OF_PIC {
        PROFILE_PIC,
        COVER_PIC
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getBaseContext();
        FacebookSdk.sdkInitialize(getApplicationContext());
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        ParseObject.registerSubclass(YardSale.class);
        ParseObject.registerSubclass(Item.class);
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(this, YARDSALE_APPLICATION_ID, YARDSALE_CLIENT_KEY);
        ParseUser.enableAutomaticUser();
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
                    getYardSales(false);
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Toast.makeText(context, "Login failed :(", Toast.LENGTH_LONG).show();
                    Log.d("DEBUG", "SIGNUP FAILED", e);
                }
            }
        });
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

    public void signUpAndLoginWithFacebook(List<String> permissions) {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(callingActivity, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                //TODO Progress bars
                if (user == null) {
                    Log.d("DEBUG", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
//                    saveUserPicForFacebookUsers(user, TYPE_OF_PIC.PROFILE_PIC);
//                    saveUserPicForFacebookUsers(user, TYPE_OF_PIC.COVER_PIC);
                    Log.d("DEBUG", "User signed up and logged in through Facebook!");
                    //TODO go to the add info to profile page
                    getYardSales(false);
                } else {
                    Log.d("DEBUG", "User logged in through Facebook!");
//                    saveUserPicForFacebookUsers(user, TYPE_OF_PIC.PROFILE_PIC);
//                    saveUserPicForFacebookUsers(user, TYPE_OF_PIC.COVER_PIC);
                    getYardSales(false);
                }
            }
        });
    }

//    private void saveUserPicForFacebookUsers(ParseUser user, TYPE_OF_PIC type) {
//        switch (type) {
//            case PROFILE_PIC:
//                if (user.get("profile_pic") == null) {
//                    Log.d("DEBUG 0", getUrlBasedOnType(type));
//                    Bitmap bitmapUserProfilePic = getPicFromFacebook(TYPE_OF_PIC.PROFILE_PIC);
//                    ParseFile profilePic = new ParseFile(ImageHelper.getBytesFromBitmap(bitmapUserProfilePic));
//                    user.put("profile_pic", profilePic);
//                    user.saveInBackground();
//                }
//            case COVER_PIC:
//                if (user.get("cover_pic") == null) {
//                    Bitmap bitmapUserProfilePic = getPicFromFacebook(TYPE_OF_PIC.COVER_PIC);
//                    ParseFile profilePic = new ParseFile(ImageHelper.getBytesFromBitmap(bitmapUserProfilePic));
//                    user.put("cover_pic", profilePic);
//                    user.saveInBackground();
//                }
//
//        }
//    }

    public void createYardSale(final Context context, final String title, final String description, Date startTime, Date endTime, String address) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String toastMessage = "title: " + title + " description: " + description + " startTime: " + dateFormat.format(startTime) + " endTime: " + dateFormat.format(endTime) + " address: " + address;
        Toast.makeText(callingActivity, toastMessage, Toast.LENGTH_LONG).show();
        //TODO get location from the given address

        YardSale yardSale = new YardSale();
        yardSale.setTitle(title);
        yardSale.setDescription(description);
        yardSale.setAddress(address);
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
                    Intent data = new Intent();
                    data.putExtra("title", String.valueOf(title));
                    data.putExtra("desc", String.valueOf(description));
                    ((Activity) context).setResult(((Activity) context).RESULT_OK, data);
                    ((Activity) context).finish();
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

    public void getYardSales(boolean getAllSales) {
        ParseQuery<YardSale> query = ParseQuery.getQuery(YardSale.class);
        query.include("seller");
        if(!getAllSales){
            //get only sales that dont belong to me
            query.whereNotEqualTo("seller",getCurrentUser());
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
    }

    public void searchForItems(String query) {
        ParseQuery<Item> searchQuery = ParseQuery.getQuery(Item.class);
        searchQuery.whereMatches("description", "(" + query + ")", "i");
        searchQuery.findInBackground(new FindCallback<Item>() {
            public void done(List<Item> results, ParseException e) {
                if (e == null) {
                    launchSearchActivity(results);
                    //   objectsWereRetrievedSuccessfully(objects);
                } else {
                    //  objectRetrievalFailed();
                }
            }
        });
    }


    public void createItem(final Context context, final String description, final Number price, ParseFile photo, YardSale yardSale) {
        Item item = new Item(description, price, photo, yardSale);
        item.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Intent data = new Intent();
                    data.putExtra("desc", String.valueOf(description));
                    data.putExtra("price", String.valueOf(price));
                    ((Activity) context).setResult(((Activity) context).RESULT_OK, data);
                    ((Activity) context).finish();
                } else {
                    Toast.makeText(context, "error while saving item!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (yardSale.getCoverPic() == null)
            setPicForYardSale(yardSale, photo);
    }

    public void updateItem(final String id, final String description, final Number price, final ParseFile photo, final YardSale yardSale) {
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

    public void getItemsForYardSale(final Context context, final YardSale yardSale, final ImageView ivCoverPic) {
        // Define the class we would btn_like to query
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        query.whereEqualTo("yardsale_id", yardSale);
        query.findInBackground(new FindCallback<Item>() {
            public void done(List<Item> itemList, ParseException e) {
                if (e == null) {
                    launchYardSaleDetailActivity(context, yardSale, itemList, ivCoverPic);
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    private static void launchYardSaleDetailActivity(Context context, YardSale yardsale, List<Item> items, ImageView ivCoverPic) {
        Intent intent = new Intent(context, YardSaleDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ArrayList<CharSequence> itemObjList = new ArrayList<>();
        for (Item item : items) {
            itemObjList.add(item.getObjectId());
        }
        intent.putCharSequenceArrayListExtra("item_list", itemObjList);
        intent.putExtra("yardsale", yardsale.getObjectId());
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) context, ivCoverPic, "itemDetail");
        context.startActivity(intent, options.toBundle());
    }

    //TODO may be generalize this
    private static void launchSearchActivity(List<Item> items) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle data = new Bundle();
        ArrayList<String> seachItems = new ArrayList<>();
        for (Item item : items) {
            seachItems.add(item.getObjectId());
        }
        data.putStringArrayList("search", seachItems);
        intent.putExtras(data);
        context.startActivity(intent);
    }

    public void deleteSale(YardSale sale) {
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

//        Naturally we can also delete in an offline manner with:
//
//        todoItem.deleteEventually();
    }

    public void deleteItem(Item item) {
        item.deleteInBackground();
    }

    // Can be triggered by a view event such as a button press
    public void shareSale(Context context, YardSale sale) {
        try {
            ParseFile file = sale.getCoverPic();
            if (file == null) {
                Toast.makeText(context, "cover pic not found!", Toast.LENGTH_SHORT).show();
                return;
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

    public static void launchItemDetailActivity(Context context, Item item, ImageView ivItemPic) {
        Intent i = new Intent(context, ItemDetailActivity.class);
        i.putExtra("selected_item", item.getObjectId());
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) context, (View) ivItemPic, "itemDetail");
        context.startActivity(i, options.toBundle());
    }

    public static ParseUser getCurrentUser() {
        return ParseUser.getCurrentUser();
    }

    public void makeMeRequest() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {
                            JSONObject userProfile = new JSONObject();
                            System.out.println("response: " + jsonObject.toString());
                            try {

                                String url = "https://graph.facebook.com/" + jsonObject.getLong("id") + "/picture?type=medium";
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
    }

    public void setLikeForSale(final YardSale sale, final ImageButton btLike, final boolean toggleLike) {
        sale.getLikesRelation().getQuery().findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> results, ParseException e) {
                if (e == null) {
                    if (toggleLike) {
                        if (!results.contains(ParseUser.getCurrentUser())) {
                            sale.addLikeForUser(ParseUser.getCurrentUser());
                            btLike.setSelected(true);
                        } else {
                            sale.removeLikeForUser(ParseUser.getCurrentUser());
                            btLike.setSelected(false);
                        }
                    } else {
                        if (results.contains(ParseUser.getCurrentUser())) {
                            btLike.setSelected(true);
                        } else {
                            btLike.setSelected(false);
                        }
                    }

                } else {
                    Log.d("ff", e.toString());
                }
            }
        });
    }
}
