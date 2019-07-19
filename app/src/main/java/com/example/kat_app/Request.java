package com.example.kat_app;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;


@ParseClassName("Request")
public class Request extends ParseObject implements Parcelable {
    //fields
    private final static String KEY_REQUEST = "request";
    private final static String KEY_PRICE = "price";
    private final static String KEY_PROJECT = "project";
    private final static String KEY_RECEIVED = "received"
    ;


    //setters and getters for Parse project object

    public Float getPrice(){
        return getNumber(KEY_PRICE).floatValue();
    }

    public Float getReceived(){
        return getNumber(KEY_RECEIVED).floatValue();
    }

    public void setPrice(Float price){
        put(KEY_PRICE, price);
    }

    public String getRequest(){
        return getString(KEY_REQUEST);
    }

    public void setRequest(String request){
        put(KEY_REQUEST, request);
    }

    public Project getProject(){
        return (Project) getParseObject(KEY_PROJECT);
    }

    public void setProject(Project proj){
        put(KEY_PROJECT, proj);
    }

    public Request(){}






}
