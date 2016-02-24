package com.born2go.lazzybee.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.GetCardFormServerByQuestion;
import com.born2go.lazzybee.adapter.RecyclerViewSearchResultListAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.api.ConnectGdatabase;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.event.RecyclerViewTouchListener;
import com.born2go.lazzybee.gtools.ContainerHolderSingleton;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.DataLayer;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements
        GetCardFormServerByQuestion.GetCardFormServerByQuestionResponse {

    private static final String TAG = "SearchActivity";
    public static final String QUERY_TEXT = "query_text";
    private static final Object GA_SCREEN = "aSearchScreen";
    private static final Object GA_SCREEN_DICTIONARY = "aDictionaryScreen";
    public static final String DISPLAY_TYPE = "display_type";
    RecyclerView mRecyclerViewSearchResults;
    TextView lbResultCount;
    TextView lbMessageNotFound;
    LearnApiImplements dataBaseHelper;
    SearchView search;
    private Context context;
    String query_text;
    int display_type = 0;
    private int ADD_TO_LEARN = 0;
    ConnectGdatabase connectGdatabase;
    View mViewAdv;
    SearchView.SearchAutoComplete mSuggerstionCard;
    List<Card> dictionaryCardList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;

        _initLazzyBeeSingleton();

        dictionaryCardList = dataBaseHelper._searchCardOrGotoDictionary(LazzyBeeShare.EMPTY, LazzyBeeShare.GOTO_DICTIONARY_CODE);

        _initRecyclerViewSearchResults();

        //Show Home as Up
        _initActonBar();

        handleIntent(getIntent());

        _initAdView();


    }

    private void _initActonBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void _initRecyclerViewSearchResults() {
        //Init RecyclerView and Layout Manager
        mRecyclerViewSearchResults = (RecyclerView) findViewById(R.id.mRecyclerViewSearchResults);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerViewSearchResults.getContext(), 1);

        //init LbResult Count
        lbResultCount = (TextView) findViewById(R.id.lbResultCount);
        lbMessageNotFound = (TextView) findViewById(R.id.lbMessageNotFound);

        //Init Touch Listener
        RecyclerViewTouchListener recyclerViewTouchListener = new RecyclerViewTouchListener(this, mRecyclerViewSearchResults, new RecyclerViewTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView lbQuestion = (TextView) view.findViewById(R.id.lbQuestion);
                try {
                    //Cast tag lbQuestion to CardId
                    Card card = (Card) lbQuestion.getTag();
                    if (card.getId() > 0) {
                        String cardID = String.valueOf(card.getId());
                        _gotoCardDetail(cardID);
                    } else {
                        Log.w(TAG, "card.getId()==0");
                    }
                } catch (Exception e) {
                    LazzyBeeShare.showErrorOccurred(context, "1_initRecyclerViewSearchResults", e);
                }

            }

            @Override
            public void onItemLongPress(View view, int position) {
                TextView lbQuestion = (TextView) view.findViewById(R.id.lbQuestion);
                try {
                    //Cast tag lbQuestion to CardId
                    Card card = (Card) lbQuestion.getTag();
                    String cardID = String.valueOf(card.getId());
                    if (card.getId() > 0) {
                        // _optionList(card);
                    } else {
                        Log.w(TAG, "card.getId()==0");
                    }
                } catch (Exception e) {
                    LazzyBeeShare.showErrorOccurred(context, "2_initRecyclerViewSearchResults", e);
                }

            }
        });
        //Set data and add Touch Listener
        mRecyclerViewSearchResults.setLayoutManager(gridLayoutManager);

        //  mRecyclerViewSearchResults.addOnItemTouchListener(recyclerViewTouchListener);
    }

    private void _initLazzyBeeSingleton() {
        //init DB SQLIte
        dataBaseHelper = LazzyBeeSingleton.learnApiImplements;

        connectGdatabase = LazzyBeeSingleton.connectGdatabase;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_search, menu);
        _defineSearchView(menu);
        return true;
    }

    private void _defineSearchView(Menu menu) {
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.search);
        search =
                (SearchView) menu.findItem(R.id.search).getActionView();
        search.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // Theme the SearchView's AutoCompleteTextView drop down. For some reason this wasn't working in styles.xml
        mSuggerstionCard = (SearchView.SearchAutoComplete) search.findViewById(R.id.search_src_text);

        if (mSuggerstionCard != null) {
            int color = Color.parseColor("#ffffffff");
            Drawable drawable = mSuggerstionCard.getDropDownBackground();
            drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

            mSuggerstionCard.setDropDownBackgroundDrawable(drawable);
            mSuggerstionCard.setTextColor(getResources().getColor(R.color.auto_complete_text_view_text_color));
        }

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(TAG, "close search view -Action:" + getIntent().getAction());
                if (getIntent().getAction().equals(LazzyBeeShare.ACTION_GOTO_DICTIONARY)) {
                    _displayDictionary();
                } else {
                    onBackPressed();
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.d(TAG, "open search view");

                return true;
            }

        });
        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Log.d(TAG, "onSuggestionSelect:" + position);
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Log.d(TAG, "onSuggestionClick:" + position);
                try {
                    CursorAdapter c = search.getSuggestionsAdapter();
                    if (c != null) {
                        Cursor cur = c.getCursor();
                        cur.moveToPosition(position);

                        String cardID = cur.getString(cur.getColumnIndex(BaseColumns._ID));
                        Log.d(TAG, "cardID:" + cardID);
                        String query = cur.getString(cur.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                        Log.d(TAG, "query:" + query);
                        int insertSuggesstionResults = dataBaseHelper._insertSuggesstion(cardID);
                        Log.d(TAG, "insertSuggesstionResults " + ((insertSuggesstionResults == -1) ? " OK" : " Fails"));
                        _gotoCardDetail(cardID);

                        //call back actionbar
                        searchItem.collapseActionView();
                    } else {
                        Log.d(TAG, "NUll searchView.getSuggestionsAdapter()");
                    }
                } catch (Exception e) {
                    LazzyBeeShare.showErrorOccurred(context, "_defineSearchView", e);
                }
                return true;
            }
        });

