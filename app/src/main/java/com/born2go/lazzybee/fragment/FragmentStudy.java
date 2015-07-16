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

        //db
        _initDatabase();

        //_getListCard random to day
        List<Card> cardListToDay = dataBaseHelper._getRandomCard(10);

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
            _setDataforWebView(cardListToDay);
        else
            Log.i(TAG, "List Card Empty");

        //@JavascriptInterface

        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return view;
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
    private void _setDataforWebView(final List<Card> cardList) {
        //init position
        final int[] position = {0};


        //Set JavaScripEnabled
        WebSettings ws = mWebViewLeadDetails.getSettings();
        ws.setJavaScriptEnabled(true);


        //size vocabulary list
        final int size = cardList.size();
        //set total vocabilary
        lbCountTotalVocabulary.setText("" + size);
        lbCountTotalVocabulary.setTag(size);
        //Current Card
        final Card[] currentCard = {cardList.get(0)};
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
                Card card = cardList.get(position[0]);

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
                //
                position[0] = position[0] + 1;
                //Check if go to end eles back
                if (position[0] <= size - 1) {
                    //next vocabulary
                    currentCard[0] = cardList.get(position[0]);

                    String currentCardId = String.valueOf(currentCard[0].getId());//get current cardId

                    _loadWebView(_getQuestionDisplay(cardList.get(position[0]).getQuestion()));
                    //mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, _getQuestionDisplay(cardList.get(position[0]).getQuestion()), mime, encoding, null);

                    //get total vocabulary by tag
                    int _count = Integer.valueOf(lbCountTotalVocabulary.getTag().toString());
                    //reset total vocabilary
                    int current_count = _count - 1;
                    lbCountTotalVocabulary.setText("" + current_count);
                    lbCountTotalVocabulary.setTag(current_count);

                    //TODO:Check Tag
                    if (btnAgain1.getTag() != null) {

                        //TODO:Get Time Queue by Tag btnAgain2
                        Long timeQueueCard = (long) btnAgain1.getTag();

                        _setQueueCard(currentCardId, timeQueueCard);

                    }

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
                //
                position[0] = position[0] + 1;
                //TODO:Check if go to end eles back
                if (position[0] <= size - 1) {
                    //Current Card
                    currentCard[0] = cardList.get(position[0]);

                    String currentCardId = String.valueOf(currentCard[0].getId());//get current carrdid

                    //Load next card
                    _loadWebView(_getQuestionDisplay(cardList.get(position[0]).getQuestion()));

                    //mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, _getQuestionDisplay(cardList.get(position[0]).getQuestion()), mime, encoding, null);

                    //get total vocabulary by tag
                    int _count = Integer.valueOf(lbCountTotalVocabulary.getTag().toString());

                    //reset total vocabilary
                    int current_count = _count - 1;
                    lbCountTotalVocabulary.setText("" + current_count);
                    lbCountTotalVocabulary.setTag(current_count);

                    //TODO:Check Tag
                    if (btnAgain2.getTag() != null) {

                        //TODO:Get Time Queue by Tag btnAgain2
                        Long timeQueueCard = (long) btnAgain2.getTag();

                        _setQueueCard(currentCardId, timeQueueCard);

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
                //
                position[0] = position[0] + 1;
                //Check if go to end eles back
                if (position[0] <= size - 1) {
                    currentCard[0] = cardList.get(position[0]);
                    String currentCardId = String.valueOf(currentCard[0].getId());//get current cardId

                    //next vocabulary
                    //mWebViewLeadDetails.loadDataWithBaseURL(ASSETS, _getQuestionDisplay(cardList.get(position[0]).getQuestion()), mime, encoding, null);
                    _loadWebView(_getQuestionDisplay(cardList.get(position[0]).getQuestion()));

                    //get total vocabulary by tag
                    int _count = Integer.valueOf(lbCountTotalVocabulary.getTag().toString());

                    //reset total vocabilary
                    int current_count = _count - 1;
                    lbCountTotalVocabulary.setText("" + current_count);
                    lbCountTotalVocabulary.setTag(current_count);

                    //TODO:Check Tag
                    if (btnAgain3.getTag() != null) {

                        //TODO:Get Time Queue by Tag btnAgain3
                        Long timeQueueCard = (long) btnAgain3.getTag();

                        _setQueueCard(currentCardId, timeQueueCard);

                    }
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
                //
                position[0] = position[0] + 1;
                //Check if go to end eles back
                if (position[0] <= size - 1) {
                    currentCard[0] = cardList.get(position[0]);

                    String currentCardId = String.valueOf(currentCard[0].getId());//get current cardId

                    //next vocabulary
                    _loadWebView(_getQuestionDisplay(cardList.get(position[0]).getQuestion()));

                    //get total vocabulary by tag
                    int _count = Integer.valueOf(lbCountTotalVocabulary.getTag().toString());

                    //reset total vocabilary
                    int current_count = _count - 1;
                    lbCountTotalVocabulary.setText("" + current_count);
                    lbCountTotalVocabulary.setTag(current_count);

                    //TODO:Check Tag
                    if (btnAgain4.getTag() != null) {

                        //TODO:Get Time Queue by Tag btnAgain4
                        Long timeQueueCard = (long) btnAgain4.getTag();

                        _setQueueCard(currentCardId, timeQueueCard);

                    }
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
