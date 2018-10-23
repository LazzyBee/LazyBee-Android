package com.born2go.lazzybee.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.shared.LazzyBeeShare;

/**
 * Created by Hue on 5/22/2016.
 */
@SuppressLint("ValidFragment")
public class DialogFirstShowAnswer extends DialogFragment {
    private final Context context;

    public DialogFirstShowAnswer(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_tip_first_show_answer, container, false);
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setDimAmount(0);
            }

        }
        _handlerAction(view);
        _handlerChangeBackgroundTargerTip(view);
        return view;
    }

    private void _handlerAction(View view) {
        view.findViewById(R.id.btnGotIt).setOnClickListener(v -> {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPreferences.edit().putBoolean(LazzyBeeShare.FIRST_TIME_SHOW_ANSWER, true).apply();
            dismiss();
        });
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());

    }

    private void _handlerChangeBackgroundTargerTip(final View view) {
        AnimationDrawable drawable = (AnimationDrawable) LazzyBeeShare.getDraweble(context, R.drawable.ani_bg_tip_show_answer_button);
        LinearLayout mTarget = view.findViewById(R.id.mTarget);
        mTarget.setBackground(drawable);
        drawable.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (dialog.getWindow() != null)
                dialog.getWindow().setLayout(width, height);
        }
    }
}
