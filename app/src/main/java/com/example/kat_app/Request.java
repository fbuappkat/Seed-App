package com.example.kat_app;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Request")
public class Request extends ParseObject {
    //fields
    private final static String KEY_REQUEST = "request";
    private final static String KEY_PRICE = "price";
    private final static String KEY_PROJECT = "project"
    ;


    //setters and getters for Parse project object

    public String getPrice(){
        return getString(KEY_PRICE);
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
    public String getProject(){
        return getString(KEY_PROJECT);
    }

    public void setProject(Project proj){
        put(KEY_PROJECT, proj);
    }



}
