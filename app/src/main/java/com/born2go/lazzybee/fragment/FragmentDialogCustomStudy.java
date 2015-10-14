package com.born2go.lazzybee.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.RecyclerViewCustomStudyAdapter;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;

import java.util.Arrays;
import java.util.List;


public class FragmentDialogCustomStudy extends DialogFragment {

    public static final String TAG = "DialogCustomStudy";
    Context context;
    LearnApiImplements learnApiImplements;

    RecyclerView mRecyclerViewCustomStudy;
    List<String> settings;
    DialogCustomStudyInferface studyInferface;

    public FragmentDialogCustomStudy() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public interface DialogCustomStudyInferface {
        void _finishCustomStudy();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_custom_study, container, false);
        context = getActivity();

        settings = Arrays.asList(context.getResources().getStringArray(R.array.custom_study));

        learnApiImplements = LazzyBeeSingleton.learnApiImplements;
        //getDialog().setTitle(getString(R.string.custom_study));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mRecyclerViewCustomStudy = (RecyclerView) view.findViewById(R.id.mRecyclerViewCustomStudy);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerViewCustomStudy.getContext(), 1);

        mRecyclerViewCustomStudy.setLayoutManager(gridLayoutManager);
        setCustomStudyAdapter();
        return view;
    }

    public void setCustomStudyAdapter() {
        RecyclerViewCustomStudyAdapter recyclerViewCustomStudyAdapter = new RecyclerViewCustomStudyAdapter(context, settings, mRecyclerViewCustomStudy);
        mRecyclerViewCustomStudy.setAdapter(recyclerViewCustomStudyAdapter);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            studyInferface = (DialogCustomStudyInferface) activity;
        } catch (Exception e) {
        }

    }
}
