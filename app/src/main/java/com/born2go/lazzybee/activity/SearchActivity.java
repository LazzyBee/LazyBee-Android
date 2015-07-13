package com.born2go.lazzybee.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.fragment.FragmentSearch;

public class SearchActivity extends ActionBarActivity implements FragmentSearch.FragmentSearchListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //
       // String query_text = getIntent().getStringArrayExtra(FragmentSearch.QUERY_TEXT).toString();

        //
        FragmentSearch fragmentSearch = (FragmentSearch) getSupportFragmentManager().findFragmentById(R.id.fragmentSearch);
//        New bunder
        Bundle bundle = new Bundle();
        //Set QUERY_TEXT
        bundle.putString(FragmentSearch.QUERY_TEXT, "a");
        //setArguments for fragmentSearch
       // fragmentSearch.setArguments(bundle);

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

    }
}
