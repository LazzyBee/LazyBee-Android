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

    TextView lbCountTotalVocabulary;

    TextView lbCountAgainInday;

    TextView lbAgainDue;

    List<Card> cardListToDay;
    List<Card> cardListAgainDay;
    List<Card> cardListDueDay;
    CardSched cardSched;

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

        //init btnShowAnswer
        btnShowAnswer = (Button) view.findViewById(R.id.btnShowAnswer);

        //init mLayoutButton
        mLayoutButton = (LinearLayout) view.findViewById(R.id.mLayoutButton);

        //init button
        btnAgain0 = (Button) view.findViewById(R.id.btnAgain0);
        btnHard1 = (Button) view.findViewById(R.id.btnHard1);
        btnGood2 = (Button) view.findViewById(R.id.btnGood2);
        btnEasy3 = (Button) view.findViewById(R.id.btnEasy3);

        //init lbCount
        lbCountTotalVocabulary = (TextView) view.findViewById(R.id.lbCountTotalVocabulary);

        lbCountAgainInday = (TextView) view.findViewById(R.id.lbCountAgainInday);

        lbAgainDue = (TextView) view.findViewById(R.id.lbAgainDue);


        //db
        _initDatabase();

        //init cardSched
        cardSched = new CardSched();


        _initTextToSpeed();


        //get new random card list to day
        cardListToDay = dataBaseHelper._getRandomCard(10);

        //get card list again to day
        cardListAgainDay = dataBaseHelper._getListCardByQueue(Card.QUEUE_REV2);

        //get Card list due to day
        cardListDueDay = dataBaseHelper._getListCardByQueue(Card.QUEUE_DAY_LRN3);


        int cardListAgainDaysize = cardListAgainDay.size();//get card List Again size

        //Todo:
        if (cardListAgainDaysize > 0) {
            //todo:Check cardListToDay contains cardAgain

//            Iterator<Card> cardListToDay_Iterator = cardListToDay.iterator();
//            Iterator<Card> cardListAgainDay_Iterator = cardListAgainDay.iterator();
//
//
//            while (cardListToDay_Iterator.hasNext()) {
//                while (cardListAgainDay_Iterator.hasNext()) {
//
//                    Card card = cardListToDay_Iterator.next();
//
//                    Card cardAgain = cardListAgainDay_Iterator.next();
//
//                    if (card.getId() == cardAgain.getId()) {
//
//                        try {
//                            Log.i(TAG, "-card:" + card.toString());
//                            //cardListToDay_Iterator.remove();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                      //  break;
//                    } else {
//                        Log.i(TAG, "-card not compare");
//                    }
//                }
//            }

            /*Remove card*/
            List<Card> listcardRemove = new ArrayList<Card>();
            //Loop cardListAgainDay
            for (Card card : cardListToDay) {
                for (Card cardAgain : cardListAgainDay) {
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
                cardListToDay.remove(card);
            }
//            for (Card cardAgain : cardListAgainDay) {
//                    Log.i(TAG, "-card:" + card);

//                Log.i(TAG, "-cardAgain:" + cardAgain.getQuestion() + ":" + cardListToDay.contains(cardAgain));
//                Log.i(TAG, "-cardAgain:" + cardAgain.toString());
//                    if (card.getQuestion().toString().equals(cardAgain.getQuestion().toString())) {
//                        Log.i(TAG, "-Question:" + card.getQuestion());
//                        cardListToDay.remove(card);
//                    }
//                else {
//                    Log.i(TAG, "-Question:" + card.getQuestion());
//                }
//                if (cardListToDay.contains(cardAgain)) {
//
//                    //todo: yes contains,Remove
//                    cardListToDay.remove(cardAgain);
//                } else {
////                    Log.i(TAG, "No contain");
//                    Log.i(TAG, "-Question:" + cardAgain.getQuestion());
//                }

//            }
//            }
        }


        //Add List Card in Today Table
        //dataBaseHelper._insertListTodayCard(cardListToDay);

//        //Check Queue List Word
//        if (_CheckQueueWord()) {
        //  cardListToDay = _getListVocabularyQueue(cardListToDay);
//        }else {
//
//        }


        //set data
        if (cardListToDay.size() > 0)
            _setDataforWebView();
        else
            Log.i(TAG, "List Card Empty");

        //
        _setCountCardList();
        _setCountDueCardList();

        //@JavascriptInterface

        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return view;
    }

    private void _initTextToSpeed() {
        //Todo:init TextToSpeech
        textToSpeech = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    //setLangguage
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
    }


    private void _setCountCardList() {


        final int list_card_again_in_today_size = cardListAgainDay.size();
        //set total vocabilary

        lbCountAgainInday.setText("" + list_card_again_in_today_size);
        lbCountAgainInday.setTag(list_card_again_in_today_size);


        //list_card_new_size vocabulary list
        int list_card_new_size = cardListToDay.size();

        //todo: set count list card new
//        if (list_card_again_in_today_size > 0) {
//            list_card_new_size = list_card_new_size - list_card_again_in_today_size;
//        }

        //set total vocabilary
        lbCountTotalVocabulary.setText("" + list_card_new_size);
        lbCountTotalVocabulary.setTag(list_card_new_size);


//        final int list_card_due_day_size = cardListDueDay.size();
//        //set total vocabilary
//
//        lbAgainDue.setText("" + list_card_due_day_size);
//        lbAgainDue.setTag(list_card_due_day_size);


    }

    private void _setCountDueCardList() {
        final int list_card_due_day_size = cardListDueDay.size();
        //set total vocabilary

        lbAgainDue.setText("" + list_card_due_day_size);
        lbAgainDue.setTag(list_card_due_day_size);
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

        //final List<Card> cardListToDay = this.cardListToDay;

        //init position
        final int[] position = {0};


        //Todo: Set  JavaScripEnabled for webview
        WebSettings ws = mWebViewLeadDetails.getSettings();
        ws.setJavaScriptEnabled(true);


        //list_card_new_size vocabulary list
        final int list_card_new_size = cardListToDay.size();

        //Current Card
        final Card[] currentCard = {this.cardListToDay.get(0)};


        //Todo: addJavascriptInterface play question
        mWebViewLeadDetails.addJavascriptInterface(new JsObjectQuestion() {
            @JavascriptInterface
            public void playQuestion() {
                //get text to Speak
                String toSpeak = currentCard[0].getQuestion();

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
                String toSpeak = currentCard[0].getAnswers();

                //Toast Text Speak
                Toast.makeText(getActivity().getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();

                //Speak text
                _speakText(toSpeak);

                //textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        }, "answers");


        //Todo: Load first card
        if (cardListAgainDay.size() > 0)
            currentCard[0] = cardListAgainDay.get(index);

        mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, _getQuestionDisplay(currentCard[0].getQuestion()), mime, encoding, null);


        //btnShowAnswer onCLick
        btnShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hide btnShowAnswer and show mLayoutButton
                btnShowAnswer.setVisibility(View.GONE);
                mLayoutButton.setVisibility(View.VISIBLE);

                //get card
                Card card = cardListToDay.get(position[0]);

                //Show answer question
                //mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, getAnswerHTML(card), mime, encoding, null);
                // Log.i(TAG, "HTML init:" + getAnswerHTML(card));

                //Load Answer
                _loadWebView(LazzyBeeShare.getAnswerHTML(card));

                //set current card
                currentCard[0] = card;

                //get  next Ivl String List
                String[] ivlStrList = cardSched.nextIvlStrLst(card);

                //set text btn
                btnAgain0.setText(Html.fromHtml(ivlStrList[0] + "<br/>" + getString(R.string.EASE_AGAIN)));
                btnHard1.setText(Html.fromHtml(ivlStrList[1] + "<br/>" + getString(R.string.EASE_HARD)));
                btnGood2.setText(Html.fromHtml(ivlStrList[2] + "<br/>" + getString(R.string.EASE_GOOD)));
                btnEasy3.setText(Html.fromHtml(ivlStrList[3] + "<br/>" + getString(R.string.EASE_EASY)));
            }
        });

        final long curren_time = new Date().getTime();/*curent time*/

        //Todo:btnAgain on click
        btnAgain0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show btnShowAnswer and hide btnAgain0
                btnShowAnswer.setVisibility(View.VISIBLE);
                mLayoutButton.setVisibility(View.GONE);

                //get current Card
                Card old_card = cardListToDay.get(position[0]);

                //Todo:update queue and Due
                int due_time = (int) (curren_time + 600);
                _updateCardQueueAndCardDue(old_card.getId(), Card.QUEUE_REV2, due_time);

                //TODO:Add current card in CardAgainList and Remove CardListNew  to day
                cardListAgainDay.add(old_card);
                cardListToDay.remove(old_card);

                _setCountCardList();

                //todo:next position
                position[0] = position[0] + 1;

                //Todo:Check if go to end eles back
                if (position[0] <= list_card_new_size - 1) {

                    //todo:next vocabulary
                    currentCard[0] = cardListToDay.get(position[0]);
                    //
                    if ((position[0] - 1) != -1) {
                        //Todo:Remove Card
                        // cardListToDay.remove(old_card);

                        //Todo:Set queue
                        old_card.setQueue(60);

                        cardListAgainDay.add(old_card);
//                        if (position[0] + 1 > list_card_new_size)
//                            cardListAgainDay.add(cardListToDay.get(position[0] + 1));
//                        else {
//                            cardListAgainDay.add(cardListToDay.get(position[0] - 1));
//
//                        }
                    }


                    String currentCardId = String.valueOf(currentCard[0].getId());//get current cardId

                    //TODO:Display next card
                    _loadWebView(_getQuestionDisplay(cardListToDay.get(position[0]).getQuestion()));
                    //mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, _getQuestionDisplay(cardListToDay.get(position[0]).getQuestion()), mime, encoding, null);

                    //get total vocabulary by tag
                    int _count = Integer.valueOf(lbCountTotalVocabulary.getTag().toString());
                    //reset total vocabilary
                    int current_count = _count - position[0];
                    lbCountTotalVocabulary.setText("" + current_count);
                    lbCountTotalVocabulary.setTag(current_count);

                    //TODO:Check Tag
//                    if (btnAgain0.getTag() != null) {
//
//                        //TODO:Get Time Queue by Tag btnHard1
//                        Long timeQueueCard = (long) btnAgain0.getTag();
//
//                        _setQueueCard(currentCardId, timeQueueCard);
//
//                    }
                    //Todo: set queue card 60s
                    _setQueueCard("" + old_card.getId(), 60l);
                    //Remo

                } else {
                    //end
                    _completeLean();
                }

            }
        });
        btnHard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show btnShowAnswer and hide btnAgain0
                btnShowAnswer.setVisibility(View.VISIBLE);
                mLayoutButton.setVisibility(View.GONE);
                //Todo:Get current card
                Card old_card = cardListToDay.get(position[0]);

                //Todo:Get next position
                position[0] = position[0] + 1;

                //TODO:Check if go to end eles back
                if (position[0] <= list_card_new_size - 1) {
                    //Todo: get next Card
                    currentCard[0] = cardListToDay.get(position[0]);

                    String currentCardId = String.valueOf(currentCard[0].getId());//get next carrdid

                    //Todo: add card to Card list again today
                    if ((position[0] - 1) != -1) {

                        // cardListToDay.remove(old_card);

                        //Todo:Set queue
                        old_card.setQueue(600);


                        //add card aagain to day
                        cardListAgainDay.add(old_card);

//                        if (position[0] + 1 > list_card_new_size)
//                            cardListAgainDay.add(cardListToDay.get(position[0] + 1));
//                        else {
//                            cardListAgainDay.add(cardListToDay.get(position[0] - 1));
//
//                        }
                    }

                    _setCountCardList();


                    //Load next card
                    _loadWebView(_getQuestionDisplay(cardListToDay.get(position[0]).getQuestion()));

                    //mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, _getQuestionDisplay(cardListToDay.get(position[0]).getQuestion()), mime, encoding, null);

                    //get total vocabulary by tag
                    int _count = Integer.valueOf(lbCountTotalVocabulary.getTag().toString());

                    //Todo: reset count new card
                    int current_count = _count - position[0];
                    lbCountTotalVocabulary.setText("" + current_count);
                    lbCountTotalVocabulary.setTag(current_count);

                    //TODO:Check Tag
                    if (btnHard1.getTag() != null) {

                        //TODO:Get Time Queue by Tag btnHard1
                        Long timeQueueCard = (long) btnHard1.getTag();

                        _setQueueCard(currentCardId, timeQueueCard);

                    } else {
                        //Demo:set again word 600s
                        _setQueueCard(currentCardId, 600l);

                    }
                } else {
                    //end
                    _completeLean();
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

                // get current card
                Card old_card = cardListToDay.get(position[0]);

                //Todo:Update card queue and card Due

                //_updateCardQueueAndCardDue(old_card.getId(), Card.QUEUE_REV2,curren_time+600);

                // get next posotion
                position[0] = position[0] + 1;

                if (position[0] <= list_card_new_size - 1) {
                    // Get next card
                    currentCard[0] = cardListToDay.get(position[0]);

                    String currentCardId = String.valueOf(currentCard[0].getId());//get next cardId

                    //Todo: Remove display card in card list new
                    cardListToDay.remove(old_card);

                    //Todo:Display card next if cardQueuesize>0 else priority cardQueue>cardDue>cardNew
                    _loadWebView(_getQuestionDisplay(cardListToDay.get(position[0]).getQuestion()));


                    //get total vocabulary by tag
                    int _count = Integer.valueOf(lbCountTotalVocabulary.getTag().toString());

                    //reset total vocabilary
                    int current_count = _count - 1;
                    lbCountTotalVocabulary.setText("" + current_count);
                    lbCountTotalVocabulary.setTag(current_count);


                } else {
                    //Todo: end study
                    _completeLean();

                }

            }
        });


        btnEasy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show btnShowAnswer and hide btnAgain0
                btnShowAnswer.setVisibility(View.VISIBLE);
                mLayoutButton.setVisibility(View.GONE);

                //Todo: get current card
                Card old_card = cardListToDay.get(position[0]);

                //Todo: get next posotion
                position[0] = position[0] + 1;

                //Check if go to end eles back
                if (position[0] <= list_card_new_size - 1) {
                    currentCard[0] = cardListToDay.get(position[0]);

                    String currentCardId = String.valueOf(currentCard[0].getId());//get current cardId

//                    if ((position[0] - 1) != -1) {
//                        // cardListToDay.remove(old_card);
//                        //Todo:Set queue
//                        old_card.setQueue(4l);
//                        //
//                        cardListDueDay.add(old_card);
////                        if (position[0] + 1 > list_card_new_size)
////                            cardListAgainDay.add(cardListToDay.get(position[0] + 1));
////                        else {
////                            cardListAgainDay.add(cardListToDay.get(position[0] - 1));
////
////                        }
//                    }

                    // _setCountCardList();


                    //next vocabulary
                    _loadWebView(_getQuestionDisplay(cardListToDay.get(position[0]).getQuestion()));

                    //get total vocabulary by tag
                    int _count = Integer.valueOf(lbCountTotalVocabulary.getTag().toString());

                    //reset total vocabilary
                    int current_count = _count - 1;
                    lbCountTotalVocabulary.setText("" + current_count);
                    lbCountTotalVocabulary.setTag(current_count);

//                    //TODO:Check Tag
//                    if (btnEasy3.getTag() != null) {
//
//                        //TODO:Get Time Queue by Tag btnEasy3
//                        Long timeQueueCard = (long) btnEasy3.getTag();
//
//                        _setQueueCard(currentCardId, timeQueueCard);
//
//                    } else {
//                        //Demo:set again word 4 day
//                        _setQueueCard(currentCardId, 86400l * 4);
//
//                    }
                } else {
                    //end
                    _completeLean();
                }
            }
        });

    }

    private void _updateCardQueueAndCardDue(int old_cardId, int id, int queueDone2) {


    }

    /**
     * Set queue time for card
     *
     * @param currentCardId cardId
     * @param timeQueueCard Time queue
     */
    private void _setQueueCard(String currentCardId, Long timeQueueCard) {
        // dataBaseHelper._updateQueueCard(currentCardId, timeQueueCard);

    }

    /**
     * Load Time queue
     * <p>set Text btnAgain0</p>
     *
     * @param card
     */
    private void _loadTimeQueue(Card card) {
        btnAgain0.setTag(60000);

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
        Log.i(TAG, "HTML FROM:" + questionDisplay.toString());
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
