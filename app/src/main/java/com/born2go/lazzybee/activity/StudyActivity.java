package com.born2go.lazzybee.activity;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.ContextThemeWrapper;
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

        int limit_today = dataBaseHelper._getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT);
        int total_learn_per_day = dataBaseHelper._getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT);
        //get card due today & agin
        againList = dataBaseHelper._getListCardByQueue(Card.QUEUE_LNR1, 0);
        dueList = dataBaseHelper._getListCardByQueue(Card.QUEUE_REV2, LazzyBeeShare.TOTAL_LEAN_PER_DAY);
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
        setResult(LazzyBeeShare.CODE_COMPLETE_STUDY_RESULTS, new Intent());
        onBackPressed();
    }

    /**
     * Init db sqlite
     */
    private void _initDatabase() {
        dataBaseHelper = new LearnApiImplements(this);
    }

    private void _initTextToSpeech() {
        String sp = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_SPEECH_RATE);
        float speech = 1.0f;
        if (sp != null)
            speech = Float.valueOf(sp);

        //Todo:init TextToSpeech
        final float finalSpeech = speech;
        textToSpeech = new TextToSpeech(this.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(finalSpeech);
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
        switch (id) {
            case android.R.id.home:
                // I do not want this...
                // Home as up button is to navigate to Home-Activity not previous acitivity
                super.onBackPressed();
                return true;
            case R.id.action_detelte:
                Log.i(TAG, "_deleteCard question:" + currentCard.getQuestion());
                _showDialogDeleteCard();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void _showDialogDeleteCard() {
        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(getString(R.string.dialog_message_delete_card, currentCard.getQuestion()))
                .setTitle(R.string.dialog_title_delete_card);

        // Add the buttons
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Update Queue_list in system table
                _deleteCard();
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();

    }


    boolean done_card = false;

    private void _deleteCard() {
        if (btnShowAnswer.getVisibility() == View.GONE) {
            btnShowAnswer.setVisibility(View.VISIBLE);
            mLayoutButton.setVisibility(View.GONE);
        }

        done_card = true;
        int currentQueue = currentCard.getQueue();
        Log.i(TAG, "_deleteCard currentQueue:" + currentQueue);
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
        Log.i(TAG, "_deleteCard question:" + currentCard.getQuestion() + ",currentQueue:" + currentCard.getQueue());
        currentCard.setQueue(Card.QUEUE_DONE_2);

        dataBaseHelper._updateCard(currentCard);
        currentCard.setQueue(currentQueue);
        Log.i(TAG, "_deleteCard After Update question:" + currentCard.getQuestion() + ",currentQueue:" + currentCard.getQueue());
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
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_LNR1);
            } else if (dueList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is Again Card 2");
                if (dueList.size() == 1)
                    currentCard = dueList.get(0);
                else if (dueList.size() > 0) {
                    position_due = dueList.size();
                    currentCard = dueList.get(position_due - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_REV2);

            } else if (todayList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is Again Card 3");
                if (todayList.size() == 1) {
                    currentCard = todayList.get(0);
                } else if (todayList.size() > 0) {
                    position = todayList.size();
                    currentCard = todayList.get(position - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_NEW_CRAM0);

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
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_REV2);

            } else if (todayList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is Due Card 2");
                if (todayList.size() == 1) {
                    currentCard = todayList.get(0);
                } else if (todayList.size() > 0) {
                    position = todayList.size();
                    currentCard = todayList.get(position - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_NEW_CRAM0);

            } else if (againList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is Due Card 3");
                if (againList.size() == 1) {
                    currentCard = againList.get(0);
                } else if (againList.size() > 0) {
                    position_again = againList.size();
                    currentCard = againList.get(position_again - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_LNR1);
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
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_NEW_CRAM0);

            } else if (againList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is New Card 2");
                if (againList.size() == 1) {
                    currentCard = againList.get(0);
                } else if (againList.size() > 0) {
                    position_again = againList.size();
                    currentCard = againList.get(position_again - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_LNR1);
            } else if (dueList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: next card is New Card 3");

                if (dueList.size() == 1)
                    currentCard = dueList.get(0);
                else if (dueList.size() > 0) {
                    position_due = dueList.size();
                    currentCard = dueList.get(position_due - 1);
                }

                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_REV2);

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
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_LNR1);
            } else if (dueList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: 2");
                if (dueList.size() == 1)
                    currentCard = dueList.get(0);
                else if (dueList.size() > 0) {
                    position_due = dueList.size();
                    currentCard = dueList.get(position_due - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_REV2);

            } else if (todayList.size() > 0) {
                Log.i(TAG, "_nextAfterDoneCard: 3");
                if (todayList.size() == 1) {
                    currentCard = todayList.get(0);
                } else if (todayList.size() > 0) {
                    position = todayList.size();
                    currentCard = todayList.get(position - 1);
                }
                //TODO:Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_NEW_CRAM0);

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
        //Setting webview
        WebSettings ws = mWebViewLeadDetails.getSettings();
        ws.setJavaScriptEnabled(true);

        //Load one card to show
        try {
            if (againList.size() > 0) {
                Log.i(TAG, "Load first again card ");
                //currentCard = againList.get(position_again);
                currentCard = againList.get(0);

                lbCountDue.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                lbCountAgain.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                lbCountNew.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
            } else if (dueList.size() > 0) {
                Log.i(TAG, "Load first duecard ");
                //currentCard = dueList.get(position_due);
                currentCard = dueList.get(0);

                lbCountDue.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                lbCountAgain.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                lbCountNew.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
            } else if (todayList.size() > 0) {
                Log.i(TAG, "Load first new card ");
                currentCard = todayList.get(position);

                lbCountDue.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                lbCountAgain.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                lbCountNew.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Showtime
        mWebViewLeadDetails.loadDataWithBaseURL(LazzyBeeShare.ASSETS, LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), LazzyBeeShare.mime, LazzyBeeShare.encoding, null);

        //Inject native handle to web element
        _addJavascriptInterfaceQuestionAndAnswer();
    }

    public void onbtnShowAnswerClick(View view) {
        _showAnswer();
    }

    public void onbtnAgainClick(View view) {
        _showBtnAnswer();
        _answerAgainCard();
    }

    private void _showBtnAnswer() {
        //TODO:show btnShowAnswer and hide btnAgain0
        btnShowAnswer.setVisibility(View.VISIBLE);
        mLayoutButton.setVisibility(View.GONE);
    }

    public void onbtnHardClick(View view) {
        _showBtnAnswer();
        _answerDueCard(Card.EASE_HARD);
    }

    public void onbtnGoodClick(View view) {
        _showBtnAnswer();
        _answerDueCard(Card.EASE_GOOD);
    }

    public void onbtnEasyClick(View view) {
        _showBtnAnswer();
        _answerDueCard(Card.EASE_EASY);
    }

    private void _showAnswer() {
        //hide btnShowAnswer and show mLayoutButton
        btnShowAnswer.setVisibility(View.GONE);
        mLayoutButton.setVisibility(View.VISIBLE);

        try {
            //get card
            Card card = currentCard;
            Card card1 = dataBaseHelper._getCardByID("" + card.getId());
            Log.i(TAG, "btnShowAnswer question=" + card.getQuestion() + ",queue=" + card.getQueue() + ",queue db:" + card1.getQueue());
            //Show answer question

            //Load Answer
            _loadWebView(LazzyBeeShare.getAnswerHTML(context, card), 10);

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


    private void _answerAgainCard() {
        Log.i(TAG, "----------------_answerAgainCard----------------");
        final int curren_time = (int) (new Date().getTime() / 1000);/*curent time*/
        int currentQueue = currentCard.getQueue();//Get current Queue
        Log.i(TAG, "_answerAgainCard:\tCurrrent Card Queue:"
                + currentQueue + ",question:" + currentCard.getQuestion());
        //_checkContainsAndRemove(againList);
        if (currentQueue < Card.QUEUE_NEW_CRAM0) {//Something's wrong???
            Log.i(TAG, "_answerAgainCard:\tQueue<Card.QUEUE_NEW_CRAM0 currentQueue:" + currentQueue);
            return;
        }

        if (currentQueue == Card.QUEUE_REV2) {
            //reset new card due
            dueList.remove(currentCard);
            int countDue = dueList.size();
            lbCountDue.setText(String.valueOf(countDue));
        } else if (currentQueue == Card.QUEUE_NEW_CRAM0) {
            //reset new card count
            todayList.remove(currentCard);
            int countNew = todayList.size();
            lbCountNew.setText(String.valueOf(countNew));
        }

        //We remove object, not index, cuz the object may be from other list
        againList.remove(currentCard);
        cardSched.answerCard(currentCard, Card.EASE_AGAIN);
        currentCard.setDue(curren_time + 60);
        //Now add to againList, don't care it is readd or add new
        againList.add(currentCard);

        int countAgain = againList.size();
        lbCountAgain.setText("" + countAgain);

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
        Log.i(TAG, "----------------------END-----------------------");
    }

    /**
     * Answer card by easy
     * get next card
     */
    private void _answerDueCard(int easy) {
        int currentQueue = currentCard.getQueue();//Get current Queue

        Log.i(TAG, "_answerDueCard:Currrent Card Queue:" + currentQueue);
        if (currentQueue >= Card.QUEUE_NEW_CRAM0) {
            //Check Contains and Remove
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
        Log.d(TAG, "Curent new card:" + currentCard.toString());
        if (todayList.size() > 0) {
            position = todayList.size() - 1;

            //get next card again
            Log.i(TAG, "_nextNewCard Position=" + position + " today:" + todayList.size());
            currentCard = todayList.get(0);

            _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_NEW_CRAM0);
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


    private void _nextDueCard() {
        Log.d(TAG, "_nextDueCard:Current Card:" + currentCard.toString());

        if (dueList.size() > 0) {//Check dueList.size()>0

            currentCard = dueList.get(0);

            lbCountDue.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            lbCountAgain.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
            lbCountNew.setPaintFlags(Paint.LINEAR_TEXT_FLAG);

            //TODO:Display next card
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


    }

    boolean flag_one = true;

    private void _nextAgainCard() {
        if (againList.size() > 0) {//Check againList.size()>0
            try {
                currentCard = againList.get(0);

                //get current time and du card
                int current_time = (int) (new Date().getTime() / 1000);
                int due = (int) currentCard.getDue();

                Log.i(TAG, "_nextAgainCard:" + current_time + ":" + due);
                if (current_time - due >= 600 || todayList.size() == 0 && dueList.size() == 0) {
                    Log.i(TAG, "_nextAgainCard:Next card is again card 2");
                    flag_one = false;

                    //Display next card
                    _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()), Card.QUEUE_LNR1);
                } else {
                    Log.i(TAG, "_nextAgainCard:Next card is due card 1");
                    _nextDueCard();
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

        if (queue == Card.QUEUE_NEW_CRAM0) {
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
