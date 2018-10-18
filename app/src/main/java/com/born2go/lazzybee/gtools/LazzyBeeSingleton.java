package com.born2go.lazzybee.gtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.db.DatabaseUpgrade;
import com.born2go.lazzybee.db.api.ConnectGdatabase;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Locale;

/**
 * Created by Hue on 9/15/2015.
 */


public class LazzyBeeSingleton {
    @SuppressLint("StaticFieldLeak")
    private static LazzyBeeSingleton instance;
    public static LearnApiImplements learnApiImplements;
    @SuppressLint("StaticFieldLeak")
    public static DataBaseHelper dataBaseHelper;
    @SuppressLint("StaticFieldLeak")
    public static DatabaseUpgrade databaseUpgrade;
    public static TextToSpeech textToSpeech;
    public static ConnectGdatabase connectGdatabase;
    @SuppressLint("StaticFieldLeak")
    private static FirebaseAnalytics mFirebaseAnalytics;
    @SuppressLint("StaticFieldLeak")
    private static FirebaseRemoteConfig mRemoteConfig;
    @SuppressLint("StaticFieldLeak")
    private static String amobPubId;
    @SuppressLint("StaticFieldLeak")
    private static Crashlytics mCrashlytics;
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @SuppressWarnings({"AccessStaticViaInstance", "WeakerAccess"})
    public LazzyBeeSingleton(Context context) {
        this.context = context;
        dataBaseHelper = new DataBaseHelper(context);
        databaseUpgrade = new DatabaseUpgrade(context);
        learnApiImplements = new LearnApiImplements(context);
        textToSpeech = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
            }
        });
        connectGdatabase = new ConnectGdatabase();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        mRemoteConfig.setDefaults(R.xml.remote_config_defaults);
    }

    public static void initInstance(Context context) {
        if (instance == null) {
            // Create the instance
            instance = new LazzyBeeSingleton(context);
        }
    }

    @SuppressWarnings("unused")
    public static LazzyBeeSingleton getInstance() {
        // Return the instance
        return instance;
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

    public static FirebaseRemoteConfig getFirebaseRemoteConfig() {
        return mRemoteConfig;
    }

    public static void setAmobPubId(String id) {
        amobPubId = id;
    }

    public static String getAmobPubId() {
        return amobPubId;
    }

    public static Crashlytics getCrashlytics() {
        if (mCrashlytics == null)
            mCrashlytics = Crashlytics.getInstance();
        return mCrashlytics;
    }

    public static LearnApiImplements getLearnApiImplements() {
        if (learnApiImplements == null)
            learnApiImplements = new LearnApiImplements(context);
        return learnApiImplements;
    }
}
