package com.born2go.lazzybee.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.RecyclerViewReviewTodayListAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.event.RecyclerViewTouchListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentReviewToday extends Fragment {


    public static final String TAG = "FragmentReviewToday";
    public static final String COURSE_ID = "courseId";

    public FragmentReviewToday() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_today, container, false);

        //init DB SQLIte
        LearnApiImplements dataBaseHelper = new LearnApiImplements(getActivity().getApplicationContext());

        //Init RecyclerView and Layout Manager
        RecyclerView mRecyclerViewReviewTodayList = (RecyclerView) view.findViewById(R.id.mRecyclerViewReviewTodayList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerViewReviewTodayList.getContext(), 1);

        //get review List Card today
        List<Card> vocabularies = dataBaseHelper._getReviewListCard();

        //Init Adapter
        RecyclerViewReviewTodayListAdapter recyclerViewReviewTodayListAdapter = new RecyclerViewReviewTodayListAdapter(vocabularies);

        //Init Touch Listener
        RecyclerViewTouchListener recyclerViewTouchListener = new RecyclerViewTouchListener(getActivity(), mRecyclerViewReviewTodayList, new RecyclerViewTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });

        //Set data and add Touch Listener
        mRecyclerViewReviewTodayList.setLayoutManager(gridLayoutManager);
        mRecyclerViewReviewTodayList.setAdapter(recyclerViewReviewTodayListAdapter);
        mRecyclerViewReviewTodayList.addOnItemTouchListener(recyclerViewTouchListener);
        return view;
    }


}
