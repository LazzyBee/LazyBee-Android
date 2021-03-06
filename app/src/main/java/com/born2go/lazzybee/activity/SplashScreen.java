package com.born2go.lazzybee.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.db.DatabaseUpgrade;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.shared.SharedPrefs;
import com.google.android.gms.ads.MobileAds;
import com.tbruyelle.rxpermissions2.RxPermissions;


import java.util.Locale;


public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 300;
    private static int GTM_TIME_OUT = 2000;
    DataBaseHelper myDbHelper;
    LearnApiImplements learnApiImplements;
    private SplashScreen thiz;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        thiz = this;
        RxPermissions rxPermissions = new RxPermissions(SplashScreen.this);
        rxPermissions.requestEach(PERMISSIONS_STORAGE).subscribe(permission -> {
            if (permission.granted) {
                Log.d(TAG, "Start Fetch");
                LazzyBeeSingleton.getFirebaseRemoteConfig().fetch(LazzyBeeShare.CACHE_EXPIRATION)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // After config data is successfully fetched, it must be activated before newly fetched
                                // values are returned.
                                LazzyBeeSingleton.getFirebaseRemoteConfig().activateFetched();
                                Log.d(TAG, "Fetch Successful");
                            } else {
                                Log.d(TAG, "Fetch Failed");
                            }
                            _initSQlIte();
                            _updateVersionDB();
                            _changeLanguage();

                            String ADMOB_PUB_ID = LazzyBeeSingleton.getFirebaseRemoteConfig().getString(LazzyBeeShare.ADMOB_PUB_ID);
                            initAppWithAmodPubId(ADMOB_PUB_ID);
                        });
            } else if (permission.shouldShowRequestPermissionRationale) {
                finish();
                System.exit(0);
            } else {
                Log.d(TAG, "");
            }
        });

    }

    private void initAppWithAmodPubId(String ADMOB_PUB_ID) {
        Log.d(TAG, "ADMOB_PUB_ID:" + ADMOB_PUB_ID);
        LazzyBeeSingleton.setAmobPubId(ADMOB_PUB_ID);
        MobileAds.initialize(thiz, ADMOB_PUB_ID);
        startMainActivity(ADMOB_PUB_ID);
    }

    private void startMainActivity(String ADMOB_PUB_ID) {
        boolean select_display_language = SharedPrefs.getInstance().get("SELECT_DISPLAY_LANGUAGE", Boolean.class, false);
        Intent i;
        if (select_display_language) {
            boolean display_intro = SharedPrefs.getInstance().get("DISPLAY_INTRO", Boolean.class, false);
            if (display_intro) {
                i = new Intent(SplashScreen.this, MainActivity.class);
            } else {
                SharedPrefs.getInstance().put("DISPLAY_INTRO", true);
                i = new Intent(SplashScreen.this, IntroActivity.class);
            }
        } else {
            SharedPrefs.getInstance().put("SELECT_DISPLAY_LANGUAGE", true);
            i = new Intent(SplashScreen.this, LanguageActivity.class);
        }
        i.putExtra(LazzyBeeShare.ADMOB_PUB_ID, ADMOB_PUB_ID);
        startActivity(i);
        this.finish();
    }

    /**
     * Looks up the externalized string resource and displays it in a pop-up dialog box.
     *
     * @param stringKey
     */
    private void displayErrorToUser(int stringKey) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(getResources().getString(stringKey));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                "OK", (dialog, which) -> {
                });
        alertDialog.show();
    }


    private void _initSQlIte() {
        Log.i(TAG, "Init SQlIte");
        myDbHelper = LazzyBeeSingleton.getDataBaseHelper();
        try {
            myDbHelper._createDataBase();
        } catch (Exception ioe) {
            //throw new Error("Unable to create database");
            //ioe.printStackTrace();
            Log.e(TAG, "Unable to create database:" + ioe.getMessage());
            //noinspection AccessStaticViaInstance
            LazzyBeeSingleton.getCrashlytics().logException(ioe);

        }

        learnApiImplements = LazzyBeeSingleton.learnApiImplements;
        if (!learnApiImplements.checkTableExist(LearnApiImplements.TABLE_STREAK)) {
            Log.d(TAG, "CREATE_TABLE_STREAK" + (learnApiImplements.executeQuery(LearnApiImplements.CREATE_TABLE_STREAK) == 1 ? " OK" : " Fails"));
        }
        if (!learnApiImplements.checkTableExist(LearnApiImplements.TABLE_SUGGESTION)) {
            Log.d(TAG, "CREATE_TABLE_SUGGESTION" + (learnApiImplements.executeQuery(LearnApiImplements.CREATE_TABLE_SUGGESTION) == 1 ? " OK" : " Fails"));
        }

    }

    private void _updateVersionDB() {
        LearnApiImplements learnApiImplements = LazzyBeeSingleton.learnApiImplements;
        //learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.DB_VERSION, String.valueOf(0));
//        //get GAE_DB_VERSION in Server
        String gae_db_version = "6";//LazzyBeeSingleton.getContainerHolder().getContainer().getString(LazzyBeeShare.GAE_DB_VERSION);
        Log.i(TAG, "Get gae_db_version on TaskManager =" + gae_db_version);
//        //put GAE_DB_VERSION to Client
        learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.GAE_DB_VERSION, gae_db_version.isEmpty() ? String.valueOf(0) : gae_db_version);

        //get version in DB
        int _dbVesion = LazzyBeeShare.DEFAULT_VERSION_DB;
        int _gdbVesion;
        String db_v = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.DB_VERSION);


        if (db_v != null) {
            _dbVesion = Integer.valueOf(db_v);
        } else {
            learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.DB_VERSION, String.valueOf(LazzyBeeShare.DEFAULT_VERSION_DB));
        }
        _gdbVesion = Integer.valueOf(gae_db_version);
        if (_dbVesion > _gdbVesion) {
            learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.DB_VERSION, String.valueOf(_dbVesion));
        }
        //
        if (_dbVesion == 0) {
            //Add 2 colum l_en and l_vn
            int add_colum_L_EN = learnApiImplements.executeQuery("ALTER TABLE " + LearnApiImplements.TABLE_VOCABULARY + " ADD COLUMN " + LearnApiImplements.KEY_L_EN + " TEXT;");
            int add_colum_L_VN = learnApiImplements.executeQuery("ALTER TABLE " + LearnApiImplements.TABLE_VOCABULARY + " ADD COLUMN " + LearnApiImplements.KEY_L_VN + " TEXT;");
            Log.i(TAG, " ADD COLUMN " + LearnApiImplements.KEY_L_EN + "?" + ((add_colum_L_EN == 1) ? "TRUE" : "FALSE"));
            Log.i(TAG, " ADD COLUMN " + LearnApiImplements.KEY_L_VN + "?" + ((add_colum_L_VN == 1) ? "TRUE" : "FALSE"));
        }

        SharedPreferences sharedpreferences = getSharedPreferences(LazzyBeeShare.MyPREFERENCES, Context.MODE_PRIVATE);
        boolean custom_list = sharedpreferences.getBoolean(LazzyBeeShare.KEY_CUSTOM_LIST, false);
        if (!custom_list) {
            learnApiImplements.addColumCustomList();
            sharedpreferences.edit().putBoolean(LazzyBeeShare.KEY_CUSTOM_LIST, true).apply();
        }

        try {
            learnApiImplements._get100Card();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void _changeLanguage() {
        String lang = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_LANGUAGE);
        Log.i(TAG, "Lang:" + lang);
        if (lang == null)
            lang = LazzyBeeShare.LANG_VI;
        String languageToLoad = lang; // your language

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

}
