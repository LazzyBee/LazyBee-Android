package com.born2go.lazzybee.activity;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.RecyclerViewCustomStudyAdapter;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.Arrays;
import java.util.List;

public class CustomStudySettingActivity extends AppCompatActivity {

    public static final String TAG = "CustomStudySetting";
    Context context;
    LearnApiImplements learnApiImplements;
    RecyclerView mRecyclerViewCustomStudy;
    List<String> settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_study_setting);
        context = this;

        final List<String> devices = Arrays.asList(context.getResources().getStringArray(R.array.devices_dev_id));
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (devices.contains(android_id)) {
            Log.d(TAG, "Device " + android_id + " is DEV");
            settings = Arrays.asList(context.getResources().getStringArray(R.array.custom_study_dev));
        } else {
            settings = Arrays.asList(context.getResources().getStringArray(R.array.custom_study));
        }
        learnApiImplements = LazzyBeeSingleton.learnApiImplements;


        mRecyclerViewCustomStudy = (RecyclerView) findViewById(R.id.mRecyclerViewCustomStudy);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerViewCustomStudy.getContext(), 1);

        mRecyclerViewCustomStudy.setLayoutManager(gridLayoutManager);
        setCustomStudyAdapter();

        //Show Home as Up
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_custom_study_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setCustomStudyAdapter() {
        try {
            RecyclerViewCustomStudyAdapter recyclerViewCustomStudyAdapter = new RecyclerViewCustomStudyAdapter(this.getSupportFragmentManager(),context, settings, mRecyclerViewCustomStudy);
            mRecyclerViewCustomStudy.setAdapter(recyclerViewCustomStudyAdapter);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }
}
