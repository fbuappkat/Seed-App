package com.example.kat_app.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_UPDATE = "update";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_LIKEDBY = "likedBy";
    public static final String KEY_CREATED_AT = "createdAt";

    public JSONArray userLikes() {
        return getJSONArray(KEY_LIKEDBY);
    }

    public void likePost(ParseUser user) { add(KEY_LIKEDBY, user); }

    public Update getUpdate() { return (Update) getParseObject(KEY_UPDATE); }

    public void setUpdate(Update update) { put(KEY_UPDATE, update); }

    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public String getComment() {
        return getString(KEY_COMMENT);
    }

    public void setComment(String comment) { put(KEY_COMMENT, comment);}


    public void unlikePost(ParseUser currentUser) {
        ArrayList<ParseUser> users = new ArrayList<>();
        users.add(currentUser);
        removeAll(KEY_LIKEDBY, users);
    }

    public int getNumLikes() {
        if (userLikes() == null) return 0;
        return userLikes().length();
    }

    public boolean isLiked() {
        JSONArray a = userLikes();
        if (a != null) {
            for (int i = 0; i < a.length(); i++) {
                try {
                    a.get(i).toString();
                    if (a.getJSONObject(i).getString("objectId").equals(ParseUser.getCurrentUser().getObjectId())) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static class Query extends ParseQuery<Comment> {
        public Query() {
            super(Comment.class);
        }

        // Get the first 20 posts
        public Query getTop() {
            setLimit(20);

            // Chronological feed
            orderByDescending(KEY_CREATED_AT);
            return this;
        }

        // Include update in the Query
        public Query withUpdate() {
            include("update");
            return this;
        }

        // Include user in the Query
        public Query withUser() {
            include("user");
            return this;
        }

        // Get post that is older than the maxDate.
        public Query getNext(Date maxDate) {
            whereLessThan(KEY_CREATED_AT, maxDate);
            return this;
        }
    }

}
