package com.born2go.lazzybee.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.GetCardFormServerByQuestion;
import com.born2go.lazzybee.adapter.GetCardFormServerByQuestion.GetCardFormServerByQuestionResponse;
import com.born2go.lazzybee.algorithms.CardSched;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.ContainerHolderSingleton;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.view.SlidingTabLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tagmanager.DataLayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.born2go.lazzybee.db.Card.QUEUE_NEW_CRAM0;

public class StudyActivity extends AppCompatActivity implements GetCardFormServerByQuestionResponse {

    private static final String TAG = "StudyActivity";
    private static final String GA_SCREEN = "aStudyScreen";
    private DataLayer mDataLayer;
    private Context context;

    boolean learn_more;

    LearnApiImplements dataBaseHelper;
    TextToSpeech textToSpeech;
    WebView mWebViewLeadDetails;
    TextView btnShowAnswer;
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

    LinearLayout container;

    public Card getBeforeCard() {
        return beforeCard;
    }

    public void setBeforeCard(Card beforeCard) {
        this.beforeCard = beforeCard;
    }


    ViewPager mViewPager;
    SlidingTabLayout mSlidingTabLayout;

    boolean answerDisplay = false;

    MenuItem btnBackBeforeCard;

    CardView mCountStudy;
    int completeStudy = 0;

    MenuItem itemFavorite;

    CardView mCardViewHelpandAdMod;

    RelativeLayout mShowAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        context = this;
        _initActonBar();
        _initDatabase();
        _trackerApplication();

        //init cardSched
        cardSched = new CardSched();


        _initView();
        _initTextToSpeech();
        _initAdView();

        _setUpStudy();

