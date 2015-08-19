package com.born2go.lazzybee.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.db.DatabaseUpgrade;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.fragment.FragmentCourse;
import com.born2go.lazzybee.fragment.FragmentProfile;
import com.born2go.lazzybee.fragment.NavigationDrawerFragment;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.identitytoolkit.GitkitClient;
import com.google.identitytoolkit.GitkitUser;
import com.google.identitytoolkit.IdToken;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_PICK_ACCOUNT = 120;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #_restoreActionBar()}.
     */
    private CharSequence mTitle;

    DataBaseHelper myDbHelper;
    DatabaseUpgrade databaseUpgrade;
    SearchView mSearchView;
    DrawerLayout drawerLayout;

    CardView mCardViewStudy;

    TextView lbNameCourse;
    TextView lbComplete;
//    TextView lbSuportCompletedCard;

    TextView lbDueToday;
    TextView lbTotalNewCount;
    TextView lbTotalsCount;

    LinearLayout pTotalCards;
    LinearLayout pTotalNewCard;
    LinearLayout pDueToday;


    Button btnStudy, btnCustomStudy;
    private LearnApiImplements dataBaseHelper;
    private Context context = this;
    SharedPreferences prefs;

    private GitkitClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        _checkLogin();
        _initSQlIte();
        _initSettingApplication();
        setContentView(R.layout.activity_main);
        _initToolBar();
        _intInterfaceView();
        _getCountCard();
        // _checkListTodayExit();
        _checkCompleteLearn();

        dataBaseHelper._get100Card();


    }

    private void _initSettingApplication() {
        _changeLanguage();
        if (_checkAutoUpdateApplication()) {
            _checkUpdate();
        }


    }

    private boolean _checkAutoUpdateApplication() {
        String auto = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.AUTO_CHECK_UPDATE_SETTING);
        if (auto == null) {
            return false;
        } else if (auto.equals(LazzyBeeShare.ON)) {
            return true;
        } else if (auto.equals(LazzyBeeShare.OFF)) {
            return false;
        } else {
            return false;
        }
    }

    private void _changeLanguage() {
        String lang = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.KEY_LANGUAGE);
        Log.i(TAG, "Lang:" + lang);
        if (lang == null)
            lang = LazzyBeeShare.LANG_EN;
        String languageToLoad = lang; // your language

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private void _checkCompleteLearn() {
        int complete = dataBaseHelper._checkCompleteLearned();
        if (complete == 1) {
            //No complete
            lbComplete.setText(LazzyBeeShare.EMPTY);
            btnStudy.setText("Study");
            btnStudy.setTag(false);
            btnCustomStudy.setTag(false);
            mCardViewStudy.setVisibility(View.VISIBLE);

            _visibilityCount(true);
        } else {
            //Comprete
            Log.i(TAG, "_checkCompleteLearn:Complete");
            lbComplete.setText(getString(R.string.congratulations));
            // lbSuportCompletedCard.setText(getString(R.string.suport_complete_card));
            btnCustomStudy.setTag(true);
            btnStudy.setTag(false);
            btnStudy.setText("Complete Learn");
            Log.i(TAG, "Learn more");
            mCardViewStudy.setVisibility(View.GONE);

            _visibilityCount(false);
        }

    }

    private void _getCountCard() {
        String dueToday = dataBaseHelper._getStringDueToday();
        int allCount = dataBaseHelper._getAllListCard().size();
        int learnCount = dataBaseHelper._getListCardLearned().size();
        if (dueToday != null)
            lbDueToday.setText(dueToday);
        lbTotalsCount.setText("" + allCount);
        lbTotalNewCount.setText("" + (allCount - learnCount));

    }

    private void _intInterfaceView() {
        mCardViewStudy = (CardView) findViewById(R.id.mCardViewStudy);
        btnStudy = (Button) findViewById(R.id.btnStudy);
        btnCustomStudy = (Button) findViewById(R.id.btnCustomStudy);

        lbNameCourse = (TextView) findViewById(R.id.lbNameCourse);
        lbComplete = (TextView) findViewById(R.id.lbComplete);

        pTotalCards = (LinearLayout) findViewById(R.id.pTotalCards);
        pTotalNewCard = (LinearLayout) findViewById(R.id.pTotalNewCard);
        pDueToday = (LinearLayout) findViewById(R.id.pDueToday);


        lbDueToday = (TextView) findViewById(R.id.lbDueToday);
        lbTotalNewCount = (TextView) findViewById(R.id.lbTotalNewCount);
        lbTotalsCount = (TextView) findViewById(R.id.lbTotalsCount);

    }

    private void _visibilityCount(boolean visibility) {
        if (visibility) {
            pTotalCards.setVisibility(View.VISIBLE);
            pTotalNewCard.setVisibility(View.VISIBLE);
            pDueToday.setVisibility(View.VISIBLE);
        } else {
            pTotalCards.setVisibility(View.GONE);
            pTotalNewCard.setVisibility(View.GONE);
            pDueToday.setVisibility(View.GONE);
        }
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
        client = GitkitClient.newBuilder(this, new GitkitClient.SignInCallbacks() {
            @Override
            public void onSignIn(IdToken idToken, GitkitUser gitkitUser) {
                //authenticate();
                Toast.makeText(context, "Sign in with:" + idToken, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSignInFailed() {
                Toast.makeText(context, "Sign in failed", Toast.LENGTH_LONG).show();
            }
        }).build();

    }

    public void authenticate() {
        Intent accountChooserIntent =
                AccountPicker.newChooseAccountIntent(null, null,
                        new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, "Select an account", null,
                        null, null);
        startActivityForResult(accountChooserIntent, REQUEST_PICK_ACCOUNT);
    }

    /**
     * Init Sql
     */
    private void _initSQlIte() {
        myDbHelper = new DataBaseHelper(context);
        databaseUpgrade = new DatabaseUpgrade(context);
        try {
            myDbHelper._createDataBase();
        } catch (IOException ioe) {
            //throw new Error("Unable to create database");
            //ioe.printStackTrace();
            Log.e(TAG, "Unable to create database:" + ioe.getMessage());

        }
        dataBaseHelper = new LearnApiImplements(context);
    }

    boolean first = true;

    private void _checkListTodayExit() {
        int checkTodayExit = dataBaseHelper._checkListTodayExit();
        Log.i(TAG, "checkTodayExit: " + checkTodayExit);

        if (checkTodayExit == -2) {
            Log.i(TAG, "_checkListTodayExit:Fist Innitial");
            lbComplete.setText(LazzyBeeShare.EMPTY);
            btnStudy.setText("Study");
            btnStudy.setTag(false);
            btnCustomStudy.setTag(false);
            mCardViewStudy.setVisibility(View.VISIBLE);
        } else {
            if (checkTodayExit > -1) {
                Log.i(TAG, "_checkListTodayExit:checkTodayExit == 1111");
                if (checkTodayExit == 0) {
                    Log.i(TAG, "_checkListTodayExit=0,Complete Learn to day");
                    lbComplete.setText(LazzyBeeShare.EMPTY);
                    // lbSuportCompletedCard.setText(LazzyBeeShare.EMPTY);
                    btnStudy.setText("Study");
                    btnStudy.setTag(false);
                    btnCustomStudy.setTag(false);
                    Log.i(TAG, "Study");
                    mCardViewStudy.setVisibility(View.VISIBLE);
                } else if (checkTodayExit == 0) {
                    Log.i(TAG, "_checkListTodayExit:checkTodayExit == 0");
                    lbComplete.setText(getString(R.string.congratulations));
                    // lbSuportCompletedCard.setText(getString(R.string.suport_complete_card));
                    btnCustomStudy.setTag(true);
                    btnStudy.setTag(false);
                    btnStudy.setText("Complete Learn");
                    Log.i(TAG, "Learn more");
                    mCardViewStudy.setVisibility(View.GONE);
                } else {
                    Log.i(TAG, "_checkListTodayExit:checkTodayExit == 432424");
                }

            } else if (checkTodayExit == -1) {
                Log.i(TAG, "_checkListTodayExit:today==-1");
                lbComplete.setText(LazzyBeeShare.EMPTY);
                //lbSuportCompletedCard.setText(LazzyBeeShare.EMPTY);
                btnStudy.setText("Study");
                btnStudy.setTag(true);
                btnCustomStudy.setTag(false);
                Log.i(TAG, "Study");
                mCardViewStudy.setVisibility(View.VISIBLE);

            }
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
        if (position == 1) {
            _gotoAddCourse();
        } else {
            // update the main content by replacing fragments
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            String courseId = "";
//            _gotoCourseDetails(courseId);
        }


//        switch (position) {
//            case 0:
//                String courseId = "";
//                _gotoCourseDetails(courseId);
//                break;
//            case 1:
//                //Goto List Course
//                FragmentListCourse fragmentListCourse = new FragmentListCourse();
//                //replace from container to fragmentCourse
//                fragmentTransaction.replace(R.id.container, fragmentListCourse)
//                        .addToBackStack(FragmentListCourse.TAG).commit();
//                break;
//            default:
////                courseId = "";
////                _gotoCourseDetails(courseId);
//        }


    }

    private void _gotoAddCourse() {
        //init intent
        Intent intent = new Intent(this, AddCourseActivity.class);
        //start intents
        startActivity(intent);


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
//                mTitle = getString(R.string.title_section2);
                break;
            case 3:
//                mTitle = getString(R.string.title_section3);
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
            MenuInflater inflater = getMenuInflater();
            // Inflate menu to add items to action bar if it is present.
            inflater.inflate(R.menu.main, menu);
            // Associate searchable configuration with the SearchView
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) menu.findItem(R.id.menu_search).getActionView();
//            searchView.setSearchableInfo(
//                    searchManager.getSearchableInfo(getComponentName()));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    // TODO Auto-generated method stub

                    Toast.makeText(getBaseContext(), query,
                            Toast.LENGTH_SHORT).show();
                    _gotoSeach(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // TODO Auto-generated method stub

                    //Toast.makeText(getBaseContext(), newText,Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
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
//                Toast.makeText(this, "BACK", Toast.LENGTH_SHORT).show();
//                onBackPressed();
                break;
            case R.id.action_settings:
                _gotoSetting();
                break;
            case R.id.action_login:
//                if (item.getTitle() == getString(R.string.action_login))
                _login();
//                else {
//                    _gotoProfile();
//                }
                break;
            case R.id.action_logout:
                //Log out Application
                Toast.makeText(this, getString(R.string.action_logout), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_check_update_database:
                //Check update app
                _checkUpdate();

                break;
            //case R.id.action_search:
            //Search
//                Toast.makeText(this, getString(R.string.action_search), Toast.LENGTH_SHORT).show();
//                _setUpSearchActionBar();
//                _gotoSeach("a");
            //
//                mSearchView.setIconified(false);
            //break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void _checkUpdate() {
        //Check vesion form server
        String db_v = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.DB_VERSION);
        int update_local_version = databaseUpgrade._getVersionDB();
        int _clientVesion;
        if (db_v == null) {
            _clientVesion = 0;
        } else {
            _clientVesion = Integer.valueOf(db_v);
        }

        if (_clientVesion == 0) {
            if (update_local_version == -1) {
                Log.i(TAG, "_checkUpdate():update_local_version == -1");
                _showComfirmUpdateDatabase(LazzyBeeShare.NO_DOWNLOAD_UPDATE);
            }
        } else {
            if (update_local_version > _clientVesion) {
                Log.i(TAG, "_checkUpdate():update_local_version > _clientVesion");
                _showComfirmUpdateDatabase(LazzyBeeShare.NO_DOWNLOAD_UPDATE);
            } else if (LazzyBeeShare.VERSION_SERVER > _clientVesion) {
                Log.i(TAG, "_checkUpdate():LazzyBeeShare.VERSION_SERVER > _clientVesion");
                _showComfirmUpdateDatabase(LazzyBeeShare.DOWNLOAD_UPDATE);
            } else {
                Log.i(TAG, "_checkUpdate():" + R.string.updated);
                Toast.makeText(context, R.string.updated, Toast.LENGTH_SHORT).show();
            }

        }


    }

    private void _showComfirmUpdateDatabase(final int type) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_update)
                .setTitle(R.string.dialog_title_update);

        // Add the buttons
        builder.setPositiveButton(R.string.btn_update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Update button
                //1.Download file from server
                //2.Open database
                //3.Upgade to my database
                //4.Remove file update
                if (type == LazzyBeeShare.DOWNLOAD_UPDATE) {
                    _downloadFile();
                } else {
                    _updateDB(type);
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();

    }

    private void _updateDB(int type) {
        try {

            databaseUpgrade.copyDataBase(type);
            List<Card> cards = databaseUpgrade._getAllCard();
            for (Card card : cards) {
                dataBaseHelper._insertOrUpdateCard(card);
            }
            dataBaseHelper._insertOrUpdateToSystemTable(LazzyBeeShare.DB_VERSION, String.valueOf(databaseUpgrade._getVersionDB()));
            databaseUpgrade.close();
        } catch (Exception e) {
            Log.e(TAG, "Update DB Error:" + e.getMessage());
            e.printStackTrace();
        }


    }

    private void _downloadFile() {


        DownloadFileUpdateDatabaseTask downloadFileUpdateDatabaseTask = new DownloadFileUpdateDatabaseTask(context);
        downloadFileUpdateDatabaseTask.execute(LazzyBeeShare.URL_DATABASE_UPDATE);
    }

    private boolean _compareToVersion(int clientVesion) {
        if (clientVesion == 0) {
            return true;
        } else {
            int update_local_version = databaseUpgrade._getVersionDB();
            if (update_local_version > clientVesion)
                return true;
            else if (LazzyBeeShare.VERSION_SERVER > clientVesion)
                return true;
            else
                return false;
        }

    }

    private void _login() {
        //Toast.makeText(context, getString(R.string.action_login), Toast.LENGTH_SHORT).show();
        client.startSignIn();


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
//        Toast.makeText(this, getString(R.string.action_settings), Toast.LENGTH_SHORT).show();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        //intit
//        FragmentSetting fragmentSetting = new FragmentSetting();
//        //replace from container to fragmentSetting
//        fragmentTransaction.replace(R.id.container, fragmentSetting)
//                .addToBackStack(FragmentSetting.TAG).commit();


        //init inten Setting
        Intent intent = new Intent(this, SettingActivity.class);
        //Start Intent
        this.startActivity(intent);
    }


    /**
     * Goto FragemenSearch with query
     */
    private void _gotoSeach(String query) {

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        //New fragmentSearch
//        FragmentSearch fragmentSearch = new FragmentSearch();
//        //New bunder
//        Bundle bundle = new Bundle();
//        //Set QUERY_TEXT
//        bundle.putString(FragmentSearch.QUERY_TEXT, query);
//        //setArguments for fragmentSearch
//        fragmentSearch.setArguments(bundle);
//        //replace from container to fragmentSearch
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, fragmentSearch)
//                .addToBackStack(FragmentSearch.TAG).commit();
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.QUERY_TEXT, query);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivityForResult(intent, 2);
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
        Log.i(TAG, "btnStudy:" + LazzyBeeShare.LEARN_MORE + ":" + btnStudy.getTag());
        _checkCompleteLearn();
        if (btnStudy.getTag() != null) {
            Intent intent = new Intent(getApplicationContext(), StudyActivity.class);
            this.startActivityForResult(intent, RESULT_OK);
        }
    }

    public void _onbtnCustomStudyOnClick(View view) {
        _learnMore();
    }

    private void _learnMore() {
        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_message_learn_more)
                .setTitle(R.string.dialog_title_learn_more);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Log.i(TAG, "btnCustomStudy:" + LazzyBeeShare.LEARN_MORE + ":" + btnCustomStudy.getTag());
                _checkCompleteLearn();
                if (btnCustomStudy.getTag() != null) {
                    Intent intent = new Intent(getApplicationContext(), StudyActivity.class);
                    intent.putExtra(LazzyBeeShare.LEARN_MORE, /*Cast tag to boolean*/(Boolean) btnCustomStudy.getTag());
                    startActivityForResult(intent, 1);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    public void _onbtnReviewOnClick(View view) {
        Toast.makeText(this, "Goto Review", Toast.LENGTH_SHORT).show();
        _gotoReviewToday();
    }

    private void _gotoReviewToday() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        //New fragmentReview
//        FragmentReviewToday fragmentReview = new FragmentReviewToday();
//        //New bunder
//        Bundle bundle = new Bundle();
//        //Set COURSE_ID
//        bundle.putString(FragmentReviewToday.COURSE_ID, "");
//        //setArguments for fragmentReview
//        fragmentReview.setArguments(bundle);
//        //replace from container to fragmentReview
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, fragmentReview)
//                .addToBackStack(FragmentReviewToday.TAG).commit();

        //init inten
        Intent intent = new Intent(this, ReviewCardActivity.class);
        //start intent
        startActivity(intent);
    }


    private void _gotoStudy(Object tag) {


//        FragmentManager fragmentManager = getSupportFragmentManager();
//        //New fragmentStudy
//        FragmentStudy fragmentStudy = new FragmentStudy();
//        //New bunder
//        Bundle bundle = new Bundle();
//        //Set COURSE_ID
//        bundle.putString(FragmentStudy.COURSE_ID, "");
//        //setArguments for fragmentStudy
//        fragmentStudy.setArguments(bundle);
//        //replace from container to fragmentStudy
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, fragmentStudy)
//                .addToBackStack(FragmentStudy.TAG).commit();

