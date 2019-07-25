package com.example.kat_app.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;

@ParseClassName("Message")
public class Message extends ParseObject {

    public static final String USER_ID_KEY = "userId";
    public static final String MESSAGE_SENDER_KEY = "sender";
    public static final String MESSAGE_RECEIVER_KEY = "receiver";
    public static final String BODY_KEY = "body";
    private static final String KEY_CREATED_AT = "createdAt";

    public String getUserId() {
        return getString(USER_ID_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public ParseUser getMessageSender() { return getParseUser(MESSAGE_SENDER_KEY); }

    public ParseUser getMessageReceiver() {return getParseUser(MESSAGE_RECEIVER_KEY); }

    public void setUserId(String userId) {
        put(USER_ID_KEY, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }

    public void setMessageSender(ParseUser sender) { put(MESSAGE_SENDER_KEY, sender); }

    public void setMessageReceiver(ParseUser receiver) {put(MESSAGE_RECEIVER_KEY, receiver); }

    public String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm aa");
        String formattedDate = dateFormat.format(this.getCreatedAt());

        return formattedDate.toUpperCase();
    }
}