        // _initShowcaseInitStudy();

    }





    private void _setUpStudy() {
        try {
            //get lean_more form intern
            learn_more = getIntent().getBooleanExtra(LazzyBeeShare.LEARN_MORE, false);

            //get custom setting study
            int limit_today = dataBaseHelper._getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT);
            int total_learn_per_day = dataBaseHelper._getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT);

            //get card due today & agin
            againList = dataBaseHelper._getListCardByQueue(Card.QUEUE_LNR1, 0);
            dueList = dataBaseHelper._getListCardByQueue(Card.QUEUE_REV2, total_learn_per_day);

            //Define Count due
            int dueCount = dueList.size();

            //get new random card list to day
            //int newCount =
            //if (newCount > 0)
            //  todayList = dataBaseHelper._getRandomCard(newCount);
            if (dueCount == 0) {
                Log.i(TAG, "_setUpStudy()  dueCount == 0");
            } else {

                Log.i(TAG, "_setUpStudy()  dueCount != 0");

                if (dueCount < total_learn_per_day) {

                    Log.i(TAG, "_setUpStudy()  dueCount < total_learn_per_day");

                    if (total_learn_per_day - dueCount < limit_today) {

                        Log.i(TAG, "_setUpStudy()  total_learn_per_day - dueCount < limit_today");
                        limit_today = total_learn_per_day - dueCount;

                    } else if (total_learn_per_day - dueCount > limit_today) {

                        Log.i(TAG, "_setUpStudy()  total_learn_per_day - dueCount > limit_today");
                    }
                } else if (dueCount >= total_learn_per_day) {

                    Log.i(TAG, "_setUpStudy()  dueCount >= total_learn_per_day");
                    limit_today = 0;
                }
                learn_more = false;
            }

            //Define todayList
            todayList = dataBaseHelper._getRandomCard(limit_today, learn_more);

            //Define count card
            int againCount = againList.size();
            int todayCount = todayList.size();

            Log.i(TAG, "againCount:" + againCount);
            Log.i(TAG, "todayCount:" + todayCount);
            Log.i(TAG, "dueCount:" + dueCount + ",limit:" + dueCount + ",today:" + todayCount);

            //Define check_learn
            //check_learn==true Study
            //check_learn==false Complete Study
            boolean check_learn = againCount > 0 || dueCount > 0 || todayCount > 0;

            Log.i(TAG, "check_learn:" + (check_learn));

            if (check_learn) {
                _showFirstCard();
                //set again count
                lbCountAgain.setText(String.valueOf(againCount));
                lbCountAgain.setTag(againCount);
                //set new Count
                lbCountNew.setText(String.valueOf(todayCount));
                lbCountNew.setTag(todayCount);
                //set Due Count
                lbCountDue.setText(String.valueOf(dueCount));
                lbCountDue.setTag(dueCount);
            } else {
                Log.i(TAG, "_completeLean");
                _completeLean();
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
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
        //LazzyBeeApplication lazzyBeeApplication = (LazzyBeeApplication) getApplication();
        //mTracker = lazzyBeeApplication.getTracker(LazzyBeeApplication.TrackerName.APP_TRACKER);
        //mTracker = lazzyBeeApplication.getDefaultTracker();

        //Log.i(TAG, "Setting screen name: " + TAG);
        //mTracker.setScreenName("Image~" + TAG);
        //mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        try {
            Log.i(TAG, "Trying to use TagManager");
            mDataLayer = LazzyBeeSingleton.mDataLayer;
            //mDataLayer.push(DataLayer.mapOf("event", "openScreen", "screenName", TAG));
            mDataLayer.pushEvent("openScreen", DataLayer.mapOf("screenName", GA_SCREEN));
//            Log.i(TAG, "Get config from TagManager: ADV_ENABLE? " +
//                    ContainerHolderSingleton.getContainerHolder().getContainer().getString(LazzyBeeShare.ADV_ENABLE));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    private void _completeLean() {
        Log.i(TAG, "----_completeLean----");
        setBeforeCard(null);
        completeStudy = LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000;
        dataBaseHelper._insertOrUpdateToSystemTable(String.valueOf(LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS_1000), String.valueOf(completeStudy));
        setResult(completeStudy, new Intent());
        finish();
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
        container = (LinearLayout) findViewById(R.id.container);
        mWebViewLeadDetails = (WebView) findViewById(R.id.mWebViewLeadDetaisl);

        //init button
        mShowAnswer = (RelativeLayout) findViewById(R.id.mShowAnswer);

        btnShowAnswer = (TextView) findViewById(R.id.btnShowAnswer);
        mLayoutButton = (LinearLayout) findViewById(R.id.mLayoutButton);

        btnAgain0 = (Button) findViewById(R.id.btnAgain0);
        btnHard1 = (Button) findViewById(R.id.btnHard1);
        btnGood2 = (Button) findViewById(R.id.btnGood2);
        btnEasy3 = (Button) findViewById(R.id.btnEasy3);

        //init lbCount
        lbCountNew = (TextView) findViewById(R.id.lbCountTotalVocabulary);
        lbCountAgain = (TextView) findViewById(R.id.lbCountAgainInday);
        lbCountDue = (TextView) findViewById(R.id.lbAgainDue);

        mCountStudy = (CardView) findViewById(R.id.mCountStudy);

        mCardViewHelpandAdMod = (CardView) findViewById(R.id.mCardViewHelpandAdMod);

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

        itemFavorite = menu.findItem(R.id.action_favorite);

        if (currentCard != null) {
            //load favorite
            if (currentCard.getStatus() == 1) {
                itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_important));
                itemFavorite.setTitle(context.getString(R.string.action_favorite));
            } else {
                itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_not_important));
                itemFavorite.setTitle(context.getString(R.string.action_not_favorite));
            }
        }
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
        //***setOnQueryTextListener***
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getBaseContext(), query,
                        Toast.LENGTH_SHORT).show();
                _search(query);
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
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
            case R.id.action_learnt:
                Log.i(TAG, "_learntCard question:" + currentCard.getQuestion());
                _learntorIgnoreCardbyQueue(Card.QUEUE_DONE_2);
                return true;
            case R.id.action_ignore:
                Log.i(TAG, "Ignore question:" + currentCard.getQuestion());
                _learntorIgnoreCardbyQueue(Card.QUEUE_SUSPENDED_1);
                return true;
            case R.id.action_update:

                //define function update card form server
                _updateCardFormServer();

                return true;
            case R.id.action_back_before_card:

                _backToBeforeCard();

                return true;
            case R.id.action_share:
                _shareCard();
                return true;
            case R.id.action_favorite:
                _addCardToFavorite();
                return true;
            case R.id.action_goto_dictionary:
                _gotoDictionnary();
                return true;
            case R.id.action_report:
                _reportCard();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void _reportCard() {
        try {
            startActivity(LazzyBeeShare.getOpenFacebookIntent(context));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    private void _gotoDictionnary() {

        Intent intent = new Intent(this, CardDetailsActivity.class);
        intent.putExtra(LazzyBeeShare.CARDID, String.valueOf(currentCard.getId()));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        startActivityForResult(intent, getResources().getInteger(R.integer.code_card_details_updated));
    }


    private void _addCardToFavorite() {
        try {
            int statusFavrite = 0;
            //Set icon drawer
            if (itemFavorite.getTitle().toString().equals(getString(R.string.action_not_favorite))) {
                statusFavrite = 1;
                itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_important));
                itemFavorite.setTitle(context.getString(R.string.action_favorite));
            } else {
                itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_not_important));
                itemFavorite.setTitle(context.getString(R.string.action_not_favorite));
            }

            //set status card and Update card
            currentCard.setStatus(statusFavrite);
            dataBaseHelper._updateCard(currentCard);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    private void _shareCard() {
        try {
            //get base url in Task Manager
            String base_url_sharing = LazzyBeeShare.DEFAULTS_BASE_URL_SHARING;
            String server_base_url_sharing = ContainerHolderSingleton.getContainerHolder().getContainer().getString(LazzyBeeShare.BASE_URL_SHARING);
            if (server_base_url_sharing != null) {
                if (server_base_url_sharing.length() > 0)
                    base_url_sharing = server_base_url_sharing;
            }

            //define base url with question
            base_url_sharing = base_url_sharing + currentCard.getQuestion();
            Log.i(TAG, "Sharing URL:" + base_url_sharing);

            //Share card
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, base_url_sharing);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }

    }

    private void _backToBeforeCard() {
        Log.i(TAG, "-----------_backToBeforeCard------------");
        try {
            if (beforeCard != null) {
                //Define before queue
                int beforeQueue = beforeCard.getQueue();
                Log.i(TAG, "_backToBeforeCard()\t question:" + beforeCard.getQuestion() +
                        "\t queue:" + beforeQueue + " due:" + beforeCard.getDue());
                switch (beforeQueue) {
                    case Card.QUEUE_NEW_CRAM0:
                        Log.i(TAG, "_backToBeforeCard\t Queue=Card.QUEUE_NEW_CRAM0");

                        //Remove beforecard in againlist
//                    if(againList.contains(beforeCard)){
//                        againList.remove(beforeCard);
//                    }
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
                        int clonetodayCount = cloneTodayList.size();

                        //Clear Data
                        todayList.clear();

                        //Readd card to new card
                        if (clonetodayCount == 0) {
                            todayList.add(beforeCard);
                        } else {
                            todayList.add(0, beforeCard);
                            for (int i = 0; i < clonetodayCount; i++) {
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
//                    if(againList.contains(beforeCard)){
//                        againList.remove(beforeCard);
//                    }
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
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
        Log.i(TAG, "------------------END-------------------");
    }

    private void _updateCardFormServer() {
        //Call Api Update Card
        GetCardFormServerByQuestion getCardFormServerByQuestion = new GetCardFormServerByQuestion(context);
        getCardFormServerByQuestion.execute(currentCard);
        getCardFormServerByQuestion.delegate = this;
    }


    private void _learntorIgnoreCardbyQueue(int queue) {
        Log.i(TAG, "-------------------_learntorIgnoreCardbyQueue:" + queue + "-------------------");
        try {
            //Show item BackBeroreCard when answer
            btnBackBeforeCard.setVisible(false);

            if (btnShowAnswer.getVisibility() == View.GONE) {
                mShowAnswer.setVisibility(View.VISIBLE);
                btnShowAnswer.setVisibility(View.VISIBLE);
                mLayoutButton.setVisibility(View.GONE);
            }
            //Define card form server
            Card card = dataBaseHelper._getCardByID(String.valueOf(currentCard.getId()));
            int currentQueue = card.getQueue();
            Log.i(TAG, "_learntorIgnoreCardbyQueue currentQueue:" + currentQueue);

            switch (currentQueue) {
                case Card.QUEUE_NEW_CRAM0:
                    Log.i(TAG, "_learntorIgnoreCardbyQueue QUEUE_NEW_CRAM0");
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
                    Log.i(TAG, "_learntorIgnoreCardbyQueue QUEUE_LNR1");
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
                    Log.i(TAG, "_learntorIgnoreCardbyQueue QUEUE_REV2");
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

            Log.i(TAG, "_learntorIgnoreCardbyQueue question:" + card.getQuestion() + ",currentQueue:" + card.getQueue());

            //Set before card
            beforeCard = null;

            currentCard.setQueue(queue);

            Log.i(TAG, "_learntorIgnoreCardbyQueue before Update question:" + card.getQuestion() + ",currentQueue:" + card.getQueue());
            int update = dataBaseHelper._updateCard(currentCard);

            if (update >= 1) {
                Log.i(TAG, "_learntorIgnoreCardbyQueue After Update question:" + card.getQuestion() + ",currentQueue:" + card.getQueue());

                currentCard.setQueue(currentQueue);
                _nextCard(currentQueue);
            }
            String message = getString(R.string.message_learnt_card_sucessful);
            if (queue == Card.QUEUE_SUSPENDED_1) {
                message = getString(R.string.message_ignore_card_sucessful);
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
        Log.i(TAG, "-----------------------END----------------------");
    }


    private void _nextCard(int currentQueue) {
        switch (currentQueue) {
            case Card.QUEUE_NEW_CRAM0:
                Log.i(TAG, "_nextCard:\t Queue=Card.QUEUE_NEW_CRAM0");
                //_nextAgainCard();
                _nextNewCard();
                break;
            case Card.QUEUE_LNR1:
                Log.i(TAG, "_nextCard:\t Queue=Card.QUEUE_LNR1");
                //_nextDueCard();
                _nextAgainCard();
                break;
            case Card.QUEUE_REV2:
                Log.i(TAG, "_nextCard:\t Queue=Card.QUEUE_REV2");
                //_nextNewCard();
                _nextDueCard();
                break;
        }
        if (itemFavorite != null) {
            //load favorite
            if (currentCard.getStatus() == 1) {
                itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_important));
                itemFavorite.setTitle(context.getString(R.string.action_favorite));
            } else {
                itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_not_important));
                itemFavorite.setTitle(context.getString(R.string.action_not_favorite));
            }
        }
    }

    /**
     * Set data for webview.
     * <p/>
     * <p>Difine onclick btnAnswer and btnAgain0.</p>
     * <p/>
     * Define JavaScrip to Speek Text.
     */
    private void _showFirstCard() {
        //Setting webview
        WebSettings ws = mWebViewLeadDetails.getSettings();
        ws.setJavaScriptEnabled(true);

        //Load first card
        if (dueList.size() > 0) {
            //Load first card is Due card
            _nextDueCard();
        } else if (againList.size() > 0) {
            //Load first card is Again card
            _nextAgainCard();
        } else if (todayList.size() > 0) {
            //Load first card is new card
            _nextNewCard();
        }

        //Inject native handle to web element
        _addJavascriptInterfaceQuestionAndAnswer();
    }

    public void onbtnShowAnswerClick(View view) {
        //Set flag Display State
        answerDisplay = true;
        _showAnswer();
//        _initShowcaseShowAnswer();

    }


    public void onbtnAgainClick(View view) {
        _showBtnAnswer();
        _answerCard(Card.EASE_AGAIN);

    }

    public void onbtnHardClick(View view) {
        _showBtnAnswer();
        _answerCard(Card.EASE_HARD);

    }

    public void onbtnGoodClick(View view) {
        _showBtnAnswer();
        _answerCard(Card.EASE_GOOD);

    }

    public void onbtnEasyClick(View view) {
        _showBtnAnswer();
        _answerCard(Card.EASE_EASY);

    }

    private void _showAnswer() {
        try {
            //hide btnShowAnswer and show mLayoutButton
            mShowAnswer.setVisibility(View.GONE);
            btnShowAnswer.setVisibility(View.GONE);
            mLayoutButton.setVisibility(View.VISIBLE);
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
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }


    private void _answerCard(int easy) {
        Log.i(TAG, "----------------_answerCard:" + easy + "----------------");
        try {
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
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
        Log.i(TAG, "-----------------------END-----------------------------");
    }

    private void _addJavascriptInterfaceQuestionAndAnswer() {
        String sp = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_SPEECH_RATE);
        float speechRate = 1.0f;
        if (sp != null) {
            speechRate = Float.valueOf(sp);
        }
        //addJavascriptInterface play question
        final float finalSpeechRate = speechRate;
        mWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectQuestion() {
            @JavascriptInterface
            public void playQuestion() {
                //get text to Speak
                String toSpeak = currentCard.getQuestion();

                //Speak text
                LazzyBeeShare._speakText(toSpeak, finalSpeechRate);

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
                LazzyBeeShare._speakText(toSpeech, finalSpeechRate);
            }
        }, "explain");
        mWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectExample() {
            @JavascriptInterface
            public void speechExample() {
                //get answer json
                String answer = currentCard.getAnswers();
                String toSpeech = LazzyBeeShare._getValueFromKey(answer, "example");

                //Speak text
                LazzyBeeShare._speakText(toSpeech, finalSpeechRate);
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


    private void _nextNewCard() {
        Log.i(TAG, "---------_nextNewCard--------");
        Log.d(TAG, "Curent new card:" + currentCard.toString());
        if (todayList.size() > 0) {
            //get next new card
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
        mShowAnswer.setVisibility(View.VISIBLE);
        btnShowAnswer.setVisibility(View.VISIBLE);
        mLayoutButton.setVisibility(View.GONE);

    }


    @Override
    public void processFinish(Card card) {
        try {
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

//            Snackbar.make(container,
//                    Html.fromHtml(LazzyBeeShare.getTextColor(context.getResources().getColor(R.color.teal_500)
//                            , getString(R.string.message_update_card_successful))), Snackbar.LENGTH_SHORT)
//                    .show();
                Toast.makeText(context, getString(R.string.message_update_card_successful), Toast.LENGTH_SHORT).show();
            } else {

//            Snackbar.make(container,
//                    Html.fromHtml(LazzyBeeShare.getTextColor(context.getResources().getColor(R.color.teal_500)
//                            , getString(R.string.message_update_card_fails))), Snackbar.LENGTH_SHORT)
//                    .show();
                Toast.makeText(context, getString(R.string.message_update_card_fails), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//
//    }
}
