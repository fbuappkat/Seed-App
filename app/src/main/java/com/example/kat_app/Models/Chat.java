package com.example.kat_app.Models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Chat")
public class Chat extends ParseObject {

    public static final String KEY_USERS = "users";
    public static final String KEY_LAST_MESSAGE_BODY = "last_message_body";
    public static final String KEY_LAST_MESSAGE_TIME = "last_message_time";
    public static final String KEY_TYPE = "type";

    private ParseUser otherUser;
    private int x;

    public List<String> getUsers() {
        return getList(KEY_USERS);
    }

    public void setType(String type) {
        put(KEY_TYPE, type);
    }

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setUsers(List<String> users) {
        put(KEY_USERS, users);
    }

    public List<String> getOtherUsers(ParseUser currUser) {
        List<String> otherUsers = getUsers();
        otherUsers.remove(currUser.getObjectId());
        return otherUsers;
    }

    public void setLastMessageBody(String body) {
        put(KEY_LAST_MESSAGE_BODY, body);
    }

    public void setLastMessageTime(String time) {
        put(KEY_LAST_MESSAGE_TIME, time);
    }

    public String getLastMessageBody() {
        return getString(KEY_LAST_MESSAGE_BODY);
    }

    public String getLastMessageTime() {
        return getString(KEY_LAST_MESSAGE_TIME);
    }

}


