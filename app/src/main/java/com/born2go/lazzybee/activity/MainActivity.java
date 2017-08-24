package com.born2go.lazzybee.activity;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.BackUpDatabaseToCSV;
import com.born2go.lazzybee.adapter.DownloadFileandUpdateDatabase;
import com.born2go.lazzybee.adapter.DownloadFileandUpdateDatabase.DownloadFileDatabaseResponse;
import com.born2go.lazzybee.adapter.SuggestionCardAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.db.DatabaseUpgrade;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.view.dialog.DialogHelp;
import com.born2go.lazzybee.view.dialog.DialogStatistics;
import com.born2go.lazzybee.fragment.NavigationDrawerFragment;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.born2go.lazzybee.db.DataBaseHelper.KEY_QUESTION;
import static com.born2go.lazzybee.db.impl.LearnApiImplements.TABLE_VOCABULARY;


public class MainActivity extends AppCompatActivity
        implements
        DownloadFileDatabaseResponse,
        NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener,
        RewardedVideoAdListener {

    private Context context = this;
    private static final String TAG = "MainActivity";
    private static final Object GA_SCREEN = "aHomeScreen";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private MainActivity activity;
    DataBaseHelper myDbHelper;
    DatabaseUpgrade databaseUpgrade;
    LearnApiImplements dataBaseHelper;

    //FrameLayout container;
    DrawerLayout drawerLayout;

    InterstitialAd mInterstitialAd;

    boolean appPause = false;
    boolean studyComplete = false;

    SharedPreferences sharedpreferences;
    private int KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT;

    Snackbar snackbarCongraturation;
    Snackbar snackbarTip;

    SearchView mSearchCardBox;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton floatingActionButton;
    private String adv_pub_id;


    private RewardedVideoAd mAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.activity = this;
        _initSQlIte();
        _initSettingApplication();

        _initToolBar();
        _intInterfaceView();

        _initDictinarySearchBox();

        _initInterstitialAd();

        _trackerApplication();

        _goHome();

        _initAdvFillStreak();

        _checkFillStreak();

    }

    private void _initAdvFillStreak() {
        mAd = MobileAds.getRewardedVideoAdInstance(context);
        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getResources().getStringArray(R.array.devices)[0])
                .addTestDevice(getResources().getStringArray(R.array.devices)[1])
                .addTestDevice(getResources().getStringArray(R.array.devices)[2])
                .addTestDevice(getResources().getStringArray(R.array.devices)[3])
                .addTestDevice("467009F00ED542DDA1694F88F807A79A")
                .build();
        //load video
        mAd.setRewardedVideoAdListener(this);
        LazzyBeeSingleton.getFirebaseRemoteConfig().fetch(LazzyBeeShare.CACHE_EXPIRATION).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String adv_streak_saver = null;
                if (task.isSuccessful()) {
                    adv_streak_saver = LazzyBeeSingleton.getFirebaseRemoteConfig().getString(LazzyBeeShare.ADV_STREAK_SAVER);
                }
                if (adv_pub_id != null && adv_streak_saver != null) {
                    mAd.loadAd(adv_pub_id + "/" + adv_streak_saver, adRequest);
                }

            }
        });

    }


    private void _initDictinarySearchBox() {
        //Define Search Dictionary box
        mSearchCardBox = (SearchView) findViewById(R.id.mSearchCard);
        mSearchCardBox.setIconifiedByDefault(false);
        mSearchCardBox.setQueryHint(getString(R.string.drawer_dictionary));

        //set provaider search
        //final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        //mSearchCardBox.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        SearchView.SearchAutoComplete autoCompleteTextView = (SearchView.SearchAutoComplete) mSearchCardBox.findViewById(R.id.search_src_text);

        autoCompleteTextView.setDropDownBackgroundResource(android.R.color.white);
        //Custom search
        // Hide icon search in searchView and set clear text icon
        ImageView search_close_btn = (ImageView) mSearchCardBox.findViewById(R.id.search_close_btn);
        if (search_close_btn != null) {
            search_close_btn.setImageDrawable(LazzyBeeShare.getDraweble(context, R.drawable.ic_clear_black_18dp));
        }
        ImageView magImage = (ImageView) mSearchCardBox.findViewById(R.id.search_mag_icon);
        if (magImage != null) {
            magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            magImage.setVisibility(View.GONE);
        }
        //set color..
        if (autoCompleteTextView != null) {
            //set Enable Spelling Suggestions
            autoCompleteTextView.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
            int color = Color.parseColor("#FFFFFF");
            Drawable drawable = autoCompleteTextView.getDropDownBackground();
            drawable.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.MULTIPLY);

            autoCompleteTextView.setDropDownBackgroundDrawable(drawable);
            autoCompleteTextView.setTextColor(getResources().getColor(R.color.grey_900));
            autoCompleteTextView.setHintTextColor(getResources().getColor(R.color.grey_600));
        }

        //query
        mSearchCardBox.setOnQueryTextListener(this);
        mSearchCardBox.setOnSuggestionListener(this);

