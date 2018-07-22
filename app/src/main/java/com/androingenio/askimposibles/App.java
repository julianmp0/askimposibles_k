package com.androingenio.askimposibles;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by julianmartinez on 13/10/17.
 */

public class App extends Application {


    private static FirebaseAnalytics mFirebaseAnalytics;

    public App() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }


    public static FirebaseAnalytics getmFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }
}
