package com.born2go.lazzybee.activity;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.algorithms.CardSched;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.fragment.FragmentStudy;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class StudyActivity extends AppCompatActivity implements FragmentStudy.FragmentStudyListener {

    private static final String TAG = "StudyActivity";
    private Context context;

    FragmentStudy fragmentStudy;
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
    //init position
    int position = 0;
    int position_again = 0;
    int position_due = 0;


    boolean complete_new_learn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        context = this;
        _initView();

        //db
        _initDatabase();

        //init cardSched
        cardSched = new CardSched();

        _initTextToSpeech();

        //get lean_more form intern
        learn_more = getIntent().getBooleanExtra(LazzyBeeShare.LEARN_MORE, false);

        //get card due today & agin
        againList = dataBaseHelper._getListCardByQueue(Card.QUEUE_LNR1);
        dueList = dataBaseHelper._getListCardByQueue(Card.QUEUE_REV2);

        //get new random card list to day
        //TODO: only take new cards if total learn today not exceed MAX_NEW_LEARN_PER_DAY
        //int newCount = 10 - (againList.size() + dueList.size);
        //if (newCount > 0)
        //  todayList = dataBaseHelper._getRandomCard(newCount);
        todayList = dataBaseHelper._getRandomCard(LazzyBeeShare.MAX_NEW_LEARN_PER_DAY, learn_more);

        int dueCount = dueList.size();
        int againCount = againList.size();
        int todayCount = todayList.size();

        Log.i(TAG, "againCount:" + againCount);
        Log.i(TAG, "dueCount:" + dueCount);
        Log.i(TAG, "todayCount:" + todayCount);

        //set data
        boolean check_learn = againCount > 0 || dueCount > 0 || todayCount > 0;

        Log.i(TAG, "check_learn:" + (check_learn));
        if (check_learn) {
//        if (todayCount > 0) {
            _setDataforWebView();

            final int list_card_again_in_today_size = againList.size();
            //set total vocabilary
            lbCountAgain.setText("" + list_card_again_in_today_size);
            lbCountAgain.setTag(list_card_again_in_today_size);

            int list_card_new_size = todayList.size();
            lbCountNew.setText("" + list_card_new_size);
            lbCountNew.setTag(list_card_new_size);


            lbCountDue.setText("" + dueList.size());
            lbCountDue.setTag(dueList.size());
        } else {
            Log.i(TAG, "_completeLean");
            _completeLean();
        }


        //Add AdView
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Log.i(TAG, LazzyBeeShare.LEARN_MORE + ":" + learn_more);
    }

    private void _completeLean() {
        onBackPressed();
    }

    /**
     * Init db sqlite
     */
    private void _initDatabase() {
        dataBaseHelper = new LearnApiImplements(this);
    }

    private void _initTextToSpeech() {
        //Todo:init TextToSpeech
        textToSpeech = new TextToSpeech(this.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_detelte) {
            Log.i(TAG, "_doneCard question:" + currentCard.getQuestion());
            _doneCard();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    boolean done_card = false;

    private void _doneCard() {
        if (btnShowAnswer.getVisibility() == View.GONE) {
            btnShowAnswer.setVisibility(View.VISIBLE);
            mLayoutButton.setVisibility(View.GONE);
        }

        done_card = true;
        int currentQueue = currentCard.getQueue();
        Log.i(TAG, "_doneCard currentQueue:" + currentQueue);
        if (currentQueue == Card.QUEUE_NEW_CRAM0) {
            //reset new card count
            todayList.remove(currentCard);
            int countNew = todayList.size();
            lbCountNew.setText("" + countNew);
        } else if (currentQueue == Card.QUEUE_LNR1) {
            //reset new card again
            againList.remove(currentCard);
            int countAgain = againList.size();
            lbCountAgain.setText("" + countAgain);

        } else if (currentQueue == Card.QUEUE_REV2) {
            //reset new card due
            dueList.remove(currentCard);
            int countDue = dueList.size();
            lbCountDue.setText("" + countDue);
        } else {
            todayList.remove(currentCard);
            againList.remove(currentCard);
            dueList.remove(currentCard);
        }
        Log.i(TAG, "_doneCard question:" + currentCard.getQuestion() + ",currentQueue:" + currentCard.getQueue());
        currentCard.setQueue(Card.QUEUE_DONE_2);

        dataBaseHelper._updateCard(currentCard);
        currentCard.setQueue(currentQueue);
        Log.i(TAG, "_doneCard After Update question:" + currentCard.getQuestion() + ",currentQueue:" + currentCard.getQueue());
        _nextAfterDoneCard(currentQueue);
    }

    private void _nextAfterDoneCard(int currentQueue) {
        Log.i(TAG, "_nextAfterDoneCard currentQueue:" + currentQueue);
        if (currentQueue == Card.QUEUE_NEW_CRAM0) {
            Log.i(TAG, "_nextAfterDoneCard: next card is Again Card");
            if (againList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is Again Card 1");
                if (againList.size() == 1) {
                    currentCard = againList.get(0);
                } else if (againList.size() > 0) {
                    position_again = againList.size();
                    currentCard = againList.get(position_again - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_LNR1);
            } else if (dueList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is Again Card 2");
                if (dueList.size() == 1)
                    currentCard = dueList.get(0);
                else if (dueList.size() > 0) {
                    position_due = dueList.size();
                    currentCard = dueList.get(position_due - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_REV2);

            } else if (todayList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is Again Card 3");
                if (todayList.size() == 1) {
                    currentCard = todayList.get(0);
                } else if (todayList.size() > 0) {
                    position = todayList.size();
                    currentCard = todayList.get(position - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_NEW_CRAM0);

            } else {
                Log.i(TAG, "_nextAfterDoneCard: next card is Again Card 4");
                _completeLean();
            }
        } else if (currentQueue == Card.QUEUE_LNR1) {
            Log.i(TAG, "_nextAfterDoneCard: next card is Due Card");
            if (dueList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is Due Card 1");
                if (dueList.size() == 1)
                    currentCard = dueList.get(0);
                else if (dueList.size() > 0) {
                    position_due = dueList.size();
                    currentCard = dueList.get(position_due - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_REV2);

            } else if (todayList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is Due Card 2");
                if (todayList.size() == 1) {
                    currentCard = todayList.get(0);
                } else if (todayList.size() > 0) {
                    position = todayList.size();
                    currentCard = todayList.get(position - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_NEW_CRAM0);

            } else if (againList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is Due Card 3");
                if (againList.size() == 1) {
                    currentCard = againList.get(0);
                } else if (againList.size() > 0) {
                    position_again = againList.size();
                    currentCard = againList.get(position_again - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_LNR1);
            } else {
                Log.i(TAG, "_nextAfterDoneCard: next card is Due Card 4");
                _completeLean();
            }
        } else if (currentQueue == Card.QUEUE_REV2) {
            Log.i(TAG, "_nextAfterDoneCard: next card is New Card:current queue:" + currentQueue);
            if (todayList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is New Card 1");
                if (todayList.size() == 1) {
                    currentCard = todayList.get(0);
                } else if (todayList.size() > 0) {
                    position = todayList.size();
                    currentCard = todayList.get(position - 1);
                }
                Log.i(TAG, "_nextAfterDoneCard: next card is New Card:current queue load:" + currentQueue);
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_NEW_CRAM0);

            } else if (againList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is New Card 2");
                if (againList.size() == 1) {
                    currentCard = againList.get(0);
                } else if (againList.size() > 0) {
                    position_again = againList.size();
                    currentCard = againList.get(position_again - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_LNR1);
            } else if (dueList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is New Card 3");

                if (dueList.size() == 1)
                    currentCard = dueList.get(0);
                else if (dueList.size() > 0) {
                    position_due = dueList.size();
                    currentCard = dueList.get(position_due - 1);
                }

                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_REV2);

            } else {
                Log.i(TAG, "_nextAfterDoneCard: next card is Again New 4");
                _completeLean();
            }

        } else {
            if (againList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard:1");
                if (againList.size() == 1) {
                    currentCard = againList.get(0);
                } else if (againList.size() > 0) {
                    position_again = againList.size();
                    currentCard = againList.get(position_again - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_LNR1);
            } else if (dueList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: 2");
                if (dueList.size() == 1)
                    currentCard = dueList.get(0);
                else if (dueList.size() > 0) {
                    position_due = dueList.size();
                    currentCard = dueList.get(position_due - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_REV2);

            } else if (todayList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: 3");
                if (todayList.size() == 1) {
                    currentCard = todayList.get(0);
                } else if (todayList.size() > 0) {
                    position = todayList.size();
                    currentCard = todayList.get(position - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_NEW_CRAM0);

            } else {
                Log.i(TAG, "_nextAfterDoneCard: 4");
                _completeLean();
            }
        }
        Log.i(TAG, "_nextAfterDoneCard  Affter next currentQueue:" + currentQueue);
    }


    /**
     * Hoan thanh khoa hoc rui quay tro lai DetailCourse
     */
    @Override
    public void completeCourse() {
        try {
            onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
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
        //Todo: Set  JavaScripEnabled for webview
        WebSettings ws = mWebViewLeadDetails.getSettings();
        ws.setJavaScriptEnabled(true);

        try {
            //Todo: Load first card
            if (againList.size() > 0) {
                Log.i(TAG, "Load first again card ");
                currentCard = againList.get(position_again);

                lbCountDue.setBackgroundResource(R.color.white);
                lbCountAgain.setBackgroundResource(R.color.teal_200);
                lbCountNew.setBackgroundResource(R.color.white);
            } else if (dueList.size() > 0) {
                //Todo: get next Card
                Log.i(TAG, "Load first duecard ");
                currentCard = dueList.get(position_due);


                lbCountDue.setBackgroundResource(R.color.teal_200);
                lbCountAgain.setBackgroundResource(R.color.white);
                lbCountNew.setBackgroundResource(R.color.white);
            } else if (todayList.size() > 0) {

                Log.i(TAG, "Load first new card ");
                currentCard = todayList.get(position);

                lbCountDue.setBackgroundResource(R.color.white);
                lbCountAgain.setBackgroundResource(R.color.white);
                lbCountNew.setBackgroundResource(R.color.teal_200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mWebViewLeadDetails.loadDataWithBaseURL(LazzyBeeShare.ASSETS, LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), LazzyBeeShare.mime, LazzyBeeShare.encoding, null);

        _addJavascriptInterfaceQuestionAndAnswer();


        //btnShowAnswer onCLick
        btnShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hide btnShowAnswer and show mLayoutButton
                btnShowAnswer.setVisibility(View.GONE);
                mLayoutButton.setVisibility(View.VISIBLE);

                try {
                    //get card
                    Card card = currentCard;
                    Card card1 = dataBaseHelper._getCardByID("" + card.getId());
                    Log.i(TAG, "btnShowAnswer question=" + card.getQuestion() + ",queue=" + card.getQueue() + ",queue db:" + card1.getQueue());
                    //Show answer question
                    //mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, getAnswerHTML(card), mime, encoding, null);
                    // Log.i(TAG, "HTML init:" + getAnswerHTML(card));

                    //Load Answer
                    _loadWebView(LazzyBeeShare.getAnswerHTML(context, card), 10);

//                    //set current card
//                    currentCard[0] = card;

                    //get  next Ivl String List
                    String[] ivlStrList = cardSched.nextIvlStrLst(card);

                    //set text btn
                    btnAgain0.setText(Html.fromHtml(ivlStrList[Card.EASE_AGAIN] + "<br/>" + getString(R.string.EASE_AGAIN)));
                    btnHard1.setText(Html.fromHtml(ivlStrList[Card.EASE_HARD] + "<br/>" + getString(R.string.EASE_HARD)));
                    btnGood2.setText(Html.fromHtml(ivlStrList[Card.EASE_GOOD] + "<br/>" + getString(R.string.EASE_GOOD)));
                    btnEasy3.setText(Html.fromHtml(ivlStrList[Card.EASE_EASY] + "<br/>" + getString(R.string.EASE_EASY)));

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        //Todo:btnAgain on click
        btnAgain0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set display card queue==2
                //update due
                //display card next if cardQueuesize>0 else priority cardQueue>cardDue>cardNew
                //if end card else complete
                //update rev_count
                //show btnShowAnswer and hide btnAgain0
                _answerAgainCard();
            }


        });
        //

        btnHard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _answerDueCard(Card.EASE_HARD);

            }
        });

        //btnGood2 onClick
        //set display card queue==2
        //update due
        //display card next if cardQueuesize>0 else priority cardQueue>cardDue>cardNew
        //if end card else complete
        btnGood2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _answerDueCard(Card.EASE_GOOD);
            }
        });


        btnEasy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _answerDueCard(Card.EASE_EASY);
            }
        });

    }

    private void _answerAgainCard() {
        final int curren_time = (int) (new Date().getTime() / 1000);/*curent time*/
        btnShowAnswer.setVisibility(View.VISIBLE);
        mLayoutButton.setVisibility(View.GONE);

        int currentQueue = currentCard.getQueue();//Get current Queue
        Log.i(TAG, "_answerAgainCard:Currrent Card Queue:"
                + currentQueue + ",question:" + currentCard.getQuestion());
        //_checkContainsAndRemove(againList);
        if (currentQueue >= Card.QUEUE_NEW_CRAM0) {
            //TODO:Reset count list again,new,due
            if (currentQueue == Card.QUEUE_NEW_CRAM0) {
                //reset new card count
                todayList.remove(currentCard);
                int countNew = todayList.size();
                lbCountNew.setText("" + countNew);
            }
//        if (currentQueue == Card.QUEUE_LNR1) {
////            reset new card again

//            int countAgain = againList.size();
//            lbCountAgain.setText("" + countAgain);
//
//        }
            if (currentQueue == Card.QUEUE_REV2) {
                //reset new card due
                dueList.remove(currentCard);
                int countDue = dueList.size();
                lbCountDue.setText("" + countDue);

            }

            againList.remove(currentCard);
            //TODO:Set queue,due using cardShed
            cardSched.answerCard(currentCard, Card.EASE_AGAIN);
            currentCard.setDue(curren_time + 60);
            againList.add(currentCard);

            int countAgain = againList.size();
            lbCountAgain.setText("" + countAgain);

            //TODO:update card
            dataBaseHelper._updateCard(currentCard);

            try {
                if (currentQueue == Card.QUEUE_NEW_CRAM0) {
                    Log.i(TAG, "_answerAgainCard:Card.QUEUE_NEW_CRAM0");
                    _nextAgainCard();

                }
                if (currentQueue == Card.QUEUE_LNR1) {
                    Log.i(TAG, "_answerAgainCard:Card.QUEUE_LNR1");
                    _nextDueCard();

                }
                if (currentQueue == Card.QUEUE_REV2) {
                    Log.i(TAG, "_answerAgainCard:Card.QUEUE_REV2");
                    _nextNewCard();
                }
            } catch (Exception e) {
                e.printStackTrace();
                _completeLean();
            }
        } else {
            Log.i(TAG, "_answerAgainCard:Queue<Card.QUEUE_NEW_CRAM0 currentQueue:" + currentQueue);
            //_nextNewCard();
            // _nextAfterDoneCard(currentQueue);
        }


//        int flag_queue = 1;
//        int due_time = (int) (curren_time + 600);
//
//        try {
//            //getCurrent Card Queue
//            int currentCardQueue = currentCard.getQueue();
//
//            //Check Contains
//            if (againList.contains(currentCard)) {
//                Log.i(TAG, "Card Contains againList");
//                //remove current Card
//                againList.remove(currentCard);
//            }
//            if (cardListAgainToday.contains(currentCard)) {
//                Log.i(TAG, "Card Contains in cardListAgainToday");
//                //remove current Card
//                cardListAgainToday.remove(currentCard);
//            }
//
//
//            cardSched.answerCard(currentCard, Card.EASE_AGAIN);
//
//
//            againList.add(currentCard);
//            cardListAgainToday.add(currentCard);
//
//
//            dataBaseHelper._updateCard(currentCard);//update card
//
//            int _count = todayList.size();
//            //reset total vocabilary
//            int current_count = _count - cardListAgainToday.size();
//            lbCountNew.setText("" + current_count);
//
//            //Update Count Again
//            int count_card_list_again = againList.size();
//            //reset total vocabilary
//            lbCountAgain.setText("" + count_card_list_again);
//
//            if (position < todayList.size()) {
//                if (currentCardQueue == Card.QUEUE_NEW_CRAM0) {
//                    //if currentCardQueue == Card.QUEUE_NEW_CRAM0 next card again if againList.size>0
//                    Log.i(TAG, "Card.QUEUE_NEW_CRAM0");
//                    _nextAgainCard();
//
//
//                } else if (currentCardQueue == Card.QUEUE_LNR1) {
//                    Log.i(TAG, "Card.QUEUE_LNR1");
//                    _nextDueCard();
//
//
//                } else if (currentCardQueue == Card.QUEUE_REV2) {
//                    Log.i(TAG, "Card.QUEUE_REV2");
//                    _nextNewCard();
//
//                }
//            } else {
//                _completeLean();
//
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    /**
     * Answer card by easy
     * get next card
     */
    private void _answerDueCard(int easy) {
        //TODO:show btnShowAnswer and hide btnAgain0
        btnShowAnswer.setVisibility(View.VISIBLE);
        mLayoutButton.setVisibility(View.GONE);

        int currentQueue = currentCard.getQueue();//Get current Queue

        Log.i(TAG, "_answerDueCard:Currrent Card Queue:" + currentQueue);
        if (currentQueue >= Card.QUEUE_NEW_CRAM0) {
            //Check Contains and Remove
            // _checkContainsAndRemove(dueList);
            _checkContainsAndRemove(cardListAddDueToDay);

            //TODO:Reset count list again,new,due
            if (currentQueue == Card.QUEUE_NEW_CRAM0) {
                //reset new card count
                todayList.remove(currentCard);
                int countNew = todayList.size();
                Log.i(TAG, "_answerDueCard:Curren new count:" + countNew);
                lbCountNew.setText("" + countNew);
            }
            if (currentQueue == Card.QUEUE_LNR1) {
                //reset new card again
                int countAgain = againList.size() - 1;
                lbCountAgain.setText("" + countAgain);
                againList.remove(currentCard);
            }
            if (currentQueue == Card.QUEUE_REV2) {
                //reset new card due
                int countDue = dueList.size() - 1;
                lbCountDue.setText("" + countDue);
                dueList.remove(currentCard);

            }


            //TODO:Set queue,due using cardShed
            cardSched.answerCard(currentCard, easy);
            // dueList.add(currentCard);
            cardListAddDueToDay.add(currentCard);

            //TODO:update card
            dataBaseHelper._updateCard(currentCard);

            //Todo:get next card by currentQueue
            try {
//            if (position <= todayList.size()) {
                if (currentQueue == Card.QUEUE_NEW_CRAM0) {
                    Log.i(TAG, "_answerDueCard:Card.QUEUE_NEW_CRAM0");
                    _nextAgainCard();

                }
                if (currentQueue == Card.QUEUE_LNR1) {
                    Log.i(TAG, "_answerDueCard:Card.QUEUE_LNR1");
                    _nextDueCard();

                }
                if (currentQueue == Card.QUEUE_REV2) {
                    Log.i(TAG, "_answerDueCard:Card.QUEUE_REV2");
                    _nextNewCard();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.i(TAG, "_answerDueCard:Queue<Card.QUEUE_NEW_CRAM0");
            _nextAgainCard();
        }
    }

    private void _addJavascriptInterfaceQuestionAndAnswer() {
        //Todo: addJavascriptInterface play question
        mWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectQuestion() {
            @JavascriptInterface
            public void playQuestion() {
                //get text to Speak
                String toSpeak = currentCard.getQuestion();

                //Toast Text Speak
                //Toast.makeText(this.getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();

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

//        //Todo: addJavascriptInterface play answer
//        mWebViewLeadDetails.addJavascriptInterface(new JsObjectAnswers() {
//            @JavascriptInterface
//            public void playAnswers() {
//                //get text to Speak
//                String toSpeak = currentCard.getAnswers();
//
//                //Toast Text Speak
//                //Toast.makeText(this, toSpeak, Toast.LENGTH_SHORT).show();
//
//                //Speak text
//                _speakText(toSpeak);
//
//                //textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
//            }
//        }, "answers");


    }

    private void _checkContainsAndRemove(List<Card> cardLis) {
        if (dueList.contains(currentCard)) {
            Log.i(TAG, "Card Contains cardList");
            //remove current Card
            cardLis.remove(currentCard);
        }
    }

    private void _updateCardQueueAndCardDue(int card_id, int queue, int due) {
        dataBaseHelper._updateCardQueueAndCardDue(/*cast in to string*/String.valueOf(card_id), queue, due);

    }


    private void _nextNewCard() {
        Log.i(TAG, "Curent new card:" + currentCard.toString());
        if (todayList.size() > 0) {

            position = todayList.size() - 1;
//            try {
            //get next card again
            Log.i(TAG, "_nextNewCard Position=" + position + " today:" + todayList.size());
            currentCard = todayList.get(position);

            //TODO:Display next card
            _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_NEW_CRAM0);

//            } catch (Exception e) {
//                Log.i(TAG, "_nextNewCard:Error:" + e.getMessage());
//                e.printStackTrace();
//                _completeLean();
//            }
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
    }

    boolean flag_due = true;

    private void _nextDueCard() {
        Log.i(TAG, "_nextDueCard:Current Card:" + currentCard.toString());

        if (dueList.size() > 0) {//Check dueList.size()>0
            if (flag_due) {
                position_due = 0;
            } else {
                position_due++;
            }
            Log.i(TAG, "_nextDueCard:Next card is due card " + dueList.size());

            // position_due = (dueList.size() - 1);
            currentCard = dueList.get(position_due);

            lbCountDue.setBackgroundResource(R.color.teal_200);
            lbCountAgain.setBackgroundResource(R.color.white);
            lbCountNew.setBackgroundResource(R.color.white);

            //TODO:Display next card
            _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_REV2);

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


    }

    boolean flag_one = true;

    private void _nextAgainCard() {
        //Log.i(TAG, "_nextAgainCard:Current Card:" + currentCard.toString());
        if (againList.size() > 0) {//Check againList.size()>0
            try {
                if (flag_one) {
                    position_again = 0;
                } else {
                    position_again++;
                }
                if (position_again < (againList.size())) {
                    currentCard = againList.get(position_again);

                    //get current time and du card
                    int current_time = (int) (new Date().getTime() / 1000);
                    int due = (int) currentCard.getDue();

                    Log.i(TAG, "_nextAgainCard:" + current_time + ":" + due);
                    if (current_time - due >= 600 || todayList.size() == 0 && dueList.size() == 0) {
                        Log.i(TAG, "_nextAgainCard:Next card is again card 2");
                        flag_one = false;
                        //TODO:Display next card
                        _loadWebView(LazzyBeeShare._getQuestionDisplay(currentCard.getQuestion()), Card.QUEUE_LNR1);

                    } else {
                        Log.i(TAG, "_nextAgainCard:Next card is due card 1");
                        _nextDueCard();
                    }
                } else {
                    if (againList.size() > 0) {
                        Log.i(TAG, "_nextAgainCard:again >0");
                        flag_one = true;
                        _nextAgainCard();
                    } else if (todayList.size() > 0) {
                        Log.i(TAG, "_nextAgainCard:Next card is new card 3");
                        _nextNewCard();
                    } else {
                        Log.i(TAG, "_nextAgainCard:_completeLean 3:" + againList.size());
                        _completeLean();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "_nextAgainCard: _completeLean();");
                _completeLean();
            }
        } else if (dueList.size() > 0) {//Check dueList.size()>0
            Log.i(TAG, "_nextAgainCard:Next card is due card");
            _nextDueCard();
        } else {
            Log.i(TAG, "_nextAgainCard:Next card is new card");
            _nextNewCard();
        }

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
        //
        //  Log.i(TAG, "HTML FROM:" + questionDisplay.toString());

        if (queue == Card.QUEUE_NEW_CRAM0) {
            //set BackBackground color
            lbCountDue.setBackgroundResource(R.color.white);
            lbCountAgain.setBackgroundResource(R.color.white);
            lbCountNew.setBackgroundResource(R.color.teal_200);
        } else if (queue == Card.QUEUE_LNR1) {
            //set BackBackground color
            lbCountDue.setBackgroundResource(R.color.white);
            lbCountAgain.setBackgroundResource(R.color.teal_200);
            lbCountNew.setBackgroundResource(R.color.white);
        } else if (queue == Card.QUEUE_REV2) {
            //set BackBackground color
            lbCountDue.setBackgroundResource(R.color.teal_200);
            lbCountAgain.setBackgroundResource(R.color.white);
            lbCountNew.setBackgroundResource(R.color.white);
        } else if (queue == 10) {
        }
        //Set Data
        mWebViewLeadDetails.loadDataWithBaseURL(LazzyBeeShare.ASSETS, questionDisplay, LazzyBeeShare.mime, LazzyBeeShare.encoding, null);

    }

//    /**
//     * init HTML question
//     */
//    private String _getQuestionDisplay(String s) {
//        String html =
//                "<!DOCTYPE html>\n" +
//                        "<html>\n" +
//                        "<head>\n" +
//                        "<style>\n" +
//                        " figure {" +
//                        "   text-align: center;" +
//                        "   margin: auto;" +
//                        "}" +
//                        "figure.image img {" +
//                        "   width: 100% !important;" +
//                        "   height: auto !important;" +
//                        "}" +
//                        "figcaption {" +
//                        "   font-size: 10px;" +
//                        "}" +
//                        "a {" +
//                        " margin-top:5px;" +
//                        "}" +
//                        "</style>\n" +
//                        "</head>\n" +
//                        "<body>\n" +
//                        "<h1 >" + s + "<a onclick='question.playQuestion();'><img src='ic_play_black.png'/></a></h1>"
//                        + "</body>\n" +
//                        "</html>";
//        return html;
//    }


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
}