//        //Handler click item suggesstion
//        mSearchCardBox.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
//            @Override
//            public boolean onSuggestionSelect(int position) {
//                Log.d(TAG, "onSuggestionSelect:" + position);
//                return false;
//            }
//
//            @Override
//            public boolean onSuggestionClick(int position) {
//                Log.d(TAG, "onSuggestionClick:" + position);
//                try {
//                    CursorAdapter c = mSearchCardBox.getSuggestionsAdapter();
//                    mFirebaseAnalytics.logEvent(LazzyBeeShare.FA_OPEN_SEARCH_HINT_HOME, new Bundle());
//                    if (c != null) {
//                        Cursor cur = c.getCursor();
//                        cur.moveToPosition(position);
//
//                        String cardID = cur.getString(cur.getColumnIndex(BaseColumns._ID));
//                        Log.d(TAG, "cardID:" + cardID);
//                        String query = cur.getString(cur.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
//                        Log.d(TAG, "query:" + query);
//                        int insertSuggesstionResults = dataBaseHelper._insertSuggesstion(cardID);
//                        Log.d(TAG, "insertSuggesstionResults " + ((insertSuggesstionResults == -1) ? " OK" : " Fails"));
//                        _gotoCardDetailbyCardId(cardID);
//
//                        //call back actionbar
//                        //mSearchCardBox.clearFocus();
//                    } else {
//                        Log.d(TAG, "NUll searchView.getSuggestionsAdapter()");
//                    }
//                } catch (Exception e) {
//                    LazzyBeeShare.showErrorOccurred(context, "_defineSearchView", e);
//                }
//                return true;
//            }
//        });


    }

    public void _checkFillStreak() {
        if (mAd.isLoaded())//Ads is load to check
            //get number in remote config firebase
            LazzyBeeSingleton.getFirebaseRemoteConfig().fetch(LazzyBeeShare.CACHE_EXPIRATION).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    int numberToFillSteak = 0;// Integer.valueOf(LazzyBeeShare.DEFAULT_STREAK_SAVER);
                    if (task.isSuccessful()) {
                        numberToFillSteak = Integer.valueOf(LazzyBeeSingleton.getFirebaseRemoteConfig().getString(LazzyBeeShare.STREAK_SAVER));
                    }
                    //Check streack
                    if (dataBaseHelper.getTotalDayStudy() > numberToFillSteak && dataBaseHelper._getCountStreak() < numberToFillSteak) {
                        //Show dialog view video to fill streak
                        _showDialogConfirmFillStreak();

                    } else {
                        Log.d(TAG, "Not record to fill streak");
                    }
                }
            });
          else Log.d(TAG,"Ads save streak not loader");

    }

    private void _showDialogConfirmFillStreak() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);
        builder.setTitle("Ops!");
        builder.setMessage("Play Video to fill streak?");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAd.show();
                dialog.dismiss();

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        // Get the AlertDialog from create()
        final AlertDialog dialog = builder.create();

        dialog.show();
    }


    private void _initInterstitialAd() {
        try {
            final String admob_pub_id = adv_pub_id;//"ca-app-pub-5245864792816840";
            final String[] adv_fullscreen_id = {LazzyBeeSingleton.getFirebaseRemoteConfig().getString(LazzyBeeShare.ADV_FULLSCREEB_ID)};//"9210342219";
            LazzyBeeSingleton.getFirebaseRemoteConfig().fetch(LazzyBeeShare.CACHE_EXPIRATION).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        adv_fullscreen_id[0] = LazzyBeeSingleton.getFirebaseRemoteConfig().getString(LazzyBeeShare.ADV_FULLSCREEB_ID);
                    }
                    if (admob_pub_id != null && adv_fullscreen_id[0] != null) {
                        String advId = admob_pub_id + "/" + adv_fullscreen_id[0];
                        Log.d(TAG, "adv_fullscreen_id:" + advId);
                        mInterstitialAd = new InterstitialAd(context);
                        mInterstitialAd.setAdUnitId(advId);

                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                requestNewInterstitial();
                                _gotoStudy(getResources().getInteger(R.integer.goto_study_code1));
                            }
                        });

                        requestNewInterstitial();
                    } else {
                        Log.d(TAG, "InterstitialAdId null");
                        mInterstitialAd = null;
                    }
                }
            });

        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_initInterstitialAd", e);
        }
    }

    private void requestNewInterstitial() {
        if (mInterstitialAd != null) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(getResources().getStringArray(R.array.devices)[0])
                    .addTestDevice(getResources().getStringArray(R.array.devices)[1])
                    .addTestDevice(getResources().getStringArray(R.array.devices)[2])
                    .addTestDevice(getResources().getStringArray(R.array.devices)[3])
                    .addTestDevice("467009F00ED542DDA1694F88F807A79A")
                    .build();

            mInterstitialAd.loadAd(adRequest);
        } else {
            Log.d(TAG, "mInterstitialAd null");
        }
    }


    private void _setUpNotification(boolean nextday) {
        Log.i(TAG, "---------setUpNotification-------");
        try {
            int hour = dataBaseHelper.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_HOUR_NOTIFICATION);
            int minute = dataBaseHelper.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_MINUTE_NOTIFICATION);
            //Check currentTime
            Calendar currentCalendar = Calendar.getInstance();
            int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);


            Calendar calendar = Calendar.getInstance();
            if (hour <= currentHour || nextday) {
                calendar.add(Calendar.DATE, 1);
            }
            // Define a time
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            //
            Long alertTime = calendar.getTimeInMillis();
            //Toast.makeText(context, "Alert time:" + alertTime, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Alert " + 0 + ",time:" + alertTime);

            //set notificaion by time
            LazzyBeeShare.scheduleNotification(context, 0, alertTime);
            Log.d(TAG, "Set notificarion time:" + hour + ":" + minute + " -" + calendar.getTime().toString());
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_setUpNotification", e);
        }
        Log.i(TAG, "---------END-------");
    }


    private void _initSettingApplication() {
        adv_pub_id = getIntent().getStringExtra(LazzyBeeShare.ADMOB_PUB_ID);
        sharedpreferences = getSharedPreferences(LazzyBeeShare.MyPREFERENCES, Context.MODE_PRIVATE);
        if (_checkSetting(LazzyBeeShare.KEY_SETTING_AUTO_CHECK_UPDATE)) {
            _checkUpdate();
        }
        LazzyBeeShare._cancelNotification(context);
        boolean first_run_app = sharedpreferences.getBoolean(LazzyBeeShare.KEY_FIRST_RUN_APP, false);
        if (!first_run_app) {
            _showHelp();
            sharedpreferences.edit().putBoolean(LazzyBeeShare.KEY_FIRST_RUN_APP, true).commit();
            dataBaseHelper._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_NOTIFICTION, LazzyBeeShare.ON);
            LazzyBeeShare._setUpNotification(context, LazzyBeeShare.DEFAULT_HOUR_NOTIFICATION, LazzyBeeShare.DEFAULT_MINUTE_NOTIFICATION);
        }

        //
        boolean custom_list = sharedpreferences.getBoolean(LazzyBeeShare.KEY_CUSTOM_LIST, false);
        if (!custom_list) {
            dataBaseHelper.addColumCustomList();
            sharedpreferences.edit().putBoolean(LazzyBeeShare.KEY_CUSTOM_LIST, true).commit();
        }


        String onoffNotification = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_NOTIFICTION);
        if (onoffNotification == null) {
            dataBaseHelper._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_NOTIFICTION, LazzyBeeShare.ON);
            LazzyBeeShare._setUpNotification(context, LazzyBeeShare.DEFAULT_HOUR_NOTIFICATION, LazzyBeeShare.DEFAULT_MINUTE_NOTIFICATION);
        }
        KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT = dataBaseHelper.getSettingIntergerValuebyKey(String.valueOf(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT));


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

    private void _intInterfaceView() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
