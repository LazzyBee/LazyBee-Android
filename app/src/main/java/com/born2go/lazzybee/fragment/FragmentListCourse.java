package com.born2go.lazzybee.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.RecyclerViewListCourseAdapter;
import com.born2go.lazzybee.db.Course;
import com.born2go.lazzybee.event.RecyclerViewTouchListener;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentListCourse extends Fragment {


    public static final String TAG = "FragmentListCourse";

    public FragmentListCourse() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_course, container, false);
        //init mRecyclerViewListCourse
        RecyclerView mRecyclerViewListCourse = (RecyclerView) view.findViewById(R.id.mRecyclerViewListCourse);

        //init Grid layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerViewListCourse.getContext(), 1);

        //init list course
        List<Course> cursorList = LazzyBeeShare.initListCourse();

        //init adpter
        RecyclerViewListCourseAdapter recyclerViewListCourseAdapter = new RecyclerViewListCourseAdapter(cursorList);

        //init touch  listener
        RecyclerViewTouchListener recyclerViewTouchListener = new RecyclerViewTouchListener(getActivity().getApplicationContext(), mRecyclerViewListCourse, new RecyclerViewTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), "show comfinm add course", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongPress(View childView, int position) {

            }
        });

        //set up mRecyclerViewListCourse+
        mRecyclerViewListCourse.setLayoutManager(gridLayoutManager);
        mRecyclerViewListCourse.setAdapter(recyclerViewListCourseAdapter);
        mRecyclerViewListCourse.addOnItemTouchListener(recyclerViewTouchListener);
        return view;
    }


}
