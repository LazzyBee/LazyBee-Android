package com.born2go.lazzybee.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.algorithms.CardSched;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.ContainerHolderSingleton;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.view.SlidingTabLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tagmanager.Container;

import java.util.Arrays;
import java.util.List;

import static com.born2go.lazzybee.db.Card.QUEUE_NEW_CRAM0;

/**
 * Created by Hue on 12/2/2015.
 */
public class StudySwipePage extends PagerAdapter {
    private static final String TAG = "StudySwipePage";
    private Context context;
    Card currentCard;
    //CardView mDetailsCardViewViewPager;
    CardView mDetailsCardViewAdv;
    LearnApiImplements learnApiImplements;
    SlidingTabLayout mDetailsSlidingTabLayout;
    WebView mDetailsWebViewLeadDetails;
    ViewPager mDetailsViewPager;
    private int type;
    private int queue;
    private int[] counts;
    CardSched cardSched;

    boolean answerDisplay = false;

    public StudySwipePage(Context context, Card card, int queue, int type, int[] counts) {
        learnApiImplements = LazzyBeeSingleton.learnApiImplements;
        cardSched = new CardSched();
        this.context = context;
        this.currentCard = card;
        this.type = type;
        this.queue = queue;
        this.counts = counts;
    }

    @Override
    public int getCount() {
        return (type == 0) ? 1 : 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Inflate a new layout from our resources
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        if (position == 0) {
            view = inflater.inflate(R.layout.view_study_main, container, false);
            _defineStudyView(view);
        } else {
            view = inflater.inflate(R.layout.view_study_details, container, false);
            _defineDetailsView(view);
        }
        container.addView(view);
        return view;
    }

