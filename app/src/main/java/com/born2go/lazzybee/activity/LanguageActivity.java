package com.born2go.lazzybee.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.shared.SharedPrefs;

import java.util.Locale;

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
        startIntro();
    }

    private void onSeletedEngLishLanguage() {
        changeLanguage(LazzyBeeShare.LANG_EN);
        startIntro();
    }

    private void startIntro() {
        String ADMOB_PUB_ID = LazzyBeeShare.EMPTY;
        if (getIntent() != null) {
            ADMOB_PUB_ID = getIntent().getStringExtra(LazzyBeeShare.ADMOB_PUB_ID);
        }
        finish();
        Intent intent = new Intent(this, IntroActivity.class);
        intent.putExtra(LazzyBeeShare.ADMOB_PUB_ID, ADMOB_PUB_ID);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void changeLanguage(String languageToLoad) {
        SharedPrefs.getInstance().put(LazzyBeeShare.KEY_LANGUAGE, languageToLoad);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

}
