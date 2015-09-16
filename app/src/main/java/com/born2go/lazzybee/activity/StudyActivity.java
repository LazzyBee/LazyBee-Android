package com.born2go.lazzybee.activity;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.LazzyBeeApplication;
import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.GetCardFormServerByQuestion;
import com.born2go.lazzybee.adapter.GetCardFormServerByQuestion.GetCardFormServerByQuestionResponse;
import com.born2go.lazzybee.algorithms.CardSched;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.api.ConnectGdatabase;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.ContainerHolderSingleton;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.view.SlidingTabLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.born2go.lazzybee.db.Card.QUEUE_NEW_CRAM0;

public class StudyActivity extends AppCompatActivity implements GetCardFormServerByQuestionResponse {

    private static final String TAG = "StudyActivity";
    private DataLayer mDataLayer;
    private Context context;

    boolean learn_more;

    LearnApiImplements dataBaseHelper;
    TextToSpeech textToSpeech;
    WebView mWebViewLeadDetails;
    Button btnShowAnswer;
    LinearLayout mLayoutButton;
    Button btnAgain0, btnHard1, btnGood2, btnEasy3;

    TextView lbCountNew;

    TextView lbCountAgain;

    TextView lbCountDue;

    List<Card> todayList = new ArrayList<Card>();
    List<Card> againList = new ArrayList<Card>();
    List<Card> dueList = new ArrayList<Card>();
    List<Card> cardListAddDueToDay = new ArrayList<Card>();

    CardSched cardSched;

    //Current Card
    Card currentCard = new Card();


    //Define before card
    Card beforeCard;

    public Card getBeforeCard() {
        return beforeCard;
    }

    public void setBeforeCard(Card beforeCard) {
        this.beforeCard = beforeCard;
    }

    //init position
    int position = 0;
    int position_again = 0;
    int position_due = 0;
    Tracker mTracker;

    ViewPager mViewPager;
    SlidingTabLayout mSlidingTabLayout;

    boolean answerDisplay = false;
    ConnectGdatabase connectGdatabase;

    MenuItem btnBackBeforeCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        context = this;
        _initLazzyBeeApi();
        _initActonBar();
        _initDatabase();
        _trackerApplication();

        //init cardSched
        cardSched = new CardSched();


        _initView();
        _initTextToSpeech();
        _initAdView();

