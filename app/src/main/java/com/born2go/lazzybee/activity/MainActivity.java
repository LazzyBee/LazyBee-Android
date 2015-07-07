package com.born2go.lazzybee.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.fragment.FragmentCardDetails;
import com.born2go.lazzybee.fragment.FragmentCourse;
import com.born2go.lazzybee.fragment.FragmentListCourse;
import com.born2go.lazzybee.fragment.FragmentProfile;
import com.born2go.lazzybee.fragment.FragmentReviewToday;
import com.born2go.lazzybee.fragment.FragmentSearch;
import com.born2go.lazzybee.fragment.FragmentSetting;
import com.born2go.lazzybee.fragment.FragmentStudy;
import com.born2go.lazzybee.fragment.NavigationDrawerFragment;

import java.io.IOException;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, FragmentStudy.FragmentStudyListener, SearchView.OnQueryTextListener,FragmentSearch.FragmentSearchListener {

    private static final String TAG = "MainActivity";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #_restoreActionBar()}.
     */
    private CharSequence mTitle;

    DataBaseHelper myDbHelper;

    SearchView mSearchView;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _initToolBar();
        _initSQlIte();
        _checkLogin();

    }

    private void _initToolBar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar_main);
//        setSupportActionBar(toolbar);
        _initNavigationDrawerFragment(null);
    }


    /**
     * Check login
     */
    private void _checkLogin() {

    }

    /**
     * Init Sql
     */
    private void _initSQlIte() {
        myDbHelper = new DataBaseHelper(this);
        try {

            myDbHelper._createDataBase();

        } catch (IOException ioe) {
            //throw new Error("Unable to create database");
            //ioe.printStackTrace();
            Log.e(TAG, "Unable to create database:" + ioe.getMessage());

        }
    }

    /**
     * Init NavigationDrawerFragment
     *
     * @param toolbar
     */
    private void _initNavigationDrawerFragment(Toolbar toolbar) {
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer, toolbar,
                drawerLayout);
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                String courseId = "";
                _gotoCourseDetails(courseId);
                break;
            case 1:
                //Goto List Course
                FragmentListCourse fragmentListCourse = new FragmentListCourse();
                //replace from container to fragmentCourse
                fragmentTransaction.replace(R.id.container, fragmentListCourse)
                        .addToBackStack(FragmentListCourse.TAG).commit();
                break;
            default:
                courseId = "";
                _gotoCourseDetails(courseId);
        }


    }

    /**
     * Repale to course details
     * Add back stack
     */
    private void _gotoCourseDetails(String course_id) {


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //Got Course Details
        FragmentCourse fragmentCourse = new FragmentCourse();
        //New bunder
        Bundle bundle = new Bundle();
        //Set Course id
        bundle.putString(FragmentCourse.COURSE_ID, course_id);
        //setArguments for fragmentCourse
        fragmentCourse.setArguments(bundle);
        //replace from container to fragmentCourse
        fragmentTransaction.replace(R.id.container, fragmentCourse)
                .addToBackStack(FragmentCourse.TAG).commit();
        //
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void _onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = FragmentCourse.TAG;
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void _restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            //init mSearchView
            mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setQueryHint(getString(R.string.action_search));
            _restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //switch id action action bar
        switch (id) {
            case android.R.id.home:
                Toast.makeText(this, "BACK", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
            case R.id.action_settings:
                _gotoSetting();
                break;
            case R.id.action_profile:
                _gotoProfile();
                break;
            case R.id.action_logout:
                //Log out Application
                Toast.makeText(this, getString(R.string.action_logout), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_search:
                //Search
                Toast.makeText(this, getString(R.string.action_search), Toast.LENGTH_SHORT).show();
                _setUpSearchActionBar();
                //
                mSearchView.setIconified(false);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up action bar
     * <p>Hide Tittle and setting menu</p>
     * <p>Add textbox in Action bar</p>
     */
    private void _setUpSearchActionBar() {
        ActionBar actionBar = getSupportActionBar();

    }

    /**
     * Goto Fragment Profile
     */
    private void _gotoProfile() {
        Toast.makeText(this, getString(R.string.action_profile), Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //intit fragmentProfile
        FragmentProfile fragmentProfile = new FragmentProfile();
        //New bunder
        Bundle bundle = new Bundle();
        //Set profile_id
        String profile_id = "";
        bundle.putString(FragmentProfile.Profile_ID, profile_id);
        //setArguments for fragmentProfile
        fragmentProfile.setArguments(bundle);
        //replace from container to fragmentProfile
        fragmentTransaction.replace(R.id.container, fragmentProfile)
                .addToBackStack(FragmentProfile.TAG).commit();

    }

    /**
     * Goto fragment setting
     */
    private void _gotoSetting() {
        Toast.makeText(this, getString(R.string.action_settings), Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //intit
        FragmentSetting fragmentSetting = new FragmentSetting();
        //replace from container to fragmentSetting
        fragmentTransaction.replace(R.id.container, fragmentSetting)
                .addToBackStack(FragmentSetting.TAG).commit();
    }

    @Override
    public void completeCourse() {
        int flag_complete_course = 1;
        onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, query, Toast.LENGTH_LONG).show();
        _gotoSeach(query);
        return true;
    }

    /**
     * Goto FragemenSearch with query
     */
    private void _gotoSeach(String query) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //New fragmentSearch
        FragmentSearch fragmentSearch = new FragmentSearch();
        //New bunder
        Bundle bundle = new Bundle();
        //Set QUERY_TEXT
        bundle.putString(FragmentSearch.QUERY_TEXT, query);
        //setArguments for fragmentSearch
        fragmentSearch.setArguments(bundle);
        //replace from container to fragmentSearch
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragmentSearch)
                .addToBackStack(FragmentSearch.TAG).commit();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /**
     * Goto Card Details with card id
     *
     * @param cardId
     */
    @Override
    public void _gotoCardDetail(String cardId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //New fragmentCardDetails
        FragmentCardDetails fragmentCardDetails = new FragmentCardDetails();
        //New bunder
        Bundle bundle = new Bundle();
        //Set QUERY_TEXT
        bundle.putString(FragmentCardDetails.CARD_ID, cardId);
        //setArguments for fragmentCardDetails
        fragmentCardDetails.setArguments(bundle);
        //replace from container to fragmentCardDetails
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragmentCardDetails)
                .addToBackStack(FragmentCardDetails.TAG).commit();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity)._onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void _onBtnStudyOnClick(View view) {
        Toast.makeText(this, "Goto Study", Toast.LENGTH_SHORT).show();
        _gotoStudy();
    }

    public void _btnCustomStudyOnClick(View view) {
        Toast.makeText(this, "under con", Toast.LENGTH_SHORT).show();

    }

    public void _onbtnReviewOnClick(View view) {
        Toast.makeText(this, "Goto Review", Toast.LENGTH_SHORT).show();
        _gotoReviewToday();
    }

    private void _gotoReviewToday() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //New fragmentReview
        FragmentReviewToday fragmentReview = new FragmentReviewToday();
        //New bunder
        Bundle bundle = new Bundle();
        //Set COURSE_ID
        bundle.putString(FragmentReviewToday.COURSE_ID, "");
        //setArguments for fragmentReview
        fragmentReview.setArguments(bundle);
        //replace from container to fragmentReview
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragmentReview)
                .addToBackStack(FragmentReviewToday.TAG).commit();
    }


    private void _gotoStudy() {


        FragmentManager fragmentManager = getSupportFragmentManager();
        //New fragmentStudy
        FragmentStudy fragmentStudy = new FragmentStudy();
        //New bunder
        Bundle bundle = new Bundle();
        //Set COURSE_ID
        bundle.putString(FragmentStudy.COURSE_ID, "");
        //setArguments for fragmentStudy
        fragmentStudy.setArguments(bundle);
        //replace from container to fragmentStudy
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragmentStudy)
                .addToBackStack(FragmentStudy.TAG).commit();
    }

}
