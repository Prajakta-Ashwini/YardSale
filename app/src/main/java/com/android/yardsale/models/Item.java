package com.android.yardsale.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Item")
public class Item extends ParseObject {

    //TODO make these objects pracelable
    public Item() {
        super();
    }

    // Add a constructor that contains core properties
    public Item(String description, Number price, ParseFile photo, YardSale yardsale_id) {
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

    public void setPhoto(ParseFile photo) {
        put("photo",photo);
    }

    public void setYardSaleId(YardSale sale) {
        put("yardsale_id",sale);
    }

    public YardSale getYardSale(){
        return (YardSale)getParseObject("yardsale_id");
    }


}

