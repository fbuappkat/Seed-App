package com.example.kat_app.Data;

import android.app.Application;

import com.example.kat_app.Models.Update;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Update.class);

        //initilaize parse
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("kat-app")
                .clientKey("kat-app")
                .server("http://kat-app.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);
    }
}
