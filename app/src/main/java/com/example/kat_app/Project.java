package com.example.kat_app;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Project")
public class Project extends ParseObject {
    //fields
    private final static String KEY_DESCRIPTION = "description";
    private final static String KEY_IMAGE = "image";
    private final static String KEY_USER = "user";
    private final static String KEY_REQUESTED = "requested";


    //setters and getters for Parse project object

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






}
