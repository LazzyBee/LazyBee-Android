package com.born2go.lazzybee.fragment;


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
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
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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
    Button btnAgain1, btnAgain2, btnAgain3, btnAgain4;

    TextView lbCountTotalVocabulary;

    TextView lbCountAgainInday;

    TextView lbAgainDue;

    List<Card> cardListToDay;
    List<Card> cardListAgainDay;
    List<Card> cardListDueDay;

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

        //init btnAgain
        btnAgain1 = (Button) view.findViewById(R.id.btnAgain1);
        btnAgain2 = (Button) view.findViewById(R.id.btnAgain2);
        btnAgain3 = (Button) view.findViewById(R.id.btnAgain3);
        btnAgain4 = (Button) view.findViewById(R.id.btnAgain4);

        //init lbCount
        lbCountTotalVocabulary = (TextView) view.findViewById(R.id.lbCountTotalVocabulary);

        lbCountAgainInday = (TextView) view.findViewById(R.id.lbCountAgainInday);

        lbAgainDue = (TextView) view.findViewById(R.id.lbAgainDue);


        //db
        _initDatabase();

        //get new random card list to day
        cardListToDay = dataBaseHelper._getRandomCard(10);

        //get card list again to day
        cardListAgainDay = dataBaseHelper._getListCardByQueue(60l);

        //get Card list due to day
        cardListDueDay = dataBaseHelper._getListCardByQueue(new Date().getTime());

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

    private void _setCountCardList() {

        //list_card_new_size vocabulary list
        final int list_card_new_size = cardListToDay.size();
        //set total vocabilary
        lbCountTotalVocabulary.setText("" + list_card_new_size);
        lbCountTotalVocabulary.setTag(list_card_new_size);


        final int list_card_again_in_today_size = cardListAgainDay.size();
        //set total vocabilary

        lbCountAgainInday.setText("" + list_card_again_in_today_size);
        lbCountAgainInday.setTag(list_card_again_in_today_size);


//        final int list_card_due_day_size = cardListDueDay.size();
//        //set total vocabilary
//
//        lbAgainDue.setText("" + list_card_due_day_size);
//        lbAgainDue.setTag(list_card_due_day_size);


    }

    private  void _setCountDueCardList(){
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
     * <p>Difine onclick btnAnswer and btnAgain.</p>
     * <p/>
     * Define JavaScrip to Speek Text.
     */
    private void _setDataforWebView() {

        final List<Card> listCardNew = cardListToDay;

        //init position
        final int[] position = {0};


        //Set JavaScripEnabled
        WebSettings ws = mWebViewLeadDetails.getSettings();
        ws.setJavaScriptEnabled(true);


        //list_card_new_size vocabulary list
        final int list_card_new_size = listCardNew.size();
        //Current Card
        final Card[] currentCard = {cardListToDay.get(0)};
        //int TextToSpeech
        textToSpeech = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

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
        //Load data for webview
        mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, _getQuestionDisplay(currentCard[0].getQuestion()), mime, encoding, null);
        //btnShowAnswer onCLick
        btnShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide btnShowAnswer and show mLayoutButton
                btnShowAnswer.setVisibility(View.GONE);
                mLayoutButton.setVisibility(View.VISIBLE);

                //get card
                Card card = listCardNew.get(position[0]);

                //Show answer question
                //mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, getAnswerHTML(card), mime, encoding, null);
                // Log.i(TAG, "HTML init:" + getAnswerHTML(card));

                //Load Answer
                _loadWebView(LazzyBeeShare.getAnswerHTML(card));

                currentCard[0] = card;

                //Load Time queue

                _loadTimeQueue(card);


            }
        });

        btnAgain1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show btnShowAnswer and hide btnAgain
                btnShowAnswer.setVisibility(View.VISIBLE);
                mLayoutButton.setVisibility(View.GONE);

                Card old_card = listCardNew.get(position[0]);

                //
                position[0] = position[0] + 1;


                //Check if go to end eles back
                if (position[0] <= list_card_new_size - 1) {
                    //next vocabulary
                    currentCard[0] = listCardNew.get(position[0]);
                    //
                    if ((position[0] - 1) != -1) {
                        //Todo:Remove Card
                        // listCardNew.remove(old_card);

                        //Todo:Set queue
                        old_card.setQueue(60l);

                        cardListAgainDay.add(old_card);
//                        if (position[0] + 1 > list_card_new_size)
//                            cardListAgainDay.add(listCardNew.get(position[0] + 1));
//                        else {
//                            cardListAgainDay.add(listCardNew.get(position[0] - 1));
//
//                        }
                    }
                    _setCountCardList();

                    String currentCardId = String.valueOf(currentCard[0].getId());//get current cardId

                    _loadWebView(_getQuestionDisplay(listCardNew.get(position[0]).getQuestion()));
                    //mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, _getQuestionDisplay(listCardNew.get(position[0]).getQuestion()), mime, encoding, null);

                    //get total vocabulary by tag
                    int _count = Integer.valueOf(lbCountTotalVocabulary.getTag().toString());
                    //reset total vocabilary
                    int current_count = _count - position[0];
                    lbCountTotalVocabulary.setText("" + current_count);
                    lbCountTotalVocabulary.setTag(current_count);

                    //TODO:Check Tag
//                    if (btnAgain1.getTag() != null) {
//
//                        //TODO:Get Time Queue by Tag btnAgain2
//                        Long timeQueueCard = (long) btnAgain1.getTag();
//
//                        _setQueueCard(currentCardId, timeQueueCard);
//
//                    }
                    //Demo:set again word 60s
                    _setQueueCard(currentCardId, 60l);

                } else {
                    //end
                    _completeLean();
                }

            }
        });
        btnAgain2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show btnShowAnswer and hide btnAgain
                btnShowAnswer.setVisibility(View.VISIBLE);
                mLayoutButton.setVisibility(View.GONE);
                //Todo:Get current card
                Card old_card = listCardNew.get(position[0]);

                //Todo:Get next position
                position[0] = position[0] + 1;

                //TODO:Check if go to end eles back
                if (position[0] <= list_card_new_size - 1) {
                    //Todo: get next Card
                    currentCard[0] = listCardNew.get(position[0]);

                    String currentCardId = String.valueOf(currentCard[0].getId());//get next carrdid

                    //Todo: add card to Card list again today
                    if ((position[0] - 1) != -1) {

                        // listCardNew.remove(old_card);

                        //Todo:Set queue
                        old_card.setQueue(600l);


                        //add card aagain to day
                        cardListAgainDay.add(old_card);

//                        if (position[0] + 1 > list_card_new_size)
//                            cardListAgainDay.add(listCardNew.get(position[0] + 1));
//                        else {
//                            cardListAgainDay.add(listCardNew.get(position[0] - 1));
//
//                        }
                    }

                    _setCountCardList();


                    //Load next card
                    _loadWebView(_getQuestionDisplay(listCardNew.get(position[0]).getQuestion()));

                    //mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, _getQuestionDisplay(listCardNew.get(position[0]).getQuestion()), mime, encoding, null);

                    //get total vocabulary by tag
                    int _count = Integer.valueOf(lbCountTotalVocabulary.getTag().toString());

                    //Todo: reset count new card
                    int current_count = _count - position[0];
                    lbCountTotalVocabulary.setText("" + current_count);
                    lbCountTotalVocabulary.setTag(current_count);

                    //TODO:Check Tag
                    if (btnAgain2.getTag() != null) {

                        //TODO:Get Time Queue by Tag btnAgain2
                        Long timeQueueCard = (long) btnAgain2.getTag();

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
        btnAgain3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show btnShowAnswer and hide btnAgain
                btnShowAnswer.setVisibility(View.VISIBLE);
                mLayoutButton.setVisibility(View.GONE);

                //Todo: get current card
                Card old_card = listCardNew.get(position[0]);

                //Todo: get next posotion
                position[0] = position[0] + 1;

                if (position[0] <= list_card_new_size - 1) {
                    //Todo: Get next card
                    currentCard[0] = listCardNew.get(position[0]);
                    String currentCardId = String.valueOf(currentCard[0].getId());//get next cardId

//                    if ((position[0] - 1) != -1) {
//                        // listCardNew.remove(old_card);
//                        //Todo:Set queue
//                        old_card.setQueue(1l);
//
//                        cardListDueDay.add(old_card);
////                        if (position[0] + 1 > list_card_new_size)
////                            cardListAgainDay.add(listCardNew.get(position[0] + 1));
////                        else {
////                            cardListAgainDay.add(listCardNew.get(position[0] - 1));
////
////                        }
//                    }

                   // _setCountCardList();


                    //next vocabulary
                    //mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, _getQuestionDisplay(listCardNew.get(position[0]).getQuestion()), mime, encoding, null);
                    _loadWebView(_getQuestionDisplay(listCardNew.get(position[0]).getQuestion()));

                    //get total vocabulary by tag
                    int _count = Integer.valueOf(lbCountTotalVocabulary.getTag().toString());

                    //reset total vocabilary
                    int current_count = _count - position[0];
                    lbCountTotalVocabulary.setText("" + current_count);
                    lbCountTotalVocabulary.setTag(current_count);
//
//                    //TODO:Check Tag
//                    if (btnAgain3.getTag() != null) {
//
//                        //TODO:Get Time Queue by Tag btnAgain3
//                        Long timeQueueCard = (long) btnAgain3.getTag();
//
//                        _setQueueCard(currentCardId, timeQueueCard);
//
//                    } else {
//                        //Demo:set again word 1 day
//                        _setQueueCard(currentCardId, 86400l);
//
//                    }
                } else {
                    //end
                    _completeLean();

                }

            }
        });
        btnAgain4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show btnShowAnswer and hide btnAgain
                btnShowAnswer.setVisibility(View.VISIBLE);
                mLayoutButton.setVisibility(View.GONE);

                //Todo: get current card
                Card old_card = listCardNew.get(position[0]);

                //Todo: get next posotion
                position[0] = position[0] + 1;

                //Check if go to end eles back
                if (position[0] <= list_card_new_size - 1) {
                    currentCard[0] = listCardNew.get(position[0]);

                    String currentCardId = String.valueOf(currentCard[0].getId());//get current cardId

//                    if ((position[0] - 1) != -1) {
//                        // listCardNew.remove(old_card);
//                        //Todo:Set queue
//                        old_card.setQueue(4l);
//                        //
//                        cardListDueDay.add(old_card);
////                        if (position[0] + 1 > list_card_new_size)
////                            cardListAgainDay.add(listCardNew.get(position[0] + 1));
////                        else {
////                            cardListAgainDay.add(listCardNew.get(position[0] - 1));
////
////                        }
//                    }

                   // _setCountCardList();


                    //next vocabulary
                    _loadWebView(_getQuestionDisplay(listCardNew.get(position[0]).getQuestion()));

                    //get total vocabulary by tag
                    int _count = Integer.valueOf(lbCountTotalVocabulary.getTag().toString());

                    //reset total vocabilary
                    int current_count = _count - position[0];
                    lbCountTotalVocabulary.setText("" + current_count);
                    lbCountTotalVocabulary.setTag(current_count);

//                    //TODO:Check Tag
//                    if (btnAgain4.getTag() != null) {
//
//                        //TODO:Get Time Queue by Tag btnAgain4
//                        Long timeQueueCard = (long) btnAgain4.getTag();
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

    /**
     * Set queue time for card
     *
     * @param currentCardId cardId
     * @param timeQueueCard Time queue
     */
    private void _setQueueCard(String currentCardId, Long timeQueueCard) {
        //TOdo:Check time inday

    }

    /**
     * Load Time queue
     * <p>set Text btnAgain</p>
     *
     * @param card
     */
    private void _loadTimeQueue(Card card) {
        btnAgain1.setTag(60000);

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
