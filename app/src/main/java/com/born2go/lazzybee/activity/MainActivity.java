package com.born2go.lazzybee.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.db.DatabaseUpgrade;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.fragment.FragmentCourse;
import com.born2go.lazzybee.fragment.FragmentDialogCustomStudy;
import com.born2go.lazzybee.fragment.FragmentProfile;
import com.born2go.lazzybee.fragment.NavigationDrawerFragment;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.utils.MyReceiver;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
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

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, FragmentDialogCustomStudy.DialogCustomStudyInferface, ConnectionCallbacks, OnConnectionFailedListener {

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
    CardView mCardViewReView;
    CardView mCardViewLearnMore;
    CardView mCardViewCustomStudy;

    TextView lbNameCourse;

    TextView lbDueToday;
    TextView lbTotalNewCount;
    TextView lbTotalsCount;


    RelativeLayout mDue, mCongratulations;
    FragmentDialogCustomStudy fragmentDialogCustomStudy;
    LinearLayout mLine;

    Button btnStudy;
    private LearnApiImplements dataBaseHelper;
    private Context context = this;

    private GitkitClient gitkitClient;
    private PendingIntent pendingIntent;
    GoogleApiClient mGoogleApiClient;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

    private boolean mSignInClicked;
    private static final int RC_SIGN_IN = 0;

    private ConnectionResult mConnectionResult;
    // Toolbar toolbar;
    List<PendingIntent> intentArray;
    AlarmManager alarmManager;

    InterstitialAd mInterstitialAd;

    int[] hour;

    // Allows us to notify the user that something happened in the background
    NotificationManager notificationManager;

    // Used to track notifications
    int notifID = 0;

    // Used to track if notification is active in the task bar
    boolean isNotificActive = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _checkAppVesion();
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        _checkLogin();
        _initSQlIte();

        _initSettingApplication();
        setContentView(R.layout.activity_main);
        //sp = PreferenceManager.getDefaultSharedPreferences(context);
//        toolbar = (Toolbar) findViewById(R.id.action_bar_main);
//        setSupportActionBar(toolbar);
        _initToolBar();
        _intInterfaceView();
        _getCountCard();
        _checkCompleteLearn();
        _initGoogleApiClient();

        dataBaseHelper._get100Card();
        _initInterstitialAd();

        _initShowcaseLazzyBee();


    }

    private void _initShowcaseLazzyBee() {
        String SHOWCASE_ID = getString(R.string.SHOWCASE_ID);
        new MaterialShowcaseView.Builder(this)
                .setTarget(mCardViewStudy)
                .setDismissText("GOT IT")
                .setContentText("This is start study")
                        //.setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .show();

        // sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(mCardViewReView,
                "This is goto review card", "GOT IT");

        sequence.addSequenceItem(mCardViewLearnMore,
                "This is learn more", "GOT IT");

        sequence.addSequenceItem(mCardViewCustomStudy,
                "This is custom study", "GOT IT");

        sequence.start();
    }

    private void _initInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                _gotoStudy(getResources().getInteger(R.integer.goto_study_code1));
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getResources().getStringArray(R.array.devices)[0])
                .addTestDevice(getResources().getStringArray(R.array.devices)[1])
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void _checkAppVesion() {

    }

    private void _initGoogleApiClient() {
        // Initializing google plus api client
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build())
//                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        // Build GoogleApiClient with access to basic profile
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(Plus.API)
//                .addScope(new Scope(Scopes.PROFILE))
//                .build();
    }

    private static int MID = 0;

    private void _setUpNotification() {

//        for (int i = 0; i < hour.length; i++) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.HOUR_OF_DAY, hour[0]);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.AM_PM,Calendar.PM);
//            Intent intent1 = new Intent(MainActivity.this, MyReceiver.class);
//            intent1.putExtra(LazzyBeeShare.INIT_NOTIFICATION, 0);
//            intent1.putExtra(LazzyBeeShare.NOTIFICATION_WHEN, calendar.getTimeInMillis());
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);
//
//        }

        for (int i = 0; i < hour.length; i++) {
            // Define a time
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.HOUR_OF_DAY, hour[i]);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.AM_PM, Calendar.PM);
//            Long alertTime = calendar.getTimeInMillis();
//
//            // Define our intention of executing AlertReceiver
//            Intent alertIntent = new Intent(this, MyReceiver.class);
//            alertIntent.putExtra(LazzyBeeShare.INIT_NOTIFICATION, i);
//
//            // Define pendingintent
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, alertIntent, 0);
//
//            // set() schedules an alarm
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alertTime, AlarmManager.INTERVAL_DAY, pendingIntent);

        }

    }

