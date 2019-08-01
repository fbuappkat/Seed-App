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
    private final static String KEY_INVESTOR = "investor";
    private final static String KEY_EQUITY = "equity";
    private final static String KEY_NUM_INVESTMENTS = "numInvestments";

    //setters and getters for Parse project object

    public Integer getNumInvestments(){
        return getInt(KEY_NUM_INVESTMENTS);
    }

    public void setNumInvestments(Integer numInvestments){
        put(KEY_NUM_INVESTMENTS, numInvestments);
    }

    public Float getEquity(){
        return (float) getDouble(KEY_EQUITY);
    }

    public void setEquity(Float equity){
        put(KEY_EQUITY, equity);
    }

    public Project getProject(){
        return (Project) get(KEY_PROJECT);
    }

    public void setProject(Project project){
        put(KEY_PROJECT, project);
    }

    public ParseUser getInvestor(){ return getParseUser(KEY_INVESTOR); }

    public void setInvestor(ParseUser user){
        put(KEY_INVESTOR, user);
    }

}
