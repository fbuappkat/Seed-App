package com.example.kat_app.Models;
import android.media.audiofx.DynamicsProcessing;
import android.os.Parcelable;
import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@ParseClassName("Transaction")
public class Transaction extends ParseObject implements Parcelable {
    //fields
    private final static String KEY_AMOUNT = "amount";
    private final static String KEY_SENDER = "sender";
    private final static String KEY_RECEIVER = "receiver";
    private final static String KEY_REQUEST = "request";
    private final static String KEY_PROJECT = "project";
    private final static String KEY_TYPE = "type";
    private final static String KEY_EQUITY = "equity";
    private static final String KEY_CREATED_AT = "createdAt";



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

    public ParseUser getReceiver(){
        return getParseUser(KEY_RECEIVER);
    }

    public void setReceiver(ParseUser sender){
        put(KEY_RECEIVER, sender);
    }

    public Equity getEquity(){
        return (Equity) get(KEY_EQUITY);
    }

    public void setEquity(Equity equity){
        put(KEY_EQUITY, equity);
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

    public void setType(String type) {
        put(KEY_TYPE, type);
    }

    public String getType() {
        return getString(KEY_TYPE);
    }

    public String getRelativeTimeAgo() {
        String twitterFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(sf.format(this.getCreatedAt())).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate.toUpperCase();
    }


    public Transaction(){}


    public static class Query extends ParseQuery<Transaction> {
        public Query() {
            super(Transaction.class);
        }

        // Get the first 20 posts
        public Query getTop() {
            setLimit(20);

            // Chronological feed
            orderByDescending(KEY_CREATED_AT);
            return this;
        }

        // Include current User in the Query
        public Query withCurrUser(ParseUser currUser) {
            whereEqualTo("sender", currUser);
            return this;
        }

        // Get post that is older than the maxDate.
        public Query getNext(Date maxDate) {
            whereLessThan(KEY_CREATED_AT, maxDate);
            return this;
        }
    }

}