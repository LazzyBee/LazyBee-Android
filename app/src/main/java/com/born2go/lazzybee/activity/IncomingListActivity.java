package com.born2go.lazzybee.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.RecyclerViewIncomingListAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.DataLayer;

import java.util.List;

public class IncomingListActivity extends AppCompatActivity {

    private static final Object GA_SCREEN = "aIncomingListScreen";
    private static final String TAG = "IncomingList";
    private Context context;
    RecyclerViewIncomingListAdapter incomingListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_list);
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
            incomingListAdapter =
                    new RecyclerViewIncomingListAdapter
                            (context, mRecyclerViewReviewTodayList, vocabularies, lbCountReviewCard);

            //Set data and add Touch Listener
            mRecyclerViewReviewTodayList.setLayoutManager(gridLayoutManager);
            mRecyclerViewReviewTodayList.setAdapter(incomingListAdapter);

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
                        RecyclerViewIncomingListAdapter fillUpnewincomingAdapter =
                                new RecyclerViewIncomingListAdapter(context, mRecyclerViewReviewTodayList, fillUpCards, lbCountReviewCard);
                        mRecyclerViewReviewTodayList.setAdapter(fillUpnewincomingAdapter);
                    }

                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "onCreate", e);
        }

        _initAdView();

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

    private void _trackerApplication() {
        try {
            DataLayer mDataLayer = LazzyBeeSingleton.mDataLayer;
            mDataLayer.pushEvent("openScreen", DataLayer.mapOf("screenName", GA_SCREEN));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_trackerApplication", e);
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

    private void _initAdView() {
        try {
            View mViewAdv =  findViewById(R.id.mViewAdv);

            if (LazzyBeeSingleton.getContainerHolder().getContainer() == null) {
                Log.d(TAG, "Refesh container holder");
                LazzyBeeSingleton.getContainerHolder().refresh();
            }

            //get value form task manager
            Container container = LazzyBeeSingleton.getContainerHolder().getContainer();
            String admob_pub_id = null;
            String adv_id = null;
            if (container == null) {
            } else {
                admob_pub_id = container.getString(LazzyBeeShare.ADMOB_PUB_ID);
                adv_id = container.getString(LazzyBeeShare.ADV_INCOMING_LIST_ID);
                Log.i(TAG, "admob -admob_pub_id:" + admob_pub_id);
                Log.i(TAG, "admob -adv_id:" + adv_id);
            }
            if (admob_pub_id != null) {
                if (adv_id == null || adv_id.equals(LazzyBeeShare.EMPTY)) {
                    mViewAdv.setVisibility(View.GONE);
                } else if (adv_id != null || adv_id.length() > 1 || !adv_id.equals(LazzyBeeShare.EMPTY) || !adv_id.isEmpty()) {
                    String advId = admob_pub_id + "/" + adv_id;
                    Log.i(TAG, "admob -AdUnitId:" + advId);
                    AdView mAdView = new AdView(this);

                    mAdView.setAdSize(AdSize.BANNER);

                    mAdView.setAdUnitId(advId);

                    AdRequest adRequest = new AdRequest.Builder()
                            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                            .addTestDevice(getResources().getStringArray(R.array.devices)[0])
                            .addTestDevice(getResources().getStringArray(R.array.devices)[1])
                            .addTestDevice(getResources().getStringArray(R.array.devices)[2])
                            .addTestDevice(getResources().getStringArray(R.array.devices)[3])
                            .build();

                    mAdView.loadAd(adRequest);

                    RelativeLayout relativeLayout = ((RelativeLayout) mViewAdv.findViewById(R.id.adView));
                    RelativeLayout.LayoutParams adViewCenter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    adViewCenter.addRule(RelativeLayout.CENTER_IN_PARENT);
                    relativeLayout.addView(mAdView, adViewCenter);

                    mViewAdv.setVisibility(View.VISIBLE);
                } else {
                    mViewAdv.setVisibility(View.GONE);
                }
            } else {
                mViewAdv.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_initAdView", e);
        }
    }

}
