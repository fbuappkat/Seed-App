package com.example.kat_app.Models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

@ParseClassName("Equity")
public class Equity extends ParseObject implements Parcelable {

    //fields
    private final static String KEY_PROJECT = "project";
    private final static String KEY_USER = "user";
    private final static String KEY_EQUITY = "equity";
    private final static String KEY_NUM_INVESTMENTS = "numInvestments";

    //setters and getters for Parse project object

    public Integer getNumInvestments(){
        return getInt(KEY_NUM_INVESTMENTS);
    }

    public void setNumInvestments(Integer numInvestments){
        put(KEY_NUM_INVESTMENTS, numInvestments);
    }

    public Double getEquity(){
        return getDouble(KEY_EQUITY);
    }

    public void setEquity(Double equity){
        put(KEY_EQUITY, equity);
    }

    public ParseObject getProject(){
        return getParseObject(KEY_PROJECT);
    }

    public void setKeyProject(ParseObject project){
        put(KEY_PROJECT, project);
    }

    public ParseUser getUser(){ return getParseUser(KEY_USER); }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

}
