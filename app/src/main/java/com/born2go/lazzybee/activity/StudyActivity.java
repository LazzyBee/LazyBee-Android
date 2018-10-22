package com.born2go.lazzybee.activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.DisableScrollingViewPager;
import com.born2go.lazzybee.adapter.SuggestionCardAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.view.DetailsView;

import com.born2go.lazzybee.view.StudyView;
import com.born2go.lazzybee.view.StudyView.OnStudyViewListener;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.view.dialog.DialogCompleteStudy;
import com.google.firebase.analytics.FirebaseAnalytics;

import static com.born2go.lazzybee.db.DataBaseHelper.KEY_QUESTION;
import static com.born2go.lazzybee.db.impl.LearnApiImplements.TABLE_VOCABULARY;

public class StudyActivity extends AppCompatActivity
        implements DialogCompleteStudy.ICompleteSutdy, OnStudyViewListener {

    private static final String TAG = "StudyActivity";
    private static final String GA_SCREEN = "aStudyScreen";
    private Context context;
    private final FirebaseAnalytics mFirebaseAnalytics = LazzyBeeSingleton.getFirebaseAnalytics();

    LearnApiImplements dataBaseHelper;


    //Current Card
    Card currentCard = new Card();
    //Define before card
    Card beforeCard;

    boolean learn_more;
    int completeStudy = 0;

    LinearLayout container;
    DisableScrollingViewPager mViewPager;
    ScreenSlidePagerAdapter pagerAdapter;

    private String detailViewTag;

    public void setBeforeCard(Card beforeCard) {
        this.beforeCard = beforeCard;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        context = this;
        _initActonBar();
        _initDatabase();
        _initView();
        _definePagerStudy();

        _trackerApplication();
    }


    private void _definePagerStudy() {
        try {
            //get lean_more form intern
            learn_more = getIntent().getBooleanExtra(LazzyBeeShare.LEARN_MORE, false);
            pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), mViewPager);
            mViewPager.setAdapter(pagerAdapter);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_definePagerStudy()", e);
        }
    }


    private void _initActonBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void _trackerApplication() {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("screenName", GA_SCREEN);
            mFirebaseAnalytics.logEvent("screenName", bundle);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_trackerApplication", e);
        }
    }

    private void _completeLean(boolean showCompleteDialog) {
        Log.i(TAG, "----_completeLean----");
        setBeforeCard(null);
        completeStudy = LazzyBeeShare.CODE_COMPLETE_STUDY_1000;
        dataBaseHelper._insertOrUpdateToSystemTable(String.valueOf(LazzyBeeShare.CODE_COMPLETE_STUDY_1000), String.valueOf(completeStudy));

        if (showCompleteDialog) {
            int result = dataBaseHelper._insetStreak();
            Log.d(TAG, "Inseart streaks result:" + result);
            if (result > -1) {
                _showDialogComplete();
            } else {
                if (learn_more)
                    _showDialogCompleteMore();
                else {
                    setResult(RESULT_CANCELED, new Intent());
                    finish();
                }
            }
        } else {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
        Log.i(TAG, "---------END---------");

    }

    private void _showDialogCompleteMore() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);
        builder.setTitle("Ops!");
        builder.setMessage("Complete study!!!");
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            setResult(RESULT_CANCELED, new Intent());
            finish();
        });
        Dialog dialog = builder.create();
        dialog.show();
        mFirebaseAnalytics.logEvent(LazzyBeeShare.FA_OPEN_STREAK_CONGRATULATION, new Bundle());
    }

    private void _showDialogComplete() {
        if (learn_more == false) {
            //Show dialog complete learn
            final DialogCompleteStudy dialogCompleteStudy = new DialogCompleteStudy(context);
            dialogCompleteStudy.show(getFragmentManager().beginTransaction(), LazzyBeeShare.EMPTY);

            int count = LazzyBeeSingleton.learnApiImplements._getCountStreak();
            Bundle bundle = new Bundle();
            mFirebaseAnalytics.logEvent("Streak", bundle);
            bundle.putString("count", String.valueOf(count));
        } else {
            _showDialogCompleteMore();
        }
    }

    /**
     * Init db sqlite
     */
    private void _initDatabase() {
        dataBaseHelper = LazzyBeeSingleton.learnApiImplements;
    }


    private void _initView() {
        container = findViewById(R.id.container);
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setPagingEnabled(false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                _setDisplayPageByPosition(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void _setDisplayPageByPosition(int position) {
        if (position == 0) {
            setTitle(R.string.title_activity_study);
        } else {
            setTitle(currentCard.getQuestion());
        }
    }

    public void onlbTipHelpClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_lazzybee_website)));
        startActivity(browserIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_study, menu);
        _defineSearchView(menu);
        return true;
    }


    private void _defineSearchView(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
//        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // Theme the SearchView's AutoCompleteTextView drop down. For some reason this wasn't working in styles.xml
        SearchView.SearchAutoComplete autoCompleteTextView = searchView.findViewById(R.id.search_src_text);

        if (autoCompleteTextView != null) {
            autoCompleteTextView.setDropDownBackgroundResource(android.R.color.white);
            //set Enable Spelling Suggestions
            autoCompleteTextView.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().length() > 2) {
                    Intent intent = new Intent(context, SearchActivity.class);
                    intent.setAction(Intent.ACTION_SEARCH);
                    intent.putExtra(SearchActivity.QUERY_TEXT, query);
                    intent.putExtra(SearchManager.QUERY, query);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityForResult(intent, LazzyBeeShare.CODE_SEARCH_RESULT);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().length() > 2) {

                    String likeQuery = "SELECT vocabulary.id,vocabulary.question,vocabulary.answers,vocabulary.level,rowid _id FROM " + TABLE_VOCABULARY + " WHERE "
                            + KEY_QUESTION + " like '" + newText + "%' OR "
                            + KEY_QUESTION + " like '% " + newText + "%'"
                            + " ORDER BY " + KEY_QUESTION + " LIMIT 50";

                    SQLiteDatabase db = LazzyBeeSingleton.dataBaseHelper.getReadableDatabase();
                    try {
                        Cursor cursor = db.rawQuery(likeQuery, null);
                        SuggestionCardAdapter suggestionCardAdapter = new SuggestionCardAdapter(context, cursor);
                        searchView.setSuggestionsAdapter(suggestionCardAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //noinspection AccessStaticViaInstance
                        LazzyBeeSingleton.getCrashlytics().logException(e);
                    } finally {
                        Log.d(TAG, "query suggetion");
                    }

                }
                return true;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Log.d(TAG, "onSuggestionSelect:" + position);
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Log.d(TAG, "onSuggestionClick:" + position);
                try {
                    CursorAdapter c = searchView.getSuggestionsAdapter();
                    if (c != null) {
                        Cursor cur = c.getCursor();
                        cur.moveToPosition(position);

                        String cardID = cur.getString(cur.getColumnIndex(BaseColumns._ID));
                        Log.d(TAG, "cardID:" + cardID);
                        String query = cur.getString(cur.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                        Log.d(TAG, "query:" + query);
                        int insertSuggesstionResults = dataBaseHelper._insertSuggesstion(cardID);
                        Log.d(TAG, "insertSuggesstionResults " + ((insertSuggesstionResults == -1) ? " OK" : " Fails"));
                        _gotoCardDetailbyID(cardID);

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
    }

    private void _gotoCardDetailbyID(String cardID) {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        intent.putExtra(LazzyBeeShare.CARDID, String.valueOf(cardID));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                // I do not want this...
                // Home as up button is to navigate to Home-Activity not previous acitivity
                if (mViewPager.getCurrentItem() == 0) {
                    super.onBackPressed();
                } else {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void close() {
        //set results complete
        setResult(LazzyBeeShare.CODE_COMPLETE_STUDY_1000, new Intent());
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    public void setDetailViewTag(String detailViewTag) {
        this.detailViewTag = detailViewTag;
    }

    public String getDetailViewTag() {
        return detailViewTag;
    }

    @Override
    public void completeLearn(boolean complete) {
        _completeLean(complete);
    }


    public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        private final DisableScrollingViewPager mViewPager;
        private final int pageCount = 2;

        public ScreenSlidePagerAdapter(FragmentManager fm, DisableScrollingViewPager mViewPager) {
            super(fm);
            this.mViewPager = mViewPager;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return StudyView.newInstance(context, getIntent(), mViewPager, ScreenSlidePagerAdapter.this, currentCard);
            else {
                return DetailsView.newInstance(context, "details");

            }

        }

        @Override
        public int getCount() {
            return pageCount;
        }


        private Fragment mCurrentFragment;

        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }
    }

    @Override
    public void _displayUserNote(Card card) {
        _showCardNote(card);

    }

    private void _showCardNote(final Card currentCard) {
        mFirebaseAnalytics.logEvent(LazzyBeeShare.FA_OPEN_A_NOTE, new Bundle());
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);

        View viewDialog = View.inflate(context, R.layout.view_dialog_user_note, null);
        final EditText txtUserNote = viewDialog.findViewById(R.id.txtUserNote);

        txtUserNote.setText(currentCard.getUser_note());

        builder.setView(viewDialog);
        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String user_note = txtUserNote.getText().toString();
            currentCard.setUser_note(user_note);
            dataBaseHelper._updateUserNoteCard(currentCard);
            ((StudyView) pagerAdapter.getCurrentFragment()).setResetUserNote(user_note);
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        // Get the AlertDialog from create()
        final AlertDialog dialog = builder.create();

        dialog.show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TextToSpeech textToSpeech = LazzyBeeSingleton.textToSpeech;
        if (textToSpeech != null)
            LazzyBeeSingleton.textToSpeech.stop();
    }

    @Override
    public void setCurrentCard(Card card) {
        currentCard = card;
    }

}
