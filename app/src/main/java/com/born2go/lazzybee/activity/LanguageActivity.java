package com.born2go.lazzybee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LanguageActivity extends AppCompatActivity {

    @BindView(R.id.btnEn)
    TextView btnEn;
    @BindView(R.id.btnVn)
    TextView btnVn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        ButterKnife.bind(this);

        btnEn.setOnClickListener(onClick -> onSeletedEngLishLanguage());
        btnVn.setOnClickListener(onClick -> onSeletedVietNameLanguage());

    }

    private void onSeletedVietNameLanguage() {
        changeLanguage(LazzyBeeShare.LANG_VI);
        restartApplication();
    }

    private void onSeletedEngLishLanguage() {
        changeLanguage(LazzyBeeShare.LANG_EN);
        restartApplication();
    }

    private void restartApplication() {
        finish();
        Intent intent = LanguageActivity.this.getPackageManager()
                .getLaunchIntentForPackage(LanguageActivity.this.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void changeLanguage(String languageToLoad) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(LanguageActivity.this);
        firebaseAnalytics.setUserProperty("Selected_language", languageToLoad);
        // Do something with the selection
        LazzyBeeSingleton.getLearnApiImplements()._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_LANGUAGE, languageToLoad);
    }

}
