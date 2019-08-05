package com.example.kat_app.Models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

@ParseClassName("Followers")
public class Followers extends ParseObject implements Parcelable {
    //fields
    private final static String KEY_USER = "user";
    private final static String KEY_FOLLOWERS = "followers";


    //setters and getters for Parse project object

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public JSONArray getFollowers() {
        return getJSONArray(KEY_FOLLOWERS);
    }

    public void setFollowers(JSONArray followers) {
        put(KEY_FOLLOWERS, followers);
    }


    public Followers() {
    }

}
