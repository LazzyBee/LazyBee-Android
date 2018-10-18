package com.born2go.lazzybee.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.GetGroupVoca;
import com.born2go.lazzybee.adapter.RecyclerViewIncomingListAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.GroupVoca;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.List;

public class IncomingListActivity extends AppCompatActivity implements GetGroupVoca.IGetGroupVoca, SwipeRefreshLayout.OnRefreshListener {

    private static final Object GA_SCREEN = "aIncomingListScreen";
    private static final String TAG = "IncomingList";
    private Context context;
    private LearnApiImplements dataBaseHelper;
    private RecyclerViewIncomingListAdapter incomingListAdapter;
    private IncomingListActivity thiz;

    private RecyclerView mRecyclerViewReviewTodayList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView lbCountReviewCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_list);
        this.context = this;
        this.thiz = this;
        //init DB SQLIte

        dataBaseHelper = LazzyBeeSingleton.learnApiImplements;

        //Init RecyclerView and Layout Manager
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mRecyclerViewReviewTodayList = (RecyclerView) findViewById(R.id.mRecyclerViewReviewTodayList);
        mRecyclerViewReviewTodayList.setLayoutManager(new LinearLayoutManager(context));

        lbCountReviewCard = (TextView) findViewById(R.id.lbCountReviewCard);
        try {
            //get review List Card today
            getIncomingList();
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "onCreate", e);
        }

        _initAdView();

        _trackerApplication();
    }

    private void getIncomingList() {
        final List<Card> vocabularies = dataBaseHelper._getIncomingListCard();

        lbCountReviewCard.setText(String.valueOf(getString(R.string.message_total_card_incoming) + vocabularies.size()));
        lbCountReviewCard.setTag(vocabularies.size());
        //Init Adapter
        incomingListAdapter =
                new RecyclerViewIncomingListAdapter
                        (context, mRecyclerViewReviewTodayList, vocabularies, lbCountReviewCard);

        //Set data and add Touch Listener

        mRecyclerViewReviewTodayList.setAdapter(incomingListAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_incoming_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.input_list_work) {
            inputWordList("");
            return true;
        } else if (id == R.id.restore_list_word) {
            restoreListIncomingWord();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void inputWordList(String wordList) {
        Intent intent = new Intent(thiz, CreateWordListActivity.class);
        if (wordList != null) {
            Bundle bundle = new Bundle();
            bundle.putString(CreateWordListActivity.WORD_LIST, wordList);
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, CreateWordListActivity.REG_INPUT_WORD_LIST);
    }

    private void restoreListIncomingWord() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.msg_restore_word_list);
        builder.setMessage(R.string.msg_please_input_restore_word_list_code);
        View view = LayoutInflater.from(context).inflate(R.layout.code_restore_list_word, null);
        final EditText codeRestore = (EditText) view.findViewById(R.id.codeRestore);
        builder.setView(view);
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            hideKeyBoard();
            dialog.dismiss();
        });
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            // hideKeyBoard();
            dialog.dismiss();
            String strCode = codeRestore.getText().toString();
            if (strCode.length() > 0) {
                Long code = Long.valueOf(strCode);
                GetGroupVoca getGroupVoca = new GetGroupVoca(context);
                getGroupVoca.execute(code);
                getGroupVoca.iGetGroupVoca = thiz;
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void _trackerApplication() {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("screenName", (String) GA_SCREEN);
            LazzyBeeSingleton.getFirebaseAnalytics().logEvent("screenName", bundle);
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
            final View mViewAdv = findViewById(R.id.mViewAdv);
            //get value form remote config
            final String admob_pub_id = LazzyBeeSingleton.getAmobPubId();
            LazzyBeeSingleton.getFirebaseRemoteConfig().fetch(LazzyBeeShare.CACHE_EXPIRATION).addOnCompleteListener(this, task -> {
                String adv_id = null;
                if (task.isComplete()) {
                    adv_id = LazzyBeeSingleton.getFirebaseRemoteConfig().getString(LazzyBeeShare.ADV_BANNER_ID);
                }
                if (admob_pub_id != null) {
                    if (adv_id == null || adv_id.equals(LazzyBeeShare.EMPTY)) {
                        mViewAdv.setVisibility(View.GONE);
                    } else if (!adv_id.equals(LazzyBeeShare.EMPTY)) {
                        String advId = admob_pub_id + "/" + adv_id;
                        Log.i(TAG, "admob -AdUnitId:" + advId);
                        AdView mAdView = new AdView(context);

                        mAdView.setAdSize(AdSize.BANNER);
                        mAdView.setAdUnitId(advId);

                        AdRequest adRequest = new AdRequest.Builder()
                                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                .addTestDevice(getResources().getStringArray(R.array.devices)[0])
                                .addTestDevice(getResources().getStringArray(R.array.devices)[1])
                                .addTestDevice(getResources().getStringArray(R.array.devices)[2])
                                .addTestDevice(getResources().getStringArray(R.array.devices)[3])
                                .addTestDevice("467009F00ED542DDA1694F88F807A79A")
                                .build();

                        mAdView.loadAd(adRequest);

                        RelativeLayout relativeLayout = ((RelativeLayout) findViewById(R.id.adView));
                        RelativeLayout.LayoutParams adViewCenter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        adViewCenter.addRule(RelativeLayout.CENTER_IN_PARENT);
                        relativeLayout.addView(mAdView, adViewCenter);

                        mAdView.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // Code to be executed when an ad finishes loading.
                                Log.d(TAG, "onAdLoaded");
                                mViewAdv.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                // Code to be executed when an ad request fails.
                                Log.d(TAG, "onAdFailedToLoad " + errorCode);
                                mViewAdv.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        mViewAdv.setVisibility(View.GONE);
                    }
                } else {
                    mViewAdv.setVisibility(View.GONE);
                }
            });

        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_initAdView", e);
        }
    }

    @Override
    public void processFinish(GroupVoca groupVoca) {
        if (groupVoca != null) {
            inputWordList(groupVoca.getListVoca());
        } else {
            Toast.makeText(context, R.string.not_found, Toast.LENGTH_SHORT).show();
        }
//        dataBaseHelper.addToIncomingList(groupVoca);
//        getIncomingList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CreateWordListActivity.REG_INPUT_WORD_LIST) {
            if (resultCode == CreateWordListActivity.UPDATE_1) {
                getIncomingList();
            }
        }
    }

    @Override
    public void onRefresh() {
        // Refresh items
        int count = (int) lbCountReviewCard.getTag();
        if (count < 100) {
            //fill up incoming list
            // int myLevel = dataBaseHelper.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_MY_LEVEL);
            dataBaseHelper._initIncomingCardIdList();
            //get new IncomingList
            List<Card> fillUpCards = dataBaseHelper._getIncomingListCard();

            //set count
            lbCountReviewCard.setText(String.valueOf(getString(R.string.message_total_card_incoming) + fillUpCards.size()));
            lbCountReviewCard.setTag(fillUpCards.size());

            //Reset adapter incoming list
            RecyclerViewIncomingListAdapter fillUpnewincomingAdapter =
                    new RecyclerViewIncomingListAdapter(context, mRecyclerViewReviewTodayList, fillUpCards, lbCountReviewCard);
            mRecyclerViewReviewTodayList.setAdapter(fillUpnewincomingAdapter);
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }
}
