package com.born2go.lazzybee.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.GetCardFormServerByQuestion;
import com.born2go.lazzybee.adapter.GetCardFormServerByQuestion.GetCardFormServerByQuestionResponse;
import com.born2go.lazzybee.adapter.SuggestionCardAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.view.SlidingTabLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Arrays;
import java.util.List;

import static com.born2go.lazzybee.db.DataBaseHelper.KEY_QUESTION;
import static com.born2go.lazzybee.db.impl.LearnApiImplements.TABLE_VOCABULARY;

public class CardDetailsActivity extends AppCompatActivity implements GetCardFormServerByQuestionResponse {
    private static final String TAG = "CardDetailsActivity";
    private static final Object GA_SCREEN = "aCardDetailsScreen";

    private Context context;
    LearnApiImplements learnApiImplements;

    Card card;
    String cardId;

    LinearLayout container;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    WebView mWebViewLeadDetails;
    View mViewAdv;
    String mySubject = "common";
    boolean sDEBUG = false;
    boolean sPOSITION_MEANING = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.context = this;

        container = (LinearLayout) findViewById(R.id.container);

        learnApiImplements = LazzyBeeSingleton.learnApiImplements;

        _initSettingUser();

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _displayCard(getCarID());

        _initAdView();

        _trackerApplication();

        FirebaseAnalytics firebaseAnalytics=FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.logEvent(LazzyBeeShare.FA_OPEN_DICTIONARY_VIEW_WORD,new Bundle());

    }

    private void _displayCard(String cardID) {
        try {

            card = learnApiImplements._getCardByID(cardID);
            setTitle(card.getQuestion());

//            if (itemFavorite != null) {
//                //load favorite
//                if (card.getStatus() == 1) {
//                    itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_important));
//                    itemFavorite.setTitle(context.getString(R.string.action_favorite));
//                } else {
//                    itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_not_important));
//                    itemFavorite.setTitle(context.getString(R.string.action_not_favorite));
//                }
//            }
            PackageCardPageAdapter packageCardPageAdapter = new PackageCardPageAdapter(context, card);
            mViewPager.setAdapter(packageCardPageAdapter);
            mSlidingTabLayout.setViewPager(mViewPager);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_displayCard", e);
        }
    }

    private void _initAdView() {
        try {
            mViewAdv =  findViewById(R.id.mViewAdv);
            //get value form task manager
            Container container = LazzyBeeSingleton.getContainerHolder().getContainer();
            String admob_pub_id = null;
            String adv_id = null;
            if (container == null) {
            } else {
                admob_pub_id = container.getString(LazzyBeeShare.ADMOB_PUB_ID);
                adv_id = container.getString(LazzyBeeShare.ADV_DETAILS_ID);
                Log.i(TAG, "admob -admob_pub_id:" + admob_pub_id);
                Log.i(TAG, "admob -adv_id:" + adv_id);
            }
            if (admob_pub_id != null) {
                if (adv_id == null || adv_id.equals(LazzyBeeShare.EMPTY)) {
                    mViewAdv.setVisibility(View.GONE);
                } else if (adv_id != null || adv_id.length() > 1 || !adv_id.equals(LazzyBeeShare.EMPTY) || !adv_id.isEmpty()) {
                    String advId = admob_pub_id + "/" + adv_id;
                    Log.i(TAG, "admob -AdUnitId:" + advId);
                    AdView mAdView = new AdView(this);

                    mAdView.setAdSize(AdSize.BANNER);
                    mAdView.setAdUnitId(advId);

                    AdRequest adRequest = new AdRequest.Builder()
                            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                            .addTestDevice(getResources().getStringArray(R.array.devices)[0])
                            .addTestDevice(getResources().getStringArray(R.array.devices)[1])
                            .addTestDevice(getResources().getStringArray(R.array.devices)[2])
                            .addTestDevice(getResources().getStringArray(R.array.devices)[3])
                            .build();

                    mAdView.loadAd(adRequest);

                    RelativeLayout relativeLayout = ((RelativeLayout) findViewById(R.id.adView));
                    RelativeLayout.LayoutParams adViewCenter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    adViewCenter.addRule(RelativeLayout.CENTER_IN_PARENT);
                    relativeLayout.addView(mAdView, adViewCenter);

                    mViewAdv.setVisibility(View.VISIBLE);
                } else {
                    mViewAdv.setVisibility(View.GONE);
                }
            } else {
                mViewAdv.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_initAdView", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card_details, menu);
        // _initAndLoadFavorite(menu);
        _defineSearchView(menu);

        return true;
    }

    private void _defineSearchView(Menu menu) {
      //  final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // Theme the SearchView's AutoCompleteTextView drop down. For some reason this wasn't working in styles.xml
        SearchView.SearchAutoComplete autoCompleteTextView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);

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
                if (query.trim() != null) {
                    if (query.trim().length() > 2) {
                        Intent intent = new Intent(context, SearchActivity.class);
                        intent.setAction(Intent.ACTION_SEARCH);
                        intent.putExtra(SearchActivity.QUERY_TEXT, query);
                        intent.putExtra(SearchManager.QUERY,query);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityForResult(intent, LazzyBeeShare.CODE_SEARCH_RESULT);
                    }
                    return true;
                } else
                    return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim() != null) {
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
                        } finally {
                            Log.d(TAG, "query suggetion");
                        }

                    }
                    return true;
                } else
                    return false;
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
                        int insertSuggesstionResults = learnApiImplements._insertSuggesstion(cardID);
                        Log.d(TAG, "insertSuggesstionResults " + ((insertSuggesstionResults == -1) ? " OK" : " Fails"));
                        _displayCard(cardID);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add_to_learn:
                _addCardToLearn();
                return true;
            case R.id.action_update:
                //
                _updateCardFormServer();
                return true;
            case R.id.action_share:
                _shareCard();
                return true;
