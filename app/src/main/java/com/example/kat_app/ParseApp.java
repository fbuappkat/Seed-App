package com.example.kat_app;

import android.app.Application;

import com.parse.Parse;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        //Todo - register Post subclass once made in Parse
        //ParseObject.registerSubclass(Post.class);


        //initilaize parse
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("kat-app")
                .clientKey("kat-app")
                .server("http://kat-app.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);
    }
}
