package com.example.diego.spaservice;

import android.app.Application;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class SpaServiceApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
