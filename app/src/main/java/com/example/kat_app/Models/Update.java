package com.example.kat_app.Models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* FBU 2019
   Update defines the elements of an update about a project and getters and setters for each.
 */
@ParseClassName("Update")
public class Update extends ParseObject {
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_NUM_LIKED_BY = "likedBy";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_PROJECT = "project";
    public static final String KEY_PROJECT_POINTER = "projectPointer";
    public final static String KEY_MEDIA = "media";
    public final static String KEY_TYPE = "type";


    public JSONArray userLikes() {
        return getJSONArray(KEY_NUM_LIKED_BY);
    }

    public List<Comment> getComments() {
        return getList(KEY_COMMENTS);
    }

    public JSONArray userComments() {
        return getJSONArray(KEY_COMMENTS);
    }

    public void likePost(ParseUser u) { add(KEY_NUM_LIKED_BY, u); }

    public Project getProject() { return (Project) getParseObject(KEY_PROJECT); }

    public void setProject(Project project) { put(KEY_PROJECT, project); }

    public ParseObject getProjectPointer() { return getParseObject(KEY_PROJECT_POINTER); }

    public void setProjectPointer(String pointer) { put(KEY_PROJECT_POINTER, pointer); }

    public JSONArray getMedia() {
        return getJSONArray("media");
    }

    public void setMedia(ParseFile file) {
        add(KEY_MEDIA, file);
    }

    public String getType() {
        return getString(KEY_TYPE);
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

    public int getNumComments() {
        if (userComments() == null) return 0;
        return userComments().length();
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


    public void addComment(Comment comment) {
        add(KEY_COMMENTS, comment);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

    public static class Query extends ParseQuery<Update> {
        public Query() {
            super(Update.class);
        }

        // Get the first 20 posts
        public Query getTop() {
            setLimit(20);

            // Chronological feed
            orderByDescending(KEY_CREATED_AT);
            return this;
        }

        // Get post that is older than the maxDate.
        public Query getNext(Date maxDate) {
            whereLessThan(KEY_CREATED_AT, maxDate);
            return this;
        }
    }

}
