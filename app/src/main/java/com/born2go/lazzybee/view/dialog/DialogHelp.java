package com.born2go.lazzybee.view.dialog;


import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.shared.LazzyBeeShare;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogHelp extends DialogFragment {


    public static final String TAG = "DialogHelp";

    public DialogHelp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_help, container, false);


        Dialog dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set style
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        WebView mWebViewHelp = (WebView) view.findViewById(R.id.mWebViewHelp);
        FloatingActionButton mFloatClose = (FloatingActionButton) view.findViewById(R.id.mFloatClose);
        ImageView mClose= (ImageView) view.findViewById(R.id.mClose);
        mWebViewHelp.loadUrl(LazzyBeeShare.ASSETS + "lazzybee_guide.htm");

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        mFloatClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }


}
