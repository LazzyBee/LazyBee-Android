package com.born2go.lazzybee.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.RecyclerViewReviewTodayListAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.event.RecyclerViewTouchListener;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;

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
        LearnApiImplements dataBaseHelper = LazzyBeeSingleton.learnApiImplements;

        //Init RecyclerView and Layout Manager
        RecyclerView mRecyclerViewReviewTodayList = (RecyclerView) view.findViewById(R.id.mRecyclerViewReviewTodayList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerViewReviewTodayList.getContext(), 1);

        TextView lbCountReviewCard = (TextView) view.findViewById(R.id.lbCountReviewCard);
        try {
            //get review List Card today
            final List<Card> vocabularies = dataBaseHelper._getReviewListCard();

            lbCountReviewCard.setText(getString(R.string.message_total_card_review) + vocabularies.size());
            //Init Adapter
            RecyclerViewReviewTodayListAdapter recyclerViewReviewTodayListAdapter = new RecyclerViewReviewTodayListAdapter(context, vocabularies);

            //Init Touch Listener
            RecyclerViewTouchListener recyclerViewTouchListener = new RecyclerViewTouchListener(getActivity(), mRecyclerViewReviewTodayList, new RecyclerViewTouchListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    try {
                        if (fragmentReviewTodayListener != null) {
                            String cardId = String.valueOf(vocabularies.get(position).getId());
                            Log.i(TAG, "CardId=" + cardId);
                            fragmentReviewTodayListener.gotoCardDetails(cardId);
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, context.getString(R.string.an_error_occurred), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, context.getString(R.string.an_error_occurred) + ":" + e.getMessage());
                    }
                }

                @Override
                public void onItemLongPress(View childView, int position) {

                }
            });

            //Set data and add Touch Listener
            mRecyclerViewReviewTodayList.setLayoutManager(gridLayoutManager);
            mRecyclerViewReviewTodayList.setAdapter(recyclerViewReviewTodayListAdapter);
            mRecyclerViewReviewTodayList.addOnItemTouchListener(recyclerViewTouchListener);
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.an_error_occurred), Toast.LENGTH_SHORT).show();
            Log.e(TAG, context.getString(R.string.an_error_occurred) + ":" + e.getMessage());
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentReviewTodayListener = (FragmentReviewTodayListener) context;
    }
}
