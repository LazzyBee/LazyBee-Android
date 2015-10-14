package com.born2go.lazzybee.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.google.android.gms.tagmanager.DataLayer;

public class AboutActivity extends AppCompatActivity {

    private static final Object GA_SCREEN = "aAboutScreen";
    private static final String TAG = "AboutActivity";
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.context=this;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void _trackerApplication() {
        try {
            DataLayer mDataLayer = LazzyBeeSingleton.mDataLayer;
            mDataLayer.pushEvent("openScreen", DataLayer.mapOf("screenName", GA_SCREEN));
        } catch (Exception e) {
            Toast.makeText(context, getString(R.string.an_error_occurred), Toast.LENGTH_SHORT).show();
            Log.e(TAG, context.getString(R.string.an_error_occurred) + ":" + e.getMessage());
        }
    }
}