//        if (display_type == LazzyBeeShare.GOTO_SEARCH_CODE) {
//            query_text = getIntent().getStringExtra(SearchManager.QUERY);
//            search.setQuery(query_text, false);
//            search.setIconified(false);
//            search.clearFocus();
//        } else {
//            search.setQuery(LazzyBeeShare.EMPTY, false);
//            search.setIconified(true);
//        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
        //***setOnQueryTextListener***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(getBaseContext(), query,
//                        Toast.LENGTH_SHORT).show();
                search.clearFocus();
                Log.d(TAG, "Vao day tiep");
                _search(query, LazzyBeeShare.GOTO_SEARCH_CODE, true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getBaseContext(), newText,Toast.LENGTH_SHORT).show();
                //_search(newText, LazzyBeeShare.GOTO_SEARCH_CODE, false);
                return false;
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Goto Card Details with card id
     *
     * @param cardId
     */
    private void _gotoCardDetail(String cardId) {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        intent.putExtra(LazzyBeeShare.CARDID, cardId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivityForResult(intent, getResources().getInteger(R.integer.code_card_details_updated));
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        setIntent(intent);
//        handleIntent(intent);
//    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SEARCH.equals(action)) {
            Log.d(TAG, "ACTION_SEARCH");
            query_text = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "Intent.ACTION_SEARCH query:" + query_text);
            if (search != null) {
                search.setQuery(query_text, false);
                getSupportActionBar().setTitle(query_text);
            } else {
                Log.d(TAG, "search view null");
            }
            _search(query_text, display_type, true);
            _trackerApplication(GA_SCREEN);
        } else if (LazzyBeeShare.ACTION_GOTO_DICTIONARY.equals(action)) {
            Log.d(TAG, "ACTION_DICTIONARY");
            _displayDictionary();
        }


    }

    private void _displayDictionary() {
        //set Title
        getSupportActionBar().setTitle(R.string.drawer_dictionary);

        query_text = null;
        //Hide count results
        lbResultCount.setVisibility(View.GONE);

        //reset adapter
        setAdapterListCard(dictionaryCardList);
        _trackerApplication(GA_SCREEN_DICTIONARY);
    }

    private void _search(String query, int display_type, boolean suggestion) {
        //use the query_text to search
        Log.d(TAG, "query_text:" + query);
        try {
            query_text = query;
//            if (query == null) {
//                lbResultCount.setVisibility(View.GONE);
//                List<Card> cardList = new ArrayList<Card>();
//                if (display_type == LazzyBeeShare.GOTO_DICTIONARY_CODE) {
//                    cardList = dataBaseHelper._searchCardOrGotoDictionary(query, display_type);
//                }
//                setAdapterListCard(cardList);
//            } else
            if (query != null || query.length() > 0) {
                //connection with internet ok search in server first
                if (LazzyBeeShare.checkConn(context)) {
                    Card cardFormDB = new Card();
                    cardFormDB.setQuestion(query);
                    GetCardFormServerByQuestion getCardFormServerByQuestion = new GetCardFormServerByQuestion(context);
                    getCardFormServerByQuestion.execute(cardFormDB);
                    getCardFormServerByQuestion.delegate = this;
                } else {
                    //failed to connect to internet
                    Log.d(TAG, getString(R.string.failed_to_connect_to_server));
                    List<Card> cardList = dataBaseHelper._searchCardOrGotoDictionary(query, display_type);
                    int result_count = cardList.size();
                    Log.i(TAG, "Search result_count:" + result_count);
                    if (result_count > 0) {
                        lbResultCount.setVisibility((display_type > 0) ? View.GONE : View.VISIBLE);
                        mRecyclerViewSearchResults.setVisibility(View.VISIBLE);
                        lbMessageNotFound.setVisibility(View.GONE);
                        //set count
                        lbResultCount.setText(String.valueOf(result_count + " " + getString(R.string.result)));
                        //Init Adapter
                        setAdapterListCard(cardList);
                    } else if (result_count == 0) {//Check result_count==0 search in server
                        lbResultCount.setVisibility(View.GONE);
                        mRecyclerViewSearchResults.setVisibility(View.GONE);
                        lbMessageNotFound.setVisibility(View.VISIBLE);
                        lbMessageNotFound.setText(getString(R.string.message_no_results_found_for, query_text));
                        _trackerWorkNotFound();
                    }
                }

            } else {
                lbResultCount.setVisibility(View.GONE);
                List<Card> cardList = new ArrayList<Card>();
                setAdapterListCard(cardList);
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_search", e);
        }
        if (suggestion) {
            hideKeyboard();
        }

    }

    private void setAdapterListCard(List<Card> cardList) {
        RecyclerViewSearchResultListAdapter recyclerViewReviewTodayListAdapter = new RecyclerViewSearchResultListAdapter(context, cardList);
        mRecyclerViewSearchResults.setAdapter(recyclerViewReviewTodayListAdapter);
    }

    private void _optionList(final Card card) {
        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));
        builder.setTitle(card.getQuestion());
        final CharSequence[] items = {getString(R.string.action_add_to_learn), getString(R.string.action_learnt)};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                //
                if (items[item] == getString(R.string.action_add_to_learn)) {
                    _addCardToQueue(card);
                } else if (items[item] == getString(R.string.action_learnt)) {
                    _doneCard(card);
                }
                _search(query_text, display_type, false);
                dialog.cancel();
            }
        });

        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void _doneCard(final Card card) {
        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(getString(R.string.dialog_message_delete_card, card.getQuestion()))
                .setTitle(R.string.dialog_title_delete_card);

        // Add the buttons
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Update Queue_list in system table
                card.setQueue(Card.QUEUE_DONE_2);
                dataBaseHelper._updateCard(card);
                String action = getString(R.string.done_card);
                Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void _addCardToQueue(final Card card) {
        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(getString(R.string.dialog_message_add_to_learn, card.getQuestion()))
                .setTitle(getString(R.string.dialog_title_add_to_learn));

        // Add the buttons
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Update Queue_list in system table
                dataBaseHelper._addCardIdToQueueList(card);
                Toast.makeText(context, getString(R.string.message_action_add_card_to_learn_complete, card.getQuestion()), Toast.LENGTH_SHORT).show();
                ADD_TO_LEARN = 1;
                setResult(LazzyBeeShare.CODE_SEARCH_RESULT, new Intent());
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }


    @Override
    public void processFinish(Card card) {
        try {
            List<Card> cardList = new ArrayList<Card>();
            int result_count;
            boolean cardNull = false;
            if (card != null) {
                Log.d(TAG, "Card get Form Server:" + card.toString());
                if (card.getId() == 0) {
                    dataBaseHelper._insertOrUpdateCard(card);
                    card.setId(dataBaseHelper._getCardIDByQuestion(card.getQuestion()));
                    cardList.add(card);
                }
            } else {
                //Not found find card form server
                Log.d(TAG, getString(R.string.not_found));
                cardNull = true;
            }
            List<Card> cardResultSearchFromDb = dataBaseHelper._searchCardOrGotoDictionary(this.query_text, display_type);
            if (cardResultSearchFromDb.size() > 0) {
                //Clone result search card form db
                List<Card> cloneSearchResults = new ArrayList<Card>(cardResultSearchFromDb);
                if (!cardNull) {
                    for (Card cardDB : cardResultSearchFromDb) {
                        if (cardDB.getId() == (card.getId())) {
                            cloneSearchResults.remove(cardDB);//Remove duplicate card
                        }
                    }
                }
                if (cloneSearchResults.size() > 0)
                    cardList.addAll(cloneSearchResults);
            }
            result_count = cardList.size();
            Log.d(TAG, "Results count:" + result_count);
            if (result_count > 0) {
                lbResultCount.setVisibility(View.VISIBLE);
                mRecyclerViewSearchResults.setVisibility(View.VISIBLE);
                lbMessageNotFound.setVisibility(View.GONE);
                lbResultCount.setText(String.valueOf(result_count + " " + getString(R.string.result)));
                setAdapterListCard(cardList);

            } else {
                lbResultCount.setVisibility(View.GONE);
                mRecyclerViewSearchResults.setVisibility(View.GONE);
                lbMessageNotFound.setVisibility(View.VISIBLE);
                lbMessageNotFound.setText(getString(R.string.message_no_results_found_for, query_text));
                _trackerWorkNotFound();
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "processFinish", e);
        }
        hideKeyboard();
    }

    private void _trackerWorkNotFound() {
        try {
            DataLayer mDataLayer = LazzyBeeSingleton.mDataLayer;
            mDataLayer.pushEvent("searchNoResult", DataLayer.mapOf("wordError", query_text));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_trackerWorkNotFound", e);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.i(TAG, "requestCode:" + requestCode + ",resultCode:" + resultCode);
//        if (resultCode == getResources().getInteger(R.integer.code_card_details_updated)) {
//            //Reload data
//            Log.i(TAG, QUERY_TEXT + ":" + query_text);
//            _search(query_text, display_type, false);
//        }
//    }

    private void _trackerApplication(Object screenName) {
        try {
            DataLayer mDataLayer = LazzyBeeSingleton.mDataLayer;
            mDataLayer.pushEvent("openScreen", DataLayer.mapOf("screenName", screenName));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_trackerApplication", e);
        }
    }

    private void hideKeyboard() {
        try {
            if (search != null) {
                search.clearFocus();
            }
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

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
        int hour = dataBaseHelper.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_HOUR_NOTIFICATION);
        int minute = dataBaseHelper.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_MINUTE_NOTIFICATION);
        LazzyBeeShare._setUpNotification(context, hour, minute);
    }

    private void _initAdView() {
        try {
            mViewAdv = findViewById(R.id.mViewAdv);
            //get value form task manager
            Container container = ContainerHolderSingleton.getContainerHolder().getContainer();
            String admob_pub_id = null;
            String adv_id = null;
            if (container == null) {
            } else {
                admob_pub_id = container.getString(LazzyBeeShare.ADMOB_PUB_ID);
//                if (getIntent().getAction().equals(LazzyBeeShare.ACTION_GOTO_DICTIONARY)) {
//                    adv_id = container.getString(LazzyBeeShare.ADV_DICTIONARY_LIST_ID);
//                } else {
                adv_id = container.getString(LazzyBeeShare.ADV_SEARCH_RESULTS_LIST_ID);
//                }
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

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Action:" + getIntent().getAction());
        //finish();
        super.onBackPressed();
    }
}
