package com.born2go.lazzybee.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.activity.StudyActivity;
import com.born2go.lazzybee.adapter.GetCardFormServerByQuestion;
import com.born2go.lazzybee.db.Card;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDetailsViewListener} interface
 * to handle interaction events.
 */
public class DetailsView extends Fragment implements GetCardFormServerByQuestion.GetCardFormServerByQuestionResponse {

    private static final String TAG = "DetailsView";
    private OnDetailsViewListener mListener;
    String tag;
    private Card card;

    CardView mDetailsCardViewAdv;
    SlidingTabLayout mDetailsSlidingTabLayout;
    WebView mDetailsWebViewLeadDetails;
    ViewPager mDetailsViewPager;

    MenuItem btnBackBeforeCard;
    MenuItem itemIgnore;
    MenuItem itemLearn;

    private Context context;


    public DetailsView(Context context, String tag) {
        // Required empty public constructor
        this.tag = tag;
        this.context = context;
    }

    public static DetailsView newInstance(Context context, String tag) {
        Bundle args = new Bundle();
        DetailsView fragment = new DetailsView(context, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_study_details, container, false);
        String myTag = getTag();
        ((StudyActivity) getActivity()).setDetailViewTag(myTag);
        _defineDetailsView(view);
        return view;
    }

    private void _defineDetailsView(View view) {
        try {
//             mDetailsCardViewViewPager = (CardView) view.findViewById(R.id.mCardViewViewPager);
            mDetailsCardViewAdv = (CardView) view.findViewById(R.id.mCardViewAdv);
            mDetailsViewPager = (ViewPager) view.findViewById(R.id.viewpager);
            mDetailsSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
            _displayCard(card);
            _initAdView(mDetailsCardViewAdv);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        if (activity instanceof OnDetailsViewListener) {
//            mListener = (OnDetailsViewListener) activity;
//        } else {
//            throw new RuntimeException(activity.toString()
//                    + " must implement OnDetailsViewListener");
//        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setCard(Card card) {
        // Log.d("DetailsView:", "dsadadad:" + card.toString());
        this.card = card;
        _displayCard(card);
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
                mDetailsViewPager.setAdapter(packageCardPageAdapter);
                mDetailsSlidingTabLayout.setViewPager(mDetailsViewPager);

                //Update Card form DB
                LazzyBeeSingleton.learnApiImplements._updateCardFormServer(card);

                Toast.makeText(context, getString(R.string.message_update_card_successful), Toast.LENGTH_SHORT).show();

                //set Result code for updated List card
            } else {
                Toast.makeText(context, getString(R.string.message_update_card_fails), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDetailsViewListener {
    }

    private void _displayCard(Card card) {
        try {
            if (card != null) {
                PackageCardPageAdapter packageCardPageAdapter = new PackageCardPageAdapter(context, card);
                mDetailsViewPager.setAdapter(packageCardPageAdapter);
                mDetailsSlidingTabLayout.setViewPager(mDetailsViewPager);
            } else {
                Log.d(TAG, "Send card null");
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
                        if (card.getL_vn() != null) {
                            displayHTML = LazzyBeeShare.getDictionaryHTML(card.getL_vn());
                        }
                        break;
                    case 1:
                        //dic ENG
                        if (card.getL_en() != null) {
                            displayHTML = LazzyBeeShare.getDictionaryHTML(card.getL_en());
                        }
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

    private void _initAdView(CardView mDetailsCardViewAdv) {
        try {
            //get value form task manager
            Container container = ContainerHolderSingleton.getContainerHolder().getContainer();
            String admob_pub_id = null;
            String adv_dictionary_id = null;
            if (container == null) {
            } else {
                admob_pub_id = container.getString(LazzyBeeShare.ADMOB_PUB_ID);
                adv_dictionary_id = container.getString(LazzyBeeShare.ADV_DICTIONARY_ID);
                Log.i(TAG, "admob -admob_pub_id:" + admob_pub_id);
                Log.i(TAG, "admob -adv_dictionary_id:" + adv_dictionary_id);
            }
            CardView mCardViewAdv = mDetailsCardViewAdv;
            if (admob_pub_id != null || adv_dictionary_id != null) {
                String advId = admob_pub_id + "/" + adv_dictionary_id;
                Log.i(TAG, "admob -AdUnitId:" + advId);
                AdView mAdView = new AdView(context);

                mAdView.setAdSize(AdSize.BANNER);
                mAdView.setAdUnitId(advId);

                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice(getResources().getStringArray(R.array.devices)[0])
                        .addTestDevice(getResources().getStringArray(R.array.devices)[1])
                        .addTestDevice(getResources().getStringArray(R.array.devices)[2])
                        .addTestDevice(getResources().getStringArray(R.array.devices)[3])
                        .build();

                mAdView.loadAd(adRequest);

                RelativeLayout relativeLayout = ((RelativeLayout) mDetailsCardViewAdv.findViewById(R.id.adView));
                RelativeLayout.LayoutParams adViewCenter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                adViewCenter.addRule(RelativeLayout.CENTER_IN_PARENT);
                relativeLayout.addView(mAdView, adViewCenter);

                mCardViewAdv.setVisibility(View.VISIBLE);
            } else {
                mCardViewAdv.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        _initMenuItem(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void _initMenuItem(Menu menu) {
        btnBackBeforeCard = menu.findItem(R.id.action_back_before_card);
        btnBackBeforeCard.setVisible(false);
        itemIgnore = menu.findItem(R.id.action_ignore);
        itemLearn = menu.findItem(R.id.action_learnt);
        MenuItem itemDictionary = menu.findItem(R.id.action_goto_dictionary);
        itemDictionary.setVisible(false);
        btnBackBeforeCard.setVisible(false);
        itemIgnore.setVisible(false);
        itemLearn.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_learnt:
                return true;
            case R.id.action_ignore:
                return true;
            case R.id.action_update:
                //define function update card form server
                _updateCardFormServer();
                return true;
            case R.id.action_back_before_card:
                return true;
            case R.id.action_share:
                _shareCard();
                return true;
            case R.id.action_goto_dictionary:
                //_gotoDictionnary();
                return true;
            case R.id.action_report:
                _reportCard();
                return true;
        }

        return false;
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

    private void _updateCardFormServer() {
        //Call Api Update Card
        GetCardFormServerByQuestion getCardFormServerByQuestion = new GetCardFormServerByQuestion(context);
        getCardFormServerByQuestion.execute(card);
        getCardFormServerByQuestion.delegate = this;
    }
}
