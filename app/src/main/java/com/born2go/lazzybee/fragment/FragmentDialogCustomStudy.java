package com.born2go.lazzybee.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.RecyclerViewCustomStudyAdapter;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.Arrays;
import java.util.List;


public class FragmentDialogCustomStudy extends DialogFragment {

    public static final String TAG = "DialogCustomStudy";
    Context context;
    LearnApiImplements learnApiImplements;

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

    RecyclerView mRecyclerViewCustomStudy;
    DialogCustomStudyInferface studyInferface;
    List<String> settings;

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
        RecyclerViewCustomStudyAdapter recyclerViewCustomStudyAdapter = new RecyclerViewCustomStudyAdapter(context, settings, getDialog(), studyInferface);
        mRecyclerViewCustomStudy.setAdapter(recyclerViewCustomStudyAdapter);

    }

    private void getSettingLimitOrUpdate(View mCardView, final TextView lbLimit, final String key, String limit) {
        // lbLimit.setVisibility(View.VISIBLE);
        int value = 0;
        if (limit == null) {
            if (key.equals(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT)) {
                value = LazzyBeeShare.DEFAULT_MAX_NEW_LEARN_PER_DAY;
            } else if (key.equals(LazzyBeeShare.KEY_SETTING_TODAY_REVIEW_CARD_LIMIT)) {
                value = LazzyBeeShare.MAX_REVIEW_LEARN_PER_DAY;
            } else if (key.equals(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT)) {
                value = LazzyBeeShare.DEFAULT_TOTAL_LEAN_PER_DAY;
            } else if (key.equals(LazzyBeeShare.KEY_SETTING_TODAY_LEARN_MORE_PER_DAY_LIMIT)) {
                value = LazzyBeeShare.DEFAULT_MAX_LEARN_MORE_PER_DAY;
            }

        } else {
            value = Integer.valueOf(limit);
        }
        lbLimit.setText(context.getString(R.string.setting_limit_card_number, value));
        final int finalValue = value;
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _showDialogConfirmSetLimitCard(key, finalValue);
            }
        });

    }

    private void _showDialogConfirmSetLimitCard(final String key, final int value) {
        // Instantiate an AlertDialog.Builder with its constructor
        String title = LazzyBeeShare.EMPTY;
        String message = LazzyBeeShare.EMPTY;
        View viewDialog = View.inflate(context, R.layout.dialog_limit_card, null);
        TextView lbSettingLimitName = (TextView) viewDialog.findViewById(R.id.lbSettingLimitName);
        final EditText txtLimit = (EditText) viewDialog.findViewById(R.id.txtLimit);

        if (key == LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT) {
            message = context.getString(R.string.dialog_message_setting_today_new_card_limit_by);
        } else if (key == LazzyBeeShare.KEY_SETTING_TODAY_REVIEW_CARD_LIMIT) {
            message = context.getString(R.string.dialog_message_setting_today_review_card_limit_by);
        } else if (key == LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT) {
            message = context.getString(R.string.dialog_message_setting_total_card_learn_pre_day_by);
        } else if (key == LazzyBeeShare.KEY_SETTING_TODAY_LEARN_MORE_PER_DAY_LIMIT) {
            message = context.getString(R.string.dialog_message_setting_max_learn_more_per_day_by);
        }

        lbSettingLimitName.setText(message);
        txtLimit.setText(String.valueOf(value));

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

        // Chain together various setter methods to set the dialog characteristics
        // builder.setMessage(R.string.dialog_message_setting_today_new_card_limit_by);
        builder.setView(viewDialog);

        // Add the buttons
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
                getDialog().hide();
            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String limit = txtLimit.getText().toString();
                learnApiImplements._insertOrUpdateToSystemTable(key, limit);
                getDialog().hide();
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        studyInferface = (DialogCustomStudyInferface) activity;

    }
}
