package com.born2go.lazzybee.fragment;


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentStudy extends Fragment {


    public static final String COURSE_ID = "lean_id";
    public static final String TAG = "FragmentStudy";
    String mime = "text/html";
    String encoding = "utf-8";
    String ASSETS = "file:///android_asset/";

    int index = 0;

    public FragmentStudy() {
        // Required empty public constructor
    }

    public interface FragmentStudyListener {
        /**
         * Hoan thanh khoa hoc rui quay tro lai DetailCourse
         */
        void completeCourse();
    }

    FragmentStudyListener fragmentStudyListener;
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
    List<Card> cardListAgainToday = new ArrayList<Card>();
    List<Card> cardListAddDueToDay = new ArrayList<Card>();

    CardSched cardSched;

    //Current Card
    Card currentCard = new Card();
    //init position
    int position = 0;
    int position_again = 0;
    int position_due = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study, container, false);

        //init Webview
        mWebViewLeadDetails = (WebView) view.findViewById(R.id.mWebViewLeadDetaisl);

        //init button

        btnShowAnswer = (Button) view.findViewById(R.id.btnShowAnswer);
        mLayoutButton = (LinearLayout) view.findViewById(R.id.mLayoutButton);

        btnAgain0 = (Button) view.findViewById(R.id.btnAgain0);
        btnHard1 = (Button) view.findViewById(R.id.btnHard1);
        btnGood2 = (Button) view.findViewById(R.id.btnGood2);
        btnEasy3 = (Button) view.findViewById(R.id.btnEasy3);

        //init lbCount
        lbCountNew = (TextView) view.findViewById(R.id.lbCountTotalVocabulary);
        lbCountAgain = (TextView) view.findViewById(R.id.lbCountAgainInday);
        lbCountDue = (TextView) view.findViewById(R.id.lbAgainDue);

        //db
        _initDatabase();

        //init cardSched
        cardSched = new CardSched();

        _initTextToSpeech();

        //get card due today & agin
        againList = dataBaseHelper._getListCardByQueue(Card.QUEUE_LNR1);
        dueList = dataBaseHelper._getListCardByQueue(Card.QUEUE_REV2);

        //get new random card list to day
        //TODO: only take new cards if total learn today not exceed MAX_LEARN_PER_DAY
        //int newCount = 10 - (againList.size() + dueList.size);
        //if (newCount > 0)
        //  todayList = dataBaseHelper._getRandomCard(newCount);
        todayList = dataBaseHelper._getRandomCard(10);

        int againCount = againList.size();//get card List Again size

        //Todo:
        if (againCount > 0) {
            //todo:Check todayList contains cardAgain
            /*Remove card*/
            List<Card> listcardRemove = new ArrayList<Card>();
            //Loop againList
            for (Card card : todayList) {
                for (Card cardAgain : againList) {
                    if (card.getId() == cardAgain.getId()) {
                        Log.i(TAG, "-card:" + card.toString());
                        try {
                            listcardRemove.add(card);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    } else {
                        Log.i(TAG, "-card not compare");
                    }

                }
            }
            for (Card card : listcardRemove) {
                todayList.remove(card);
            }
        }

        //set data
        if (todayList.size() > 0) {
            _setDataforWebView();

            final int list_card_again_in_today_size = againList.size();
            //set total vocabilary
            lbCountAgain.setText("" + list_card_again_in_today_size);
            lbCountAgain.setTag(list_card_again_in_today_size);

            int list_card_new_size = todayList.size();
            lbCountNew.setText("" + list_card_new_size);
            lbCountNew.setTag(list_card_new_size);


        } else
            Log.i(TAG, "List Card Empty");

        //

//        _setCountCardList();
//        _setCountDueCardList();

        //@JavascriptInterface

        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return view;
    }

    private void _initTextToSpeech() {
        //Todo:init TextToSpeech
        textToSpeech = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
    }

    private void _setCountCardList() {
        //list_card_new_size vocabulary list
        int list_card_new_size = todayList.size();

        //todo: set count list card new
//        if (list_card_again_in_today_size > 0) {
//            list_card_new_size = list_card_new_size - list_card_again_in_today_size;
//        }
        //set total vocabilary
        lbCountNew.setText("" + list_card_new_size);
        lbCountNew.setTag(list_card_new_size);


//        final int list_card_due_day_size = dueList.size();
//        //set total vocabilary
//
//        lbCountDue.setText("" + list_card_due_day_size);
//        lbCountDue.setTag(list_card_due_day_size);


    }

    private void _setCountDueCardList() {
        final int list_card_due_day_size = dueList.size();
        //set total vocabilary

        lbCountDue.setText("" + list_card_due_day_size);
        lbCountDue.setTag(list_card_due_day_size);
    }

    /**
     * get List Word queue in today
     *
     * @return listvocabulary
     */
    private List<Card> _getListVocabularyQueue(List<Card> cardListToDay) {
        return null;
    }

    /**
     * if
     * have word queue in today
     * <p>return true</p>
     * <p>not have</p>
     * <p>return false</p>
     */
    private boolean _CheckQueueWord() {
        return false;
    }


    /**
     * Set data for webview.
     * <p/>
     * <p>Difine onclick btnAnswer and btnAgain0.</p>
     * <p/>
     * Define JavaScrip to Speek Text.
     */
    private void _setDataforWebView() {

        //final List<Card> todayList = this.todayList;


        //Todo: Set  JavaScripEnabled for webview
        WebSettings ws = mWebViewLeadDetails.getSettings();
        ws.setJavaScriptEnabled(true);


        //list_card_new_size vocabulary list
        final int list_card_new_size = todayList.size();


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

        mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, _getQuestionDisplay(currentCard.getQuestion()), mime, encoding, null);

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
                    //Show answer question
                    //mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, getAnswerHTML(card), mime, encoding, null);
                    // Log.i(TAG, "HTML init:" + getAnswerHTML(card));

                    //Load Answer
                    _loadWebView(LazzyBeeShare.getAnswerHTML(card));

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

        final long curren_time = new Date().getTime() / 1000;/*curent time*/

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
                btnShowAnswer.setVisibility(View.VISIBLE);
                mLayoutButton.setVisibility(View.GONE);
                int flag_queue = 1;
                int due_time = (int) (curren_time + 600);
                try {
                    //getCurrent Card Queue
                    int currentCardQueue = currentCard.getQueue();

                    //Check Contains
                    if (againList.contains(currentCard)) {
                        Log.i(TAG, "Card Contains againList");
                        //remove current Card
                        againList.remove(currentCard);
                    }
                    if (cardListAgainToday.contains(currentCard)) {
                        Log.i(TAG, "Card Contains in cardListAgainToday");
                        //remove current Card
                        cardListAgainToday.remove(currentCard);
                    }


                    currentCard.setQueue(Card.QUEUE_LNR1);
                    currentCard.setDue(due_time);


                    againList.add(currentCard);
                    cardListAgainToday.add(currentCard);

                    int _count = todayList.size();
                    //reset total vocabilary
                    int current_count = _count - cardListAgainToday.size();
                    lbCountNew.setText("" + current_count);

                    //Update Count Again
                    int count_card_list_again = againList.size();
                    //reset total vocabilary
                    lbCountAgain.setText("" + count_card_list_again);

                    if (position < todayList.size()) {
                        if (currentCardQueue == Card.QUEUE_NEW_CRAM0) {
                            //if currentCardQueue == Card.QUEUE_NEW_CRAM0 next card again if againList.size>0
                            Log.i(TAG, "Card.QUEUE_NEW_CRAM0");
                            _nextAgainCard();


                        } else if (currentCardQueue == Card.QUEUE_LNR1) {
                            Log.i(TAG, "Card.QUEUE_LNR1");
                            _nextDueCard();


                        } else if (currentCardQueue == Card.QUEUE_REV2) {
                            Log.i(TAG, "Card.QUEUE_REV2");
                            _nextNewCard();

                        }
                    } else {
                        _completeLean();


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


//                try {
//                    if (currentCard[0].getQueue() == Card.QUEUE_LNR1) {
//                        Log.e(TAG, "Card.QUEUE_LNR1");
//                        currentCard[0].setQueue(Card.QUEUE_LNR1);
//                        currentCard[0].setDue(due_time);
//                        againList.add(currentCard[0]);
//                        cardListAgainToday.add(currentCard[0]);
//
//                        //Card again next card is due
//                        if (dueList.size() > 0) {
//                            //Size>0 next card is card due
//                            Log.e(TAG, "Card.QUEUE_LNR1:DUE LIST");
//                            currentCard[0] = dueList.get(position_again[0]);
//
//                            lbCountDue.setBackgroundResource(R.color.teal_200);
//                            lbCountAgain.setBackgroundResource(R.color.white);
//                            lbCountNew.setBackgroundResource(R.color.white);
//
//                            //TODO:Display next card
//                            _loadWebView(_getQuestionDisplay(currentCard[0].getQuestion()));
//                        } else {
//                            //Size<0 next card new card
//                            Log.e(TAG, "Card.QUEUE_LNR1:NEW LIST");
//                            if (todayList.size() > 0) {
//
//                                position[0]++;
//
//                                if (position[0] <= list_card_new_size - 1) {
//                                    //get next card again
//                                    currentCard[0] = todayList.get(position[0]);
//
//                                    lbCountDue.setBackgroundResource(R.color.white);
//                                    lbCountAgain.setBackgroundResource(R.color.white);
//                                    lbCountNew.setBackgroundResource(R.color.teal_200);
//
//
//                                    //TODO:Display next card
//                                    _loadWebView(_getQuestionDisplay(currentCard[0].getQuestion()));
//
//                                } else {
//                                    _completeLean();
//                                }
//                            } else {
//                                //
//                                _completeLean();
//                            }
//                        }
//                    } else if (currentCard[0].getQueue() == Card.QUEUE_REV2) {
//                        Log.e(TAG, "Card.QUEUE_REV2:");
//                        currentCard[0].setQueue(Card.QUEUE_LNR1);
//                        currentCard[0].setDue(due_time);
//                        againList.add(currentCard[0]);
//                        cardListAgainToday.add(currentCard[0]);
//
//                        //Card d?a next card is new
//                        if (todayList.size() > 0) {
//                            Log.e(TAG, "Card.QUEUE_REV2:NEW LIST");
//                            //
//                            position[0]++;
//                            if (position[0] <= list_card_new_size - 1) {
//                                //get next card again
//                                currentCard[0] = todayList.get(position[0]);
//
//                                lbCountDue.setBackgroundResource(R.color.white);
//                                lbCountAgain.setBackgroundResource(R.color.white);
//                                lbCountNew.setBackgroundResource(R.color.teal_200);
//
//                                //TODO:Display next card
//                                _loadWebView(_getQuestionDisplay(currentCard[0].getQuestion()));
//
//                            } else {
//                                _completeLean();
//                            }
//                        } else {
//                            //next is again
//                            _completeLean();
//                        }
//
//                    } else if (currentCard[0].getQueue() == Card.QUEUE_NEW_CRAM0) {
//                        currentCard[0].setQueue(Card.QUEUE_LNR1);
//                        currentCard[0].setDue(due_time);
//                        againList.add(currentCard[0]);
//                        cardListAgainToday.add(currentCard[0]);
//
//                        Log.e(TAG, "Card.QUEUE_NEW_CRAM0:");
//                        if (againList.size() > 0 && position_again[0] <= todayList.size() - 1) {
//
//                            long currenTime = new Date().getTime() / 1000;
//
//
//                            Log.e(TAG, "Card.QUEUE_NEW_CRAM0:AGAIN LIST:" + position_again[0]);
//                            try {
//                                position_again[0]++;
//                                currentCard[0] = againList.get(position_again[0]);
//                                boolean check_again = false;
//                                for (Card cardAgain : againList) {
//                                    currentCard[0] = cardAgain;
//                                    if (currentCard[0].getDue() < currenTime) {
//                                        lbCountDue.setBackgroundResource(R.color.white);
//                                        lbCountAgain.setBackgroundResource(R.color.teal_200);
//                                        lbCountNew.setBackgroundResource(R.color.white);
//
//                                        //TODO:Display next card
//                                        _loadWebView(_getQuestionDisplay(currentCard[0].getQuestion()));
//                                        check_again = true;
//                                        //break;
//                                    } else {
//                                        check_again = false;
//                                    }
//                                }
//                                if (!check_again) {
//                                    if (todayList.size() > 0) {
//
//                                        position[0] = position[0] + 1;
//                                        if (position[0] <= list_card_new_size - 1) {
//                                            //get next card again
//                                            currentCard[0] = todayList.get(position[0]);
//
//                                            lbCountDue.setBackgroundResource(R.color.white);
//                                            lbCountAgain.setBackgroundResource(R.color.white);
//                                            lbCountNew.setBackgroundResource(R.color.teal_200);
//
//
//                                            //TODO:Display next card
//                                            _loadWebView(_getQuestionDisplay(currentCard[0].getQuestion()));
//
//                                        } else {
//                                            _completeLean();
//                                        }
//
//                                    }
//                                }
//
//                            } catch (Exception e) {
//
//
//                                if (todayList.size() > 0) {
//
//                                    position[0] = position[0] + 1;
//                                    if (position[0] <= list_card_new_size - 1) {
//                                        //get next card again
//                                        currentCard[0] = todayList.get(position[0]);
//
//                                        lbCountDue.setBackgroundResource(R.color.white);
//                                        lbCountAgain.setBackgroundResource(R.color.white);
//                                        lbCountNew.setBackgroundResource(R.color.teal_200);
//
//
//                                        //TODO:Display next card
//                                        _loadWebView(_getQuestionDisplay(currentCard[0].getQuestion()));
//
//                                    } else {
//                                        _completeLean();
//                                    }
//
//                                }
//                            }
//                        } else {
//
////                            Log.e(TAG, "Card.QUEUE_NEW_CRAM0:NEW LIST");
////                            currentCard[0].setQueue(Card.QUEUE_LNR1);
////                            currentCard[0].setDue(due_time);
////                            againList.add(currentCard[0]);
////
////                            if (todayList.size() > 0) {
////
////                                position[0] = position[0] + 1;
////                                if (position[0] <= list_card_new_size - 1) {
////                                    //get next card again
////                                    currentCard[0] = todayList.get(position[0]);
////
////                                    lbCountDue.setBackgroundResource(R.color.white);
////                                    lbCountAgain.setBackgroundResource(R.color.white);
////                                    lbCountNew.setBackgroundResource(R.color.teal_200);
////
////
////                                    //TODO:Display next card
////                                    _loadWebView(_getQuestionDisplay(currentCard[0].getQuestion()));
////
////                                } else {
////                                    _completeLean();
////                                }
////
////
////                            } else {
////                                //
////                                _completeLean();
////                            }
//                            _completeLean();
//                        }
//
//                    }
//
//
//                    int _count = todayList.size();
//                    //reset total vocabilary
//                    int current_count = _count - cardListAgainToday.size();
//                    lbCountNew.setText("" + current_count);
//
//                    //Update Count Again
//                    int count_card_list_again = againList.size();
//                    //reset total vocabilary
//                    lbCountAgain.setText("" + count_card_list_again);
//
//
////                    //todo:next position
////                    position[0] = position[0] + 1;
////                    if (againList.size() > 0) {
////                        if (position[0] <= list_card_new_size - 1) {
////                            //get next card again
////                            currentCard[0] = againList.get(position[0]);
////
////                            lbCountDue.setBackgroundResource(R.color.white);
////                            lbCountAgain.setBackgroundResource(R.color.teal_200);
////                            lbCountNew.setBackgroundResource(R.color.white);
////
////                            //TODO:Display next card
////                            _loadWebView(_getQuestionDisplay(currentCard[0].getQuestion()));
////
////                        } else {
////                            //end
////                            _completeLean();
////                        }
////
////                    } else {
////                        if (dueList.size() > 0) {
////                            //Todo: get next Card
////                            currentCard[0] = dueList.get(position[0]);
////                            lbCountDue.setBackgroundResource(R.color.teal_200);
////                            lbCountAgain.setBackgroundResource(R.color.white);
////                            lbCountNew.setBackgroundResource(R.color.white);
////                        } else if (todayList.size() > 0) {
////                            currentCard[0] = todayList.get(position[0]);
////
////                            lbCountDue.setBackgroundResource(R.color.white);
////                            lbCountAgain.setBackgroundResource(R.color.white);
////                            lbCountNew.setBackgroundResource(R.color.teal_200);
////                        }
////                    }
////
////
////                    //Todo:Check if go to end eles back
////                    if (position[0] <= list_card_new_size - 1) {
////
////                        //todo:next vocabulary
////                        currentCard[0] = todayList.get(position[0]);
////
////                        //TODO:Display next card
////                        _loadWebView(_getQuestionDisplay(currentCard[0].getQuestion()));
////
////                    } else {
////                        //end
////                        _completeLean();
////                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }


        });
        //

        btnHard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show btnShowAnswer and hide btnAgain0
                btnShowAnswer.setVisibility(View.VISIBLE);
                mLayoutButton.setVisibility(View.GONE);

                int currentCardQueue = currentCard.getQueue();

                //Check Contains and Remove
                _checkContainsAndRemove(dueList);
                _checkContainsAndRemove(cardListAddDueToDay);


                //set answer Card
                cardSched.answerCard(currentCard, Card.EASE_HARD);

                dueList.add(currentCard);
                cardListAddDueToDay.add(currentCard);

                int _count = todayList.size();
                //reset total vocabilary
                int current_count = _count - cardListAddDueToDay.size();
                lbCountNew.setText("" + current_count);

                try {
                    if (position < todayList.size()) {
                        if (currentCardQueue == Card.QUEUE_NEW_CRAM0) {
                            //if currentCardQueue == Card.QUEUE_NEW_CRAM0 next card again if againList.size>0
                            Log.i(TAG, "Card.QUEUE_NEW_CRAM0");
                            _nextAgainCard();


                        } else if (currentCardQueue == Card.QUEUE_LNR1) {
                            Log.i(TAG, "Card.QUEUE_LNR1");
                            _nextDueCard();


                        } else if (currentCardQueue == Card.QUEUE_REV2) {
                            Log.i(TAG, "Card.QUEUE_REV2");
                            _nextNewCard();

                        }
                    } else {
                        _completeLean();


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                //show btnShowAnswer and hide btnAgain0
                btnShowAnswer.setVisibility(View.VISIBLE);
                mLayoutButton.setVisibility(View.GONE);

                int currentCardQueue = currentCard.getQueue();

                //Check Contains and Remove
                _checkContainsAndRemove(dueList);
                _checkContainsAndRemove(cardListAddDueToDay);

                //set answer Card
                cardSched.answerCard(currentCard, Card.EASE_GOOD);

                dueList.add(currentCard);
                cardListAddDueToDay.add(currentCard);

                int _count = todayList.size();
                //reset total vocabilary
                int current_count = _count - cardListAddDueToDay.size();
                lbCountNew.setText("" + current_count);

                try {
                    if (position < todayList.size()) {
                        if (currentCardQueue == Card.QUEUE_NEW_CRAM0) {
                            //if currentCardQueue == Card.QUEUE_NEW_CRAM0 next card again if againList.size>0
                            Log.i(TAG, "Card.QUEUE_NEW_CRAM0");
                            _nextAgainCard();


                        } else if (currentCardQueue == Card.QUEUE_LNR1) {
                            Log.i(TAG, "Card.QUEUE_LNR1");
                            _nextDueCard();


                        } else if (currentCardQueue == Card.QUEUE_REV2) {
                            Log.i(TAG, "Card.QUEUE_REV2");
                            _nextNewCard();

                        }
                    } else {
                        _completeLean();


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        btnEasy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show btnShowAnswer and hide btnAgain0
                btnShowAnswer.setVisibility(View.VISIBLE);
                mLayoutButton.setVisibility(View.GONE);

                int currentCardQueue = currentCard.getQueue();

                //Check Contains and Remove
                _checkContainsAndRemove(dueList);
                _checkContainsAndRemove(cardListAddDueToDay);

                //set answer Card
                cardSched.answerCard(currentCard, Card.EASE_EASY);

                dueList.add(currentCard);
                cardListAddDueToDay.add(currentCard);


                int _count = todayList.size();
                //reset total vocabilary
                int current_count = _count - cardListAddDueToDay.size();
                lbCountNew.setText("" + current_count);

                try {
                    if (position < todayList.size()) {
                        if (currentCardQueue == Card.QUEUE_NEW_CRAM0) {
                            //if currentCardQueue == Card.QUEUE_NEW_CRAM0 next card again if againList.size>0
                            Log.i(TAG, "Card.QUEUE_NEW_CRAM0");
                            _nextAgainCard();


                        } else if (currentCardQueue == Card.QUEUE_LNR1) {
                            Log.i(TAG, "Card.QUEUE_LNR1");
                            _nextDueCard();


                        } else if (currentCardQueue == Card.QUEUE_REV2) {
                            Log.i(TAG, "Card.QUEUE_REV2");
                            _nextNewCard();

                        }
                    } else {
                        _completeLean();


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void _addJavascriptInterfaceQuestionAndAnswer() {
        //Todo: addJavascriptInterface play question
        mWebViewLeadDetails.addJavascriptInterface(new JsObjectQuestion() {
            @JavascriptInterface
            public void playQuestion() {
                //get text to Speak
                String toSpeak = currentCard.getQuestion();

                //Toast Text Speak
                Toast.makeText(getActivity().getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();

                //Speak text
                _speakText(toSpeak);

                //textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        }, "question");

        //Todo: addJavascriptInterface play answer
        mWebViewLeadDetails.addJavascriptInterface(new JsObjectAnswers() {
            @JavascriptInterface
            public void playAnswers() {
                //get text to Speak
                String toSpeak = currentCard.getAnswers();

                //Toast Text Speak
                Toast.makeText(getActivity().getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();

                //Speak text
                _speakText(toSpeak);

                //textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        }, "answers");
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
        Log.i(TAG, "Next new card");
        if (todayList.size() > 0) {
            position++;

            if (position <= todayList.size() - 1) {
                //get next card again
                currentCard = todayList.get(position);

                //set BackBackground color
                lbCountDue.setBackgroundResource(R.color.white);
                lbCountAgain.setBackgroundResource(R.color.white);
                lbCountNew.setBackgroundResource(R.color.teal_200);


                //TODO:Display next card
                _loadWebView(_getQuestionDisplay(currentCard.getQuestion()));

            } else {
                _completeLean();
            }
        } else {
            _completeLean();
        }
    }

    private void _nextDueCard() {
        if (dueList.size() > 0) {//Check againList.size()>0
            Log.i(TAG, "Have Card Due,Next card is due card");
            try {
                currentCard = dueList.get(position_due);

                lbCountDue.setBackgroundResource(R.color.teal_200);
                lbCountAgain.setBackgroundResource(R.color.white);
                lbCountNew.setBackgroundResource(R.color.white);

                //TODO:Display next card
                _loadWebView(_getQuestionDisplay(currentCard.getQuestion()));
            } catch (Exception e) {
                e.printStackTrace();
                position_due++;
                if (position_due < (dueList.size() - 1)) {

                    currentCard = againList.get(position_due);

                    lbCountDue.setBackgroundResource(R.color.teal_200);
                    lbCountAgain.setBackgroundResource(R.color.white);
                    lbCountNew.setBackgroundResource(R.color.white);

                    //TODO:Display next card
                    _loadWebView(_getQuestionDisplay(currentCard.getQuestion()));
                } else {
                    Log.i(TAG, "_nextDueCard:New Card");
                    _nextNewCard();
                }
            }


        } else if (dueList.size() > 0) {//Check dueList.size()>0

            Log.i(TAG, "Have Card Due,Next card is due card");
            _nextDueCard();
        } else {
            _nextNewCard();
        }


    }

    private void _nextAgainCard() {
        int current_time = (int) (new Date().getTime() / 1000);
        int due = (int) currentCard.getDue();
        if (current_time - due >= 600) {
            if (againList.size() > 0) {//Check againList.size()>0
                Log.i(TAG, "Have Card Again,Next card is again card");
                try {
                    currentCard = againList.get(position_again);

                    lbCountDue.setBackgroundResource(R.color.white);
                    lbCountAgain.setBackgroundResource(R.color.teal_200);
                    lbCountNew.setBackgroundResource(R.color.white);

                    //TODO:Display next card
                    _loadWebView(_getQuestionDisplay(currentCard.getQuestion()));
                } catch (Exception e) {
                    e.printStackTrace();
                    position_again++;
                    if (position_again < (againList.size() - 1)) {

                        currentCard = againList.get(position_again);

                        lbCountDue.setBackgroundResource(R.color.white);
                        lbCountAgain.setBackgroundResource(R.color.teal_200);
                        lbCountNew.setBackgroundResource(R.color.white);

                        //TODO:Display next card
                        _loadWebView(_getQuestionDisplay(currentCard.getQuestion()));
                    } else {
                        Log.i(TAG, "_nextAgainCard:New Card");
                        _nextNewCard();
                    }
                }


            } else if (dueList.size() > 0) {//Check dueList.size()>0

                Log.i(TAG, "Have Card Due,Next card is due card");
                _nextDueCard();
            } else {
                _nextNewCard();
            }
        } else {
            _nextNewCard();
        }
    }
    /**
     * Load string Html
     */
    private void _loadWebView(String questionDisplay) {
        //Clear View
        if (Build.VERSION.SDK_INT < 18) {
            mWebViewLeadDetails.clearView();
        } else {
            mWebViewLeadDetails.loadUrl("about:blank");
        }
        //
        //  Log.i(TAG, "HTML FROM:" + questionDisplay.toString());
        //Set Data
        mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, questionDisplay, mime, encoding, null);

    }

    /**
     * Init db sqlite
     */
    private void _initDatabase() {
        dataBaseHelper = new LearnApiImplements(getActivity());
    }


    /**
     * return DetalsCourse
     */
    private void _completeLean() {
        Toast.makeText(getActivity().getApplicationContext(), "Hoan thanh", Toast.LENGTH_SHORT);
        fragmentStudyListener.completeCourse();

    }


    /**
     * init HTML question
     */
    private String _getQuestionDisplay(String s) {
        String html =
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<style>\n" +
                        " figure {" +
                        "   text-align: center;" +
                        "   margin: auto;" +
                        "}" +
                        "figure.image img {" +
                        "   width: 100% !important;" +
                        "   height: auto !important;" +
                        "}" +
                        "figcaption {" +
                        "   font-size: 10px;" +
                        "}" +
                        "a {" +
                        " margin-top:5px;" +
                        "}" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<h1>" + s + "<a onclick='question.playQuestion();'><img src='ic_play_black.png'/></a></h1>"
                        + "</body>\n" +
                        "</html>";
        return html;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fragmentStudyListener = (FragmentStudyListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*
    *Java Scrip Object Question
    * */
    public class JsObjectQuestion {
        @JavascriptInterface
        public String toString() {
            return "question";
        }
    }

    /*
    *Java Scrip Object Answers
    * */
    public class JsObjectAnswers {
        @JavascriptInterface
        public String toString() {
            return "answers";
        }

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
