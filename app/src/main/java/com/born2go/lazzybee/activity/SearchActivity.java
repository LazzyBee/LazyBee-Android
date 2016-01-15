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
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.GetCardFormServerByQuestion;
import com.born2go.lazzybee.adapter.RecyclerViewSearchResultListAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.api.ConnectGdatabase;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.event.RecyclerViewTouchListener;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.android.gms.tagmanager.DataLayer;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements
        GetCardFormServerByQuestion.GetCardFormServerByQuestionResponse {

    private static final String TAG = "SearchActivity";
    public static final String QUERY_TEXT = "query_text";
    private static final Object GA_SCREEN = "aSearchScreen";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
//        Toast.makeText(context,"Search onCreate",Toast.LENGTH_SHORT).show();

        _initLazzyBeeSingleton();

        _initRecyclerViewSearchResults();
        //Search by text
        handleIntent(getIntent());

        //Show Home as Up
        _initActonBar();

        _trackerApplication();

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
                    LazzyBeeShare.showErrorOccurred(context, e);
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
                    LazzyBeeShare.showErrorOccurred(context, e);
                }

            }
        });
        //Set data and add Touch Listener
        mRecyclerViewSearchResults.setLayoutManager(gridLayoutManager);

        mRecyclerViewSearchResults.addOnItemTouchListener(recyclerViewTouchListener);
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
        SearchView.SearchAutoComplete autoCompleteTextView = (SearchView.SearchAutoComplete) search.findViewById(R.id.search_src_text);

        if (autoCompleteTextView != null) {
            int color = Color.parseColor("#ffffffff");
            Drawable drawable = autoCompleteTextView.getDropDownBackground();
            drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

            autoCompleteTextView.setDropDownBackgroundDrawable(drawable);
            autoCompleteTextView.setTextColor(getResources().getColor(R.color.auto_complete_text_view_text_color));
        }

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(TAG, "onMenuItemActionCollapse");
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.d(TAG, "onMenuItemActionExpand");
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
                    LazzyBeeShare.showErrorOccurred(context, e);
                }
                return true;
            }
        });

        if (display_type == LazzyBeeShare.GOTO_SEARCH_CODE) {
            query_text = getIntent().getStringExtra(SearchManager.QUERY);
            search.setQuery(query_text, false);
            search.setIconified(false);
            search.clearFocus();
        } else {
            search.setQuery(LazzyBeeShare.EMPTY, false);
            search.setIconified(true);
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
        //***setOnQueryTextListener***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(getBaseContext(), query,
//                        Toast.LENGTH_SHORT).show();
                search.clearFocus();
                Log.i(TAG, "Vao day tiep");
                //  _search(query, LazzyBeeShare.GOTO_SEARCH_CODE, true);
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
            finish();
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

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        display_type = intent.getIntExtra(DISPLAY_TYPE, LazzyBeeShare.GOTO_SEARCH_CODE);
        Log.d(TAG, "display_type=" + display_type);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.d(TAG, "ACTION_SEARCH");
            query_text = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "query:" + query_text);
            display_type = LazzyBeeShare.GOTO_SEARCH_CODE;
            if (search != null)
                search.setQuery(query_text, false);
        }
        _search(query_text, display_type, true);
    }

    private void _search(String query, int display_type, boolean suggestion) {
        //use the query_text to search
        Log.d(TAG, "query_text:" + query);
        try {
            query_text = query;
            if (query == null) {
                lbResultCount.setVisibility(View.GONE);
                List<Card> cardList = new ArrayList<Card>();
                if (display_type == LazzyBeeShare.GOTO_DICTIONARY_CODE) {
                    cardList = dataBaseHelper._searchCardOrGotoDictionary(query, display_type);
                }
                setAdapterListCard(cardList);
            } else if (query != null || query.length() > 0) {
                Card cardFormDB = dataBaseHelper._getCardByQuestion(query);
                if (cardFormDB == null) {
                    cardFormDB = new Card();
                    cardFormDB.setQuestion(query);
                    GetCardFormServerByQuestion getCardFormServerByQuestion = new GetCardFormServerByQuestion(context);
                    getCardFormServerByQuestion.execute(cardFormDB);
                    getCardFormServerByQuestion.delegate = this;
                }
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
                    //Define Card
                    Card card = new Card();
                    card.setQuestion(query);
                    //Call Search in server
                    GetCardFormServerByQuestion getCardFormServerByQuestion = new GetCardFormServerByQuestion(context);
                    getCardFormServerByQuestion.execute(card);
                    getCardFormServerByQuestion.delegate = this;
                }
            } else {
                lbResultCount.setVisibility(View.GONE);
                List<Card> cardList = new ArrayList<Card>();
                setAdapterListCard(cardList);
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
        if (suggestion)
            hideKeyboard();


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
            if (card != null) {
                if (card.getId() == 0) {
                    dataBaseHelper._insertOrUpdateCard(card);
                    card.setId(dataBaseHelper._getCardIDByQuestion(card.getQuestion()));
                }
                Log.d(TAG, "card:" + card.toString());
                cardList.add(card);
            } else {
                Log.d(TAG, getString(R.string.not_found));
            }
            cardList.addAll(dataBaseHelper._searchCardOrGotoDictionary(this.query_text, display_type));
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
            LazzyBeeShare.showErrorOccurred(context, e);
        }
        hideKeyboard();
    }

    private void _trackerWorkNotFound() {
        try {
            DataLayer mDataLayer = LazzyBeeSingleton.mDataLayer;
            mDataLayer.pushEvent("searchNoResult", DataLayer.mapOf("wordError", query_text));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "requestCode:" + requestCode + ",resultCode:" + resultCode);
        if (resultCode == getResources().getInteger(R.integer.code_card_details_updated)) {
            //Reload data
            Log.i(TAG, QUERY_TEXT + ":" + query_text);
            _search(query_text, display_type, false);
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
}
