package com.example.kat_app;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/* FBU 2019
   Update defines the elements of an update about a project and getters and setters for each.
 */
@ParseClassName("Post")
public class Update extends ParseObject {
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_USER = "user";

    public String getCaption() {
        return getString(KEY_CAPTION);
    }

    public void setCaption(String caption) {
        put(KEY_CAPTION, caption);
    }

    public String getUser() {
        return getString(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

}
