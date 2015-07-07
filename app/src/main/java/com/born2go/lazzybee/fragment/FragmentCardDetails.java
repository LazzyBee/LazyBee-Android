package com.born2go.lazzybee.fragment;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCardDetails extends Fragment {


    public static final String CARD_ID = "cardId";
    public static final String TAG = "FragmentCardDetails";
    String cardId;
    TextToSpeech tts;

    public FragmentCardDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardId = getArguments().getString(CARD_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_details, container, false);

        //init webview
        WebView mWebViewCardDetails = (WebView) view.findViewById(R.id.mWebViewCardDetails);

        //init Db
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());

        //Get Card by ID
        final Card card = dataBaseHelper._getCardByID(cardId);

        //get html
        String htmlView = LazzyBeeShare.getAnswerHTML(card);

        //Set Data
        mWebViewCardDetails.loadDataWithBaseURL(LazzyBeeShare.ASSETS, htmlView, LazzyBeeShare.mime, LazzyBeeShare.encoding, null);

        //init text to Speak
        tts = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        //Speak text
        mWebViewCardDetails.addJavascriptInterface(new JsObjectQuestion() {
            @JavascriptInterface
            public void playQuestion() {
                //get text to Speak
                String toSpeak = card.getQuestion();

                //Toast Text Speak
                Toast.makeText(getActivity().getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();

                //Speak text
                speakText(toSpeak);

                //tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        }, "question");

        mWebViewCardDetails.addJavascriptInterface(new JsObjectAnswers() {
            @JavascriptInterface
            public void playAnswers() {
                //get text to Speak
                String toSpeak = card.getQuestion();

                //Toast Text Speak
                Toast.makeText(getActivity().getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();

                //Speak text
                speakText(toSpeak);

                //tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        }, "answers");


        return view;
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
    public void speakText(String toSpeak) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(toSpeak);
        } else {
            ttsUnder20(toSpeak);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

}
