package com.example.kat_app.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/* FBU 2019
   Update defines the elements of an update about a project and getters and setters for each.
 */
@ParseClassName("Update")
public class Update extends ParseObject {
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_NUM_LIKED_BY = "likedBy";

    public JSONArray userLikes() {
        return getJSONArray(KEY_NUM_LIKED_BY);
    }

    public void likePost(ParseUser u) {
        add(KEY_NUM_LIKED_BY, u);
    }

    public void unlikePost(ParseUser currentUser) {
        ArrayList<ParseUser> users = new ArrayList<>();
        users.add(currentUser);
        removeAll("likedBy", users);
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

    public String getCaption() {
        return getString(KEY_CAPTION);
    }

    public void setCaption(String caption) {
        put(KEY_CAPTION, caption);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

}