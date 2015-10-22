package com.born2go.lazzybee.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.GetCardFormServerByQuestion;
import com.born2go.lazzybee.adapter.GetCardFormServerByQuestion.GetCardFormServerByQuestionResponse;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.ContainerHolderSingleton;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.view.SlidingTabLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.DataLayer;

import java.util.Arrays;
import java.util.List;

public class CardDetailsActivity extends AppCompatActivity implements GetCardFormServerByQuestionResponse {


    private static final String TAG = "CardDetailsActivity";
    private static final Object GA_SCREEN = "aCardDetailsScreen";

    private Context context;


    Card card;
    String cardId;

    LearnApiImplements learnApiImplements;

    private SlidingTabLayout mSlidingTabLayout;

    private ViewPager mViewPager;
    WebView mWebViewLeadDetails;
    TextToSpeech textToSpeech;

    MenuItem itemFavorite;

    LinearLayout container;

    CardView mCardViewAdv;
    CardView mCardViewViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.context = this;

        mCardViewViewPager = (CardView) findViewById(R.id.mCardViewViewPager);

        container = (LinearLayout) findViewById(R.id.container);

        learnApiImplements = LazzyBeeSingleton.learnApiImplements;


        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _displayCard();

        _initAdView();

        _trackerApplication();


    }

    private void _displayCard() {
        try {
            cardId = getIntent().getStringExtra(LazzyBeeShare.CARDID);
            card = learnApiImplements._getCardByID(cardId);

            setTitle(card.getQuestion());

            if (itemFavorite != null) {
                //load favorite
                if (card.getStatus() == 1) {
                    itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_important));
                    itemFavorite.setTitle(context.getString(R.string.action_favorite));
                } else {
                    itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_not_important));
                    itemFavorite.setTitle(context.getString(R.string.action_not_favorite));
                }
            }
            PackageCardPageAdapter packageCardPageAdapter = new PackageCardPageAdapter(context, card);
            mViewPager.setAdapter(packageCardPageAdapter);
            mSlidingTabLayout.setViewPager(mViewPager);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    private void _initAdView() {
        try {
            //get value form task manager
            Container container = ContainerHolderSingleton.getContainerHolder().getContainer();
            String adb_ennable;
            if (container == null) {
                adb_ennable = LazzyBeeShare.NO;
            } else {
                adb_ennable = container.getString(LazzyBeeShare.ADV_ENABLE);

            }
            mCardViewAdv = (CardView) findViewById(R.id.mCardViewAdv);
            AdView mAdView = (AdView) findViewById(R.id.adView);
            if (adb_ennable.equals(LazzyBeeShare.YES)) {
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(getResources().getStringArray(R.array.devices)[0])
                        .addTestDevice(getResources().getStringArray(R.array.devices)[1])
                        .build();
                mAdView.loadAd(adRequest);
                mCardViewAdv.setVisibility(View.VISIBLE);
                //
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                mCardViewViewPager.setLayoutParams(param);
            } else {
                mCardViewAdv.setVisibility(View.GONE);
                //
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 0f);
                mCardViewViewPager.setLayoutParams(param);

            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
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
        itemFavorite = menu.findItem(R.id.action_favorite);
        if (card != null) {
            //load favorite
            if (card.getStatus() == 1) {
                itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_important));
                itemFavorite.setTitle(context.getString(R.string.action_favorite));
            } else {
                itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_not_important));
                itemFavorite.setTitle(context.getString(R.string.action_not_favorite));
            }
        }

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
            case R.id.action_share:
                _shareCard();
                return true;
            case R.id.action_favorite:

                _addCardToFavorite();
                return true;
            case R.id.action_report:
                _reportCard();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void _shareCard() {
        try {
            //get card in Db
            if (card.getQuestion() == null)
                card = learnApiImplements._getCardByID(cardId);

            //get base url in Task Manager
            String base_url_sharing = LazzyBeeShare.DEFAULTS_BASE_URL_SHARING;
            String server_base_url_sharing = ContainerHolderSingleton.getContainerHolder().getContainer().getString(LazzyBeeShare.BASE_URL_SHARING);
            if (server_base_url_sharing != null) {
                if (server_base_url_sharing.length() > 0)
                    base_url_sharing = server_base_url_sharing;
            }

            //define base url with question
            base_url_sharing = base_url_sharing + card.getQuestion();
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

    private void _reportCard() {
        try {
            startActivity(LazzyBeeShare.getOpenFacebookIntent(context));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    private void _addCardToFavorite() {
        try {
            if (card.getQuestion() == null)
                card = learnApiImplements._getCardByID(cardId);

            int statusFavrite = 0;
            //Set icon drawer
            if (itemFavorite.getTitle().toString().equals(getString(R.string.action_not_favorite))) {
                statusFavrite = 1;
                itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_important));
                itemFavorite.setTitle(context.getString(R.string.action_favorite));
            } else {
                itemFavorite.setIcon(LazzyBeeShare.getDraweble(context, R.drawable.ic_action_not_important));
                itemFavorite.setTitle(context.getString(R.string.action_not_favorite));
            }

            //set status card and Update card
            card.setStatus(statusFavrite);
            learnApiImplements._updateCard(card);

            Toast.makeText(context, getString(R.string.message_add_favorite_card_done, card.getQuestion()), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }


    private void _updateCardFormServer() {
        //Check card==null get card form Sqlite by cardID
        if (card == null)
            card = learnApiImplements._getCardByID(cardId);

        GetCardFormServerByQuestion getCardFormServerByQuestion = new GetCardFormServerByQuestion(context);
        getCardFormServerByQuestion.execute(card);
        getCardFormServerByQuestion.delegate = this;


    }

    private void _addCardToLearn() {
        try {
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
                    //Update Queue_list in system table
                    learnApiImplements._addCardIdToQueueList(card);
//                Snackbar.make(container,
//                        Html.fromHtml(LazzyBeeShare.getTextColor(context.getResources().getColor(R.color.teal_500)
//                                , getString(R.string.message_action_add_card_to_learn_complete, card.getQuestion()))), Snackbar.LENGTH_SHORT)
//                        .show();
                    Toast.makeText(context, getString(R.string.message_action_add_card_to_learn_complete, card.getQuestion()), Toast.LENGTH_SHORT).show();

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
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
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
        try {
            if (card != null) {
                //Update Success reload data
                this.card.setAnswers(card.getAnswers());
                this.card.setL_vn(card.getL_vn());
                this.card.setL_en(card.getL_en());

                //Update Success reload data
                //Set Adapter
                PackageCardPageAdapter packageCardPageAdapter = new PackageCardPageAdapter(context, this.card);
                mViewPager.setAdapter(packageCardPageAdapter);
                mSlidingTabLayout.setViewPager(mViewPager);

                //Update Card form DB
                learnApiImplements._updateCardFormServer(card);

                Toast.makeText(context, getString(R.string.message_update_card_successful), Toast.LENGTH_SHORT).show();

                //set Result code for updated List card
                setResult(getResources().getInteger(R.integer.code_card_details_updated), new Intent(this, this.getIntent().getComponent().getClass()));
            } else {
                Toast.makeText(context, getString(R.string.message_update_card_fails), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    class PackageCardPageAdapter extends PagerAdapter {
        Card card;
        List<String> packages;
        private Context context;

        public PackageCardPageAdapter(Context context, Card card) {
            this.card = card;
            this.context = context;
            packages = Arrays.asList(context.getString(R.string.dictionary_vn_en), context.getString(R.string.dictionary_en_en), context.getString(R.string.dictionary_lazzybee));
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
            _addJavascriptInterface(card);

            try {
                String displayHTML = LazzyBeeShare.EMPTY;
                switch (position) {
                    case 0:
                        //dic VN
                        displayHTML = LazzyBeeShare.getDictionaryHTML(card.getL_vn());
                        break;
                    case 1:
                        //dic ENG
                        displayHTML = LazzyBeeShare.getDictionaryHTML(card.getL_en());
                        break;
                    case 2:
                        //dic Lazzybee
                        displayHTML = LazzyBeeShare.getAnswerHTML(context, card);
                        break;
                }
                Log.i(TAG, "Tab Dic:" + displayHTML);

                mWebViewLeadDetails.loadDataWithBaseURL(LazzyBeeShare.ASSETS, displayHTML, LazzyBeeShare.mime, LazzyBeeShare.encoding, null);
            } catch (Exception e) {
                LazzyBeeShare.showErrorOccurred(context, e);
            }


            // Return the View
            return view;
        }

        private void _addJavascriptInterface(final Card card) {
            String sp = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_SPEECH_RATE);
            float speechRate = 1.0f;
            if (sp != null) {
                speechRate = Float.valueOf(sp);
            }
            //addJavascriptInterface play question
            final float finalSpeechRate = speechRate;
            mWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectQuestion() {
                @JavascriptInterface
                public void playQuestion() {
                    String toSpeak = card.getQuestion();

                    //Speak text
                    LazzyBeeShare._speakText(toSpeak, finalSpeechRate);
                }
            }, "question");
            mWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectExplain() {
                @JavascriptInterface
                public void speechExplain() {
                    //get answer json
                    String answer = card.getAnswers();
                    String toSpeech = LazzyBeeShare._getValueFromKey(answer, "explain");

                    //Speak text
                    LazzyBeeShare._speakText(toSpeech, finalSpeechRate);
                }
            }, "explain");
            mWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectExample() {
                @JavascriptInterface
                public void speechExample() {
                    //get answer json
                    String answer = card.getAnswers();
                    String toSpeech = LazzyBeeShare._getValueFromKey(answer, "example");

                    //Speak text
                    LazzyBeeShare._speakText(toSpeech, finalSpeechRate);
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

    @Override
    protected void onDestroy() {
        //_stopTextToSpeech();
        super.onDestroy();

    }

    private void _trackerApplication() {
        try {
            DataLayer mDataLayer = LazzyBeeSingleton.mDataLayer;
            mDataLayer.pushEvent("openScreen", DataLayer.mapOf("screenName", GA_SCREEN));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

}
