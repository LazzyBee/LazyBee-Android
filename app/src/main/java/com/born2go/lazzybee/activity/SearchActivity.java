package com.born2go.lazzybee.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.RecyclerViewSearchResultListAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.event.RecyclerViewTouchListener;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.List;

public class SearchActivity extends ActionBarActivity {

    private static final String TAG = "SearchActivity";
    public static final String QUERY_TEXT = "query";
    TextView txtSearch;
    RecyclerView mRecyclerViewSearchResults;
    TextView lbResultCount;
    LearnApiImplements dataBaseHelper;
    SearchView search;
    private Context context;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = this;
        //init DB SQLIte
        dataBaseHelper = new LearnApiImplements(this.getApplicationContext());

        query = getIntent().getStringExtra(QUERY_TEXT);

        search = (SearchView) findViewById(R.id.search);

        //Init RecyclerView and Layout Manager
        mRecyclerViewSearchResults = (RecyclerView) findViewById(R.id.mRecyclerViewSearchResults);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerViewSearchResults.getContext(), 1);

        //init LbResult Count
        lbResultCount = (TextView) findViewById(R.id.lbResultCount);
        //Init Touch Listener
        RecyclerViewTouchListener recyclerViewTouchListener = new RecyclerViewTouchListener(this, mRecyclerViewSearchResults, new RecyclerViewTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView lbQuestion = (TextView) view.findViewById(R.id.lbQuestion);
                //Cast tag lbQuestion to CardId
                String cardID = String.valueOf(lbQuestion.getTag());
                _gotoCardDetail(cardID);

            }

            @Override
            public void onItemLongPress(View childView, int position) {
                Log.i(TAG,"Long Press");
            }
        });
        _search(query);
        handleIntent(getIntent());

        //Set data and add Touch Listener
        mRecyclerViewSearchResults.setLayoutManager(gridLayoutManager);

        mRecyclerViewSearchResults.addOnItemTouchListener(recyclerViewTouchListener);

        //Show Home as Up
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_search, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

                Toast.makeText(getBaseContext(), String.valueOf(hasFocus),
                        Toast.LENGTH_SHORT).show();
            }
        });

        //***setOnQueryTextListener***
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                Toast.makeText(getBaseContext(), query,
                        Toast.LENGTH_SHORT).show();
                _search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub

                //Toast.makeText(getBaseContext(), newText,Toast.LENGTH_SHORT).show();
                return false;
            }
        });
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
        if (id == android.R.id.home) {
            finish();
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
        this.startActivityForResult(intent, RESULT_OK);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            _search(query);
        }
    }

    private void _search(String query) {
        //use the query to search
        Log.i(TAG, "query:" + query);
        List<Card> cardList = dataBaseHelper._searchCard(query);
        int result_count = cardList.size();
        Log.i(TAG, "Search result_count:" + result_count);

        //set count
        lbResultCount.setText(result_count + " " + getString(R.string.result));

        //Check result_count==0 search in server
        if (result_count == 0) {
            //Search in server
        }

        //Init Adapter
        RecyclerViewSearchResultListAdapter recyclerViewReviewTodayListAdapter = new RecyclerViewSearchResultListAdapter(context, cardList);
        mRecyclerViewSearchResults.setAdapter(recyclerViewReviewTodayListAdapter);
    }


}
