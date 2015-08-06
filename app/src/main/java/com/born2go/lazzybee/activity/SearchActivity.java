package com.born2go.lazzybee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.RecyclerViewSearchResultListAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.event.RecyclerViewTouchListener;
import com.born2go.lazzybee.fragment.FragmentSearch;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.List;

public class SearchActivity extends ActionBarActivity implements FragmentSearch.FragmentSearchListener {

    private static final String TAG = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //create LayoutInflater
        LayoutInflater inflater = LayoutInflater.from(this);

        //infater custom action bar
        View mActionBarSearchActivityCustom = inflater.inflate(R.layout.actionbar_seach_activity, null);

        //int txtSearch
        final TextView txtSearch = (TextView) mActionBarSearchActivityCustom.findViewById(R.id.txtSearch);

        //init fragmentTransaction
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        //init FragmentSearch
        final FragmentSearch fragmentSearch = new FragmentSearch();
        txtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);


        //set and show custom action bar search
        getSupportActionBar().setCustomView(mActionBarSearchActivityCustom);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //init DB SQLIte
        final LearnApiImplements dataBaseHelper = new LearnApiImplements(this.getApplicationContext());

        //Init RecyclerView and Layout Manager
        final RecyclerView mRecyclerViewSearchResults = (RecyclerView) findViewById(R.id.mRecyclerViewSearchResults);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerViewSearchResults.getContext(), 1);

        //init LbResult Count
        final TextView lbResultCount = (TextView) findViewById(R.id.lbResultCount);


        //

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    //Get queue text value form txtSearch
                    String query_text = txtSearch.getText().toString();
                    Log.i(SearchActivity.TAG, "Queue text:" + query_text);
                    //search data form DB
                    List<Card> cardList = dataBaseHelper._searchCard(query_text);
                    int result_count = cardList.size();
                    Log.i(TAG, "Search result_count:" + result_count);

                    //set count
                    lbResultCount.setText(result_count + " " + getString(R.string.result));

                    //Check result_count==0 search in server
                    if (result_count == 0) {
                        //Search in server
                    }

                    //Init Adapter
                    RecyclerViewSearchResultListAdapter recyclerViewReviewTodayListAdapter = new RecyclerViewSearchResultListAdapter(cardList);
                    mRecyclerViewSearchResults.setAdapter(recyclerViewReviewTodayListAdapter);

                    return true;
                }
                return false;
            }
        });


        //Init Touch Listener
        RecyclerViewTouchListener recyclerViewTouchListener = new RecyclerViewTouchListener(this, mRecyclerViewSearchResults, new RecyclerViewTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView lbQuestion = (TextView) view.findViewById(R.id.lbQuestion);
                //Cast tag lbQuestion to CardId
                String cardID = String.valueOf(lbQuestion.getTag());
               _gotoCardDetail(cardID);

            }
        });

        //Set data and add Touch Listener
        mRecyclerViewSearchResults.setLayoutManager(gridLayoutManager);

        mRecyclerViewSearchResults.addOnItemTouchListener(recyclerViewTouchListener);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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

    /**
     * Goto Card Details with card id
     *
     * @param cardId
     */
    @Override
    public void _gotoCardDetail(String cardId) {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        intent.putExtra(LazzyBeeShare.CARDID, cardId);
        startActivity(intent);
    }
}
