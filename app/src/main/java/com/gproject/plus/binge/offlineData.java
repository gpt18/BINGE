package com.gproject.plus.binge;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class offlineData extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
