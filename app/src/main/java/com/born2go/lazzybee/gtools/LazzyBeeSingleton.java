package com.born2go.lazzybee.gtools;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.db.DatabaseUpgrade;
import com.born2go.lazzybee.db.api.ConnectGdatabase;
import com.born2go.lazzybee.db.impl.LearnApiImplements;

import java.util.Locale;

/**
 * Created by Hue on 9/15/2015.
 */
public class LazzyBeeSingleton {
    private static LazzyBeeSingleton instance;

    public static LearnApiImplements learnApiImplements;
    public static DataBaseHelper dataBaseHelper;
    public static DatabaseUpgrade databaseUpgrade;
    public static TextToSpeech textToSpeech;
    public static ConnectGdatabase connectGdatabase;
    public LazzyBeeSingleton(Context context) {
        dataBaseHelper = new DataBaseHelper(context);
        databaseUpgrade = new DatabaseUpgrade(context);
        learnApiImplements = new LearnApiImplements(context);
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
        connectGdatabase = new ConnectGdatabase();
    }

    public static void initInstance(Context context) {
        if (instance == null) {
            // Create the instance
            instance = new LazzyBeeSingleton(context);
        }
    }

    public static LazzyBeeSingleton getInstance() {
        // Return the instance
        return instance;
    }
}
