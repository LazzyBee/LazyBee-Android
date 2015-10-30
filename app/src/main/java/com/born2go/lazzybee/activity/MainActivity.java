package com.born2go.lazzybee.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.DownloadFileandUpdateDatabase;
import com.born2go.lazzybee.adapter.DownloadFileandUpdateDatabase.DownloadFileDatabaseResponse;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.db.DatabaseUpgrade;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.fragment.NavigationDrawerFragment;
import com.born2go.lazzybee.gtools.ContainerHolderSingleton;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.DataLayer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        DownloadFileDatabaseResponse {

    private Context context = this;
    private static final String TAG = "MainActivity";
    private static final Object GA_SCREEN = "aHomeScreen";

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
    LearnApiImplements dataBaseHelper;

    FrameLayout container;
    DrawerLayout drawerLayout;

    CardView mCardViewStudy;
    CardView mCardViewReView;
    CardView mCardViewLearnMore;

    CardView mCardViewCustomStudy;
    TextView lbNameCourse;
    TextView lbDueToday;
    TextView lbTotalNewCount;

    TextView lbTotalsCount;
    TextView lbReview;

    TextView lbStudy;
    TextView lbCustomStudy;


    TextView txtMessageCongratulation;
    InterstitialAd mInterstitialAd;

    RelativeLayout mDue, mCongratulations;
    LinearLayout mLine;

    boolean appPause = false;
    boolean studyComplete = false;

    int countCardNoLearn = 0;
    int complete = 0;

    SearchView searchView;

    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _initSQlIte();
        _initSettingApplication();

        _initToolBar();
        _intInterfaceView();

        _getCountCard();
        //Check complete Learn
        complete = _checkCompleteLearn();

        _initInterstitialAd();

        _trackerApplication();


    }


    private void _initInterstitialAd() {
        try {
            Container container = ContainerHolderSingleton.getContainerHolder().getContainer();
            String adb_ennable;
            String admob_pub_id = LazzyBeeShare.EMPTY;
            String adv_fullscreen_id = LazzyBeeShare.EMPTY;
            if (container == null) {
                adb_ennable = LazzyBeeShare.NO;
            } else {
                adb_ennable = container.getString(LazzyBeeShare.ADV_ENABLE);
                admob_pub_id = container.getString(LazzyBeeShare.ADMOB_PUB_ID);
                adv_fullscreen_id = container.getString(LazzyBeeShare.ADV_FULLSCREEB_ID);

            }
            String advId = admob_pub_id + "/" + adv_fullscreen_id;
            if (admob_pub_id == null || adv_fullscreen_id == null) {
                advId = "ca-app-pub-3940256099942544/1033173712";
            }
            Log.i(TAG, "adb_ennable ? " + adb_ennable);
            Log.i(TAG, "advId ? " + advId);

            if (adb_ennable.equals(LazzyBeeShare.YES)) {
                mInterstitialAd = new InterstitialAd(this);
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
                mInterstitialAd = null;
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    private void requestNewInterstitial() {
        if (mInterstitialAd != null) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(getResources().getStringArray(R.array.devices)[0])
                    .addTestDevice(getResources().getStringArray(R.array.devices)[1])
                    .build();

            mInterstitialAd.loadAd(adRequest);
        } else {
            Log.i(TAG, "mInterstitialAd null");
        }
    }


    private void _setUpNotification(boolean nextday) {
        Log.i(TAG, "---------setUpNotification-------");
        try {
            int hour = dataBaseHelper.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_HOUR_NOTIFICATION);
            int minute = dataBaseHelper.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_MINUTE_NOTIFICATION);
            if (hour == 0)
                hour = 8;//default time
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
            Log.e(TAG, "Set notificarion time:" + hour + ":" + minute);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
        Log.i(TAG, "---------END-------");
    }


    private void _initSettingApplication() {
        sharedpreferences = getSharedPreferences(LazzyBeeShare.MyPREFERENCES, Context.MODE_PRIVATE);
        if (_checkSetting(LazzyBeeShare.KEY_SETTING_AUTO_CHECK_UPDATE)) {
            _checkUpdate();
        }
        LazzyBeeShare._cancelNotification(context);
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


    private int _checkCompleteLearn() {
        int complete = dataBaseHelper.getSettingIntergerValuebyKey(String.valueOf(LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000));
        try {
            int check = dataBaseHelper._checkListTodayExit();
            int total = dataBaseHelper.getSettingIntergerValuebyKey(String.valueOf(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT));

            if (total == 0)
                total = LazzyBeeShare.DEFAULT_TOTAL_LEAN_PER_DAY;

            int countDue = dataBaseHelper._getCountListCardByQueue(Card.QUEUE_REV2, total);
            int countAgain = dataBaseHelper._getCountListCardByQueue(Card.QUEUE_LNR1, 0);

            Log.i(TAG, "_checkCompleteLearn:\t complete code:" + complete);
            Log.i(TAG, "_checkCompleteLearn:\t check code:" + check);
            int visibility = getResources().getInteger(R.integer.visibility_state_study0);
            //complete=0 chua hoc xong
            //complete>0
            if (complete == LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000) {
                //check > 0 hoc xong rui nhung van con card
                //check = 0 hoc het card rui
                //check < 0 chua hoc xong

                //state0 chua hoc song
                //state1 hoc xon mot luot va van con tu de hoc(trong ngay)
                //state2 hoc xong het rui
                if (check == -1) {
                    //ngay moi rui
                    Log.i(TAG, "_checkCompleteLearn:\t chua hoc xong");
                    visibility = getResources().getInteger(R.integer.visibility_state_study0);
                } else {
                    check = check + countDue + countAgain;
                    Log.i(TAG, "_checkCompleteLearn:\t check count:" + check);
                    if (check > 0) {
                        //inday finish Lession van cho hoc tiep
                        visibility = getResources().getInteger(R.integer.visibility_state_study1);
                        Log.i(TAG, "_checkCompleteLearn:\t hoc xong rui nhung van con card");
                    } else if (check == 0) {
                        //hoc het card rui
                        Log.i(TAG, "_checkCompleteLearn:\t hoc het card rui 1");
                        visibility = getResources().getInteger(R.integer.visibility_state_study2);
                    } else {
                        //chua hoc xong
                        Log.i(TAG, "_checkCompleteLearn:\t chua hoc xong");
                        visibility = getResources().getInteger(R.integer.visibility_state_study0);
                    }
                }
            } else if (complete == 0) {
                //chua hoc xong
                Log.i(TAG, "_checkCompleteLearn:\t chua hoc xong 2");
                visibility = getResources().getInteger(R.integer.visibility_state_study0);
            }
//        else {
//            //hoc het card rui
//            Log.i(TAG, "_checkCompleteLearn:\t hoc het card rui 2");
//            visibility = getResources().getInteger(R.integer.visibility_state_study2);
//        }
            _visibilityCount(visibility);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
        return complete;
    }


    private void _getCountCard() {
        Log.i(TAG, "--------------------------_getCountCard()------------------------------");
        try {
            String dueToday = dataBaseHelper._getStringDueToday();
            int allCount = dataBaseHelper._getCountAllListCard();
            int learnCount = dataBaseHelper._getCountListCardLearned();
            countCardNoLearn = allCount - learnCount;

            if (dueToday != null) {
                lbDueToday.setText(Html.fromHtml(dueToday));
            }
//            String reviewText = "<font color=" + context.getResources().getColor(R.color.teal_500) + "> " + getString(R.string.review) + "</font>" +
//                    "<font color=" + context.getResources().getColor(R.color.red_500) + ">(" + learnCount + ")</font>";
//            Log.i(TAG, "_getCountCard \t  reviewText:" + reviewText);
//            lbReview.setText(Html.fromHtml(reviewText));

            lbTotalsCount.setText(String.valueOf(allCount));
            lbTotalNewCount.setText(String.valueOf(countCardNoLearn));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
        Log.i(TAG, "-------------------------_getCountCard() END----------------------------\n");


    }

    private void _intInterfaceView() {
        container = (FrameLayout) findViewById(R.id.container);
        //Define Card View
        mCardViewStudy = (CardView) findViewById(R.id.mCardViewStudy);
        mCardViewReView = (CardView) findViewById(R.id.mCardViewReView);
        mCardViewLearnMore = (CardView) findViewById(R.id.mCardViewLearnMore);
        mCardViewCustomStudy = (CardView) findViewById(R.id.mCardViewCustomStudy);

        lbNameCourse = (TextView) findViewById(R.id.lbNameCourse);
        lbStudy = (TextView) findViewById(R.id.lbStudy);
        lbCustomStudy = (TextView) findViewById(R.id.lbCustomStudy);

        mDue = (RelativeLayout) findViewById(R.id.mDue);
        mCongratulations = (RelativeLayout) findViewById(R.id.mCongratulations);

        lbDueToday = (TextView) findViewById(R.id.lbDueToday2);
        lbTotalNewCount = (TextView) findViewById(R.id.lbTotalNewCount2);
        lbTotalsCount = (TextView) findViewById(R.id.lbTotalsCount2);

        mLine = (LinearLayout) findViewById(R.id.mLine);

        lbReview = (TextView) findViewById(R.id.lbReview);

        txtMessageCongratulation = (TextView) findViewById(R.id.txtMessageCongratulation);

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

        //Define message congratilation
        String messgage_congratilation = getString(R.string.message_congratulations_with_tip,
                "<b><u>" + getString(R.string.learn_more) + "</u></b>",
                "<b><u>" + getString(R.string.learned) + "</u></b>");

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


            mCongratulations.setVisibility(View.GONE);
            mLine.setVisibility(View.GONE);
            //set message continue
            messgage_congratilation = getString(R.string.message_congratulations_continue,
                    "<b><u>" + getString(R.string.study) + "</u></b>");
            // Toast.makeText(context, getString(R.string.congratulations), Toast.LENGTH_SHORT).show();

        } else if (visibilityCode == getResources().getInteger(R.integer.visibility_state_study2)
                || countCardNoLearn == 0) {
            //state2 hoc xong het rui & hoc het card rui
            mCardViewStudy.setVisibility(View.GONE);
            mDue.setVisibility(View.GONE);

            mLine.setVisibility(View.GONE);
            mCongratulations.setVisibility(View.GONE);
            //_showDialogCongraturation(messgage_congratilation);
            //Toast.makeText(context, getString(R.string.congratulations), Toast.LENGTH_SHORT).show();

        }
        //Log.i(TAG, "_visibilityCount \t message_congratulations:" + messgage_congratilation);
        txtMessageCongratulation.setText(Html.fromHtml(messgage_congratilation));

    }

    private void _showDialogCongraturation(String messgage_congratilation) {
        final Snackbar snackbar =
                Snackbar
                        .make(mCardViewReView, messgage_congratilation, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackBarView.setBackgroundColor(getResources().getColor(R.color.snackbar_background_color));
        snackbar.show();
    }

    private void _initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        _initNavigationDrawerFragment(toolbar);
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
                //Toast.makeText(context, R.string.under_construction, Toast.LENGTH_SHORT).show();
                _gotoAbout();
                break;
            case LazzyBeeShare.DRAWER_ADD_COURSE_INDEX:
                //_gotoAddCourse();
                Toast.makeText(context, R.string.under_construction, Toast.LENGTH_SHORT).show();
                break;
            case LazzyBeeShare.DRAWER_SETTINGS_INDEX:
                _gotoSetting();
                break;
            case LazzyBeeShare.DRAWER_USER_INDEX:
                //Toast.makeText(context, R.string.action_login, Toast.LENGTH_SHORT).show();
                break;
            case LazzyBeeShare.DRAWER_COURSE_INDEX:
                break;
            case LazzyBeeShare.DRAWER_DICTIONARY_INDEX:
                _gotoDictionary();
                break;
            default:
                break;


        }

    }

    private void _gotoDictionary() {
        _gotoSeachOrDictionary(LazzyBeeShare.GOTO_DICTIONARY, LazzyBeeShare.GOTO_DICTIONARY_CODE);

    }

    private void _gotoAbout() {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }

    private void _gotoAddCourse() {
        //_initInterstitialAd intent
        Intent intent = new Intent(this, AddCourseActivity.class);
        //start intents
        startActivity(intent);


    }


    public void _restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.app_name));
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
            searchView =
                    (SearchView) menu.findItem(R.id.menu_search).getActionView();
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                }
            });
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    _restoreActionBar();
                    return false;
                }
            });
            searchView.setQueryHint(getString(R.string.hint_search));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    _gotoSeachOrDictionary(query, LazzyBeeShare.GOTO_SEARCH_CODE);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
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
//            case R.id.action_settings:
//                _gotoSetting();
//                break;
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
//                _gotoSeachOrDictionary("a");
            //
