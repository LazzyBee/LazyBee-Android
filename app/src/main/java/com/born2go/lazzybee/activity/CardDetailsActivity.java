package com.born2go.lazzybee.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.view.SlidingTabLayout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CardDetailsActivity extends ActionBarActivity {


    private static final String TAG = "CardDetailsActivity";
    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;


    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        String cardId = getIntent().getStringExtra(LazzyBeeShare.CARDID);
        LearnApiImplements learnApiImplements = new LearnApiImplements(getApplicationContext());

        _initTextToSpeech();

//        if (savedInstanceState == null) {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
//            transaction.replace(R.id.sample_content_fragment, fragment);
//            transaction.commit();
//        }
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        //mViewPager.setAdapter(new SamplePagerAdapter());
        Log.i(TAG, "CardId=" + cardId);
        Card card = learnApiImplements._getCardByID(cardId);
        if (card == null) {
            card = new Card();
            card.setQuestion("Hello");
        }
//        textToSpeech.speak(card.getQuestion(), TextToSpeech.QUEUE_ADD, null);
//
//
//        _speakText(card.getQuestion());
        PackageCardPageAdapter packageCardPageAdapter = new PackageCardPageAdapter(card);

        mViewPager.setAdapter(packageCardPageAdapter);


        // END_INCLUDE (setup_viewpager)
        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        // END_INCLUDE (setup_slidingtablayout)

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    WebView mWebViewLeadDetails;
    class PackageCardPageAdapter extends PagerAdapter {
        Card card;
        List<String> packages;



        public PackageCardPageAdapter(Card card) {
            this.card = card;
            if (card.getPackage() != null) {
                this.packages = LazzyBeeShare.getListPackageFormString(card.getPackage());
                if (packages.size() == 0) {
                    this.packages = Arrays.asList("Common");
                }
            } else
                this.packages = Arrays.asList("Common");

        }


        @Override
        public CharSequence getPageTitle(int position) {
            return packages.get(position);
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return packages.size();
        }

        /**
         * Determines whether a page View is associated with a specific key object
         * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
         * required for a PagerAdapter to function properly.
         *
         * @param view   Page View to check for association with <code>object</code>
         * @param object Object to check for association with <code>view</code>
         * @return true if <code>view</code> is associated with the key object <code>object</code>
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }


        /**
         * Create the page for the given position.  The adapter is responsible
         * for adding the view to the container given here, although it only
         * must ensure this is done by the time it returns from
         * {@link #finishUpdate(ViewGroup)}.
         *
         * @param container The containing View in which the page will be shown.
         * @param position  The page position to be instantiated.
         * @return Returns an Object representing the new page.  This does not
         * need to be a View, but can be some other container of the page.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view = getLayoutInflater().inflate(R.layout.page_package_card_item,
                    container, false);
            // Add the newly created View to the ViewPager
            container.addView(view);

            //
            mWebViewLeadDetails = (WebView) view.findViewById(R.id.mWebViewCardDetails);



            WebSettings ws = mWebViewLeadDetails.getSettings();
            ws.setJavaScriptEnabled(true);

            _addJavascriptInterfaceQuestionAndAnswer();

            String answer = LazzyBeeShare.getAnswerHTMLwithPackage(card, packages.get(position), getString(R.string.explain), getString(R.string.example),true);

            Log.i(TAG, answer);

            mWebViewLeadDetails.loadDataWithBaseURL(LazzyBeeShare.ASSETS, answer, LazzyBeeShare.mime, LazzyBeeShare.encoding, null);


            // Return the View
            return view;
        }

        private void _addJavascriptInterfaceQuestionAndAnswer() {
            //Todo: addJavascriptInterface play question
            mWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectQuestion() {
                @JavascriptInterface
                public void playQuestion() {
                    //get text to Speak
                    String toSpeak = card.getQuestion();

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
                    String answer = card.getAnswers();
                    String toSpeech = LazzyBeeShare._getValueFromKey(answer, "explain");

                    //Speak text
                    _speakText(toSpeech);
                }
            }, "explain");
            mWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectExample() {
                @JavascriptInterface
                public void speechExample() {
                    //get answer json
                    String answer = card.getAnswers();
                    String toSpeech = LazzyBeeShare._getValueFromKey(answer, "example");

                    //Speak text
                    _speakText(toSpeech);
                }
            }, "example");

        }


    }


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

    private void _initTextToSpeech() {
        //Todo:init TextToSpeech
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        return;
//        if (mWebViewLeadDetails.canGoBack()) {
//
//        } else {
//            super.onBackPressed();
//        }
    }
}
