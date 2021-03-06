package com.android.yardsale.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Date;


@ParseClassName("YardSale")
public class YardSale extends ParseObject {
    public YardSale() {
        super();
    }

    // Add a constructor that contains core properties
    public YardSale(String title, String description, Date startTime, Date endTime, String address, ParseGeoPoint location, ParseUser seller) {
        super();
        setTitle(title);
        setDescription(description);
        setStartTime(startTime);
        setEndTime(endTime);
        setAddress(address);
        setLocation(location);
        setSeller(seller);
        setCreatedAt(new Date());
    }

    public Date getCreatedAt() {
        return getDate("createdAtDate");
    }

    public void setCreatedAt(Date endTime) {
        put("createdAtDate", endTime);
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setStartTime(Date startTime) {
        put("start_time", startTime);
    }

    public Date getStartTime() {
        return getDate("start_time");
    }

    public void setEndTime(Date endTime) {
        put("end_time", endTime);
    }

    public Date getEndTime() {
        return getDate("end_time");
    }

    public void setLocation(ParseGeoPoint location) {
        put("location", location);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setAddress(String address) {
        put("address", address);
    }

    public String getAddress() {
        return getString("address");
    }

    public void setSeller(ParseUser seller) {
        put("seller", seller);
    }

    public ParseUser getSeller() {
        return getParseUser("seller");
    }

    public ParseFile getCoverPic() {
        return getParseFile("cover_pic");
    }

    public void setCoverPic(ParseFile photo) {
        put("cover_pic", photo);
    }

    public static ParseQuery<YardSale> getQuery() {
        return ParseQuery.getQuery(YardSale.class);
    }

    public ParseRelation<ParseUser> getLikesRelation() {
        return getRelation("user_likes");
    }

    public void addLikeForUser(ParseUser user) {
        try {
            getLikesRelation().add(user);
            ParsePush.subscribeInBackground("yardsale_" + getObjectId());
            Log.e("PUSH SUBSCRIBE", getObjectId() + getSeller().fetchIfNeeded().getUsername());
            saveInBackground();
        } catch (ParseException e) {
            Log.d("Error:", "Not able to get username");
            e.printStackTrace();
        }
    }

    public void removeLikeForUser(ParseUser user) {
        try {
            getLikesRelation().remove(user);
            ParsePush.unsubscribeInBackground("yardsale_" + getObjectId());
            Log.e("PUSH UNSUBSCRIBE", getObjectId() + getSeller().fetchIfNeeded().getUsername());
            saveInBackground();
        } catch (ParseException e) {
            Log.d("Error:", "Not able to get username");
            e.printStackTrace();
        }
    }

    public ParseRelation<Item> getItemsRelation() {
        return getRelation("items");
    }

    public void addItem(Item tag) {
        getItemsRelation().add(tag);
        saveInBackground();
    }

    public void removeItem(Item tag) {
        getItemsRelation().remove(tag);
        saveInBackground();
    }
}
