package com.android.yardsale.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

@ParseClassName("Wishlist")
public class Wishlist extends ParseObject{
    public Wishlist(){
        super();
    }

    public Wishlist(ParseUser user, ParseRelation<Item> item){
        super();
        setUser(user);
        setItem(item);
    }

    public void setUser(ParseUser user){
        put("user", user);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setItem(ParseRelation<Item> item){
        put("item", item);
    }

    public ParseRelation<Item> getItem(){
        return getRelation("item");
    }
}
