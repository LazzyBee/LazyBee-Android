package com.born2go.lazzybee.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;


public class AboutActivity extends AppCompatActivity {

    private static final Object GA_SCREEN = "aAboutScreen";
    private static final String TAG = "AboutActivity";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        WebView mWebViewHelp = (WebView) findViewById(R.id.mWebViewHelp);
        mWebViewHelp.loadUrl(LazzyBeeShare.ASSETS + "lazzybee_guide.htm");
        this.context = this;
        _trackerApplication();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }


    private void _trackerApplication() {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("screenName", (String) GA_SCREEN);
            LazzyBeeSingleton.getFirebaseAnalytics().logEvent("screenName",bundle);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_trackerApplication", e);
        }
    }
}