//        container = (FrameLayout) findViewById(R.id.mContainer);
//        container.requestFocus();
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setPadding(0, 0, 0, 0);
    }

    public void onlbTipHelpClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_lazzybee_website)));
        startActivity(browserIntent);
    }


    private void _showDialogCongraturation(String messgage_congratilation) {
        snackbarCongraturation =
                Snackbar
                        .make(this.coordinatorLayout, messgage_congratilation, Snackbar.LENGTH_LONG);
        View snackBarView = snackbarCongraturation.getView();
        snackBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbarCongraturation.dismiss();
            }
        });
        snackBarView.setBackgroundColor(getResources().getColor(R.color.snackbar_background_color));
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                snackbarCongraturation.show();
            }
        }.start();

    }

    private void _initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //_initNavigationDrawerFragment(toolbar);

        _initDrawer(toolbar);


    }

    private void _initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //reset major
                String mMajorValue = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_MY_SUBJECT);
                String mMajor = null;
                MenuItem mItemSelectMajor = navigationView.getMenu().getItem(1);
                if (mMajorValue != null) {
                    if (mMajorValue.equals(context.getString(R.string.subject_it_value)))
                        mMajor = context.getString(R.string.subject_it);
                    else if (mMajorValue.equals(context.getString(R.string.subject_economy_value)))
                        mMajor = context.getString(R.string.subject_economy);
                    else if (mMajorValue.equals(context.getString(R.string.subject_science_value)))
                        mMajor = context.getString(R.string.subject_science);
                    else if (mMajorValue.equals(context.getString(R.string.subject_medical_value)))
                        mMajor = context.getString(R.string.subject_medical);
                    else if (mMajorValue.equals(context.getString(R.string.subject_ielts_value)))
                        mMajor = context.getString(R.string.subject_ielts);
                    else if (mMajorValue.equals(context.getString(R.string.subject_600_toeic_value)))
                        mMajor = context.getString(R.string.subject_600toeic);
                    else
                        mMajor = null;
                }
                if (mMajor != null) {
                    mItemSelectMajor.setTitle(context.getString(R.string.drawer_subject) + " (" + mMajor + ")");
                } else {
                    mItemSelectMajor.setTitle(context.getString(R.string.drawer_subject));
                }
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

        //set version app
        try {
            String versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
            TextView lbAppVersion = (TextView) findViewById(R.id.mVesionApp);
            lbAppVersion.setText("Version:" + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Init Sql
     */
    private void _initSQlIte() {
        myDbHelper = LazzyBeeSingleton.dataBaseHelper;
        databaseUpgrade = LazzyBeeSingleton.databaseUpgrade;
        dataBaseHelper = LazzyBeeSingleton.learnApiImplements;
    }


    /**
     * Init NavigationDrawerFragment
     *
     * @param toolbar
     */
    private void _initNavigationDrawerFragment(Toolbar toolbar) {
        try {
//            mNavigationDrawerFragment = (NavigationDrawerFragment)
//                    getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
//            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//            // Set up the drawer.
//            mNavigationDrawerFragment.setUp(
//                    R.id.navigation_drawer, toolbar,
//                    drawerLayout);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_initNavigationDrawerFragment", e);
        }
    }


    //    @Override
//    public void onNavigationDrawerItemSelected(int position) {
//        try {
//            switch (position) {
//                case LazzyBeeShare.DRAWER_ABOUT_INDEX:
//                    //Toast.makeText(context, R.string.under_construction, Toast.LENGTH_SHORT).show();
//                    _gotoAbout();
//                    break;
//                case LazzyBeeShare.DRAWER_ADD_COURSE_INDEX:
//                    //_gotoAddCourse();
//                    Toast.makeText(context, R.string.under_construction, Toast.LENGTH_SHORT).show();
//                    break;
//                case LazzyBeeShare.DRAWER_SETTINGS_INDEX:
//                    _gotoSetting();
//
//                    break;
//                case LazzyBeeShare.DRAWER_USER_INDEX:
//                    //Toast.makeText(context, R.string.action_login, Toast.LENGTH_SHORT).show();
//                    break;
//                case LazzyBeeShare.DRAWER_COURSE_INDEX:
//                    break;
//                case LazzyBeeShare.DRAWER_DICTIONARY_INDEX:
//                    _gotoDictionary();
//                    break;
//                case LazzyBeeShare.DRAWER_MAJOR_INDEX:
//                    showSelectMajor();
//                    break;
//                case LazzyBeeShare.DRAWER_HELP_INDEX:
//                    _showHelp();
//                    break;
//                case LazzyBeeShare.DRAWER_STATISTICAL_INDEX:
//                    _showStatistical();
//                    break;
//                case LazzyBeeShare.DRAWER_HOME_INDEX:
//                    _goHome();
//                    break;
//                case LazzyBeeShare.DRAWER_TEST_YOUR_VOCA_INDEX:
//                    _goTestYourVoca();
//                    break;
//                default:
//                    break;
//            }
//        } catch (Exception e) {
//            LazzyBeeShare.showErrorOccurred(context, "onNavigationDrawerItemSelected", e);
//        }
//
//
//    }
//
    private void _goTestYourVoca() {
        LazzyBeeSingleton.getFirebaseAnalytics().logEvent(LazzyBeeShare.FA_OPEN_TEST_YOUR_VOCA, new Bundle());
        if (LazzyBeeShare.checkConn(context)) {
            Intent intent = new Intent(context, TestYourVoca.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
        } else {
            Toast.makeText(context, R.string.failed_to_connect_to_server, Toast.LENGTH_SHORT).show();
        }
    }

    private void _goHome() {
        //getSupportFragmentManager().beginTransaction().add(R.id.mContainer, new ViewHome()).commit();
    }

    private void _showStatistical() {
        LazzyBeeSingleton.getFirebaseAnalytics().logEvent(LazzyBeeShare.FA_OPEN_LEARNING_PROGRESS, new Bundle());
        try {
            DialogStatistics dialogStatistics = new DialogStatistics(context);
            dialogStatistics.show(getSupportFragmentManager(), DialogStatistics.TAG);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_showStatistical", e);
        }

    }

    private void showSelectMajor() {
        LazzyBeeSingleton.getFirebaseAnalytics().logEvent(LazzyBeeShare.FA_OPEN_CHOOSE_MAJOR, new Bundle());
        View mSelectMajor = View.inflate(context, R.layout.view_select_major, null);
        final CheckBox cbIt = (CheckBox) mSelectMajor.findViewById(R.id.cbIt);
        final CheckBox cbEconomy = (CheckBox) mSelectMajor.findViewById(R.id.cbEconomy);
        final CheckBox cbScience = (CheckBox) mSelectMajor.findViewById(R.id.cbScience);
        final CheckBox cbMedicine = (CheckBox) mSelectMajor.findViewById(R.id.cbMedicine);
        final CheckBox cbIelts = (CheckBox) mSelectMajor.findViewById(R.id.cbIelts);
        final CheckBox cbx600Toeic = (CheckBox) mSelectMajor.findViewById(R.id.cbx600Toeic);

        //get my subbject
        String my_subject = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_MY_SUBJECT);
        if (my_subject == null) {
            cbEconomy.setChecked(false);
            cbScience.setChecked(false);
            cbMedicine.setChecked(false);
            cbIelts.setChecked(false);
            cbIt.setChecked(false);
            cbx600Toeic.setChecked(false);
        } else {
            Log.d(TAG, "MY_SUBJECT:" + my_subject);
            if (my_subject.equals(getString(R.string.subject_it_value))) {
                cbIt.setChecked(true);
            } else if (my_subject.equals(getString(R.string.subject_economy_value))) {
                cbEconomy.setChecked(true);
            } else if (my_subject.equals(getString(R.string.subject_science_value))) {
                cbScience.setChecked(true);
            } else if (my_subject.equals(getString(R.string.subject_medical_value))) {
                cbMedicine.setChecked(true);
            } else if (my_subject.equals(getString(R.string.subject_ielts_value))) {
                cbIelts.setChecked(true);
            } else if (my_subject.equals(getString(R.string.subject_600_toeic_value))) {
                cbx600Toeic.setChecked(true);
            }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);
        builder.setTitle(context.getString(R.string.select_subject_title));
        final CharSequence[] items_value = context.getResources().getStringArray(R.array.subjects_value);
        final int[] seleted_index = {0};
        final int[] checker = {-1};
        cbIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    // Put some meat on the sandwich
                    cbEconomy.setChecked(false);
                    cbScience.setChecked(false);
                    cbMedicine.setChecked(false);
                    cbIelts.setChecked(false);
                    cbx600Toeic.setChecked(false);
                    checker[0] = 0;
                } else {
                    checker[0] = -2;
                }
            }
        });
        cbEconomy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    // Put some meat on the sandwich
                    cbIt.setChecked(false);
                    cbScience.setChecked(false);
                    cbMedicine.setChecked(false);
                    cbIelts.setChecked(false);
                    cbx600Toeic.setChecked(false);
                    checker[0] = 1;
                } else {
                    checker[0] = -2;
                }
            }
        });
        cbScience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    // Put some meat on the sandwich
                    cbIt.setChecked(false);
                    cbEconomy.setChecked(false);
                    cbMedicine.setChecked(false);
                    cbIelts.setChecked(false);
                    cbx600Toeic.setChecked(false);
                    checker[0] = 2;
                } else {
                    checker[0] = -2;
                }
            }
        });
        cbMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    // Put some meat on the sandwich
                    cbIt.setChecked(false);
                    cbEconomy.setChecked(false);
                    cbScience.setChecked(false);
                    cbIelts.setChecked(false);
                    cbx600Toeic.setChecked(false);
                    checker[0] = 3;
                } else {
                    checker[0] = -2;
                }
            }
        });
        cbIelts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    // Put some meat on the sandwich
                    cbIt.setChecked(false);
                    cbEconomy.setChecked(false);
                    cbScience.setChecked(false);
                    cbMedicine.setChecked(false);
                    cbx600Toeic.setChecked(false);
                    checker[0] = 4;
                } else {
                    checker[0] = -2;
                }
            }
        });
        cbx600Toeic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    // Put some meat on the sandwich
                    cbIt.setChecked(false);
                    cbEconomy.setChecked(false);
                    cbScience.setChecked(false);
                    cbMedicine.setChecked(false);
                    cbIelts.setChecked(false);
                    checker[0] = 5;
                } else {
                    checker[0] = -2;
                }
            }
        });
        builder.setView(mSelectMajor);

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int mylevel = dataBaseHelper.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_MY_LEVEL);
                String subjectSelected;
                if (checker[0] > -1) {
                    subjectSelected = String.valueOf(items_value[checker[0]]);
                    //save my subjects
                    dataBaseHelper._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_MY_SUBJECT, subjectSelected);

                    //reset incomming list
                    dataBaseHelper._initIncomingCardIdListbyLevelandSubject(mylevel, subjectSelected);
                    LazzyBeeSingleton.getFirebaseAnalytics().setUserProperty("Selected_major", String.valueOf(subjectSelected));
                } else if (checker[0] == -2) {
                    //save my subjects
                    dataBaseHelper._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_MY_SUBJECT, LazzyBeeShare.EMPTY);

                    //reset incomming list
                    dataBaseHelper._initIncomingCardIdList();
                    LazzyBeeSingleton.getFirebaseAnalytics().setUserProperty("Selected_major", String.valueOf(""));
                }

                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void _gotoDictionary() {
        LazzyBeeSingleton.getFirebaseAnalytics().logEvent(LazzyBeeShare.FA_OPEN_DICTIONARY, new Bundle());
        //_gotoSeachOrDictionary(LazzyBeeShare.GOTO_DICTIONARY, LazzyBeeShare.GOTO_DICTIONARY_CODE);
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.QUERY_TEXT, LazzyBeeShare.GOTO_DICTIONARY);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(LazzyBeeShare.ACTION_GOTO_DICTIONARY);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        this.startActivityForResult(intent, LazzyBeeShare.CODE_SEARCH_RESULT);
        //startActivity(intent);


    }

    private void _gotoAbout() {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }

    private void _showDialogWithMessage(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);
        builder.setTitle("Ops!");
        builder.setMessage(Html.fromHtml(message));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // Get the AlertDialog from create()
        final AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void _showHelp() {
        DialogHelp dialogHelp = new DialogHelp();
        dialogHelp.show(getSupportFragmentManager(), DialogHelp.TAG);
    }


    public void _restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.app_name));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    private void _dismissTip() {
        if (snackbarCongraturation != null) {
            Log.d(TAG, "Stop snackbar Congraturation");
            snackbarCongraturation.dismiss();
        }
        if (snackbarTip != null) {
            Log.d(TAG, "Stop snackbar Tip");
            snackbarTip.dismiss();
        }
    }


    private void _gotoCardDetailbyCardId(String cardId) {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        intent.putExtra(LazzyBeeShare.CARDID, cardId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivityForResult(intent, getResources().getInteger(R.integer.code_card_details_updated));
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
                break;

        }


        return super.onOptionsItemSelected(item);
    }


    private boolean _checkUpdate() {
        try {
            if (dataBaseHelper._checkUpdateDataBase()) {
                Log.i(TAG, "Co Update");
                Toast.makeText(context, "Co Update", Toast.LENGTH_SHORT).show();
                _showComfirmUpdateDatabase(LazzyBeeShare.DOWNLOAD_UPDATE);
                return true;
            } else {
                Toast.makeText(context, "Khong co Update", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Khong co Update");
                return false;
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_checkUpdate", e);
            return false;
        }
    }

    private void _showComfirmUpdateDatabase(final int type) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);

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
        // Container container = LazzyBeeSingleton.getContainerHolder().getContainer();
        String base_url = "http://222.255.29.25/lazzybee/";
//        if (container == null) {
//            base_url = getString(R.string.url_lazzybee_website);
//        } else {
//            base_url = container.getString(LazzyBeeShare.BASE_URL_DB);
//
//        }
        String db_v = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.DB_VERSION);
        int version = LazzyBeeShare.DEFAULT_VERSION_DB;
        if (db_v != null) {
            version = Integer.valueOf(db_v);
        }
        String dbUpdateName = (version + 1) + ".db";
        String download_url = base_url + dbUpdateName;
        Log.i(TAG, "download_url=" + download_url);

        if (!base_url.isEmpty() || base_url != null) {

            DownloadFileandUpdateDatabase downloadFileandUpdateDatabase = new DownloadFileandUpdateDatabase(context, version + 1);

            //downloadFileandUpdateDatabase.execute(LazzyBeeShare.URL_DATABASE_UPDATE);
            downloadFileandUpdateDatabase.execute(download_url);
            downloadFileandUpdateDatabase.downloadFileDatabaseResponse = this;
        } else {
            Toast.makeText(context, R.string.message_download_database_fail, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Goto setting
     */
    private void _gotoSetting() {
        LazzyBeeSingleton.getFirebaseAnalytics().logEvent(LazzyBeeShare.FA_OPEN_SETTING, new Bundle());
        //_initInterstitialAd inten Setting
        Intent intent = new Intent(this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Start Intent
        this.startActivity(intent);
    }


    /**
     * Goto FragemenSearch with query_text
     */
    private void _gotoSeachOrDictionary(String query, int type) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.DISPLAY_TYPE, type);
        intent.putExtra(SearchActivity.QUERY_TEXT, query);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        this.startActivityForResult(intent, LazzyBeeShare.CODE_SEARCH_RESULT);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        _dismissTip();
    }

    @Override
    public void processFinish(int code) {
        if (code == 1) {
            //Download and update Complete
            if (!_checkUpdate())
                Toast.makeText(context, context.getString(R.string.mesage_update_database_successful), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getString(R.string.mesage_update_database_fails), Toast.LENGTH_SHORT).show();
        }
    }

    public void _onBtnStudyOnClick(View view) {
        int countDue = dataBaseHelper._getCountListCardByQueue(Card.QUEUE_REV2, KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT);
        if (countDue > 0) {
            Log.d(TAG, "onBtnStudyOnClick\t-countDue:" + countDue);
            _gotoStudy(getResources().getInteger(R.integer.goto_study_code0));
        } else {
            int countAgain = dataBaseHelper._getCountListCardByQueue(Card.QUEUE_LNR1, 0);
            if (countAgain > 0) {
                Log.d(TAG, "onBtnStudyOnClick\t-countAgain:" + countAgain);
                _gotoStudy(getResources().getInteger(R.integer.goto_study_code0));
            } else {
                int check = dataBaseHelper._checkListTodayExit();
                Log.d(TAG, "onBtnStudyOnClick\t-queueList:" + check);
                if (check == -1 || check == -2 || check > 0) {
                    _gotoStudy(getResources().getInteger(R.integer.goto_study_code0));
                } else if (check == 0) {
                    String message = getString(R.string.congratulations_learnmore, " '<b>" + getString(R.string.learn_more) + "</b>' ");
                    _showDialogWithMessage(message);
                }
            }
        }
    }

    private void _gotoStudy(int type) {
        //goto_study_code0 study
        //goto_study_code1 learnmore
        studyComplete = false;
        Intent intent = new Intent(getApplicationContext(), StudyActivity.class);
        intent.setAction(LazzyBeeShare.STUDY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if (type == getResources().getInteger(R.integer.goto_study_code0)) {
            this.startActivityForResult(intent, LazzyBeeShare.ACTION_CODE_GOTO_STUDY);
        } else if (type == getResources().getInteger(R.integer.goto_study_code1)) {
            intent.putExtra(LazzyBeeShare.LEARN_MORE, true);
            this.startActivityForResult(intent, LazzyBeeShare.ACTION_CODE_GOTO_STUDY);
        }
    }


    public void _onLearnMoreClick(View view) {
        int countDue = dataBaseHelper._getCountListCardByQueue(Card.QUEUE_REV2, KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT);
        if (countDue > 0) {
            Log.d(TAG, "_onLearnMoreClick:\t -countDue:" + countDue);
            _showDialogWithMessage(getString(R.string.message_you_not_complete));
        } else {
            int countAgain = dataBaseHelper._getCountListCardByQueue(Card.QUEUE_LNR1, 0);
            if (countAgain > 0) {
                Log.d(TAG, "_onLearnMoreClick:\t -countAgain:" + countAgain);
                _showDialogWithMessage(getString(R.string.message_you_not_complete));
            } else {
                int check = dataBaseHelper._checkListTodayExit();
                Log.d(TAG, "_onLearnMoreClick:\t -queueList:" + check);
                if (check == -1 || check == -2 || check > 0) {
                    _showDialogWithMessage(getString(R.string.message_you_not_complete));
                } else if (check == 0) {
                    _learnMore();
                }
            }
        }
    }

    private void _learnMore() {
        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_message_learn_more)
                .setTitle(R.string.dialog_title_learn_more);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                if (mInterstitialAd == null) {
                    _gotoStudy(getResources().getInteger(R.integer.goto_study_code1));
                } else {
                    if (mInterstitialAd.isLoaded()) {
                        // Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show();
                        mInterstitialAd.show();

                    } else {
                        //ko load van sang study
                        _gotoStudy(getResources().getInteger(R.integer.goto_study_code1));

                    }
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
        LazzyBeeSingleton.getFirebaseAnalytics().logEvent(LazzyBeeShare.FA_OPEN_INCOMING, new Bundle());
        Intent intent = new Intent(this, IncomingListActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d(TAG, "onActivityResult \t requestCode:" + requestCode + ",resultCode:" + resultCode);
        if (requestCode == LazzyBeeShare.ACTION_CODE_GOTO_STUDY) {
            if (resultCode == LazzyBeeShare.CODE_COMPLETE_STUDY_1000) {
                // Log.d(TAG, "Congratilation study LazzyBee");

                //Reset notification
                LazzyBeeShare._cancelNotification(context);
                _setUpNotification(true);

                int count = dataBaseHelper._getCountStreak();
                Log.d(TAG, "Congratilation study LazzyBee,Streak count:" + count);
                if (count % 10 == 0) {
                    //Show dialog backup db
                    _showDialogBackupDataBase();
                } else {
                    //Show message congratilation
                    String messgage_congratilation = getString(R.string.congratulations);
                    _showDialogCongraturation(messgage_congratilation);
                    _checkFillStreak();
                }


                //Save time congratilation in SharedPreferences
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putLong(LazzyBeeShare.KEY_TIME_COMPLETE_LEARN, new Date().getTime());
                editor.commit();

            } else {
                Log.d(TAG, "Not congratilation study LazzyBee");
                _showDialogTip();
            }
        }
    }

    private void _showDialogBackupDataBase() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.message_backup_database)
                .setTitle(R.string.title_backup_database);

        // Add the buttons
        builder.setPositiveButton(R.string.btnBackUp, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //  _updateDB(type);
                String device_id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                int mini = 1;
                BackUpDatabaseToCSV exportDatabaseToCSV = new BackUpDatabaseToCSV(activity, context, device_id, mini);
                exportDatabaseToCSV.execute();
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

//    private void _restoreSearchView() {
//        if (searchView != null) {
//            searchView.setQuery(LazzyBeeShare.EMPTY, false);
////            searchView.clearFocus();
//            searchView.setIconified(true);
//        }
//    }


    private void _showDialogTip() {
        try {
//            Container container = LazzyBeeSingleton.getContainerHolder().getContainer();
            //String pop_up_maxnum="1";
            String popup_text = "Cải tiến chất lượng âm thanh trên Android";
            String popup_url = "http://www.lazzybee.com/blog/android_improve_voice_quality";
//            if (container == null) {
//                popup_text = null;
//                Log.d(TAG, "ContainerHolder Null");
//            } else {
//                pop_up_maxnum = container.getString(LazzyBeeShare.POPUP_MAXNUM);
//                if (pop_up_maxnum == null || pop_up_maxnum.equals(LazzyBeeShare.EMPTY)) {
//                    popup_text = container.getString(LazzyBeeShare.POPUP_TEXT);
//                    popup_url = container.getString(LazzyBeeShare.POPUP_URL);
//                    Log.d(TAG, "pop_up_maxnum Null");
//                } else {
//
//                    Log.d(TAG, "pop_up_maxnum:" + pop_up_maxnum);
//                    int number = LazzyBeeShare.showRandomInteger(1, Integer.valueOf(pop_up_maxnum), new Random());
//                    Log.d(TAG, "Random pop:" + number);
//                    popup_text = container.getString(LazzyBeeShare.POPUP_TEXT + number);
//                    popup_url = container.getString(LazzyBeeShare.POPUP_URL + number);
//                    Log.d(TAG, "popup_text:" + popup_text + ",popup_url:" + popup_url);
//                }
//
//            }
            if (popup_text != null) {
                snackbarTip =
                        Snackbar
                                .make(this.coordinatorLayout, popup_text, Snackbar.LENGTH_INDEFINITE);

                View snackBarView = snackbarTip.getView();
                final String finalPopup_url = popup_url;
                snackBarView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            snackbarTip.dismiss();
                            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalPopup_url));
                            startActivity(myIntent);
                        } catch (ActivityNotFoundException e) {
                            Log.e(TAG, "No application can handle this request."
                                    + " Please install a webbrowser");
                        }
                    }
                });
                snackBarView.setBackgroundColor(getResources().getColor(R.color.snackbar_background_color));

                snackbarTip.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        snackbarTip.dismiss();
                    }
                }, 7000);
