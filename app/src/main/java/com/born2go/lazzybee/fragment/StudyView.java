package com.born2go.lazzybee.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.activity.CardDetailsActivity;
import com.born2go.lazzybee.activity.StudyActivity;
import com.born2go.lazzybee.adapter.CustomViewPager;
import com.born2go.lazzybee.adapter.GetCardFormServerByQuestion;
import com.born2go.lazzybee.algorithms.CardSched;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.ContainerHolderSingleton;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.born2go.lazzybee.db.Card.QUEUE_NEW_CRAM0;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnStudyViewListener} interface
 * to handle interaction events.
 */
public class StudyView extends Fragment implements GetCardFormServerByQuestion.GetCardFormServerByQuestionResponse {

    private static final String TAG = "StudyView";
    private final CardSched cardSched = new CardSched();
    private final Context context;
    private OnStudyViewListener mListener;
    private Card card;
    private LearnApiImplements dataBaseHelper;

    TextToSpeech textToSpeech;
    LinearLayout container;

    MenuItem btnBackBeforeCard;
    MenuItem itemIgnore;
    MenuItem itemLearn;

    LinearLayout mLayoutButton;

    WebView mWebViewLeadDetails;

    TextView btnShowAnswer;
    TextView btnAgain0, btnHard1, btnGood2, btnEasy3;

    TextView lbCountNew;
    TextView lbCountAgain;
    TextView lbCountDue;

    CardView mCountStudy;

    CardView mCardViewHelpandAdMod;
    RelativeLayout mShowAnswer;

    List<Card> todayList = new ArrayList<Card>();
    List<Card> againList = new ArrayList<Card>();
    List<Card> dueList = new ArrayList<Card>();
    List<Card> cardListAddDueToDay = new ArrayList<Card>();
    //Current Card
    Card currentCard = new Card();
    //Define before card
    Card beforeCard;

    boolean answerDisplay = false;
    boolean learn_more;
    int completeStudy = 0;
    //TextView lbTagetActionStudy;


//    View view_study_button;
//    View view_study_count;


    private int currentPage = 0;
    private String detailViewTag;
    private Intent intent;
    StudyActivity.ScreenSlidePagerAdapter screenSlidePagerAdapter;

    DetailsView detailsView;
    CustomViewPager mViewPager;

    public void setBeforeCard(Card beforeCard) {
        this.beforeCard = beforeCard;
    }

    public StudyView(Context context, Intent intent, CustomViewPager mViewPager, StudyActivity.ScreenSlidePagerAdapter screenSlidePagerAdapter, Card card) {
        this.card = card;
        this.context = context;
        this.intent = intent;
        this.mViewPager = mViewPager;
        this.screenSlidePagerAdapter = screenSlidePagerAdapter;
        _initDatabase();
        _initTextToSpeech();
    }

    public static StudyView newInstance(Context context, Intent intent, CustomViewPager mViewPager, StudyActivity.ScreenSlidePagerAdapter screenSlidePagerAdapter, Card card) {
        Bundle args = new Bundle();
        StudyView fragment = new StudyView(context, intent, mViewPager, screenSlidePagerAdapter, card);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_study_main, container, false);
        _initView(view);

