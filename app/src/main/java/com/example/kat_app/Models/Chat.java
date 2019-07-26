package com.example.kat_app.Models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
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

    public List<ParseUser> getUsers() {
        return getList(KEY_USERS);
    }

    public void setType(String type) {
        put(KEY_TYPE, type);
    }

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setUsers(List<ParseUser> users) {
        put(KEY_USERS, users);
    }

    public List<ParseUser> getOtherUsers(ParseUser currUser) {
        List<ParseUser> otherUsers = getUsers();
        otherUsers.remove(currUser);
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