//    private void UnregisterAlarmBroadcast() {
//        alarmManager.cancel(pendingIntent);
//        getBaseContext().unregisterReceiver(myReceiver);
//    }

    private void _initSettingApplication() {
        hour = getResources().getIntArray(R.array.notification_hours);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        _changeLanguage();
        if (_checkSetting(LazzyBeeShare.KEY_SETTING_AUTO_CHECK_UPDATE)) {
            _checkUpdate();
        }
        if (_checkSetting(LazzyBeeShare.KEY_SETTING_NOTIFICTION)) {
            _cancelNotification();
            _setUpNotification();
        }


    }

    private boolean _checkSetting(String key) {
        String auto = dataBaseHelper._getValueFromSystemByKey(key);
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
            lang = LazzyBeeShare.LANG_VI;
        String languageToLoad = lang; // your language

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private int _checkCompleteLearn() {
        String value = dataBaseHelper._getValueFromSystemByKey(String.valueOf(LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000));
        String totalLearnCard = dataBaseHelper._getValueFromSystemByKey(String.valueOf(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT));

        int complete = 0;
        int check = dataBaseHelper._checkListTodayExit();
        int total = LazzyBeeShare.DEFAULT_TOTAL_LEAN_PER_DAY;
        if (totalLearnCard != null)
            total = Integer.valueOf(totalLearnCard);

        int countDue = dataBaseHelper._getListCardByQueue(Card.QUEUE_REV2, total).size();

        if (value != null) {
            complete = Integer.valueOf(value);
        }
        Log.i(TAG, "_checkCompleteLearn:\t complete code:" + check);
        int visibility = getResources().getInteger(R.integer.visibility_state_study0);
        //complete=0 chua hoc xong
        //complete=CODE_COMPLETE_STUDY_RESULTS_1000 hoc song mot lươt
        if (complete == LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000) {
            //check > 0 hoc xong rui nhung van con card
            //check = 0 hoc het card rui
            //check < 0 chua hoc xong
            //state0 chua hoc song
            //state1 hoc xon mot luot va van con tu de hoc(trong ngay)
            //state2 hoc xong het rui
            check = check + countDue;
            Log.i(TAG, "_checkCompleteLearn:\t check count:" + check);
            if (check > 0) {
                //inday finish Lession van cho hoc tiep
                visibility = getResources().getInteger(R.integer.visibility_state_study1);
                Log.i(TAG, "_checkCompleteLearn:\t hoc xong rui nhung van con card");
            } else if (check == 0) {
                //hoc het card rui
                Log.i(TAG, "_checkCompleteLearn:\t hoc het card rui");
                visibility = getResources().getInteger(R.integer.visibility_state_study2);
            } else {
                //chua hoc xong
                Log.i(TAG, "_checkCompleteLearn:\t chua hoc xong");
                visibility = getResources().getInteger(R.integer.visibility_state_study0);
            }
        } else if (complete == 0) {
            //Chua hoc xong
            Log.i(TAG, "_checkCompleteLearn:\t chua hoc xong 2");
            visibility = getResources().getInteger(R.integer.visibility_state_study0);
        }
        _visibilityCount(visibility);
        return complete;
    }

    private void _cancelNotification() {
        notificationManager.cancelAll();
        for (int i = 0; i < hour.length; i++) {
            Intent intent = new Intent(this, MyReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, i, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
        }
        // If the notification is still active close it
        //notificationManager.cancel(notifID);

    }

    private void _getCountCard() {
        Log.i(TAG, "--------------------------_getCountCard()------------------------------\n");

        String dueToday = dataBaseHelper._getStringDueToday();
        int allCount = dataBaseHelper._getAllListCard().size();
        int learnCount = dataBaseHelper._getListCardLearned().size();

        Log.i(TAG, "-------------------------------END-------------------------------------\n");

        if (dueToday != null) {
            lbDueToday.setText(Html.fromHtml(dueToday));
        }
        lbTotalsCount.setText(String.valueOf(allCount));
        lbTotalNewCount.setText(String.valueOf(learnCount));

    }

    private void _intInterfaceView() {
        //Define Card View
        mCardViewStudy = (CardView) findViewById(R.id.mCardViewStudy);
        mCardViewReView = (CardView) findViewById(R.id.mCardViewReView);
        mCardViewLearnMore = (CardView) findViewById(R.id.mCardViewLearnMore);
        mCardViewCustomStudy = (CardView) findViewById(R.id.mCardViewCustomStudy);

        lbNameCourse = (TextView) findViewById(R.id.lbNameCourse);


        mDue = (RelativeLayout) findViewById(R.id.mDue);
        mCongratulations = (RelativeLayout) findViewById(R.id.mCongratulations);

        lbDueToday = (TextView) findViewById(R.id.lbDueToday2);
        lbTotalNewCount = (TextView) findViewById(R.id.lbTotalNewCount2);
        lbTotalsCount = (TextView) findViewById(R.id.lbTotalsCount2);

        mLine = (LinearLayout) findViewById(R.id.mLine);


        TextView lbTipHelp = (TextView) findViewById(R.id.lbTipHelp);
        lbTipHelp.setText("****************************" + getString(R.string.url_lazzybee_website) + "****************************");
        lbTipHelp.setSelected(true);
        //lbTipHelp.setTypeface(null, Typeface.BOLD);
        lbTipHelp.setSingleLine();
        lbTipHelp.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        lbTipHelp.setHorizontallyScrolling(true);
    }

    public void onlbTipHelpClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_lazzybee_website)));
        startActivity(browserIntent);
    }

    private void _visibilityCount(int visibilityCode) {
        //state0 chua hoc song
        //state1 hoc xon mot luot va van con tu de hoc(trong ngay)
        //state2 hoc xong het rui
        int learnerdCount = dataBaseHelper._getListCardLearned().size();
        if (visibilityCode == getResources().getInteger(R.integer.visibility_state_study0)) {
            //state0 chua hoc song
            mCardViewStudy.setVisibility(View.VISIBLE);
            mDue.setVisibility(View.VISIBLE);
            mCongratulations.setVisibility(View.GONE);
            mLine.setVisibility(View.GONE);

        } else if (visibilityCode == getResources().getInteger(R.integer.visibility_state_study1)) {
            //state1 hoc xon mot luot va van con tu de hoc(trong ngay)
            mDue.setVisibility(View.VISIBLE);
            mCardViewStudy.setVisibility(View.VISIBLE);
            mCongratulations.setVisibility(View.VISIBLE);
            mLine.setVisibility(View.VISIBLE);

        } else if (visibilityCode == getResources().getInteger(R.integer.visibility_state_study2)
                || learnerdCount == 0) {
            //state2 hoc xong het rui & hoc het card rui
            mCardViewStudy.setVisibility(View.GONE);
            mDue.setVisibility(View.GONE);
            mCongratulations.setVisibility(View.VISIBLE);
            mLine.setVisibility(View.VISIBLE);
        }

//        if (visibility) {
//            mDue.setVisibility(View.VISIBLE);
//            mCongratulations.setVisibility(View.GONE);
//            mLine.setVisibility(View.GONE);
//        } else {
//            mDue.setVisibility(View.GONE);
//            mCongratulations.setVisibility(View.VISIBLE);
//            mLine.setVisibility(View.VISIBLE);
//        }
    }

    private void _initToolBar() {
        _initNavigationDrawerFragment(null);
    }


    /**
     * Check login
     */
    private void _checkLogin() {
        gitkitClient = GitkitClient.newBuilder(this, new GitkitClient.SignInCallbacks() {
            @Override
            public void onSignIn(IdToken idToken, GitkitUser gitkitUser) {
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
        myDbHelper = LazzyBeeSingleton.dataBaseHelper;
        databaseUpgrade = LazzyBeeSingleton.databaseUpgrade;
        try {
            myDbHelper._createDataBase();
        } catch (IOException ioe) {
            //throw new Error("Unable to create database");
            //ioe.printStackTrace();
            Log.e(TAG, "Unable to create database:" + ioe.getMessage());

        }
        dataBaseHelper = LazzyBeeSingleton.learnApiImplements;
    }


    /**
     * Init NavigationDrawerFragment
     *
     * @param toolbar
     */
    private void _initNavigationDrawerFragment(Toolbar toolbar) {
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
//        mTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer, toolbar,
                drawerLayout);
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case LazzyBeeShare.DRAWER_ABOUT_INDEX:
                Toast.makeText(context, R.string.under_construction, Toast.LENGTH_SHORT).show();
                break;
            case LazzyBeeShare.DRAWER_ADD_COURSE_INDEX:
                //_gotoAddCourse();
                Toast.makeText(context, R.string.under_construction, Toast.LENGTH_SHORT).show();
                break;
            case LazzyBeeShare.DRAWER_SETTINGS_INDEX:
                _gotoSetting();
                break;
            case LazzyBeeShare.DRAWER_USER_INDEX:
                Toast.makeText(context, R.string.action_login, Toast.LENGTH_SHORT).show();
                break;
            case LazzyBeeShare.DRAWER_COURSE_INDEX:
                break;
            default:
                break;


        }

    }

    private void _gotoAddCourse() {
        //_initInterstitialAd intent
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
            // return true;
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
//            case R.id.action_login:
////                if (item.getTitle() == getString(R.string.action_login))
//                _login();
////                signInWithGplus();
////                else {
////                    _gotoProfile();
////                }
//                break;
//            case R.id.action_logout:
//                //Log out Application
//                Toast.makeText(this, getString(R.string.action_logout), Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.action_check_update_database:
//                //Check update app
//                _checkUpdate();
//
//                break;
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

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
//        if (!mGoogleApiClient.isConnecting()) {
//            mSignInClicked = true;
//            resolveSignInError();
//        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
//        if (mConnectionResult.hasResolution()) {
//            try {
//                mIntentInProgress = true;
//                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
//            } catch (IntentSender.SendIntentException e) {
//                mIntentInProgress = false;
//                mGoogleApiClient.connect();
//            }
//        }
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
        gitkitClient.startSignIn();


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
     * Goto setting
     */
    private void _gotoSetting() {
        //_initInterstitialAd inten Setting
        Intent intent = new Intent(this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Start Intent
        this.startActivity(intent);
    }


    /**
     * Goto FragemenSearch with query
     */
    private void _gotoSeach(String query) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.QUERY_TEXT, query);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivityForResult(intent, LazzyBeeShare.CODE_SEARCH_RESULT);
    }

    @Override
    public void _finishCustomStudy() {
        //fragmentDialogCustomStudy.dismiss();
        //fragmentDialogCustomStudy.
        fragmentDialogCustomStudy.setCustomStudyAdapter();
        _getCountCard();

    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        getProfileInformation();

        // Update the UI after signin
        // updateUI(true);
    }

    private void getProfileInformation() {

    }

    @Override
    public void onConnectionSuspended(int i) {
        // mGoogleApiClient.connect();
        //updateUI(false);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            Log.e(TAG, "ConnectionFailed:" + result.getErrorCode());
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // mGoogleApiClient.connect();
        _stopNotificationServices();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
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
        int learnerdCount = dataBaseHelper._getListCardLearned().size();
        if (learnerdCount == 0) {
            Toast.makeText(context, "Ban da hoc het tu moi", Toast.LENGTH_SHORT).show();
        } else {
        }
        _gotoStudy(getResources().getInteger(R.integer.goto_study_code0));
    }

    private void _gotoStudy(int type) {
        //goto_study_code0 study
        //goto_study_code1 learnmore

        Toast.makeText(context, "Goto Study", Toast.LENGTH_SHORT).show();
        if (type == getResources().getInteger(R.integer.goto_study_code0)) {
            Intent intent = new Intent(getApplicationContext(), StudyActivity.class);
            this.startActivityForResult(intent, LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000);
            //this.startActivityForResult(intent, RESULT_OK);

        } else if (type == getResources().getInteger(R.integer.goto_study_code1)) {
            Intent intent = new Intent(getApplicationContext(), StudyActivity.class);
            intent.putExtra(LazzyBeeShare.LEARN_MORE, true);
            startActivityForResult(intent, LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000);
            String key = String.valueOf(LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000);
            dataBaseHelper._insertOrUpdateToSystemTable(key, String.valueOf(1));
            // _setUpNotification();
        }


    }


    public void _onCustomStudyOnClick(View view) {
        //_gotoSetting();
        _showDialogCustomStudy();

    }

    private void _showDialogCustomStudy() {
        FragmentManager fm = getSupportFragmentManager();
        fragmentDialogCustomStudy = new FragmentDialogCustomStudy();
        fragmentDialogCustomStudy.show(fm, FragmentDialogCustomStudy.TAG);

    }

    public void _onLearnMoreClick(View view) {
        int learnerdCount = dataBaseHelper._getListCardLearned().size();
        if (learnerdCount == 0) {
            Toast.makeText(context, "Ban da hoc het tu moi", Toast.LENGTH_SHORT).show();
        } else {
            int finish = _checkCompleteLearn();
            Log.i(TAG, "Complet code:" + finish);
            if (finish == LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000) {
                _learnMore();
            } else {
                Toast.makeText(context, R.string.message_you_not_complete, Toast.LENGTH_SHORT).show();
            }
        }

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
                //_checkCompleteLearn(0);
                if (mInterstitialAd.isLoaded()) {
                    Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show();
                    mInterstitialAd.show();

                } else {
                    //ko load van sang study
                    _gotoStudy(getResources().getInteger(R.integer.goto_study_code1));

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

//    private void _gotoStudyLearnMore() {
//
//    }

    public void _onbtnReviewOnClick(View view) {
        Toast.makeText(this, "Goto Review", Toast.LENGTH_SHORT).show();
        _gotoReviewToday();
    }

    private void _gotoReviewToday() {
        //_initInterstitialAd inten
        Intent intent = new Intent(this, ReviewCardActivity.class);
        //start intent
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        //
        //int backStackcount = getSupportFragmentManager().getBackStackEntryCount();
        //Log.i(TAG, "backStackcount:" + backStackcount);
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        int init = sp.getInt(LazzyBeeShare.INIT_NOTIFICATION, 2);
        Log.i(TAG, "_initInterstitialAd noti:" + init);
        sp.edit().putInt(LazzyBeeShare.INIT_NOTIFICATION, 1).commit();
        Log.i(TAG, "b _initInterstitialAd noti:" + init);
        _startNotificationServices();
        //this.finish();
        System.exit(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i(TAG, "requestCode:" + requestCode + ",resultCode:" + resultCode);
        if (requestCode == LazzyBeeShare.CODE_SEARCH_RESULT) {
            //(resultCode);
            _checkCompleteLearn();
            _getCountCard();

        }
        if (requestCode == LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000) {
            _checkCompleteLearnbyResultCode(resultCode);
            _getCountCard();
        }
//        if (requestCode == 1) {
//            _getCountCard();
//        }
        if (fragmentDialogCustomStudy != null) {
            //fragmentDialogCustomStudy.dismiss();
        }
//        if (!gitkitClient.handleActivityResult(requestCode, resultCode, intent)) {
//            super.onActivityResult(requestCode, resultCode, intent);
//        }

//        if (requestCode == RC_SIGN_IN) {
//            if (resultCode != RESULT_OK) {
//                mSignInClicked = false;
//            }
//
//            mIntentInProgress = false;
//
//            if (!mGoogleApiClient.isConnecting()) {
//            }
//                mGoogleApiClient.connect();
//        }
    }

    private void _checkCompleteLearnbyResultCode(int resultCode) {
        if (resultCode == LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000 || resultCode == LazzyBeeShare.CODE_SEARCH_RESULT) {
            //Complete
            String value = String.valueOf(LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000);
            dataBaseHelper._insertOrUpdateToSystemTable(value, value);
            // _visibilityCount(false, visibility);
        } else {
            String value = String.valueOf(LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000);
            dataBaseHelper._insertOrUpdateToSystemTable(value, String.valueOf(1));
            // _visibilityCount(true, visibility);


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        _checkCompleteLearn();
//        if (fragmentDialogCustomStudy != null)
//            fragmentDialogCustomStudy.dismiss();
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        sp.edit().putInt(LazzyBeeShare.INIT_NOTIFICATION, 2).commit();
        _stopNotificationServices();

    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (!gitkitClient.handleIntent(intent)) {
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        sp.edit().putInt(LazzyBeeShare.INIT_NOTIFICATION, 1).commit();
        _startNotificationServices();
    }

    private void _startNotificationServices() {
//        Intent service1 = new Intent(context, MyAlarmService.class);
//        context.startService(service1);
    }

    private void _stopNotificationServices() {
//        Intent service1 = new Intent(context, MyAlarmService.class);
//        context.stopService(service1);
    }

    @Override
    protected void onDestroy() {
        // unregisterReceiver(myReceiver);
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

}