        _setUpStudy();
        _handlerButton();
        return view;
    }

    private void _handlerButton() {
        View.OnClickListener showAnswer = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setPagingEnabled(true);
                answerDisplay = true;
                _showAnswer();
                mListener.setCurrentCard(currentCard);

            }
        };
        mShowAnswer.setOnClickListener(showAnswer);
        btnShowAnswer.setOnClickListener(showAnswer);
        btnHard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setPagingEnabled(false);
                _showBtnAnswer();
                _answerCard(Card.EASE_HARD);
            }
        });
        btnEasy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setPagingEnabled(false);
                _showBtnAnswer();
                _answerCard(Card.EASE_EASY);
            }
        });
        btnAgain0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setPagingEnabled(false);
                _showBtnAnswer();
                _answerCard(Card.EASE_AGAIN);
            }
        });
        btnGood2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setPagingEnabled(false);
                _showBtnAnswer();
                _answerCard(Card.EASE_GOOD);

            }
        });
    }

    private void setDisplayCard(Card cardFromDB) {
        String detailsTag = ((StudyActivity) getActivity()).getDetailViewTag();
        Log.d(TAG, "Detais Tag:" + detailsTag);
        detailsView =
                (DetailsView) getActivity().getSupportFragmentManager().findFragmentByTag(detailsTag);
        detailsView.setCard(cardFromDB);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnStudyViewListener) activity;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
                    _loadWebView(LazzyBeeShare.getAnswerHTML(context, card), 10, 1);

                } else {
                    //Load question
                    _loadWebView(LazzyBeeShare._getQuestionDisplay(context, card), card.getQueue(), 0);
                }
                if (answerDisplay) {
                    mViewPager.setPagingEnabled(true);
                } else {
                    mViewPager.setPagingEnabled(false);
                }
                //Update Card form DB
                dataBaseHelper._updateCardFormServer(card);
                Toast.makeText(context, getString(R.string.message_update_card_successful), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, getString(R.string.message_update_card_fails), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    public interface OnStudyViewListener {
        void completeLearn(boolean complete);

        void _displayUserNote(Card card);

        void setCurrentCard(Card card);


    }

    private void _initDatabase() {
        dataBaseHelper = LazzyBeeSingleton.learnApiImplements;
    }

    private void _initView(View view) {
        container = (LinearLayout) view.findViewById(R.id.container);
        //init button
        mShowAnswer = (RelativeLayout) view.findViewById(R.id.mShowAnswer);

        btnShowAnswer = (TextView) view.findViewById(R.id.btnShowAnswer);
        mLayoutButton = (LinearLayout) view.findViewById(R.id.mLayoutButton);

        btnAgain0 = (TextView) view.findViewById(R.id.btnAgain0);
        btnHard1 = (TextView) view.findViewById(R.id.btnHard1);
        btnGood2 = (TextView) view.findViewById(R.id.btnGood2);
        btnEasy3 = (TextView) view.findViewById(R.id.btnEasy3);

        // init lbCount
        lbCountNew = (TextView) view.findViewById(R.id.lbCountTotalVocabulary);
        lbCountAgain = (TextView) view.findViewById(R.id.lbCountAgainInday);
        lbCountDue = (TextView) view.findViewById(R.id.lbAgainDue);

        mCountStudy = (CardView) view.findViewById(R.id.mCountStudy);

        mWebViewLeadDetails = (WebView) view.findViewById(R.id.mWebViewLeadDetaisl);

        mCardViewHelpandAdMod = (CardView) view.findViewById(R.id.mCardViewHelpandAdMod);

        FloatingActionButton mFloatActionButtonUserNote = (FloatingActionButton) view.findViewById(R.id.mFloatActionButtonUserNote);

        mFloatActionButtonUserNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener._displayUserNote(currentCard);
            }
        });
    }

    private void _initTextToSpeech() {
        String sp = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_SPEECH_RATE);
        float speech = 1.0f;

        if (sp != null)
            speech = Float.valueOf(sp);

        textToSpeech = LazzyBeeSingleton.textToSpeech;
        textToSpeech.setSpeechRate(speech);
    }

    private void _setUpStudy() {
        try {
            //get lean_more form intern
            learn_more = intent.getBooleanExtra(LazzyBeeShare.LEARN_MORE, false);

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
//                set again count
                _setCountAgain();
                //set new Count
                _setCountNew();
                //set Due Count
                _setCountDue();
            } else {
                Log.i(TAG, "_completeLean");
                _completeLean(false);
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    private void _completeLean(boolean b) {
        mListener.completeLearn(b);
    }

    private void _showFirstCard() {
        WebSettings ws = mWebViewLeadDetails.getSettings();
        ws.setJavaScriptEnabled(true);
        _addJavascriptInterface(mWebViewLeadDetails, currentCard);

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
    }

    private void _nextNewCard() {
        Log.i(TAG, "---------_nextNewCard--------");
        Log.d(TAG, "Curent new card:" + currentCard.toString());
        if (todayList.size() > 0) {
            //get next new card
            currentCard = todayList.get(0);
            //Display question
            _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard), QUEUE_NEW_CRAM0, 0);
        } else if (againList.size() > 0) {
            Log.i(TAG, "_nextNewCard:Next card is Again card");
            _nextAgainCard();
        } else if (dueList.size() > 0) {
            Log.i(TAG, "_nextNewCard:Next card is Due card");
            _nextDueCard();
        } else if (todayList.size() == 0) {
            Log.i(TAG, "_nextNewCard:_completeLean");
            _completeLean(true);
        }
        Log.i(TAG, "--------------END------------");
    }


    private void _nextDueCard() {
        Log.i(TAG, "---------_nextDueCard--------");
        if (dueList.size() > 0) {//Check dueList.size()>0

            currentCard = dueList.get(0);

            //Display next card
            _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard), Card.QUEUE_REV2, 0);

        } else if (todayList.size() > 0) {
            Log.i(TAG, "_nextDueCard:Next card is new card");
            _nextNewCard();
        } else if (againList.size() > 0) {//Check againList.size()>0
            Log.i(TAG, "_nextDueCard:Next card is again card");
            _nextAgainCard();
        } else if (todayList.size() == 0) {
            Log.i(TAG, "_nextDueCard:_completeLean");
            _completeLean(true);
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
                btnGood2.setEnabled(false);
                btnEasy3.setEnabled(false);


                Log.i(TAG, "_nextAgainCard:Next card is again card 2");

                //Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard), Card.QUEUE_LNR1, 0);
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

    private void _loadWebView(String questionDisplay, int queue, int type) {
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

//        show btnShowAnswer and hide btnAgain0
        mShowAnswer.setVisibility(View.VISIBLE);
        btnShowAnswer.setVisibility(View.VISIBLE);
        mLayoutButton.setVisibility(View.GONE);

        btnGood2.setEnabled(true);
        btnEasy3.setEnabled(true);
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
            setDisplayCard(cardFromDB);
            //Show answer question
            _loadWebView(LazzyBeeShare.getAnswerHTML(context, cardFromDB), card.getQueue(), 1);

//            get  next Ivl String List
            String[] ivlStrList = cardSched.nextIvlStrLst(cardFromDB, context);
            String text_btnAgain = LazzyBeeShare.getHTMLButtonAnswer(context, ivlStrList[Card.EASE_AGAIN], getString(R.string.EASE_AGAIN), R.color.color_level_btn_answer);
            String text_btnHard1 = LazzyBeeShare.getHTMLButtonAnswer(context, ivlStrList[Card.EASE_HARD], getString(R.string.EASE_HARD), R.color.color_level_btn_answer);

            String text_btnGood2 = LazzyBeeShare.getHTMLButtonAnswer(context, ivlStrList[Card.EASE_GOOD], getString(R.string.EASE_GOOD), (card.getQueue() == Card.QUEUE_LNR1) ? R.color.color_level_btn_answer_disable : R.color.color_level_btn_answer);
            String text_btnEasy3 = LazzyBeeShare.getHTMLButtonAnswer(context, ivlStrList[Card.EASE_EASY], getString(R.string.EASE_EASY), (card.getQueue() == Card.QUEUE_LNR1) ? R.color.color_level_btn_answer_disable : R.color.color_level_btn_answer);
            //set text btn
            btnAgain0.setText(Html.fromHtml(text_btnAgain));
            btnHard1.setText(Html.fromHtml(text_btnHard1));
            btnGood2.setText(Html.fromHtml(text_btnGood2));
            btnEasy3.setText(Html.fromHtml(text_btnEasy3));


            btnAgain0.setTag(ivlStrList[Card.EASE_AGAIN]);
            btnHard1.setTag(ivlStrList[Card.EASE_HARD]);
            btnGood2.setTag(ivlStrList[Card.EASE_GOOD]);
            btnEasy3.setTag(ivlStrList[Card.EASE_EASY]);

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


            if (currentQueue < QUEUE_NEW_CRAM0) {//Something's wrong???
                Log.i(TAG, "_answerCard:\tQueue<Card.QUEUE_NEW_CRAM0 currentQueue:" + currentQueue);
                return;
            }
            if (currentQueue == QUEUE_NEW_CRAM0) {
                if(todayList.contains(currentCard)){
                    //reset new card count
                    boolean remove = todayList.remove(currentCard);
                    if (!remove) {
                        //Remove index 0
                        todayList.remove(0);
                    }
                    //int countNew = todayList.size();
                    //lbCountNew.setText(getString(R.string.study_new) + ": " + String.valueOf(countNew));
                    _setCountNew();
                }
            } else if (currentQueue == Card.QUEUE_LNR1) {
                if (easy > Card.EASE_AGAIN) {
                    if(againList.contains(currentCard)){
                        //reset new card again
                        boolean remove = againList.remove(currentCard);
                        if (!remove) {
                            //Remove index 0
                            againList.remove(0);
                        }
                        //int countAgain = againList.size();
                        //lbCountAgain.setText(getString(R.string.study_again) + ": " + String.valueOf(countAgain));
                        _setCountAgain();
                    }
                }
            } else if (currentQueue == Card.QUEUE_REV2) {
                if (dueList.contains(currentCard)){
                    //reset new card due
                    boolean remove = dueList.remove(currentCard);
                    if (!remove) {
                        //Remove index 0
                        dueList.remove(0);

                    }
                    ///int countDue = dueList.size();
                    //lbCountDue.setText(getString(R.string.study_review) + ": " + String.valueOf(countDue));
                    _setCountDue();
                }

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
                //Check Contains and Remove
                if (againList.contains(currentCard)) {
                    Log.i(TAG, "Card Contains cardList");
                    //remove current Card
                    againList.remove(currentCard);
                }
                //set Due for again card = 600 second(10 minute)
                currentCard.setDue(curren_time + 600);

                //Add card to againList
                againList.add(currentCard);

                //reset count Againt
//                int countAgain = againList.size();
//                lbCountAgain.setText(getString(R.string.study_again) + ": " + String.valueOf(countAgain));
                _setCountAgain();
            } else if (easy > Card.EASE_AGAIN) {
                //Check Contains and Remove
                _checkContainsAndRemove(cardListAddDueToDay);
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
    }

    private void _checkContainsAndRemove(List<Card> cardLis) {
        if (cardLis.contains(currentCard)) {
            Log.i(TAG, "Card Contains cardList");
            //remove current Card
            cardLis.remove(currentCard);
        }
    }


    private void _addJavascriptInterface(WebView mDetailsWebViewLeadDetails, final Card card) {
        String sp = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_SPEECH_RATE);
        float speechRate = 1.0f;
        if (sp != null) {
            speechRate = Float.valueOf(sp);
        }
        //addJavascriptInterface play question
        final float finalSpeechRate = speechRate;
        mDetailsWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectQuestion() {
            @JavascriptInterface
            public void playQuestion() {
                String toSpeak = currentCard.getQuestion();

                //Speak text
                LazzyBeeShare._speakText(toSpeak, finalSpeechRate);
            }
        }, "question");
        mDetailsWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectExplain() {
            @JavascriptInterface
            public void speechExplain() {
                //get answer json
                String answer = currentCard.getAnswers();
                String toSpeech = LazzyBeeShare._getValueFromKey(answer, "explain");

                //Speak text
                LazzyBeeShare._speakText(toSpeech, finalSpeechRate);
            }
        }, "explain");
        mDetailsWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectExample() {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        _initMenuItem(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
            case R.id.action_goto_dictionary:
                _gotoDictionnary();
                return true;
            case R.id.action_report:
                _reportCard();
                return true;
        }

        return false;
    }

    private void _initMenuItem(Menu menu) {
        btnBackBeforeCard = menu.findItem(R.id.action_back_before_card);
        btnBackBeforeCard.setVisible(false);
        itemIgnore = menu.findItem(R.id.action_ignore);
        itemLearn = menu.findItem(R.id.action_learnt);
        MenuItem itemDictionary = menu.findItem(R.id.action_goto_dictionary);
        itemDictionary.setVisible(false);
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
                    // int countNew = todayList.size();
                    //lbCountNew.setText(getString(R.string.study_new) + ": " + String.valueOf(countNew));
                    _setCountNew();
                    break;
                case Card.QUEUE_LNR1:
                    Log.i(TAG, "_learntorIgnoreCardbyQueue QUEUE_LNR1");
                    //reset new card again
                    boolean removeAgain = againList.remove(currentCard);
                    if (!removeAgain) {
                        //Remove index 0
                        todayList.remove(0);
                    }
                    // int countAgain = againList.size();
                    //lbCountAgain.setText(getString(R.string.study_again) + ": " + String.valueOf(countAgain));
                    _setCountAgain();
                    break;
                case Card.QUEUE_REV2:
                    Log.i(TAG, "_learntorIgnoreCardbyQueue QUEUE_REV2");
                    //reset new card due
                    boolean removeDue = dueList.remove(currentCard);
                    if (!removeDue) {
                        //Remove index 0
                        todayList.remove(0);
                    }
                    //int countDue = dueList.size();
                    //lbCountDue.setText(getString(R.string.study_review) + ": " + String.valueOf(countDue));
                    _setCountDue();
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
            mViewPager.setPagingEnabled(false);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
        Log.i(TAG, "-----------------------END----------------------");
    }

    private void _updateCardFormServer() {
        //Call Api Update Card
        GetCardFormServerByQuestion getCardFormServerByQuestion = new GetCardFormServerByQuestion(context);
        getCardFormServerByQuestion.execute(currentCard);
        getCardFormServerByQuestion.delegate = this;
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

                        _setCountAgain();

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
                        _setCountNew();

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
                        //Set again count
                        _setCountAgain();

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

                        //Set again count
                        _setCountAgain();

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
                        _setCountDue();


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
                    _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard), currentCard.getQueue(), 0);
                }
                Log.i(TAG, "_backToBeforeCard()\t" + getString(R.string.number_row_updated, results_num));
                // Toast.makeText(context, getString(R.string.number_row_updated, results_num), Toast.LENGTH_SHORT).show();

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

    private void _setCountNew() {
        int countNew = todayList.size();
        lbCountNew.setText(getString(R.string.study_new) + ": " + String.valueOf(countNew));
    }

    private void _setCountAgain() {
        int countAgain = againList.size();
        lbCountAgain.setText(getString(R.string.study_again) + ": " + String.valueOf(countAgain));
    }

    private void _setCountDue() {
        int countDue = dueList.size();
        lbCountDue.setText(getString(R.string.study_review) + ": " + String.valueOf(countDue));
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

    private void _gotoDictionnary() {

        Intent intent = new Intent(context, CardDetailsActivity.class);
        intent.putExtra(LazzyBeeShare.CARDID, String.valueOf(currentCard.getId()));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void _reportCard() {
        try {
            startActivity(LazzyBeeShare.getOpenFacebookIntent(context));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

}
