package com.example.kat_app.Models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;


@ParseClassName("Project")
public class Project extends ParseObject implements Parcelable {
    //fields
    private final static String KEY_DESCRIPTION = "description";
    private final static String KEY_NAME = "name";
    private final static String KEY_IMAGE = "image";
    private final static String KEY_USER = "author";
    private final static String KEY_FOLLOWERS = "followers";
    private final static String KEY_CATEGORY = "category";
    private final static String KEY_INVESTORS = "investors";
    private final static String KEY_MEDIA = "media";

    //setters and getters for Parse project object

    public String getName(){
        return getString(KEY_NAME);
    }

    public void setName(String name){
        put(KEY_NAME, name);
    }

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public void setCategory(String category){
        put(KEY_CATEGORY, category);
    }

    public String getCategory(){
        return getString(KEY_CATEGORY);
    }

    public JSONArray getFollowers(){
        return getJSONArray(KEY_FOLLOWERS);
    }

    public JSONArray getMedia() {
        return getJSONArray("media");
    }

    public void setMedia(ParseFile file) {
        add(KEY_MEDIA, file);
    }

    public JSONArray getInvestors(){
        return getJSONArray(KEY_INVESTORS);
    }


    public static class Query extends ParseQuery<Project> {
        public Query(){
            super(Project.class);
        }

        public Query getTop(int add){
            setLimit(20 + add);
            return this;
        }

        public Query withUser(){
            include("user");
            return this;
        }

    }


    public Project(){}




}
