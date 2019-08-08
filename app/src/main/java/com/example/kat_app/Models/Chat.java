package com.example.kat_app.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Chat")
public class Chat extends ParseObject {

    public static final String KEY_USERS = "users";
    public static final String KEY_LAST_MESSAGE_BODY = "last_message_body";
    public static final String KEY_LAST_MESSAGE_TIME = "last_message_time";
    public static final String KEY_TYPE = "type";
    public static final String KEY_MESSAGE_COUNT = "messageCount";
    private static final String KEY_MESSAGE_POINTER = "lastMessagePointer";

    private ParseUser otherUser;
    private int x;

    public List<String> getUsers() {
        return getList(KEY_USERS);
    }

    public void setType(String type) {
        put(KEY_TYPE, type);
    }

    public int getMessageCount() { return getInt(KEY_MESSAGE_COUNT); }

    public void increaseMessageCount() { put(KEY_MESSAGE_COUNT, getMessageCount() + 1); }

    public void setFirstMessageCount() { put(KEY_MESSAGE_COUNT, 1); }

    public ParseObject getLastMessagePointer() { return getParseObject(KEY_MESSAGE_POINTER); }

    public void setLastMessagePointer(Message message) { put(KEY_MESSAGE_POINTER, message); }

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


