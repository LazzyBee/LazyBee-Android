package com.born2go.lazzybee.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.born2go.lazzybee.adapter.PackageCardPageFragmentAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDetailsViewListener} interface
 * to handle interaction events.
 */
@SuppressLint("ValidFragment")
public class DetailsView extends Fragment implements GetCardFormServerByQuestion.GetCardFormServerByQuestionResponse {

    private static final String TAG = "DetailsView";
    private OnDetailsViewListener mListener;
    final String tag;
    private Card card;

    View mViewAdv;
    SlidingTabLayout mDetailsSlidingTabLayout;
    WebView mDetailsWebViewLeadDetails;
    ViewPager mDetailsViewPager;

    MenuItem btnBackBeforeCard;
    MenuItem itemIgnore;
    MenuItem itemLearn;

    private final Context context;
    private View viewAdv;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_study_details, container, false);
        String myTag = getTag();
        if (getActivity() != null)
            ((StudyActivity) getActivity()).setDetailViewTag(myTag);
        _defineDetailsView(view);

        viewAdv = createPageSponser(inflater, container);

        return view;
    }

    private View createPageSponser(LayoutInflater inflater, ViewGroup container) {
        View viewPageSponser = inflater.inflate(R.layout.page_sponsor, container, false);
        _initAdView(viewPageSponser, AdSize.MEDIUM_RECTANGLE);
        return viewPageSponser;
    }

    private void _defineDetailsView(View view) {
        try {
            mViewAdv = view.findViewById(R.id.mCardViewAdv);
            _initAdView(mViewAdv, AdSize.BANNER);
            mDetailsViewPager = view.findViewById(R.id.viewpager);
            mDetailsSlidingTabLayout = view.findViewById(R.id.sliding_tabs);
            mDetailsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 2) {
                        mViewAdv.setVisibility(View.GONE);
                    } else {
                        mViewAdv.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            _displayCard(card);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_defineDetailsView", e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setCard(Card card) {
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
            LazzyBeeShare.showErrorOccurred(context, "processFinish", e);
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
            LazzyBeeShare.showErrorOccurred(context, "_displayCard", e);
        }
    }

    class PackageCardPageAdapter extends PagerAdapter {
        final Card card;
        final List<String> packages;
        private final Context context;
        LayoutInflater layoutInflater;

        public PackageCardPageAdapter(Context context, Card card) {
            this.card = card;
            this.context = context;
            packages = Arrays.asList(context.getString(R.string.dictionary_vn_en), context.getString(R.string.dictionary_en_en), "Sponsor");
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
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return object == view;
        }

        @NonNull
        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            // Inflate a new layout from our resources
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = null;
            if (inflater != null) {
                if (position < 2) {
                    view = inflater.inflate(R.layout.page_package_card_item, container, false);
                    // Add the newly created View to the ViewPager
                    mDetailsWebViewLeadDetails = view.findViewById(R.id.mWebViewCardDetails);
                    mDetailsWebViewLeadDetails.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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
                        LazzyBeeShare.showErrorOccurred(context, "instantiateItem", e);
                    }
                } else {
                    view = viewAdv;
                }
            }
            container.addView(view);
            return view;
        }


        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

    }

    private void _initAdView(final View mViewAdv, final AdSize banner) {
        try {
            if (getActivity() != null) {
                LazzyBeeSingleton.getFirebaseRemoteConfig().fetch(LazzyBeeShare.CACHE_EXPIRATION).addOnCompleteListener(getActivity(), task -> {
                    String admob_pub_id = null;//"ca-app-pub-5245864792816840";
                    String adv_banner_id = null;//"7733609014";
                    if (task.isComplete()) {
                        admob_pub_id = LazzyBeeSingleton.getFirebaseRemoteConfig().getString(LazzyBeeShare.ADMOB_PUB_ID);
                        adv_banner_id = LazzyBeeSingleton.getFirebaseRemoteConfig().getString(LazzyBeeShare.ADV_BANNER_ID);
                    }
                    if (admob_pub_id != null) {
                        if (adv_banner_id == null || adv_banner_id.equals(LazzyBeeShare.EMPTY)) {
                            mViewAdv.setVisibility(View.GONE);
                        } else if (!adv_banner_id.equals(LazzyBeeShare.EMPTY)) {
                            String advId = admob_pub_id + "/" + adv_banner_id;
                            Log.i(TAG, "admob -AdUnitId:" + advId);
                            AdView mAdView = new AdView(context);

                            mAdView.setAdSize(banner);
                            mAdView.setAdUnitId(advId);

                            AdRequest adRequest = new AdRequest.Builder()
                                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                    .addTestDevice(getResources().getStringArray(R.array.devices)[0])
                                    .addTestDevice(getResources().getStringArray(R.array.devices)[1])
                                    .addTestDevice(getResources().getStringArray(R.array.devices)[2])
                                    .addTestDevice("467009F00ED542DDA1694F88F807A79A")
                                    .build();

                            mAdView.loadAd(adRequest);
                            mAdView.setAdListener(new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    // Code to be executed when an ad finishes loading.
                                    Log.i(TAG, "Ads " + banner.toString() + ":onAdLoaded");
                                }

                                @Override
                                public void onAdFailedToLoad(int errorCode) {
                                    // Code to be executed when an ad request fails.
                                    Log.i(TAG, "Ads " + banner.toString() + ":onAdFailedToLoad " + errorCode);
                                }

                                @Override
                                public void onAdOpened() {
                                    // Code to be executed when an ad opens an overlay that
                                    // covers the screen.
                                    Log.i(TAG, "Ads " + banner.toString() + ":onAdOpened");
                                }

                                @Override
                                public void onAdLeftApplication() {
                                    // Code to be executed when the user has left the app.
                                    Log.i(TAG, "Ads " + banner.toString() + ":onAdLeftApplication");
                                }

                                @Override
                                public void onAdClosed() {
                                    // Code to be executed when when the user is about to return
                                    // to the app after tapping on an ad.
                                    Log.i(TAG, "Ads " + banner.toString() + ":onAdClosed");
                                }
                            });

                            RelativeLayout relativeLayout = mViewAdv.findViewById(R.id.adView);
                            RelativeLayout.LayoutParams adViewCenter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            adViewCenter.addRule(RelativeLayout.CENTER_IN_PARENT);
                            relativeLayout.addView(mAdView, adViewCenter);

                            mViewAdv.setVisibility(View.VISIBLE);
                        } else {
                            mViewAdv.setVisibility(View.GONE);
                        }
                    } else {
                        mViewAdv.setVisibility(View.GONE);
                    }
                });
            }

        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_initAdView", e);
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
            final String[] base_url_sharing = {LazzyBeeShare.DEFAULTS_BASE_URL_SHARING};
            if (getActivity() != null) {
                LazzyBeeSingleton.getFirebaseRemoteConfig().fetch(LazzyBeeShare.CACHE_EXPIRATION).addOnCompleteListener(getActivity(), task -> {
                    String server_base_url_sharing = null;//"http://www.lazzybee.com/vdict";
                    if (task.isSuccessful()) {
                        server_base_url_sharing = LazzyBeeSingleton.getFirebaseRemoteConfig().getString(LazzyBeeShare.SERVER_BASE_URL_SHARING);
                    }
                    if (server_base_url_sharing != null) {
                        if (server_base_url_sharing.length() > 0)
                            base_url_sharing[0] = server_base_url_sharing;
                    }

                    //define base url with question
                    base_url_sharing[0] = base_url_sharing[0] + card.getQuestion();
                    Log.i(TAG, "Sharing URL:" + base_url_sharing[0]);

                    //Share card
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, base_url_sharing[0]);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                });
            }

        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_shareCard", e);
        }

    }

    private void _reportCard() {
        try {
            startActivity(LazzyBeeShare.getOpenFacebookIntent(context));
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_reportCard", e);
        }
    }

    private void _updateCardFormServer() {
        if (LazzyBeeShare.checkConn(context)) {
            //Call Api Update Card
            GetCardFormServerByQuestion getCardFormServerByQuestion = new GetCardFormServerByQuestion(context, null);
            getCardFormServerByQuestion.execute(card);
            getCardFormServerByQuestion.delegate = this;
        } else {
            Toast.makeText(context, R.string.failed_to_connect_to_server, Toast.LENGTH_SHORT).show();
        }
    }

}