//                mSearchView.setIconified(false);
            //break;
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
            LazzyBeeShare.showErrorOccurred(context, e);
            return false;
        }

//        //Check vesion form server
//        String db_v = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.DB_VERSION);
//
//        int update_local_version = databaseUpgrade._getVersionDB();
//        int _clientVesion;
//
//        //Check version
//        if (db_v == null) {
//            _clientVesion = 0;
//        } else {
//            _clientVesion = Integer.valueOf(db_v);
//        }
//
//        if (_clientVesion == 0) {
//            if (update_local_version == -1) {
//                Log.i(TAG, "_checkUpdate():update_local_version == -1");
//                _showComfirmUpdateDatabase(LazzyBeeShare.NO_DOWNLOAD_UPDATE);
//            }
//        } else {
//            if (update_local_version > _clientVesion) {
//                Log.i(TAG, "_checkUpdate():update_local_version > _clientVesion");
//                _showComfirmUpdateDatabase(LazzyBeeShare.NO_DOWNLOAD_UPDATE);
//            } else if (LazzyBeeShare.VERSION_SERVER > _clientVesion) {
//                Log.i(TAG, "_checkUpdate():LazzyBeeShare.VERSION_SERVER > _clientVesion");
//                _showComfirmUpdateDatabase(LazzyBeeShare.DOWNLOAD_UPDATE);
//            } else {
//                Log.i(TAG, "_checkUpdate():" + R.string.updated);
//                //Toast.makeText(context, R.string.updated, Toast.LENGTH_SHORT).show();
//            }
//
//        }


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
        Container container = ContainerHolderSingleton.getContainerHolder().getContainer();
        String base_url;
        if (container == null) {
            base_url = getString(R.string.url_lazzybee_website);
        } else {
            base_url = container.getString(LazzyBeeShare.BASE_URL_DB);

        }
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
    }

    @Override
    public void processFinish(int code) {
        if (code == 1) {
            //Dowload and update Complete
            if (!_checkUpdate())
                Toast.makeText(context, context.getString(R.string.mesage_update_database_successful), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getString(R.string.mesage_update_database_fails), Toast.LENGTH_SHORT).show();
        }
    }

    public void _onBtnStudyOnClick(View view) {
        if (countCardNoLearn == 0) {
            Toast.makeText(context, getString(R.string.message_no_new_card), Toast.LENGTH_SHORT).show();
        }
        _gotoStudy(getResources().getInteger(R.integer.goto_study_code0));

    }

    private void _gotoStudy(int type) {
        //goto_study_code0 study
        //goto_study_code1 learnmore

        //Toast.makeText(context, "Goto Study", Toast.LENGTH_SHORT).show();
        studyComplete = false;
        if (type == getResources().getInteger(R.integer.goto_study_code0)) {
            Intent intent = new Intent(getApplicationContext(), StudyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivityForResult(intent, LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000);

            //set Slide ac
//            overridePendingTransition(R.anim.slide_right, 0);
            //this.startActivityForResult(intent, RESULT_OK);

        } else if (type == getResources().getInteger(R.integer.goto_study_code1)) {
            Intent intent = new Intent(getApplicationContext(), StudyActivity.class);
            intent.putExtra(LazzyBeeShare.LEARN_MORE, true);
            this.startActivityForResult(intent, LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000);
            String key = String.valueOf(LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000);
            dataBaseHelper._insertOrUpdateToSystemTable(key, String.valueOf(1));
            // _setUpNotification();
        }
    }


    public void _onLearnMoreClick(View view) {
        if (countCardNoLearn == 0) {
            Toast.makeText(context, getString(R.string.message_no_new_card), Toast.LENGTH_SHORT).show();
        } else {
            // int finish = _checkCompleteLearn();
            Log.i(TAG, "Complete code:" + complete);
            if (complete == LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000) {
                _learnMore();
            } else {
//                Snackbar.make(container, getString(R.string.message_you_not_complete), Snackbar.LENGTH_LONG)
//                        .show();
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
        //Toast.makeText(this, "Goto Review", Toast.LENGTH_SHORT).show();
        _gotoReviewToday();
    }

    private void _gotoReviewToday() {
        //_initInterstitialAd inten
        Intent intent = new Intent(this, ReviewCardActivity.class);
        //start intent
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i(TAG, "onActivityResult \t requestCode:" + requestCode + ",resultCode:" + resultCode);
        if (requestCode == LazzyBeeShare.CODE_SEARCH_RESULT) {
            if (resultCode == 1
                    || requestCode == LazzyBeeShare.CODE_SEARCH_RESULT) {
                complete = _checkCompleteLearn();
                _getCountCard();
            }
            _restoreSearchView();
        }
        if (requestCode == LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000) {
            if (resultCode == LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000) {
                LazzyBeeShare._cancelNotification(context);
                _setUpNotification(true);
                String messgage_congratilation = getString(R.string.congratulations);
                _showDialogCongraturation(messgage_congratilation);
                studyComplete = true;

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putLong(LazzyBeeShare.KEY_TIME_COMPLETE_LEARN, new Date().getTime());
                editor.commit();
            } else {
                _showDialogTip();
            }
            complete = _checkCompleteLearn();
            _getCountCard();
        }
    }

    private void _restoreSearchView() {
        if (searchView != null) {
            searchView.setQuery(LazzyBeeShare.EMPTY, false);
//            searchView.clearFocus();
            searchView.setIconified(true);
        }
    }

    private void _showDialogTip() {
        try {
            Container container = ContainerHolderSingleton.getContainerHolder().getContainer();
            String popup_text;
            String popup_url = LazzyBeeShare.EMPTY;
            if (container == null) {
                popup_text = null;
            } else {
                popup_text = container.getString(LazzyBeeShare.POPUP_TEXT);
                popup_url = container.getString(LazzyBeeShare.POPUP_URL);

            }
            if (popup_text != null) {
                final Snackbar snackbar =
                        Snackbar
                                .make(mCardViewReView, popup_text, Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                final String finalPopup_url = popup_url;
                snackBarView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            snackbar.dismiss();
                            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalPopup_url));
                            startActivity(myIntent);
                        } catch (ActivityNotFoundException e) {
                            Log.e(TAG, "No application can handle this request."
                                    + " Please install a webbrowser");
                        }
                    }
                });
                snackBarView.setBackgroundColor(getResources().getColor(R.color.snackbar_background_color));

                snackbar.setDuration(7000).show();
            } else {
                Log.e(TAG, "popup_text null");
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        if (appPause) {
            //re check complete learn
            _checkCompleteLearn();
            _getCountCard();
        }
        LazzyBeeShare._cancelNotification(context);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
        appPause = true;
        Log.i(TAG, "studyComplete ?" + studyComplete);
        _setUpNotification(studyComplete);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    private void _trackerApplication() {
        try {
            DataLayer mDataLayer = LazzyBeeSingleton.mDataLayer;
            mDataLayer.pushEvent("openScreen", DataLayer.mapOf("screenName", GA_SCREEN));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

}
