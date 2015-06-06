package com.android.yardsale.models;

/**
 * Created by Prajakta on 6/5/2015.
 */

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;

import java.io.File;

@ParseClassName("Item")
public class Item extends ParseObject {
   public Item() {
        super();
    }

    // Add a constructor that contains core properties
    public Item(String description, Number price, File photo, ParseRelation<YardSale> yardsale_id) {
        super();
        setDescription(description);
        setPrice(price);
        setPhoto(photo);
        setYardSaleId(yardsale_id);

    }

    public void setDescription(String description) {
        put("description", description);
    }

    public String getDescription(){
        return getString("description");
    }

    public void setPrice(Number price) {
        put("price",price);
    }

    public Number getPrice(){
        return getNumber("price");
    }

    public ParseFile getPhoto(){
        return getParseFile("photo");
    }

    public void setPhoto(File photo) {
        put("photo",photo);
    }

    public void setYardSaleId(ParseRelation<YardSale> sale) {
        put("yardsale_id",sale);
    }

    public ParseRelation<YardSale> getYardSale(){
        return getRelation("yardsale_id");
    }


}

