package com.sketch.papertracingart;

import android.app.Application;
import android.media.audiofx.EnvironmentalReverb;

public class MyApp extends Application {

    public static MyApp myApp;

    public static final int DB_Version= 1;

    public static String DB_NAme= "image_database";

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;


    }


}
