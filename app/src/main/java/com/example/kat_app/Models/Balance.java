package com.example.kat_app.Models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;


@ParseClassName("Balance")
public class Balance extends ParseObject implements Parcelable {

    //fields
    private final static String KEY_BALANCE = "amount";

    public Balance() {

    }

    //setters and getters for Parse project object

    public double getBalanceAmount(){
        return getDouble(KEY_BALANCE);
    }

    public void setBalance(String name){
        put(KEY_BALANCE, name);
    }



}
