package com.born2go.lazzybee;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
