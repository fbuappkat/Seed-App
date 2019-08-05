package com.example.kat_app.Data;

import android.app.Application;

import com.example.kat_app.Models.Balance;
import com.example.kat_app.Models.Chat;
import com.example.kat_app.Models.Comment;
import com.example.kat_app.Models.Equity;
import com.example.kat_app.Models.Followers;
import com.example.kat_app.Models.Message;
import com.example.kat_app.Models.Update;
import com.example.kat_app.Models.Project;
import com.example.kat_app.Models.Request;
import com.example.kat_app.Models.Transaction;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Update.class);
        ParseObject.registerSubclass(Project.class);
        ParseObject.registerSubclass(Request.class);
        ParseObject.registerSubclass(Transaction.class);
        ParseObject.registerSubclass(Balance.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Chat.class);
        ParseObject.registerSubclass(Equity.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Followers.class);

        // Use for monitoring Parse network traffic
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        //initilaize parse
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("kat-app")
                .clientKey("kat-app")
                .server("http://kat-app.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);

        ParseFacebookUtils.initialize(this);
    }
}
