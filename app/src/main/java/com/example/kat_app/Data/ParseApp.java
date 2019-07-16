package com.example.kat_app.Data;

import android.app.Application;

import com.example.kat_app.Project;
import com.example.kat_app.Request;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        //Todo - register Post subclass once made in Parse
        ParseObject.registerSubclass(Project.class);
        ParseObject.registerSubclass(Request.class);

        //initilaize parse
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("kat-app")
                .clientKey("kat-app")
                .server("http://kat-app.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);
    }
}
