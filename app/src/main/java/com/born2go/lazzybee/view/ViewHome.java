package com.born2go.lazzybee.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.born2go.lazzybee.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewHome extends Fragment {


    public ViewHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_home, container, false);
    }

}
