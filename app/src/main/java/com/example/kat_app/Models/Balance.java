package com.example.kat_app.Models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("Balance")
public class Balance extends ParseObject implements Parcelable {
    //fields
    private final static String KEY_AMOUNT = "amount";
    private final static String KEY_USER = "user";


    //setters and getters for Parse project object

    public Float getAmount(){
        return getNumber(KEY_AMOUNT).floatValue();
    }

    public void setAmount(float amt){
        put(KEY_AMOUNT, amt);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }



    public Balance(){}



}