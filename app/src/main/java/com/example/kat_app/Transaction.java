package com.example.kat_app;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("Transaction")
public class Transaction extends ParseObject implements Parcelable {
    //fields
    private final static String KEY_AMOUNT = "amount";
    private final static String KEY_SENDER = "sender";
    private final static String KEY_REQUEST = "request";
    private final static String KEY_PROJECT = "project";



    //setters and getters for Parse project object

    public Float getAmount(){
        return getNumber(KEY_AMOUNT).floatValue();
    }

    public void setAmount(float amt){
        put(KEY_AMOUNT, amt);
    }

    public ParseUser getSender(){
        return getParseUser(KEY_SENDER);
    }

    public void setSender(ParseUser sender){
        put(KEY_SENDER, sender);
    }

    public Request getRequest(){
        return (Request) getParseObject(KEY_REQUEST);
    }

    public void setRequest(Request request){
        put(KEY_REQUEST, request);
    }

    public Project getProject(){
        return (Project) getParseObject(KEY_PROJECT);
    }

    public void setProject(Project project){
        put(KEY_PROJECT, project);
    }



    public Transaction(){}



}