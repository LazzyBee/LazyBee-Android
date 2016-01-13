package com.born2go.lazzybee.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
    FloatingActionButton mFloatActionButtonUserNote;


    private Intent intent;
    StudyActivity.ScreenSlidePagerAdapter screenSlidePagerAdapter;

    DetailsView detailsView;
    CustomViewPager mViewPager;

    int widthStudyDisplay = -1, heightStudyDisplay = -1;
    String mySubject = "common";
    boolean sDEBUG = false;
    boolean sPOSITION_MEANING = false;
    int sTimeShowAnswer;


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

    View.OnClickListener showAnswer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mViewPager.setPagingEnabled(true);
            answerDisplay = true;
            _showAnswer();
            mListener.setCurrentCard(currentCard);
            mFloatActionButtonUserNote.setVisibility(View.VISIBLE);


        }
    };

    private void _handlerButton() {
        btnHard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _processingAnswerCard(Card.EASE_HARD);
            }
        });
        btnEasy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _processingAnswerCard(Card.EASE_EASY);
            }
        });
        btnAgain0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _processingAnswerCard(Card.EASE_AGAIN);
            }
        });
        btnGood2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _processingAnswerCard(Card.EASE_GOOD);
            }
        });
    }

    private void _processingAnswerCard(final int ea) {
        _showBtnAnswer();
        mViewPager.setPagingEnabled(false);
        _answerCard(ea);
        _handlerTimeShowAswerButton();

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
                    _loadWebView(LazzyBeeShare.getAnswerHTML(context, card, mySubject, sDEBUG, sPOSITION_MEANING), 10, 1);

                } else {
                    //Load question
                    _loadWebView(LazzyBeeShare._getQuestionDisplay(context, card, mySubject), card.getQueue(), 0);
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

    private void _initView(final View view) {
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

        final CardView mDisplay = (CardView) view.findViewById(R.id.mDisplay);


        mWebViewLeadDetails = (WebView) view.findViewById(R.id.mWebViewLeadDetaisl);

        //get widthStudyDisplay heightStudyDisplay display
        ViewTreeObserver observer = mDisplay.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16)
                    mDisplay.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    mDisplay.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = mDisplay.getMeasuredWidth();
                int height = mDisplay.getMeasuredHeight();

                Log.d(TAG, "Display:" + width + "\t:\t" + height);
                setDisplaySize(width, height);
            }
        });

        mCardViewHelpandAdMod = (CardView) view.findViewById(R.id.mCardViewHelpandAdMod);

        mFloatActionButtonUserNote = (FloatingActionButton) view.findViewById(R.id.mFloatActionButtonUserNote);

        mFloatActionButtonUserNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener._displayUserNote(currentCard);
                Log.d(TAG, "Current Position:" + v.getX() + "\t:\t" + v.getY());
            }
        });
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int densityDpi = (int) (metrics.density * 160f);
        Log.d(TAG, "DPI:" + densityDpi);
        mFloatActionButtonUserNote.setOnTouchListener(new View.OnTouchListener() {
            public boolean shouldClick;
            float dX, dY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN:
                        shouldClick = true;
                        dX = (mFloatActionButtonUserNote.getX() - event.getRawX());
                        dY = (mFloatActionButtonUserNote.getY() - event.getRawY());
                        break;
                    case MotionEvent.ACTION_UP:
                        if (shouldClick)
                            mFloatActionButtonUserNote.performClick();//call click
                        break;

                    case MotionEvent.ACTION_MOVE:
                        shouldClick = false;
                        float eX = (event.getRawX() + dX);//define position X
                        float eY = (event.getRawY() + dY);//define position Y

                        if (widthStudyDisplay > -1 && heightStudyDisplay > -1) {
                            if (eX < 0) {
                                eX = 0f;
                            } else if (eX >= widthStudyDisplay - (widthStudyDisplay * 0.15)) {
                                eX = (float) (widthStudyDisplay - (widthStudyDisplay * 0.2));
                            }
                            if (eY < 0) {
                                eY = 0f;
                            } else if (eY >= heightStudyDisplay - (heightStudyDisplay * 0.1)) {
                                eY = (float) (heightStudyDisplay - (heightStudyDisplay * 0.15));
                            }
                        } else {
                            Log.d(TAG, "WidthHeight =-1");
                        }
                        mFloatActionButtonUserNote.animate()//move button
                                .x(eX)
                                .y(eY)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }

        });

    }

    private void setDisplaySize(int width, int height) {
        this.widthStudyDisplay = width;
        this.heightStudyDisplay = height;

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
            dueList = dataBaseHelper._getListCardByQueue(Card.QUEUE_REV2, total_learn_per_day);


            int dueCount = dueList.size(); //Define Count due
            int numberAgainCard = total_learn_per_day - dueCount;
            Log.d(TAG, "numberAgainCard:" + numberAgainCard);
            todayList = new ArrayList<Card>();
            int againCount = 0;//Define count again
            if (numberAgainCard > 0) {
                againList = dataBaseHelper._getListCardByQueue(Card.QUEUE_LNR1, numberAgainCard);
                againCount = againList.size();
                int numberNewCard = total_learn_per_day - (dueCount + againCount);
                if (numberNewCard > 0) {
                    if (numberNewCard > limit_today)
                        numberNewCard = limit_today;
                    Log.d(TAG, "numberNewCard:" + numberNewCard);
                    //Define todayList
                    todayList = dataBaseHelper._getRandomCard(numberNewCard, learn_more);
                }
            }


//            if (dueCount > 0 && dueCount < total_learn_per_day) {
//
//            }else if (dueCount > 0){
//                limit_today = total_learn_per_day;
//            }
            //get new random card list to day
            //int newCount =
            //if (newCount > 0)
            //  todayList = dataBaseHelper._getRandomCard(newCount);
//            if (dueCount == 0) {
//                Log.i(TAG, "_setUpStudy()  dueCount == 0");
//            } else {
//
//                Log.i(TAG, "_setUpStudy()  dueCount != 0");
//
//                if (dueCount < total_learn_per_day) {
//
//                    Log.i(TAG, "_setUpStudy()  dueCount < total_learn_per_day");
//
//                    if (total_learn_per_day - dueCount < limit_today) {
//
//                        Log.i(TAG, "_setUpStudy()  total_learn_per_day - dueCount < limit_today");
//                        limit_today = total_learn_per_day - dueCount;
//
//                    } else if (total_learn_per_day - dueCount > limit_today) {
//
//                        Log.i(TAG, "_setUpStudy()  total_learn_per_day - dueCount > limit_today");
//                    }
//                } else if (dueCount >= total_learn_per_day) {
//
//                    Log.i(TAG, "_setUpStudy()  dueCount >= total_learn_per_day");
//                    limit_today = 0;
//                }
//                learn_more = false;
//            }


            int todayCount = todayList.size();
            Log.d(TAG, "dueCount:" + dueCount + ",againCount:" + againCount + ",today:" + todayCount);

            //Define check_learn
            //check_learn==true Study
            //check_learn==false Complete Study
            boolean check_learn = (againCount + dueCount + todayCount) > 0;

            Log.d(TAG, "check_learn:" + (check_learn));

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
        boolean show = false;
        //Load first card
        if (dueList.size() > 0) {
            //Load first card is Due card
            _nextDueCard();
            show = true;
        } else if (againList.size() > 0) {
            //Load first card is Again card
            _nextAgainCard();
            show = true;
        } else if (todayList.size() > 0) {
            //Load first card is new card
            _nextNewCard();
            show = true;
        } else {
            _completeLean(false);
        }
        if (show) {
            _handlerTimeShowAswerButton();
        }
    }

    private void _nextNewCard() {
        try {
            Log.i(TAG, "---------_nextNewCard--------");
            if (todayList.size() > 0) {
                currentCard = todayList.get(0);//get next new card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard, mySubject), QUEUE_NEW_CRAM0, 0);//Display question
            } else if (againList.size() > 0) {
                Log.i(TAG, "_nextNewCard:Next card is Again card");
                _nextAgainCard();
            } else if (dueList.size() > 0) {
                Log.i(TAG, "_nextNewCard:Next card is Due card");
                _nextDueCard();
            } else {
                Log.i(TAG, "_nextNewCard:_completeLean");
                _completeLean(true);
            }
            Log.i(TAG, "--------------END------------");
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }


    private void _nextDueCard() {
        try {
            Log.i(TAG, "---------_nextDueCard--------");
            if (dueList.size() > 0) {//Check dueList.size()>0
                currentCard = dueList.get(0);//get current card in DueList
                //Display next card
                _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard, mySubject), Card.QUEUE_REV2, 0);
            } else if (againList.size() > 0) {//Check againList.size()>0
                Log.i(TAG, "_nextDueCard:Next card is again card");
                _nextAgainCard();
            } else if (todayList.size() > 0) {
                Log.i(TAG, "_nextDueCard:Next card is new card");
                _nextNewCard();
            } else {
                Log.i(TAG, "_nextDueCard:_completeLean");
                _completeLean(true);
            }
            Log.i(TAG, "--------------END------------");
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }

    }


    private void _nextAgainCard() {
        try {
            Log.i(TAG, "---------_nextAgainCard--------");
            if (againList.size() > 0) {//Check againList.size()>0

                currentCard = againList.get(0);//get currentCard in AgainList
                int current_time = (int) (new Date().getTime() / 1000);//Define current time and due card by second
                int due = (int) currentCard.getDue();

                int time = (current_time - due);
                Log.d(TAG, "_nextAgainCard: \t" + current_time + "-" + due + "=" + time);
                if ((time >= 600) || todayList.size() == 0) {//Check due<current_time
                    btnGood2.setEnabled(false);
                    btnEasy3.setEnabled(false);

                    Log.d(TAG, "_nextAgainCard:Next card is again card 2");

                    _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard, mySubject), Card.QUEUE_LNR1, 0);//Display next card
                } else if (todayList.size() > 0) {
                    Log.i(TAG, "_nextAgainCard:Next card is new card 1");
                    _nextNewCard();
                } else {
                    Log.i(TAG, "_nextAgainCard:Next card 3");
                }

            } else if (dueList.size() > 0) {//Check dueList.size()>0
                Log.i(TAG, "_nextAgainCard:Next card is due card");
                _nextDueCard();
            } else if (todayList.size() > 0) {
                Log.i(TAG, "_nextAgainCard:Next card is new card");
                _nextNewCard();
            } else {
                _completeLean(true);
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
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
//            //Define get card
            Card card = currentCard;

            Card cardFromDB = dataBaseHelper._getCardByID(String.valueOf(card.getId()));

            //currentCard = cardFromDB;//set current card

            Log.i(TAG, "btnShowAnswer question=" + card.getQuestion() + ",queue=" + card.getQueue() + ",queue db:" + cardFromDB.getQueue());
            setDisplayCard(cardFromDB);
            //Show answer question
            _loadWebView(LazzyBeeShare.getAnswerHTML(context, cardFromDB, mySubject, sDEBUG, sPOSITION_MEANING), card.getQueue(), 1);

//            get  next Ivl String List
            String[] ivlStrList = cardSched.nextIvlStrLst(cardFromDB, context);
            String text_btnAgain = LazzyBeeShare.getHTMLButtonAnswer(context, ivlStrList[Card.EASE_AGAIN],
                    getString(R.string.EASE_AGAIN), R.color.color_level_btn_answer);
            String text_btnHard1 = LazzyBeeShare.getHTMLButtonAnswer(context, ivlStrList[Card.EASE_HARD],
                    getString(R.string.EASE_HARD), R.color.color_level_btn_answer);

            String text_btnGood2 = LazzyBeeShare.getHTMLButtonAnswer(context, ivlStrList[Card.EASE_GOOD],
                    getString(R.string.EASE_GOOD), (cardFromDB.getQueue() == Card.QUEUE_LNR1) ?
                            R.color.color_level_btn_answer_disable : R.color.color_level_btn_answer);

            String text_btnEasy3 = LazzyBeeShare.getHTMLButtonAnswer(context, ivlStrList[Card.EASE_EASY],
                    getString(R.string.EASE_EASY), (cardFromDB.getQueue() == Card.QUEUE_LNR1) ?
                            R.color.color_level_btn_answer_disable : R.color.color_level_btn_answer);
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
            final int curren_time = (int) (new Date().getTime() / 1000);//define current time by second

            int currentQueue = currentCard.getQueue();//Get current Queue
            Log.i(TAG, "_answerCard:Currrent Card Queue:" + currentQueue);

            Card card = dataBaseHelper._getCardByID(String.valueOf(currentCard.getId())); //Define card form db

            setBeforeCard(card);//setBeforeCard=current card;

            btnBackBeforeCard.setVisible(true);//Show item BackBeroreCard when answer

            if (currentQueue < QUEUE_NEW_CRAM0) {//Something's wrong???
                Log.i(TAG, "_answerCard:\tQueue<Card.QUEUE_NEW_CRAM0 currentQueue:" + currentQueue);
                return;
            }
            if (currentQueue == QUEUE_NEW_CRAM0) {//queue=New
                boolean containNew = false;
                for (Card newCard : todayList) {
                    if (newCard.getQuestion().equals(currentCard.getQuestion())) {//check currrentCard contain
                        todayList.remove(newCard);
                        containNew = true;
                        break;
                    }
                }
                if (containNew) {
                    _setCountNew();
                } else {
                    Log.i(TAG, "No contain in todayList");
                }
            } else if (currentQueue == Card.QUEUE_LNR1) {//queue=Again
                boolean containAgain = false;
                for (Card againCard : againList) {
                    if (againCard.getQuestion().equals(currentCard.getQuestion())) {//check currrentCard contain
                        againList.remove(againCard);
                        containAgain = true;
                        break;
                    }
                }
                if (containAgain) {
                    _setCountAgain();
                } else {
                    Log.i(TAG, "No contain in againList");
                }
            } else if (currentQueue == Card.QUEUE_REV2) {//queue=Reiview
                boolean containDue = false;
                for (Card dueCard : dueList) {
                    if (dueCard.getQuestion().equals(currentCard.getQuestion())) {//check currrentCard contain
                        dueList.remove(dueCard);
                        containDue = true;
                        break;
                    }
                }
                if (containDue) {
                    _setCountDue();
                } else {
                    Log.i(TAG, "No contain in dueList");
                }
            }

            Log.i(TAG, "_answerCard Before Update Card " + currentCard.getQuestion() +
                    " to queue " + currentCard.getQueue() + " currentQueue:" + currentQueue);
            Log.i(TAG, "_answerCard Berore answer beforeCard " + beforeCard.getQuestion() +
                    " to queue " + beforeCard.getQueue() + " currentQueue:" + currentQueue);


            cardSched.answerCard(currentCard, easy);//Set queue,due using cardShed

            // beforeCard.setQueue(currentQueue);
            Log.i(TAG, "_answerCard answer beforeCard " + beforeCard.getQuestion() +
                    " to queue " + beforeCard.getQueue() + " currentQueue:" + currentQueue);

            if (easy == Card.EASE_AGAIN) {
                _checkContainsAndRemove(againList);//Check Contains and Remove

                currentCard.setDue(curren_time + 600); //set Due for again card = 600 second(10 minute)

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


            int update = dataBaseHelper._updateCard(currentCard);//update card form DB
            if (update >= 1) {
                Log.i(TAG, "_answerCard Update Card " + currentCard.getQuestion() +
                        " to queue " + currentCard.getQueue() + " OK");
                Log.i(TAG, "_answerCard Update beforeCard " + beforeCard.getQuestion() +
                        " to queue " + beforeCard.getQueue());

                _nextCard(currentQueue);//next Card by Queue
                mFloatActionButtonUserNote.setVisibility(View.GONE);

            } else {
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
                _nextNewCard();
                break;
            case Card.QUEUE_LNR1:
                Log.i(TAG, "_nextCard:\t Queue=Card.QUEUE_LNR1");
                _nextAgainCard();
                break;
            case Card.QUEUE_REV2:
                Log.i(TAG, "_nextCard:\t Queue=Card.QUEUE_REV2");
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
        _initSettingUser();
    }

    private void _initSettingUser() {
        mySubject = LazzyBeeShare.getSubjectSetting();
        sDEBUG = LazzyBeeShare.getDebugSetting();
        sPOSITION_MEANING = LazzyBeeShare.getPositionMeaning();
        sTimeShowAnswer = dataBaseHelper.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_TIME_SHOW_ANSWER);


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
                    boolean containNew = false;
                    for (Card newCard : todayList) {
                        if (newCard.getQuestion().equals(currentCard.getQuestion())) {//check currrentCard contain
                            todayList.remove(newCard);
                            containNew = true;
                            break;
                        }
                    }
                    if (containNew) {
                        _setCountNew();
                    } else {
                        Log.d(TAG, "No contain in todayList");
                    }
                    break;
                case Card.QUEUE_LNR1:
                    boolean containAgain = false;
                    for (Card againCard : againList) {
                        if (againCard.getQuestion().equals(currentCard.getQuestion())) {//check currrentCard contain
                            againList.remove(againCard);
                            containAgain = true;
                            break;
                        }
                    }
                    if (containAgain) {
                        _setCountAgain();
                    } else {
                        Log.d(TAG, "No contain in againList");
                    }
                    break;
                case Card.QUEUE_REV2:
                    boolean containDue = false;
                    for (Card dueCard : dueList) {
                        if (dueCard.getQuestion().equals(currentCard.getQuestion())) {//check currrentCard contain
                            dueList.remove(dueCard);
                            containDue = true;
                            break;
                        }
                    }
                    if (containDue) {
                        _setCountDue();
                    } else {
                        Log.d(TAG, "No contain in dueList");
                    }
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
            _handlerTimeShowAswerButton();

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
        Log.i(TAG, "-----------------------END----------------------");
    }

    private CountDownTimer countDownTimer;

    private void _handlerTimeShowAswerButton() {
        if (sTimeShowAnswer > -1) {
            mShowAnswer.setOnClickListener(null);
            btnShowAnswer.setOnClickListener(null);
            mShowAnswer.setBackgroundColor(context.getResources().getColor(R.color.grey_600));
            btnShowAnswer.setBackgroundColor(context.getResources().getColor(R.color.grey_600));
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            countDownTimer = new CountDownTimer(((sTimeShowAnswer) * 1000), 100) {
                public void onTick(long millisUntilFinished) {
                    int second = Math.round((millisUntilFinished / 1000));
                    Log.d(TAG, "second:" + second);
                    btnShowAnswer.setText(context.getString(R.string.show_answer) + String.valueOf(" (" + (second + 1) + "s)"));
                }

                public void onFinish() {
                    btnShowAnswer.setText(R.string.show_answer);
                    mShowAnswer.setOnClickListener(showAnswer);
                    btnShowAnswer.setOnClickListener(showAnswer);
                    mShowAnswer.setBackgroundColor(context.getResources().getColor(R.color.button_green_color));
                    btnShowAnswer.setBackgroundColor(context.getResources().getColor(R.color.button_green_color));
                }
            }.start();
        } else {
            mShowAnswer.setOnClickListener(showAnswer);
            btnShowAnswer.setOnClickListener(showAnswer);
        }
    }

    private void _updateCardFormServer() {
        if (LazzyBeeShare.checkConn(context)) {
            //Call Api Update Card
            GetCardFormServerByQuestion getCardFormServerByQuestion = new GetCardFormServerByQuestion(context);
            getCardFormServerByQuestion.execute(currentCard);
            getCardFormServerByQuestion.delegate = this;
        } else {
            Toast.makeText(context, R.string.failed_to_connect_to_server, Toast.LENGTH_SHORT).show();
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

                        List<Card> cloneTodayList = new ArrayList<Card>(todayList); //Define clone todayList
                        int clonetodayCount = cloneTodayList.size();

                        todayList.clear(); //Clear Data

                        //Readd card to new card
                        if (clonetodayCount == 0) {
                            todayList.add(beforeCard);
                        } else {
                            todayList.add(0, beforeCard);
                            for (int i = 0; i < clonetodayCount; i++) {
                                todayList.add(i + 1, cloneTodayList.get(i));
                            }
                        }

                        _setCountNew(); //Set new count

                        break;
                    case Card.QUEUE_LNR1:
                        Log.i(TAG, "_backToBeforeCard\t Queue=Card.QUEUE_LNR1");

                        List<Card> cloneAgainList = new ArrayList<Card>(againList);//Define clone againList
                        int agianCount = cloneAgainList.size();

                        againList.clear();//Clear Data

                        //Readd card to again card
                        if (agianCount == 0) {
                            againList.add(beforeCard);
                        } else {
                            againList.add(0, beforeCard);
                            for (int i = 0; i < agianCount; i++) {
                                againList.add(i + 1, cloneAgainList.get(i));
                            }
                        }

                        _setCountAgain(); //Set again count

                        break;
                    case Card.QUEUE_REV2:
                        Log.i(TAG, "_backToBeforeCard\t Queue=Card.QUEUE_REV2");

                        List<Card> cloneDuelist = new ArrayList<Card>(dueList);//Define clone duelist
                        int dueCount = cloneDuelist.size();

                        dueList.clear();//Clear Data

                        //Readd card to again card
                        if (cloneDuelist.size() == 0) {
                            dueList.add(beforeCard);
                        } else {
                            dueList.add(0, beforeCard);
                            for (int i = 0; i < dueCount; i++) {
                                dueList.add(i + 1, cloneDuelist.get(i));
                            }
                        }

                        _setCountDue();//Set new count

                        break;
                }

                int results_num = dataBaseHelper._updateCard(beforeCard);//update card before card

                if (results_num >= 1) {
                    currentCard = dataBaseHelper._getCardByID(String.valueOf(beforeCard.getId())); //Get card form DB by id before card

                    if (beforeQueue == Card.QUEUE_NEW_CRAM0)
                        dataBaseHelper._addCardIdToQueueList(beforeCard);//Update form queueList In DB

                    Log.i(TAG, "_backToBeforeCard()\t currentCardquestion:" + currentCard.getQuestion() +
                            "\t queue:" + currentCard.getQueue() + " due:" + currentCard.getDue());

                    _showBtnAnswer();
                    _loadWebView(LazzyBeeShare._getQuestionDisplay(context, currentCard, mySubject), currentCard.getQueue(), 0);
                }
                Log.i(TAG, "_backToBeforeCard()\t" + getString(R.string.number_row_updated, results_num));
            } else {
                Toast.makeText(context, R.string.message_error_back_before_card, Toast.LENGTH_SHORT).show();
            }

            btnBackBeforeCard.setVisible(false); //Hide btnBackBeforeCard
            _handlerTimeShowAswerButton();
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
