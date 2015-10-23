package com.born2go.lazzybee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.fragment.FragmentReviewToday;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.android.gms.tagmanager.DataLayer;

public class ReviewCardActivity extends AppCompatActivity implements FragmentReviewToday.FragmentReviewTodayListener {

    private static final String TAG = "ReviewCardActivity";
    private static final Object GA_SCREEN = "aReviewCardScreen";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_card);
        this.context = this;
        _trackerApplication();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_review_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void gotoCardDetails(String cardId) {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        intent.putExtra(LazzyBeeShare.CARDID, cardId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, getResources().getInteger(R.integer.code_card_details_updated));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Updated Card reload activity
        if (resultCode == getResources().getInteger(R.integer.code_card_details_updated)) {
            //reload activity
            finish();
            startActivity(getIntent());
        }
    }

    private void _trackerApplication() {
        try {
            DataLayer mDataLayer = LazzyBeeSingleton.mDataLayer;
            mDataLayer.pushEvent("openScreen", DataLayer.mapOf("screenName", GA_SCREEN));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LazzyBeeShare._cancelNotification(context);
    }
    @Override
    protected void onPause() {
        super.onPause();
        int hour = LazzyBeeSingleton.learnApiImplements.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_HOUR_NOTIFICATION);
        int minute = LazzyBeeSingleton.learnApiImplements.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_MINUTE_NOTIFICATION);
        LazzyBeeShare._setUpNotification(context, hour, minute);
    }
}
