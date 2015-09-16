package com.born2go.lazzybee.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.RecyclerViewSettingListAdapter;

import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    Context context;
    RecyclerView mRecyclerViewSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        this.context = this;
        initSettingView();
    }

    private void initSettingView() {

        mRecyclerViewSettings = (RecyclerView) findViewById(R.id.mRecyclerViewSettings);

        /*context.getString(R.string.setting_learn_title),*//*Title*//*
                context.getString(R.string.setting_today_new_card_limit),
                context.getString(R.string.setting_today_review_card_limit),
                context.getString(R.string.setting_total_learn_per_day),
                context.getString(R.string.setting_lines),
                context.getString(R.string.setting_notification),
                context.getString(R.string.setting_lines),
                context.getString(R.string.setting_update_title),
                context.getString(R.string.setting_check_update),
                context.getString(R.string.setting_auto_check_update),
                context.getString(R.string.setting_lines),
                context.getString(R.string.setting_debug_info),
                context.getString(R.string.setting_lines),
                context.getString(R.string.setting_language),
                context.getString(R.string.setting_lines)*/
        final List<String> settings = Arrays.asList(context.getResources().getStringArray(R.array.settings));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        RecyclerViewSettingListAdapter recyclerViewSettingListAdapter = new RecyclerViewSettingListAdapter(mRecyclerViewSettings.getContext(), settings,mRecyclerViewSettings);

        mRecyclerViewSettings.setLayoutManager(gridLayoutManager);
        mRecyclerViewSettings.setAdapter(recyclerViewSettingListAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