//                new CountDownTimer(3000, 1000) {
//                    public void onTick(long millisUntilFinished) {
//                    }
//
//                    public void onFinish() {
//
//                    }
//                }.start();
            } else {
                Log.e(TAG, "popup_text null");
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_showDialogTip", e);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        LazzyBeeShare._cancelNotification(context);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
        appPause = true;
        Log.d(TAG, "studyComplete ?" + studyComplete);
        //_setUpNotification(studyComplete);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
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

    public void onSearchDictionary(View view) {
        String query = mSearchCardBox.getQuery().toString();
        if (query.length() > 0) {
            //goto Search
            //_gotoSeachOrDictionary(query, LazzyBeeShare.GOTO_SEARCH_CODE);
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra(SearchActivity.DISPLAY_TYPE, LazzyBeeShare.GOTO_SEARCH_CODE);
            intent.putExtra(SearchManager.QUERY, query);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.setAction(Intent.ACTION_SEARCH);
            startActivity(intent);
        } else {
            //Suggestion word
            //mAutoSearchDictionaryBox.setFocusable(true);
            //mAutoSearchDictionaryBox.setFocusableInTouchMode(true);
            mSearchCardBox.requestFocus();

            //show keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mSearchCardBox, InputMethodManager.SHOW_IMPLICIT);
        }

    }

    public void removeAllFocus(View view) {
        // mAutoSearchDictionaryBox.clearFocus();
        //mSearch.clearFocus();

        mSearchCardBox.clearFocus();

        _hideKeyboard();
    }

    public void _hideKeyboard() {
        //hide keyboad
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void onBtnStudyReverseOnClick(View view) {
        int countDue = dataBaseHelper._getCountListCardByQueue(Card.QUEUE_REV2, KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT);
        if (countDue > 0) {
            Log.d(TAG, "onBtnStudyReverseOnClick() -countDue :" + countDue);
            _showDialogWithMessage(getString(R.string.msg_finishing_your_daily_target));
        } else {
            int countAgain = dataBaseHelper._getCountListCardByQueue(Card.QUEUE_LNR1, 0);
            if (countAgain > 0) {
                Log.d(TAG, "onBtnStudyReverseOnClick() -countAgain :" + countAgain);
                _showDialogWithMessage(getString(R.string.msg_finishing_your_daily_target));
            } else {
                int check = dataBaseHelper._checkListTodayExit();
                Log.d(TAG, "onBtnStudyReverseOnClick() -queueList :" + check);
                if (check == -1 || check == -2 || check > 0) {
                    _showDialogWithMessage(getString(R.string.msg_finishing_your_daily_target));
                } else if (check == 0) {
                    int countCardLearner = dataBaseHelper._getCountListCardLearned();
                    Log.d(TAG, "onBtnStudyReverseOnClick() -countCardLearner :" + countCardLearner);
                    if (countCardLearner >= LazzyBeeShare.LIMIT_UNLOCK_FERTURE_STUDY_REVERSER) {
                        studyReverse();
                    } else {
                        _showDialogWithMessage(getString(R.string.msg_limit_unlock_feture_study_reverse));
                    }
                }
            }
        }
    }

    private void studyReverse() {
        studyComplete = false;
        Intent intent = new Intent(getApplicationContext(), StudyActivity.class);
        intent.setAction(LazzyBeeShare.REVERSE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivityForResult(intent, LazzyBeeShare.ACTION_CODE_GOTO_STUDY);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_major) {
            showSelectMajor();
        } else if (id == R.id.nav_statistical) {
            _showStatistical();
        } else if (id == R.id.nav_statistical) {
            _showStatistical();
        } else if (id == R.id.nav_test_your_voca) {
            _goTestYourVoca();
        } else if (id == R.id.nav_dictionary) {
            _gotoDictionary();
        } else if (id == R.id.nav_setting) {
            _gotoSetting();
        } else if (id == R.id.nav_help) {
            _showHelp();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.trim() != null) {
            if (query.trim().length() > 2) {
                Intent intent = new Intent(this, SearchActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchActivity.QUERY_TEXT, query);
                intent.putExtra(SearchManager.QUERY, query);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                this.startActivityForResult(intent, LazzyBeeShare.CODE_SEARCH_RESULT);
                return true;
            } else {
                Log.d(TAG, "query is short");
                return false;
            }

        } else {
            Log.d(TAG, "query is empty");
            return false;
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        return searchCard(query);
    }

    private boolean searchCard(String query) {
        if (query.trim() != null) {
            if (query.trim().length() > 2) {

                String likeQuery = "SELECT vocabulary.id,vocabulary.question,vocabulary.answers,vocabulary.level,rowid _id FROM " + TABLE_VOCABULARY + " WHERE "
                        + KEY_QUESTION + " like '" + query + "%' OR "
                        + KEY_QUESTION + " like '% " + query + "%'"
                        + " ORDER BY " + KEY_QUESTION + " LIMIT 50";

                SQLiteDatabase db = LazzyBeeSingleton.dataBaseHelper.getReadableDatabase();
                try {
                    Cursor cursor = db.rawQuery(likeQuery, null);
                    SuggestionCardAdapter suggestionCardAdapter = new SuggestionCardAdapter(context, cursor);
                    mSearchCardBox.setSuggestionsAdapter(suggestionCardAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Log.d(TAG, "query suggetion");
                }
                return true;
            } else {
                Log.d(TAG, "query is short");
                return false;
            }

        } else {
            Log.d(TAG, "query is empty");
            return false;
        }
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        return false;
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.d(TAG, "onRewardedVideoAdLoaded");
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.d(TAG, "onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.d(TAG, "onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        //FillStreak
        Log.d(TAG, "onRewardedVideoAdClosed");
        _fillStreak();
    }

    private void _fillStreak() {
        dataBaseHelper.fillStreak(7);
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.d(TAG, "onRewarded");
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d(TAG, "onRewardedVideoAdLeftApplication");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.d(TAG, "onRewardedVideoAdFailedToLoad");
        //_fillStreak();
    }
}