//        Log.i(TAG, LazzyBeeShare.LEARN_MORE + ":" + (Boolean) tag);
//
//        Intent intent = new Intent(this, StudyActivity.class);
//        intent.putExtra(LazzyBeeShare.LEARN_MORE, /*Cast tag to boolean*/(Boolean) tag);
//
//        this.startActivityForResult(intent, 1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //
        int backStackcount = getSupportFragmentManager().getBackStackEntryCount();
        //Log.i(TAG, "backStackcount:" + backStackcount);
        try {
            String back_stack = getSupportFragmentManager().getBackStackEntryAt(0).getName();
            Log.i(TAG, "back_stack:" + back_stack);
        } catch (Exception e) {
            this.finish();
            System.exit(0);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (!client.handleActivityResult(requestCode, resultCode, intent)) {
            super.onActivityResult(requestCode, resultCode, intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Resume");
        _checkCompleteLearn();
        _getCountCard();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (!client.handleIntent(intent)) {
            super.onNewIntent(intent);
        }
    }

    class DownloadFileUpdateDatabaseTask extends AsyncTask<String, Void, Void> {
        Context context;

        public DownloadFileUpdateDatabaseTask(Context context) {
            this.context = context;

        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                URL u = new URL(params[0]);

                File sdCard_dir = Environment.getExternalStorageDirectory();
                File file = new File(sdCard_dir.getAbsolutePath() + "/" + LazzyBeeShare.DOWNLOAD + "/" + LazzyBeeShare.DB_UPDATE_NAME);
                //dlDir.mkdirs();
                InputStream is = u.openStream();

                DataInputStream dis = new DataInputStream(is);

                byte[] buffer = new byte[1024];
                int length;

                FileOutputStream fos = new FileOutputStream(file);
                while ((length = dis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fos.close();
                Log.e("Download file update:", "Complete");
                _updateDB(LazzyBeeShare.DOWNLOAD_UPDATE);
            } catch (MalformedURLException mue) {
                Log.e("SYNC getUpdate", "malformed url error", mue);
            } catch (IOException ioe) {
                Log.e("SYNC getUpdate", "io error", ioe);
            } catch (SecurityException se) {
                Log.e("SYNC getUpdate", "security error", se);
            }
            return null;
        }
    }
}
