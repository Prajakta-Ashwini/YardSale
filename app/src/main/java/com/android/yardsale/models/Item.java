package com.android.yardsale.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Item")
public class Item extends ParseObject {

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

    public String getDescription() {
        return getString("description");
    }

    public void setPrice(Number price) {
        put("price", price);
    }

    public Number getPrice() {
        return getNumber("price");
    }

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public void setPhoto(ParseFile photo) {
        put("photo", photo);
    }

    public void setYardSaleId(YardSale sale) {
        put("yardsale_id", sale);
    }

    public YardSale getYardSale() {
        return (YardSale) getParseObject("yardsale_id");
    }

    public static ArrayList<Item> fromList(List<ParseObject> list) {
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            items.add(Item.from(list.get(i)));
        }
        return items;
    }

    public static Item from(ParseObject itemObject) {
        String description = itemObject.getString("description");
        Number price = itemObject.getNumber("price");
        ParseFile photo = itemObject.getParseFile("photo");
        YardSale yardsale = (YardSale) itemObject.getParseObject("yardsale_id");
        return new Item(description, price, photo, yardsale);
    }


}

