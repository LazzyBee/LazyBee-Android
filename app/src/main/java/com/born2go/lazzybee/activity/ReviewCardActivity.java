package com.born2go.lazzybee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.RecyclerViewReviewTodayListAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.android.gms.tagmanager.DataLayer;

import java.util.List;

public class ReviewCardActivity extends AppCompatActivity {

    private static final String TAG = "ReviewCardActivity";
    private static final Object GA_SCREEN = "aReviewCardScreen";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_card);
        this.context = this;
        //init DB SQLIte
        final LearnApiImplements dataBaseHelper = LazzyBeeSingleton.learnApiImplements;

        //Init RecyclerView and Layout Manager
        final RecyclerView mRecyclerViewReviewTodayList = (RecyclerView) findViewById(R.id.mRecyclerViewReviewTodayList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerViewReviewTodayList.getContext(), 1);
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        final TextView lbCountReviewCard = (TextView) findViewById(R.id.lbCountReviewCard);
        try {
            //get review List Card today
            final List<Card> vocabularies = dataBaseHelper._getIncomingListCard();

            lbCountReviewCard.setText(getString(R.string.message_total_card_incoming) + vocabularies.size());
            lbCountReviewCard.setTag(vocabularies.size());
            //Init Adapter
            RecyclerViewReviewTodayListAdapter recyclerViewReviewTodayListAdapter =
                    new RecyclerViewReviewTodayListAdapter
                            (context, mRecyclerViewReviewTodayList, vocabularies, lbCountReviewCard);

            //Set data and add Touch Listener
            mRecyclerViewReviewTodayList.setLayoutManager(gridLayoutManager);
            mRecyclerViewReviewTodayList.setAdapter(recyclerViewReviewTodayListAdapter);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Refresh items
                    int count = (int) lbCountReviewCard.getTag();
                    if (count < 100) {
                        //fill up incoming list
                        int myLevel = dataBaseHelper.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_MY_LEVEL);
                        dataBaseHelper._initIncomingCardIdList();
                        //get new IncomingList
                        List<Card> fillUpCards = dataBaseHelper._getIncomingListCard();

                        //set count
                        lbCountReviewCard.setText(getString(R.string.message_total_card_incoming) + fillUpCards.size());
                        lbCountReviewCard.setTag(fillUpCards.size());

                        //Reset adapter incoming list
                        RecyclerViewReviewTodayListAdapter fillUpnewincomingAdapter =
                                new RecyclerViewReviewTodayListAdapter(context, mRecyclerViewReviewTodayList, fillUpCards, lbCountReviewCard);
                        mRecyclerViewReviewTodayList.setAdapter(fillUpnewincomingAdapter);
                    }

                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
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