//            case R.id.action_favorite:
//
//                //_addCardToFavorite();
//                return true;
            case R.id.action_report:
                _reportCard();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void _shareCard() {
        try {
            //get card in Db
            if (card.getQuestion() == null)
                card = learnApiImplements._getCardByID(cardId);

            //get base url in Task Manager
            String base_url_sharing = LazzyBeeShare.DEFAULTS_BASE_URL_SHARING;
            String server_base_url_sharing = LazzyBeeSingleton.getContainerHolder().getContainer().getString(LazzyBeeShare.BASE_URL_SHARING);
            if (server_base_url_sharing != null) {
                if (server_base_url_sharing.length() > 0)
                    base_url_sharing = server_base_url_sharing;
            }

            //define base url with question
            base_url_sharing = base_url_sharing + card.getQuestion();
            Log.i(TAG, "Sharing URL:" + base_url_sharing);

            //Share card
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, base_url_sharing);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_shareCard", e);
        }

    }

    private void _reportCard() {
        try {
            startActivity(LazzyBeeShare.getOpenFacebookIntent(context));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_reportCard", e);
        }
    }

    private void _addCardToFavorite() {
        try {
            if (card.getQuestion() == null)
                card = learnApiImplements._getCardByID(cardId);

            int statusFavrite = 0;
            //Set icon drawer
//            if (itemFavorite.getTitle().toString().equals(getString(R.string.action_not_favorite))) {
//                statusFavrite = 1;
//                itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_important));
//                itemFavorite.setTitle(context.getString(R.string.action_favorite));
//            } else {
//                itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_not_important));
//                itemFavorite.setTitle(context.getString(R.string.action_not_favorite));
//            }

            //set status card and Update card
            card.setStatus(statusFavrite);
            learnApiImplements._updateCard(card);

            Toast.makeText(context, getString(R.string.message_add_favorite_card_done, card.getQuestion()), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_addCardToFavorite", e);
        }
    }


    private void _updateCardFormServer() {
        if (LazzyBeeShare.checkConn(context)) {
            //Check card==null get card form Sqlite by cardID
            if (card == null)
                card = learnApiImplements._getCardByID(cardId);

            GetCardFormServerByQuestion getCardFormServerByQuestion = new GetCardFormServerByQuestion(context);
            getCardFormServerByQuestion.execute(card);
            getCardFormServerByQuestion.delegate = this;
        } else {
            Toast.makeText(context, R.string.failed_to_connect_to_server, Toast.LENGTH_SHORT).show();
        }


    }

    private void _addCardToLearn() {
        try {
            String queue_list = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.QUEUE_LIST);
            List<String> cardIDs = learnApiImplements._getListCardIdFromStringArray(queue_list);
            if (cardIDs.contains(cardId)) {
                Toast.makeText(context, getString(R.string.message_action_add_card_to_learn_complete, card.getQuestion()), Toast.LENGTH_SHORT).show();
            } else {
                if (card == null)
                    card = learnApiImplements._getCardByID(cardId);
                learnApiImplements._addCardIdToQueueList(card);
                Toast.makeText(context, getString(R.string.message_action_add_card_to_learn_complete, card.getQuestion()), Toast.LENGTH_SHORT).show();
            }


//            // Instantiate an AlertDialog.Builder with its constructor
//            final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));
//
//            // Chain together various setter methods to set the dialog characteristics
//            builder.setMessage(getString(R.string.dialog_message_add_to_learn, card.getQuestion()))
//                    .setTitle(getString(R.string.dialog_title_add_to_learn));
//
//            // Add the buttons
//            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    //Update Queue_list in system table
//
//
//                }
//            });
//            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    // User cancelled the dialog
//                    dialog.cancel();
//                }
//            });
//            // Get the AlertDialog from create()
//            AlertDialog dialog = builder.create();

            // dialog.show();
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_addCardToLearn", e);
        }
    }


    @Override
    public void processFinish(Card card) {
        try {
            if (card != null) {
                //Update Success reload data
                this.card.setAnswers(card.getAnswers());
                this.card.setL_vn(card.getL_vn());
                this.card.setL_en(card.getL_en());

                //Update Success reload data
                //Set Adapter
                PackageCardPageAdapter packageCardPageAdapter = new PackageCardPageAdapter(context, this.card);
                mViewPager.setAdapter(packageCardPageAdapter);
                mSlidingTabLayout.setViewPager(mViewPager);

                //Update Card form DB
                learnApiImplements._updateCardFormServer(card);

                Toast.makeText(context, getString(R.string.message_update_card_successful), Toast.LENGTH_SHORT).show();

                //set Result code for updated List card
                setResult(getResources().getInteger(R.integer.code_card_details_updated), new Intent(this, this.getIntent().getComponent().getClass()));
            } else {
                Toast.makeText(context, getString(R.string.message_update_card_fails), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "processFinish", e);
        }
    }

    private void _initSettingUser() {
        mySubject = LazzyBeeShare.getMySubject();
        sDEBUG = LazzyBeeShare.getDebugSetting();
        sPOSITION_MEANING = LazzyBeeShare.getPositionMeaning();
    }

    public String getCarID() {
        try {
            cardId = getIntent().getStringExtra(LazzyBeeShare.CARDID);
        } catch (Exception e) {
            cardId = LazzyBeeShare.EMPTY;
            LazzyBeeShare.showErrorOccurred(context, "getCarID", e);
        }
        return cardId;
    }

    class PackageCardPageAdapter extends PagerAdapter {
        Card card;
        List<String> packages;
        private Context context;

        public PackageCardPageAdapter(Context context, Card card) {
            this.card = card;
            this.context = context;
            packages = Arrays.asList(context.getString(R.string.dictionary_vn_en), context.getString(R.string.dictionary_en_en), context.getString(R.string.dictionary_lazzybee));
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return packages.get(position);
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return packages.size();
        }

        /**
         * Determines whether a page View is associated with a specific key object
         * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
         * required for a PagerAdapter to function properly.
         *
         * @param view   Page View to check for association with <code>object</code>
         * @param object Object to check for association with <code>view</code>
         * @return true if <code>view</code> is associated with the key object <code>object</code>
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }


        /**
         * Create the page for the given position.  The adapter is responsible
         * for adding the view to the container given here, although it only
         * must ensure this is done by the time it returns from
         * {@link #finishUpdate(ViewGroup)}.
         *
         * @param container The containing View in which the page will be shown.
         * @param position  The page position to be instantiated.
         * @return Returns an Object representing the new page.  This does not
         * need to be a View, but can be some other container of the page.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view = getLayoutInflater().inflate(R.layout.page_package_card_item, container, false);

            // Add the newly created View to the ViewPager
            container.addView(view);
            //
            mWebViewLeadDetails = (WebView) view.findViewById(R.id.mWebViewCardDetails);
            WebSettings ws = mWebViewLeadDetails.getSettings();
            ws.setJavaScriptEnabled(true);
            _addJavascriptInterface(card);

            try {
                String displayHTML = LazzyBeeShare.EMPTY;
                switch (position) {
                    case 0:
                        //dic VN
                        displayHTML = LazzyBeeShare.getDictionaryHTML(card.getL_vn());
                        break;
                    case 1:
                        //dic ENG
                        displayHTML = LazzyBeeShare.getDictionaryHTML(card.getL_en());
                        break;
                    case 2:
                        //dic Lazzybee
                        displayHTML = LazzyBeeShare.getAnswerHTML(context, card, mySubject, sDEBUG, sPOSITION_MEANING);
                        break;
                }
                Log.i(TAG, "Tab Dic:" + displayHTML);

                mWebViewLeadDetails.loadDataWithBaseURL(LazzyBeeShare.ASSETS, displayHTML, LazzyBeeShare.mime, LazzyBeeShare.encoding, null);
            } catch (Exception e) {
                LazzyBeeShare.showErrorOccurred(context, "instantiateItem", e);
            }


            // Return the View
            return view;
        }

        private void _addJavascriptInterface(final Card card) {
            String sp = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_SPEECH_RATE);
            float speechRate = 1.0f;
            if (sp != null) {
                speechRate = Float.valueOf(sp);
            }
            //addJavascriptInterface play question
            final float finalSpeechRate = speechRate;
            mWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectQuestion() {
                @JavascriptInterface
                public void playQuestion() {
                    String toSpeak = card.getQuestion();

                    //Speak text
                    LazzyBeeShare._speakText(toSpeak, finalSpeechRate);
                }
            }, "question");
            mWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectExplain() {
                @JavascriptInterface
                public void speechExplain() {
                    //get answer json
                    //String answer = card.getAnswers();
                    String toSpeech = card.getExplain(mySubject,LazzyBeeShare.TO_SPEECH_1);//LazzyBeeShare._getValueFromKey(answer, "explain");

                    //Speak text
                    LazzyBeeShare._speakText(toSpeech, finalSpeechRate);
                }
            }, "explain");
            mWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectExample() {
                @JavascriptInterface
                public void speechExample() {
                    //get answer json
                   // String answer = card.getAnswers();
                    String toSpeech = card.getExample(mySubject,LazzyBeeShare.TO_SPEECH_1);//LazzyBeeShare._getValueFromKey(answer, "example");

                    //Speak text
                    LazzyBeeShare._speakText(toSpeech, finalSpeechRate);
                }
            }, "example");
        }


        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            // Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
        }

    }

    private void _trackerApplication() {
        try {
            DataLayer mDataLayer = LazzyBeeSingleton.mDataLayer;
            mDataLayer.pushEvent("openScreen", DataLayer.mapOf("screenName", GA_SCREEN));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_trackerApplication", e);
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
        int hour = learnApiImplements.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_HOUR_NOTIFICATION);
        int minute = learnApiImplements.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_MINUTE_NOTIFICATION);
        LazzyBeeShare._setUpNotification(context, hour, minute);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TextToSpeech textToSpeech = LazzyBeeSingleton.textToSpeech;
        if (textToSpeech != null)
            LazzyBeeSingleton.textToSpeech.stop();
    }

}
