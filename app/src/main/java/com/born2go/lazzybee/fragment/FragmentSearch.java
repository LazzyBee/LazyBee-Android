package com.born2go.lazzybee.fragment;


import android.app.Activity;
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
import com.born2go.lazzybee.adapter.RecyclerViewSearchResultListAdapter;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.event.RecyclerViewTouchListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSearch extends Fragment {


    public static final String QUERY_TEXT = "query";
    public static final String TAG = "FragmentSearch";
    String query;

    public FragmentSearch() {
        // Required empty public constructor
    }

    public interface FragmentSearchListener {
        /**
         * Goto Card Details with card id
         */
        void _gotoCardDetail(String cardId);
    }

    FragmentSearchListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            query = getArguments().getString(QUERY_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        //init DB SQLIte
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity().getApplicationContext());

        //Init RecyclerView and Layout Manager
        RecyclerView mRecyclerViewSearchResults = (RecyclerView) view.findViewById(R.id.mRecyclerViewSearchResults);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerViewSearchResults.getContext(), 1);

        //init LbResult Count
        TextView lbResultCount = (TextView) view.findViewById(R.id.lbResultCount);

        //search data form DB
        List<Card> cardList = dataBaseHelper._searchCard(query);
        int result_count = cardList.size();
        Log.i(TAG, "Search result_count:" + result_count);

        //set count
        lbResultCount.setText(result_count + " " + getString(R.string.result));

        //Check result_count==0 search in server
        if (result_count == 0) {
            //Search in server
        }

        //Init Adapter
        RecyclerViewSearchResultListAdapter recyclerViewReviewTodayListAdapter = new RecyclerViewSearchResultListAdapter(cardList);

        //Init Touch Listener
        RecyclerViewTouchListener recyclerViewTouchListener = new RecyclerViewTouchListener(getActivity(), mRecyclerViewSearchResults, new RecyclerViewTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView lbQuestion = (TextView) view.findViewById(R.id.lbQuestion);
                //Cast tag lbQuestion to CardId
                String cardID = String.valueOf(lbQuestion.getTag());
                Toast.makeText(getActivity(), "Card:" + cardID, Toast.LENGTH_SHORT).show();
                if (mListener != null)
                    mListener._gotoCardDetail(cardID);

            }
        });

        //Set data and add Touch Listener
        mRecyclerViewSearchResults.setLayoutManager(gridLayoutManager);
        mRecyclerViewSearchResults.setAdapter(recyclerViewReviewTodayListAdapter);
        mRecyclerViewSearchResults.addOnItemTouchListener(recyclerViewTouchListener);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (FragmentSearchListener) activity;
    }
}
