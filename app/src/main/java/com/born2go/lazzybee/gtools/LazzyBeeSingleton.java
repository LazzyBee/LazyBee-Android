package com.born2go.lazzybee.gtools;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.db.DatabaseUpgrade;
import com.born2go.lazzybee.db.api.ConnectGdatabase;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;

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
    public static DataLayer mDataLayer;
    private static ContainerHolder containerHolder;

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
        mDataLayer = TagManager.getInstance(context).getDataLayer();
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

    public static ContainerHolder getContainerHolder() {
        return containerHolder;
    }

    public static void setContainerHolder(ContainerHolder c) {
        containerHolder = c;
    }
}
