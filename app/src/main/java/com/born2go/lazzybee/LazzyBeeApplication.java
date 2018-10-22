package com.born2go.lazzybee;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Hue on 9/4/2015.
 */
public class LazzyBeeApplication extends MultiDexApplication {
    private static LazzyBeeApplication mSelf;
    private Gson mGSon;

    public LazzyBeeApplication() {
        super();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize the initLazzyBeeSingleton
        mSelf = this;
        mGSon = new Gson();
//        final Fabric fabric = new Fabric.Builder(this)
//                .kits(new Crashlytics())
//                .debuggable(true)  // Enables Crashlytics debugger
//                .build();
//        Fabric.with(fabric);
        Fabric.with(getApplicationContext(), new Crashlytics());
        initLazzyBeeSingleton();
    }

    protected void initLazzyBeeSingleton() {
        // Initialize the instance of TextToSpeechSingleton
        LazzyBeeSingleton.initInstance(getApplicationContext());
    }

    public static LazzyBeeApplication self() {
        return mSelf;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public Gson getGSon() {
        return mGSon;
    }

}