    private void _defineStudyView(View view) {
        try {
            _defineStudyCount(view);
            _defineStudyWebView(view);
            _defineStudyButton(view);

            FloatingActionButton mFloatActionButtonUserNote = (FloatingActionButton) view.findViewById(R.id.mFloatActionButtonUserNote);

            mFloatActionButtonUserNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _showCardNote(currentCard);
                }
            });
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }

    }

    private void _defineStudyWebView(View view) {
        WebView mStudyWebView = (WebView) view.findViewById(R.id.mWebViewLeadDetaisl);
        WebSettings ws = mStudyWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        _addJavascriptInterface(mStudyWebView, currentCard);
        String display = (type == 0) ? LazzyBeeShare._getQuestionDisplay(context, currentCard.getQuestion()) : LazzyBeeShare.getAnswerHTML(context, currentCard);
        mStudyWebView.loadDataWithBaseURL(LazzyBeeShare.ASSETS, display, LazzyBeeShare.mime, LazzyBeeShare.encoding, null);
    }

    private void _defineStudyCount(View view) {
        Log.d(TAG, "queue:" + queue);
        TextView lbCountNew = (TextView) view.findViewById(R.id.lbCountTotalVocabulary);
        TextView lbCountAgain = (TextView) view.findViewById(R.id.lbCountAgainInday);
        TextView lbCountDue = (TextView) view.findViewById(R.id.lbAgainDue);
        //set again count
        lbCountAgain.setText(context.getString(R.string.study_again) + ": " + String.valueOf(counts[1]));
        lbCountAgain.setTag(counts[1]);
        //set new Count
        lbCountNew.setText(context.getString(R.string.study_new) + ": " + String.valueOf(counts[0]));
        lbCountNew.setTag(counts[0]);
        //set Due Count
        lbCountDue.setText(context.getString(R.string.study_review) + ": " + String.valueOf(counts[2]));
        lbCountDue.setTag(counts[2]);
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
        } else {

        }
    }

    private void _defineStudyButton(View view) {
        final RelativeLayout mShowAnswer = (RelativeLayout) view.findViewById(R.id.mShowAnswer);

        final TextView btnShowAnswer = (TextView) view.findViewById(R.id.btnShowAnswer);
        final LinearLayout mLayoutButton = (LinearLayout) view.findViewById(R.id.mLayoutButton);

        TextView btnAgain0 = (TextView) view.findViewById(R.id.btnAgain0);
        TextView btnHard1 = (TextView) view.findViewById(R.id.btnHard1);
        TextView btnGood2 = (TextView) view.findViewById(R.id.btnGood2);
        TextView btnEasy3 = (TextView) view.findViewById(R.id.btnEasy3);
        if (type == 0) {

        } else if (type == 1) {
            mShowAnswer.setVisibility(View.GONE);
            btnShowAnswer.setVisibility(View.GONE);
            mLayoutButton.setVisibility(View.VISIBLE);
            String[] ivlStrList = cardSched.nextIvlStrLst(currentCard, context);
            String text_btnAgain = LazzyBeeShare.getHTMLButtonAnswer(context, ivlStrList[Card.EASE_AGAIN], context.getString(R.string.EASE_AGAIN), R.color.color_level_btn_answer);
            String text_btnHard1 = LazzyBeeShare.getHTMLButtonAnswer(context, ivlStrList[Card.EASE_HARD], context.getString(R.string.EASE_HARD), R.color.color_level_btn_answer);

            String text_btnGood2 = LazzyBeeShare.getHTMLButtonAnswer(context, ivlStrList[Card.EASE_GOOD], context.getString(R.string.EASE_GOOD), (currentCard.getQueue() == Card.QUEUE_LNR1) ? R.color.color_level_btn_answer_disable : R.color.color_level_btn_answer);
            String text_btnEasy3 = LazzyBeeShare.getHTMLButtonAnswer(context, ivlStrList[Card.EASE_EASY], context.getString(R.string.EASE_EASY), (currentCard.getQueue() == Card.QUEUE_LNR1) ? R.color.color_level_btn_answer_disable : R.color.color_level_btn_answer);
            //set text btn
            btnAgain0.setText(Html.fromHtml(text_btnAgain));
            btnHard1.setText(Html.fromHtml(text_btnHard1));
            btnGood2.setText(Html.fromHtml(text_btnGood2));
            btnEasy3.setText(Html.fromHtml(text_btnEasy3));


            btnAgain0.setTag(ivlStrList[Card.EASE_AGAIN]);
            btnHard1.setTag(ivlStrList[Card.EASE_HARD]);
            btnGood2.setTag(ivlStrList[Card.EASE_GOOD]);
            btnEasy3.setTag(ivlStrList[Card.EASE_EASY]);
        }
    }



    private void _defineDetailsView(View view) {
        try {
            // mDetailsCardViewViewPager = (CardView) view.findViewById(R.id.mCardViewViewPager);
            mDetailsCardViewAdv = (CardView) view.findViewById(R.id.mCardViewAdv);
            mDetailsViewPager = (ViewPager) view.findViewById(R.id.viewpager);
            mDetailsSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
            _displayCard(currentCard);
            _initAdView(mDetailsCardViewAdv);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    private void _initAdView(CardView mDetailsCardViewAdv) {
        try {
            String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.i(TAG, "My android_id:" + android_id);

            //get value form task manager
            Container container = ContainerHolderSingleton.getContainerHolder().getContainer();
            String adb_ennable;
            String admob_pub_id = LazzyBeeShare.EMPTY;
            String adv_dictionary_id = LazzyBeeShare.EMPTY;
            if (container == null) {
                adb_ennable = LazzyBeeShare.NO;
            } else {
                adb_ennable = container.getString(LazzyBeeShare.ADV_ENABLE);
                admob_pub_id = container.getString(LazzyBeeShare.ADMOB_PUB_ID);
                adv_dictionary_id = container.getString(LazzyBeeShare.ADV_DICTIONARY_ID);

            }
            String advId = admob_pub_id + "/" + adv_dictionary_id;
            if (admob_pub_id == null || adv_dictionary_id == null) {
                advId = context.getString(R.string.banner_ad_unit_id);
            }

            Log.d(TAG, "AdUnitId:" + admob_pub_id + "/" + adv_dictionary_id);
            if (adb_ennable.equals(LazzyBeeShare.YES)) {

                AdView mAdView = new AdView(context);
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(context.getResources().getStringArray(R.array.devices)[0])
                        .addTestDevice(context.getResources().getStringArray(R.array.devices)[1])
                        .addTestDevice(context.getResources().getStringArray(R.array.devices)[2])
                        .build();
                mAdView.setAdSize(AdSize.BANNER);
                mAdView.setAdUnitId(advId);
                mAdView.loadAd(adRequest);

                RelativeLayout relativeLayout = ((RelativeLayout) mDetailsCardViewAdv.findViewById(R.id.adView));
                RelativeLayout.LayoutParams adViewCenter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                adViewCenter.addRule(RelativeLayout.CENTER_IN_PARENT);
                relativeLayout.addView(mAdView, adViewCenter);

                mDetailsCardViewAdv.setVisibility(View.VISIBLE);

            } else {
                mDetailsCardViewAdv.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    private void _displayCard(Card card) {
        try {
            PackageCardPageAdapter packageCardPageAdapter = new PackageCardPageAdapter(context, card);
            mDetailsViewPager.setAdapter(packageCardPageAdapter);
            mDetailsSlidingTabLayout.setViewPager(mDetailsViewPager);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    /**
     * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
     * {@link View}.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    class PackageCardPageAdapter extends PagerAdapter {
        Card card;
        List<String> packages;
        private Context context;

        public PackageCardPageAdapter(Context context, Card card) {
            this.card = card;
            this.context = context;
            packages = Arrays.asList(context.getString(R.string.dictionary_vn_en), context.getString(R.string.dictionary_en_en));
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return packages.get(position);
        }

        @Override
        public int getCount() {
            return packages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.page_package_card_item, container, false);

            // Add the newly created View to the ViewPager
            container.addView(view);
            //
            mDetailsWebViewLeadDetails = (WebView) view.findViewById(R.id.mWebViewCardDetails);
            WebSettings ws = mDetailsWebViewLeadDetails.getSettings();
            ws.setJavaScriptEnabled(true);
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
                }
                //Log.i(TAG, "Tab Dic:" + displayHTML.);

                mDetailsWebViewLeadDetails.loadDataWithBaseURL(LazzyBeeShare.ASSETS, displayHTML, LazzyBeeShare.mime, LazzyBeeShare.encoding, null);
            } catch (Exception e) {
                LazzyBeeShare.showErrorOccurred(context, e);
            }


            // Return the View
            return view;
        }


        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    private void _addJavascriptInterface(WebView mDetailsWebViewLeadDetails, final Card card) {
        String sp = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_SPEECH_RATE);
        float speechRate = 1.0f;
        if (sp != null) {
            speechRate = Float.valueOf(sp);
        }
        //addJavascriptInterface play question
        final float finalSpeechRate = speechRate;
        mDetailsWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectQuestion() {
            @JavascriptInterface
            public void playQuestion() {
                String toSpeak = card.getQuestion();

                //Speak text
                LazzyBeeShare._speakText(toSpeak, finalSpeechRate);
            }
        }, "question");
        mDetailsWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectExplain() {
            @JavascriptInterface
            public void speechExplain() {
                //get answer json
                String answer = card.getAnswers();
                String toSpeech = LazzyBeeShare._getValueFromKey(answer, "explain");

                //Speak text
                LazzyBeeShare._speakText(toSpeech, finalSpeechRate);
            }
        }, "explain");
        mDetailsWebViewLeadDetails.addJavascriptInterface(new LazzyBeeShare.JsObjectExample() {
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

    private void _showCardNote(final Card currentCard) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

        View viewDialog = View.inflate(context, R.layout.view_dialog_user_note, null);
        final EditText txtUserNote = (EditText) viewDialog.findViewById(R.id.txtUserNote);

        txtUserNote.setText(currentCard.getUser_note());

        builder.setView(viewDialog);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String user_note = txtUserNote.getText().toString();
                if (!user_note.isEmpty()) {
                    currentCard.setUser_note(user_note);
                    learnApiImplements._updateUserNoteCard(currentCard);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // Get the AlertDialog from create()
        final AlertDialog dialog = builder.create();

        dialog.show();

    }

}
