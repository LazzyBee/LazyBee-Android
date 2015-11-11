package com.born2go.lazzybee.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.RecyclerViewReviewTodayListAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentReviewToday extends Fragment {


    public static final String TAG = "FragmentReviewToday";
    public static final String COURSE_ID = "courseId";
    private Context context;

    public FragmentReviewToday() {
        // Required empty public constructor
    }

    public interface FragmentReviewTodayListener {
        void gotoCardDetails(String cardId);
    }

    FragmentReviewTodayListener fragmentReviewTodayListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_today, container, false);

        //
        context = getActivity();

        //init DB SQLIte
        final LearnApiImplements dataBaseHelper = LazzyBeeSingleton.learnApiImplements;

        //Init RecyclerView and Layout Manager
        final RecyclerView mRecyclerViewReviewTodayList = (RecyclerView) view.findViewById(R.id.mRecyclerViewReviewTodayList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerViewReviewTodayList.getContext(), 1);
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        final TextView lbCountReviewCard = (TextView) view.findViewById(R.id.lbCountReviewCard);
        try {
            //get review List Card today
            final List<Card> vocabularies = dataBaseHelper._getIncomingListCard();

            lbCountReviewCard.setText(getString(R.string.message_total_card_incoming) + vocabularies.size());
            lbCountReviewCard.setTag(vocabularies.size());
            //Init Adapter
            final RecyclerViewReviewTodayListAdapter recyclerViewReviewTodayListAdapter = new RecyclerViewReviewTodayListAdapter(context, mRecyclerViewReviewTodayList, vocabularies, lbCountReviewCard);

            //Set data and add Touch Listener
            mRecyclerViewReviewTodayList.setLayoutManager(gridLayoutManager);
            mRecyclerViewReviewTodayList.setAdapter(recyclerViewReviewTodayListAdapter);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Refresh items
                    int count = (int) lbCountReviewCard.getTag();
                    if (count < 100) {
                        //conver card to cardID
                        List<String> cardIds = dataBaseHelper._converlistCardToListCardId(recyclerViewReviewTodayListAdapter.getVocabularies());

                        //limit
                        int limit = 100 - recyclerViewReviewTodayListAdapter.getVocabularies().size();

                        //fill up incoming list
                        dataBaseHelper.initIncomingListwithLimit(cardIds, limit);

                        //get new IncomingList
                        List<Card> fillUpCards = dataBaseHelper._getIncomingListCard();

                        //set count
                        lbCountReviewCard.setText(getString(R.string.message_total_card_incoming) + fillUpCards.size());
                        lbCountReviewCard.setTag(fillUpCards.size());

                        //Reset adapter incoming list
                        RecyclerViewReviewTodayListAdapter fillUpnewincomingAdapter = new RecyclerViewReviewTodayListAdapter(context, mRecyclerViewReviewTodayList, fillUpCards, lbCountReviewCard);
                        mRecyclerViewReviewTodayList.setAdapter(fillUpnewincomingAdapter);
                        mRecyclerViewReviewTodayList.getAdapter().notifyItemRangeChanged(0, fillUpnewincomingAdapter.getItemCount());
                    }

                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
        return view;
    }


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        fragmentReviewTodayListener = (FragmentReviewTodayListener) context;
    }
}
