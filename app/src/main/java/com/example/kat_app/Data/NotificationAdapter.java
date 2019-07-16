package com.example.kat_app.Data;

import android.content.Context;

import com.example.kat_app.Model.Notification;

import java.util.List;

public class NotificationAdapter {

    private List<Notification> notifications;
    Context context;

    public NotificationAdapter(List<Notification> posts, Context context) {
        this.notifications = posts;
        this.context = context;
    }



}
