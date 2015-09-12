package com.born2go.lazzybee.activity;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.UpdateContenCardFormServer;
import com.born2go.lazzybee.adapter.UpdateContenCardFormServer.AsyncResponse;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.view.SlidingTabLayout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CardDetailsActivity extends AppCompatActivity implements AsyncResponse{


    private static final String TAG = "CardDetailsActivity";

    private Context context;


    Card card;
    String cardId;

    LearnApiImplements learnApiImplements;

    private SlidingTabLayout mSlidingTabLayout;

    private ViewPager mViewPager;
    WebView mWebViewLeadDetails;
    TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.context = this;
        _initTextToSpeech();
        cardId = getIntent().getStringExtra(LazzyBeeShare.CARDID);
        learnApiImplements = new LearnApiImplements(context);
//        if (savedInstanceState == null) {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString(LazzyBeeShare.CARDID, cardId);
//            fragment.setArguments(bundle);
//            transaction.replace(R.id.sample_content_fragment, fragment);
//            transaction.commit();
//        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Card card = learnApiImplements._getCardByID(cardId);
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        PackageCardPageAdapter packageCardPageAdapter = new PackageCardPageAdapter(context, card);
//        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.setAdapter(packageCardPageAdapter);
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);

        mSlidingTabLayout.setViewPager(mViewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card_details, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        Log.i(TAG, "MENU ITEM:" + id + ",Home:" + android.R.id.home);
        switch (id) {
            case android.R.id.home:
                finish();
                onBackPressed();
                return true;
            case R.id.action_add_to_learn:
                _addCardToLearn();
                return true;
            case R.id.action_update:
                //
                _updateCardFormServer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void _updateCardFormServer() {
        //Check card==null get card form Sqlite by cardID
        if (card == null)
            card = learnApiImplements._getCardByID(cardId);

        UpdateContenCardFormServer updateContenCardFormServer = new UpdateContenCardFormServer(context);
        AsyncTask<String, Void, Card> asyncTask=    updateContenCardFormServer.execute(card.getQuestion());
        updateContenCardFormServer.delegate=this;


    }

    private void _addCardToLearn() {
        if (card == null)
            card = learnApiImplements._getCardByID(cardId);
        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(getString(R.string.dialog_message_add_to_learn, card.getQuestion()))
                .setTitle(getString(R.string.dialog_title_add_to_learn));

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // TODO:Update Queue_list in system table
                learnApiImplements._addCardIdToQueueList(cardId);

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Take care of calling onBackPressed() for pre-Eclair platforms.
     *
     * @param keyCode
     * @param event
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // do something on back.
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void processFinish(Card card) {
        if (card != null) {
            //Update Success reload data
            this.card.setAnswers(card.getAnswers());

            //Update Success reload data
            //Set Adapter
            PackageCardPageAdapter packageCardPageAdapter = new PackageCardPageAdapter(context, this.card);
            mViewPager.setAdapter(packageCardPageAdapter);
            mSlidingTabLayout.setViewPager(mViewPager);

            Toast.makeText(context, "Update card ok", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Update card error", Toast.LENGTH_SHORT).show();
        }
    }

    class PackageCardPageAdapter extends PagerAdapter {
        Card card;
        List<String> packages;
        private Context context;

        public PackageCardPageAdapter(Context context, Card card) {
            this.card = card;
            this.context = context;
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
            View view = getLayoutInflater().inflate(R.layout.page_package_card_item, container, false);

            // Add the newly created View to the ViewPager
            container.addView(view);
            //
            mWebViewLeadDetails = (WebView) view.findViewById(R.id.mWebViewCardDetails);
            WebSettings ws = mWebViewLeadDetails.getSettings();
            ws.setJavaScriptEnabled(true);

            _addJavascriptInterfaceQuestionAndAnswer();


            String answer = LazzyBeeShare.getAnswerHTMLwithPackage(context, card, packages.get(position), true);

            // Log.i(TAG, answer);

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
                    //Speak text
                    _speakText(toSpeak);
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

        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            // Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
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
        //init TextToSpeech
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
    }



}