        _setUpStudy();
    }

    GoogleAccountCredential credential;

    private void _initLazzyBeeApi() {
//        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(this,
//                "server:client_id:1090254847247-hhq28qf96obdjm7c7pgr2qo2mt2o842l.apps.googleusercontent.com");
        credential = GoogleAccountCredential.usingAudience(this, "1090254847247-hhq28qf96obdjm7c7pgr2qo2mt2o842l.apps.googleusercontent.com");


    }

    private void _setUpStudy() {
        //get lean_more form intern
        learn_more = getIntent().getBooleanExtra(LazzyBeeShare.LEARN_MORE, false);

        int limit_today = dataBaseHelper._getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT);
        int total_learn_per_day = dataBaseHelper._getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT);
        //get card due today & agin
        againList = dataBaseHelper._getListCardByQueue(Card.QUEUE_LNR1, 0);
        dueList = dataBaseHelper._getListCardByQueue(Card.QUEUE_REV2, total_learn_per_day);
        int dueCount = dueList.size();

        //get new random card list to day
        //int newCount =
        //if (newCount > 0)
        //  todayList = dataBaseHelper._getRandomCard(newCount);
        if (dueCount == 0) {
            Log.i(TAG, "onCreate()  dueCount == 0");
        } else {
            Log.i(TAG, "onCreate()  dueCount != 0");
            if (dueCount < total_learn_per_day) {
                Log.i(TAG, "onCreate()  dueCount < total_learn_per_day");
                if (total_learn_per_day - dueCount < limit_today) {
                    Log.i(TAG, "onCreate()  total_learn_per_day - dueCount < limit_today");
                    limit_today = total_learn_per_day - dueCount;
                } else if (total_learn_per_day - dueCount > limit_today) {
                    Log.i(TAG, "onCreate()  total_learn_per_day - dueCount > limit_today");
                }
            } else if (dueCount >= total_learn_per_day) {
                Log.i(TAG, "onCreate()  dueCount >= total_learn_per_day");
                limit_today = 0;
            }
        }
        if (dueCount > 0)
            learn_more = false;

        todayList = dataBaseHelper._getRandomCard(limit_today, learn_more);

        int againCount = againList.size();
        int todayCount = todayList.size();

        Log.i(TAG, "againCount:" + againCount);
        Log.i(TAG, "todayCount:" + todayCount);
        Log.i(TAG, "dueCount:" + dueCount + ",limit:" + dueCount + ",today:" + todayCount);

        //set data
        boolean check_learn = againCount > 0 || dueCount > 0 || todayCount > 0;

        Log.i(TAG, "check_learn:" + (check_learn));
        if (check_learn) {
//        if (todayCount > 0) {
            _setDataforWebView();

            final int list_card_again_in_today_size = againList.size();
            //set total vocabilary
            lbCountAgain.setText(String.valueOf(list_card_again_in_today_size));
            lbCountAgain.setTag(list_card_again_in_today_size);

            int list_card_new_size = todayList.size();
            lbCountNew.setText(String.valueOf(list_card_new_size));
            lbCountNew.setTag(list_card_new_size);


            lbCountDue.setText(String.valueOf(dueList.size()));
            lbCountDue.setTag(dueList.size());
        } else {
            Log.i(TAG, "_completeLean");
            _completeLean();
        }
    }

    private void _initAdView() {
        AdView mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getResources().getStringArray(R.array.devices)[0])
                .addTestDevice(getResources().getStringArray(R.array.devices)[1])
                .build();
        mAdView.loadAd(adRequest);
    }

    private void _initActonBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void _trackerApplication() {
        LazzyBeeApplication lazzyBeeApplication = (LazzyBeeApplication) getApplication();
        //mTracker = lazzyBeeApplication.getTracker(LazzyBeeApplication.TrackerName.APP_TRACKER);
        //mTracker = lazzyBeeApplication.getDefaultTracker();

        //Log.i(TAG, "Setting screen name: " + TAG);
        //mTracker.setScreenName("Image~" + TAG);
        //mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Log.i(TAG, "Trying to use TagManager");
        mDataLayer = TagManager.getInstance(this).getDataLayer();
        //mDataLayer.push(DataLayer.mapOf("event", "openScreen", "screenName", TAG));
        mDataLayer.pushEvent("openScreen", DataLayer.mapOf("screenName", TAG));

        Log.i(TAG, "Get config from TagManager: adv_enable? " +
                ContainerHolderSingleton.getContainerHolder().getContainer().getString("adv_enable"));
    }

    private void _completeLean() {
        Log.i(TAG, "----_completeLean----");
        setBeforeCard(null);
        setResult(LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000, new Intent());
        onBackPressed();
        Log.i(TAG, "---------END---------");

    }

    /**
     * Init db sqlite
     */
    private void _initDatabase() {
        dataBaseHelper = LazzyBeeSingleton.learnApiImplements;
    }

    private void _initTextToSpeech() {
        String sp = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_SPEECH_RATE);
        float speech = 1.0f;
        if (sp != null)
            speech = Float.valueOf(sp);
        textToSpeech = LazzyBeeSingleton.textToSpeech;
        textToSpeech.setSpeechRate(speech);


    }

    private void _initView() {
        mWebViewLeadDetails = (WebView) findViewById(R.id.mWebViewLeadDetaisl);

        //init button

        btnShowAnswer = (Button) findViewById(R.id.btnShowAnswer);
        mLayoutButton = (LinearLayout) findViewById(R.id.mLayoutButton);

        btnAgain0 = (Button) findViewById(R.id.btnAgain0);
        btnHard1 = (Button) findViewById(R.id.btnHard1);
        btnGood2 = (Button) findViewById(R.id.btnGood2);
        btnEasy3 = (Button) findViewById(R.id.btnEasy3);

        //init lbCount
        lbCountNew = (TextView) findViewById(R.id.lbCountTotalVocabulary);
        lbCountAgain = (TextView) findViewById(R.id.lbCountAgainInday);
        lbCountDue = (TextView) findViewById(R.id.lbAgainDue);

//        mViewPager = (ViewPager) findViewById(R.id.viewpager);
//        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);

        TextView lbTipHelp = (TextView) findViewById(R.id.lbTipHelp);
        lbTipHelp.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + getString(R.string.message_hellp_study) + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t");
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

    public boolean isLearn_more() {
        return learn_more;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_study, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        btnBackBeforeCard = menu.findItem(R.id.action_back_before_card);
        btnBackBeforeCard.setVisible(false);
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
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

    private void _search(String query) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.QUERY_TEXT, query);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivityForResult(intent, 2);
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
                super.onBackPressed();
                return true;
            case R.id.action_detelte:
                Log.i(TAG, "_deleteCard question:" + currentCard.getQuestion());
                //_showDialogDeleteCard();
                _deleteCard();
                return true;
            case R.id.action_update:

                //define function update card form server
                _updateCardFormServer();

                return true;
            case R.id.action_back_before_card:

                _backToBeforeCard();

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void _backToBeforeCard() {
        Log.i(TAG, "-----------_backToBeforeCard------------");
        if (beforeCard != null) {
            //Define before queue
            int beforeQueue = beforeCard.getQueue();
            Log.i(TAG, "_backToBeforeCard()\t question:" + beforeCard.getQuestion() +
                    "\t queue:" + beforeQueue + " due:" + beforeCard.getDue());
            switch (beforeQueue) {
                case Card.QUEUE_NEW_CRAM0:
                    Log.i(TAG, "_backToBeforeCard\t Queue=Card.QUEUE_NEW_CRAM0");

                    //Remove beforecard in againlist
                    for (Card card : againList) {
                        if (card.getId() == beforeCard.getId()) {
                            againList.remove(card);
                            break;
                        }
                    }
                    lbCountAgain.setText(String.valueOf(againList.size()));

                    //setDue and set Lat_itv to default =0
//                    beforeCard.setDue(0l);
//                    beforeCard.setLast_ivl(0);

                    //Define clone todayList
                    List<Card> cloneTodayList = new ArrayList<Card>(todayList);
                    int todayCount = cloneTodayList.size();

                    //Clear Data
                    todayList.clear();

                    //Readd card to new card
                    if (todayCount == 0) {
                        todayList.add(beforeCard);
                    } else {
                        todayList.add(0, beforeCard);
                        for (int i = 0; i < todayCount; i++) {
                            todayList.add(i + 1, cloneTodayList.get(i));
                        }
                    }

                    //Set new count
                    int countNew = todayList.size();
                    lbCountNew.setText(String.valueOf(countNew));

                    break;
                case Card.QUEUE_LNR1:
                    Log.i(TAG, "_backToBeforeCard\t Queue=Card.QUEUE_LNR1");

                    //Remove beforecard in againlist
                    for (Card card : againList) {
                        if (card.getId() == beforeCard.getId()) {
                            againList.remove(card);
                            break;
                        }
                    }
                    lbCountAgain.setText(String.valueOf(againList.size()));

                    //Define clone againList
                    List<Card> cloneAgainList = new ArrayList<Card>(againList);
                    int agianCount = cloneAgainList.size();

                    //Clear Data
                    againList.clear();

                    //Readd card to again card
                    if (agianCount == 0) {
                        againList.add(beforeCard);
                    } else {
                        againList.add(0, beforeCard);
                        for (int i = 0; i < agianCount; i++) {
                            againList.add(i + 1, cloneAgainList.get(i));
                        }
                    }

                    //Set new count
                    int countAgain = againList.size();
                    lbCountAgain.setText(String.valueOf(countAgain));

                    break;
                case Card.QUEUE_REV2:
                    Log.i(TAG, "_backToBeforeCard\t Queue=Card.QUEUE_REV2");

                    //Define clone duelist
                    List<Card> cloneDuelist = new ArrayList<Card>(dueList);
                    int dueCount = cloneDuelist.size();

                    //Clear Data
                    dueList.clear();

                    //Readd card to again card
                    if (cloneDuelist.size() == 0) {
                        dueList.add(beforeCard);
                    } else {
                        dueList.add(0, beforeCard);
                        for (int i = 0; i < dueCount; i++) {
                            dueList.add(i + 1, cloneDuelist.get(i));
                        }
                    }

                    //Set new count
                    int countDue = dueList.size();
                    lbCountDue.setText(String.valueOf(countDue));

                    break;
            }


            //update card
            int results_num = dataBaseHelper._updateCard(beforeCard);

            if (results_num >= 1) {
                //Get card form DB
                currentCard = dataBaseHelper._getCardByID(String.valueOf(beforeCard.getId()));
                if (beforeQueue == Card.QUEUE_NEW_CRAM0)
                    //Update form queueList In DB
                    dataBaseHelper._addCardIdToQueueList(beforeCard);

                Log.i(TAG, "_backToBeforeCard()\t currentCardquestion:" + currentCard.getQuestion() +
                        "\t queue:" + currentCard.getQueue() + " due:" + currentCard.getDue());

                _showBtnAnswer();
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), currentCard.getQueue());
            }
            Log.i(TAG, "_backToBeforeCard()\t" + getString(R.string.number_row_updated, results_num));
            Toast.makeText(context, getString(R.string.number_row_updated, results_num), Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(context, R.string.message_error_back_before_card, Toast.LENGTH_SHORT).show();
        }

        //Hide btnBackBeforeCard
        btnBackBeforeCard.setVisible(false);

        Log.i(TAG, "------------------END-------------------");
    }

    private void _updateCardFormServer() {
        //Call Api Update Card
        GetCardFormServerByQuestion getCardFormServerByQuestion = new GetCardFormServerByQuestion(context);
        getCardFormServerByQuestion.execute(currentCard);
        getCardFormServerByQuestion.delegate = this;
    }


    boolean done_card = false;

    private void _deleteCard() {
        Log.i(TAG, "-------------------deleteCard-------------------");
        if (btnShowAnswer.getVisibility() == View.GONE) {
            btnShowAnswer.setVisibility(View.VISIBLE);
            mLayoutButton.setVisibility(View.GONE);
        }
        done_card = true;
        //Define card form server
        Card card = dataBaseHelper._getCardByID(String.valueOf(currentCard.getId()));
        int currentQueue = card.getQueue();
        Log.i(TAG, "_deleteCard currentQueue:" + currentQueue);

        switch (currentQueue) {
            case Card.QUEUE_NEW_CRAM0:
                Log.i(TAG, "_deleteCard QUEUE_NEW_CRAM0");
                //reset new card count
                boolean removeNew = todayList.remove(currentCard);
                if (!removeNew) {
                    //Remove index 0
                    todayList.remove(0);
                }
                int countNew = todayList.size();
                lbCountNew.setText(String.valueOf(countNew));
                break;
            case Card.QUEUE_LNR1:
                Log.i(TAG, "_deleteCard QUEUE_LNR1");
                //reset new card again
                boolean removeAgain = againList.remove(currentCard);
                if (!removeAgain) {
                    //Remove index 0
                    todayList.remove(0);
                }
                int countAgain = againList.size();
                lbCountAgain.setText(String.valueOf(countAgain));
                break;
            case Card.QUEUE_REV2:
                Log.i(TAG, "_deleteCard QUEUE_REV2");
                //reset new card due
                boolean removeDue = dueList.remove(currentCard);
                if (!removeDue) {
                    //Remove index 0
                    todayList.remove(0);
                }
                int countDue = dueList.size();
                lbCountDue.setText(String.valueOf(countDue));
                break;
        }

        Log.i(TAG, "_deleteCard question:" + card.getQuestion() + ",currentQueue:" + card.getQueue());

        //Set before card
        beforeCard = null;

        currentCard.setQueue(Card.QUEUE_DONE_2);

        Log.i(TAG, "_deleteCard before Update question:" + card.getQuestion() + ",currentQueue:" + card.getQueue());
        int update = dataBaseHelper._updateCard(currentCard);

        if (update >= 1) {
            Log.i(TAG, "_deleteCard After Update question:" + card.getQuestion() + ",currentQueue:" + card.getQueue());

            currentCard.setQueue(currentQueue);
            _nextCard(currentQueue);
        }

        Toast.makeText(context, getString(R.string.number_row_updated, update), Toast.LENGTH_SHORT).show();

        Log.i(TAG, "-----------------------END----------------------");
    }


    private void _nextCard(int currentQueue) {
        switch (currentQueue) {
            case Card.QUEUE_NEW_CRAM0:
                Log.i(TAG, "_nextCard:\t Queue=Card.QUEUE_NEW_CRAM0");
                _nextAgainCard();
                break;
            case Card.QUEUE_LNR1:
                Log.i(TAG, "_nextCard:\t Queue=Card.QUEUE_LNR1");
                _nextDueCard();
                break;
            case Card.QUEUE_REV2:
                Log.i(TAG, "_nextCard:\t Queue=Card.QUEUE_REV2");
                _nextNewCard();
                break;
        }
    }

    /**
     * Set data for webview.
     * <p/>
     * <p>Difine onclick btnAnswer and btnAgain0.</p>
     * <p/>
     * Define JavaScrip to Speek Text.
     */
    private void _setDataforWebView() {
        //Setting webview
        WebSettings ws = mWebViewLeadDetails.getSettings();
        ws.setJavaScriptEnabled(true);

        //Load one card to show
        try {
            if (againList.size() > 0) {
                Log.i(TAG, "Load first again card ");
                //currentCard = againList.get(position_again);
                currentCard = againList.get(0);
                //get current time and du card
                int current_time = (int) (new Date().getTime() / 1000);
                int due = (int) currentCard.getDue();

                Log.i(TAG, "_setDataforWebView:" + current_time + ":" + due);
                if (current_time - due >= 600 || todayList.size() == 0 && dueList.size() == 0) {
                    Log.i(TAG, "_setDataforWebView:Next card is again card 2");

                    lbCountDue.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                    lbCountAgain.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                    lbCountNew.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                    //Display next card
                    _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_LNR1);
                } else {
                    Log.i(TAG, "_setDataforWebView:Next card is due card 1");
                    _nextDueCard();
                }

            } else if (dueList.size() > 0) {
                Log.i(TAG, "Load first duecard ");
                //currentCard = dueList.get(position_due);
                currentCard = dueList.get(0);

                lbCountDue.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                lbCountAgain.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                lbCountNew.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                mWebViewLeadDetails.loadDataWithBaseURL(LazzyBeeShare.ASSETS, LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), LazzyBeeShare.mime, LazzyBeeShare.encoding, null);
            } else if (todayList.size() > 0) {
                Log.i(TAG, "Load first new card ");
                currentCard = todayList.get(position);

                lbCountDue.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                lbCountAgain.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                lbCountNew.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                mWebViewLeadDetails.loadDataWithBaseURL(LazzyBeeShare.ASSETS, LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), LazzyBeeShare.mime, LazzyBeeShare.encoding, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Showtime
//        StudyCardPageAdapter studyCardPageAdapter = new StudyCardPageAdapter(context, currentCard, 0);
//        mViewPager.setAdapter(studyCardPageAdapter);
//        mSlidingTabLayout.setViewPager(mViewPager);


        //Inject native handle to web element
        _addJavascriptInterfaceQuestionAndAnswer();
    }

    public void onbtnShowAnswerClick(View view) {
        //Set flag Display State
        answerDisplay = true;
        _showAnswer();

    }

    private void _displayCardByType(int showType) {
        StudyCardPageAdapter studyCardPageAdapter = new StudyCardPageAdapter(context, currentCard, showType);
        mViewPager.setAdapter(studyCardPageAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    public void onbtnAgainClick(View view) {
        _showBtnAnswer();
        //_answerAgainCard();
        _answerCard(Card.EASE_AGAIN);

    }

    public void onbtnHardClick(View view) {
        _showBtnAnswer();
        // _answerDueCard(Card.EASE_HARD);
        _answerCard(Card.EASE_HARD);
    }

    public void onbtnGoodClick(View view) {
        _showBtnAnswer();
        //_answerDueCard(Card.EASE_GOOD);
        _answerCard(Card.EASE_GOOD);
    }

    public void onbtnEasyClick(View view) {
        _showBtnAnswer();
        //_answerDueCard(Card.EASE_EASY);
        _answerCard(Card.EASE_EASY);
    }

    private void _showAnswer() {
        //hide btnShowAnswer and show mLayoutButton
        btnShowAnswer.setVisibility(View.GONE);
        mLayoutButton.setVisibility(View.VISIBLE);
        try {
            //Define get card
            Card card = currentCard;

            Card cardFromDB = dataBaseHelper._getCardByID(String.valueOf(card.getId()));
            Log.i(TAG, "btnShowAnswer question=" + card.getQuestion() + ",queue=" + card.getQueue() + ",queue db:" + cardFromDB.getQueue());

            //Show answer question
            _loadWebView(LazzyBeeShare.getAnswerHTML(context, cardFromDB), 10);

            //get  next Ivl String List
            String[] ivlStrList = cardSched.nextIvlStrLst(cardFromDB);

            //set text btn
            btnAgain0.setText(Html.fromHtml(ivlStrList[Card.EASE_AGAIN] + "<br/>" + getString(R.string.EASE_AGAIN)));
            btnHard1.setText(Html.fromHtml(ivlStrList[Card.EASE_HARD] + "<br/>" + getString(R.string.EASE_HARD)));
            btnGood2.setText(Html.fromHtml(ivlStrList[Card.EASE_GOOD] + "<br/>" + getString(R.string.EASE_GOOD)));
            btnEasy3.setText(Html.fromHtml(ivlStrList[Card.EASE_EASY] + "<br/>" + getString(R.string.EASE_EASY)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void _answerCard(int easy) {
        Log.i(TAG, "----------------_answerCard:" + easy + "----------------");

        final int curren_time = (int) (new Date().getTime() / 1000);

        //Get current Queue
        int currentQueue = currentCard.getQueue();
        Log.i(TAG, "_answerCard:Currrent Card Queue:" + currentQueue);

        //Define card form server
        Card card = dataBaseHelper._getCardByID(String.valueOf(currentCard.getId()));

        //setBeforeCard=current card;
        setBeforeCard(card);

        //Show item BackBeroreCard when answer
        btnBackBeforeCard.setVisible(true);

        //Check Contains and Remove
        _checkContainsAndRemove(cardListAddDueToDay);

        if (currentQueue < QUEUE_NEW_CRAM0) {//Something's wrong???
            Log.i(TAG, "_answerCard:\tQueue<Card.QUEUE_NEW_CRAM0 currentQueue:" + currentQueue);
            return;
        }
        if (currentQueue == QUEUE_NEW_CRAM0) {
            //reset new card count
            boolean remove = todayList.remove(currentCard);
            if (!remove) {
                //Remove index 0
                todayList.remove(0);
            }
            int countNew = todayList.size();
            lbCountNew.setText(String.valueOf(countNew));

        } else if (currentQueue == Card.QUEUE_LNR1) {
            if (easy > Card.EASE_AGAIN) {
                //reset new card again
                boolean remove = againList.remove(currentCard);
                if (!remove) {
                    //Remove index 0
                    againList.remove(0);
                }
                int countAgain = againList.size();
                lbCountAgain.setText(String.valueOf(countAgain));
            }
        } else if (currentQueue == Card.QUEUE_REV2) {
            //reset new card due
            boolean remove = dueList.remove(currentCard);
            if (!remove) {
                //Remove index 0
                dueList.remove(0);
            }
            int countDue = dueList.size();
            lbCountDue.setText(String.valueOf(countDue));
        }

        Log.i(TAG, "_answerCard Before Update Card " + currentCard.getQuestion() +
                " to queue " + currentCard.getQueue() + " currentQueue:" + currentQueue);
        Log.i(TAG, "_answerCard Berore answer beforeCard " + beforeCard.getQuestion() +
                " to queue " + beforeCard.getQueue() + " currentQueue:" + currentQueue);

        //Set queue,due using cardShed
        cardSched.answerCard(currentCard, easy);

        // beforeCard.setQueue(currentQueue);
        Log.i(TAG, "_answerCard answer beforeCard " + beforeCard.getQuestion() +
                " to queue " + beforeCard.getQueue() + " currentQueue:" + currentQueue);

        if (easy == Card.EASE_AGAIN) {

            againList.remove(currentCard);
            //set Due for again card = 600 second(10 minute)
            currentCard.setDue(curren_time + 600);

            //Add card to againList
            againList.add(currentCard);

            //reset count Againt
            int countAgain = againList.size();
            lbCountAgain.setText(String.valueOf(countAgain));
        } else if (easy > Card.EASE_AGAIN) {
            //Add currentCard to DueList
            cardListAddDueToDay.add(currentCard);
        }


        //update card form DB
        int update = dataBaseHelper._updateCard(currentCard);
        if (update >= 1) {
            Log.i(TAG, "_answerCard Update Card " + currentCard.getQuestion() +
                    " to queue " + currentCard.getQueue() + " OK");
            Log.i(TAG, "_answerCard Update beforeCard " + beforeCard.getQuestion() +
                    " to queue " + beforeCard.getQueue());

            _nextCard(currentQueue);

        } else {
            //
            Log.i(TAG, "_answerCard Update Card " + currentCard.getQuestion() + " to queue " + currentCard.getQueue() + " Fails");
        }
        Log.i(TAG, "-----------------------END-----------------------------");
    }

    private void _addJavascriptInterfaceQuestionAndAnswer() {
        //Todo: addJavascriptInterface play question
        mWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectQuestion() {
            @JavascriptInterface
            public void playQuestion() {
                //get text to Speak
                String toSpeak = currentCard.getQuestion();

                //Speak text
                _speakText(toSpeak);

                //textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        }, "question");
        mWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectExplain() {
            @JavascriptInterface
            public void speechExplain() {
                //get answer json
                String answer = currentCard.getAnswers();
                String toSpeech = LazzyBeeShare._getValueFromKey(answer, "explain");

                //Speak text
                _speakText(toSpeech);
            }
        }, "explain");
        mWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectExample() {
            @JavascriptInterface
            public void speechExample() {
                //get answer json
                String answer = currentCard.getAnswers();
                String toSpeech = LazzyBeeShare._getValueFromKey(answer, "example");

                //Speak text
                _speakText(toSpeech);
            }
        }, "example");
    }

    private void _checkContainsAndRemove(List<Card> cardLis) {
        if (cardLis.contains(currentCard)) {
            Log.i(TAG, "Card Contains cardList");
            //remove current Card
            cardLis.remove(currentCard);
        }
    }

    private void _updateCardQueueAndCardDue(int card_id, int queue, int due) {
        dataBaseHelper._updateCardQueueAndCardDue(/*cast in to string*/String.valueOf(card_id), queue, due);

    }

    private void _nextNewCard() {
        Log.i(TAG, "---------_nextNewCard--------");
        Log.d(TAG, "Curent new card:" + currentCard.toString());
        if (todayList.size() > 0) {
            position = todayList.size() - 1;
            //get next card again
            Log.i(TAG, "_nextNewCard Position=" + position + " today:" + todayList.size());
            currentCard = todayList.get(0);
            //Display question
            _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), QUEUE_NEW_CRAM0);
        } else if (againList.size() > 0) {
            Log.i(TAG, "_nextNewCard:Next card is Again card");
            _nextAgainCard();
        } else if (dueList.size() > 0) {
            Log.i(TAG, "_nextNewCard:Next card is Due card");
            _nextDueCard();
        } else if (todayList.size() == 0) {
            Log.i(TAG, "_nextNewCard:_completeLean");
            _completeLean();
        }
        Log.i(TAG, "--------------END------------");
    }


    private void _nextDueCard() {
        Log.i(TAG, "---------_nextDueCard--------");
        if (dueList.size() > 0) {//Check dueList.size()>0

            currentCard = dueList.get(0);

            lbCountDue.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            lbCountAgain.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
            lbCountNew.setPaintFlags(Paint.LINEAR_TEXT_FLAG);

            //Display next card
            _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_REV2);

        } else if (todayList.size() > 0) {
            Log.i(TAG, "_nextDueCard:Next card is new card");
            _nextNewCard();
        } else if (againList.size() > 0) {//Check againList.size()>0
            Log.i(TAG, "_nextDueCard:Next card is again card");
            _nextAgainCard();
        } else if (todayList.size() == 0) {
            Log.i(TAG, "_nextDueCard:_completeLean");
            _completeLean();
        }
        try {

        } catch (Exception e) {
            Log.i(TAG, "_nextDueCard:Erorr:" + e.getMessage());
            _completeLean();
        }
        Log.i(TAG, "--------------END------------");


    }


    private void _nextAgainCard() {
        Log.i(TAG, "---------_nextAgainCard--------");
        if (againList.size() > 0) {//Check againList.size()>0
//            try {
            currentCard = againList.get(0);
            //Define current time and due card by second
            int current_time = (int) (new Date().getTime() / 1000);
            int due = (int) currentCard.getDue();

            Log.i(TAG, "_nextAgainCard:" + current_time + ":" + due);
            //Check due<current_time
            if (current_time - due >= 600 || todayList.size() == 0 && dueList.size() == 0) {
                Log.i(TAG, "_nextAgainCard:Next card is again card 2");
                //Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_LNR1);
            } else {
                Log.i(TAG, "_nextAgainCard:Next card is due card 1");
                _nextDueCard();
            }
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.i(TAG, "_nextAgainCard: _completeLean();");
//                _completeLean();
//            }
        } else if (dueList.size() > 0) {//Check dueList.size()>0
            Log.i(TAG, "_nextAgainCard:Next card is due card");
            _nextDueCard();
        } else {
            Log.i(TAG, "_nextAgainCard:Next card is new card");
            _nextNewCard();
        }
        Log.i(TAG, "--------------END--------------");

    }

    /**
     * Load string Html
     */
    private void _loadWebView(String questionDisplay, int queue) {
        //Clear View
        if (Build.VERSION.SDK_INT < 18) {
            mWebViewLeadDetails.clearView();
        } else {
            mWebViewLeadDetails.loadUrl("about:blank");
        }

        if (queue == QUEUE_NEW_CRAM0) {
            //set BackBackground color
            lbCountDue.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
            lbCountAgain.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
            lbCountNew.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        } else if (queue == Card.QUEUE_LNR1) {
            //set BackBackground color
            lbCountDue.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
            lbCountAgain.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            lbCountNew.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
        } else if (queue == Card.QUEUE_REV2) {
            //set BackBackground color
            lbCountDue.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            lbCountAgain.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
            lbCountNew.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
        } else if (queue == 10) {
        }

        //Set Data
        mWebViewLeadDetails.loadDataWithBaseURL(LazzyBeeShare.ASSETS, questionDisplay, LazzyBeeShare.mime, LazzyBeeShare.encoding, null);

    }

    private void _showBtnAnswer() {
        //Set flag Display State
        answerDisplay = false;

        //show btnShowAnswer and hide btnAgain0
        btnShowAnswer.setVisibility(View.VISIBLE);
        mLayoutButton.setVisibility(View.GONE);

    }


    /**
     * Speak text theo version andorid
     */
    public void _speakText(String toSpeak) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            _textToSpeechGreater21(toSpeak);
        } else {
            _textToSpeechUnder20(toSpeak);
        }
    }

    @SuppressWarnings("deprecation")
    private void _textToSpeechUnder20(String text) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void _textToSpeechGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    public void processFinish(Card card) {
        //Display Card
        if (card != null) {
            this.currentCard.setAnswers(card.getAnswers());
            //Update Success reload data
            if (answerDisplay) {
                //Load answer
                _loadWebView(LazzyBeeShare.getAnswerHTML(context, card), 10);
            } else {
                //Load question
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, card.getQuestion()), card.getQueue());
            }

            //Update Card form DB
            dataBaseHelper._updateCardFormServer(card);

            Toast.makeText(context, "Update card ok", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Update card error", Toast.LENGTH_SHORT).show();
        }
    }

    class StudyCardPageAdapter extends PagerAdapter {
        Card card;
        List<String> packages;
        private Context context;
        WebView mWebViewStudyConten;
        private int showType;
        TextView lbDue;
        TextView lbNew;
        TextView lbAgain;

        public StudyCardPageAdapter(Context context, Card card, int showType) {
            this.card = card;
            this.context = context;
            this.showType = showType;
            if (showType == 0)
                packages = new ArrayList<String>(Arrays.asList("Study"));
            else
                packages = new ArrayList<String>(Arrays.asList("Study", "Dic"));

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

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view = getLayoutInflater().inflate(R.layout.page_study_card, container, false);

            // Add the newly created View to the ViewPager
            container.addView(view);
            CardView mCardViewCount = (CardView) view.findViewById(R.id.mCardViewCount);
            mWebViewStudyConten = (WebView) view.findViewById(R.id.mWebViewStudyConten);

            lbDue = (TextView) view.findViewById(R.id.lbDue);
            lbNew = (TextView) view.findViewById(R.id.lbNew);
            lbAgain = (TextView) view.findViewById(R.id.lbAgain);

            WebSettings ws = mWebViewStudyConten.getSettings();
            ws.setJavaScriptEnabled(true);

            _addJavascriptInterfaceQuestionAndAnswer();

            List<String> packs = Arrays.asList("common", "dic");

            String answer = LazzyBeeShare.EMPTY;
            if (showType == 0) {
                answer = LazzyBeeShare._getQuestionDisplay(context, card.getQuestion());
            } else {
                answer = LazzyBeeShare.getAnswerHTMLwithPackage(context, card, packs.get(position), false);
            }
            if (position != 0) {
                mCardViewCount.setVisibility(View.GONE);
            }

            lbDue.setText(lbCountDue.getText());
            lbNew.setText(lbCountNew.getText());
            lbAgain.setText(lbCountAgain.getText());
            _setPlanPlag(card.getQueue());

            mWebViewStudyConten.loadDataWithBaseURL(LazzyBeeShare.ASSETS, answer, LazzyBeeShare.mime, LazzyBeeShare.encoding, null);


            return view;
        }

        private void _setPlanPlag(int queue) {
            if (queue == QUEUE_NEW_CRAM0) {
                //set BackBackground color
                lbDue.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                lbAgain.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                lbNew.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            } else if (queue == Card.QUEUE_LNR1) {
                //set BackBackground color
                lbDue.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                lbAgain.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                lbNew.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
            } else if (queue == Card.QUEUE_REV2) {
                //set BackBackground color
                lbDue.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                lbAgain.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                lbNew.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
            } else if (queue == 10) {
            }
        }

        private void _addJavascriptInterfaceQuestionAndAnswer() {
            //Todo: addJavascriptInterface play question
            mWebViewStudyConten.addJavascriptInterface(new LazzyBeeShare.JsObjectQuestion() {
                @JavascriptInterface
                public void playQuestion() {
                    //get text to Speak
                    String toSpeak = card.getQuestion();

                    //Toast Text Speak
                    //Toast.makeText(this.getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();

                    //Speak text
                    _speakText(toSpeak);

                    //textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            }, "question");
            mWebViewStudyConten.addJavascriptInterface(new LazzyBeeShare.JsObjectExplain() {
                @JavascriptInterface
                public void speechExplain() {
                    //get answer json
                    String answer = card.getAnswers();
                    String toSpeech = LazzyBeeShare._getValueFromKey(answer, "explain");

                    //Speak text
                    _speakText(toSpeech);
                }
            }, "explain");
            mWebViewStudyConten.addJavascriptInterface(new LazzyBeeShare.JsObjectExample() {
                @JavascriptInterface
                public void speechExample() {
                    //get answer json
                    String answer = card.getAnswers();
                    String toSpeech = LazzyBeeShare._getValueFromKey(answer, "example");

                    //Speak text
                    _speakText(toSpeech);
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



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
