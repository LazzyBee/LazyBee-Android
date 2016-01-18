package com.born2go.lazzybee.view.dialog;


import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.born2go.lazzybee.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFragmentCustomCourse extends DialogFragment {


    public DialogFragmentCustomCourse() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_fragment_custom_course, container, false);
        getDialog().setTitle(getString(R.string.custom_study));
        return view;
    }


}
