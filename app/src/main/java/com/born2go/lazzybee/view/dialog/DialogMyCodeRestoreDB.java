package com.born2go.lazzybee.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.born2go.lazzybee.R;

/**
 * Created by Hue on 1/7/2016.
 */
@SuppressLint("ValidFragment")
public class DialogMyCodeRestoreDB extends DialogFragment {


    public static final String TAG = "DialogMyCodeRestoreDB";
    private final String code;

    public DialogMyCodeRestoreDB(String code) {
        this.code = code;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_my_restore_code_db, container, false);

        Dialog dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set style
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        ImageView mClose = (ImageView) view.findViewById(R.id.mClose);
        TextView lbMyRestoreCode = (TextView) view.findViewById(R.id.lbMyRestoreCode);
        lbMyRestoreCode.setText(code);
        View.OnClickListener closeDialog = v -> getDialog().dismiss();
        mClose.setOnClickListener(closeDialog);
        view.findViewById(R.id.btnClose).setOnClickListener(closeDialog);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (d.getWindow() != null) {
                d.getWindow().setLayout(width, height);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }
}
