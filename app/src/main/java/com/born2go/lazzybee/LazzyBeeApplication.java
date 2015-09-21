package com.born2go.lazzybee;

import android.app.Application;

import com.born2go.lazzybee.gtools.LazzyBeeSingleton;

/**
 * Created by Hue on 9/4/2015.
 */
public class LazzyBeeApplication extends Application {

    public LazzyBeeApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize the initLazzyBeeSingleton
        initLazzyBeeSingleton();
    }
    
    protected void initLazzyBeeSingleton() {
        // Initialize the instance of TextToSpeechSingleton
        LazzyBeeSingleton.initInstance(getApplicationContext());
    }

}
