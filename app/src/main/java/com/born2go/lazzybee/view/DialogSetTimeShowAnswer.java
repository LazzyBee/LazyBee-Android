package com.born2go.lazzybee.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.RecyclerViewCustomStudyAdapter;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Hue on 12/22/2015.
 */
public class DialogSetTimeShowAnswer extends DialogFragment {


    public static final String TAG = "SetTimeShowAnswer";
    private Context context;
    private RecyclerViewCustomStudyAdapter adapter;

    public DialogSetTimeShowAnswer(RecyclerViewCustomStudyAdapter adapter, Context context, int timeSet) {
        this.adapter = adapter;
        this.context = context;
        this.timeSet = timeSet;
        Log.d(TAG, "timeSet:" + timeSet);
    }

    private int timeSet;
    private int time = -1;
    private Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_set_time_show_answer, container, false);

        this.dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set style
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        _defineHeaderDialogSetTime(view);
        _defineContainerDialogSetTime(view);
        return view;
    }

    private void _defineHeaderDialogSetTime(View view) {
        ImageView mClose = (ImageView) view.findViewById(R.id.mClose);
        TextView lbSave = (TextView) view.findViewById(R.id.lbSave);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        lbSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "time:" + time);
                LazzyBeeSingleton.learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_TIME_SHOW_ANSWER, String.valueOf(time));
                dialog.dismiss();
                adapter._reloadRecylerView();
            }
        });
    }

    private void _defineContainerDialogSetTime(View view) {
        final List<String> seconds = Arrays.asList(context.getResources().getStringArray(R.array.time_show_answer));
        View mSetTimeNow = view.findViewById(R.id.mSetTimeNow);
        View mSetTime3s = view.findViewById(R.id.mSetTime3s);
        View mSetTime5s = view.findViewById(R.id.mSetTime5s);
        View mSetTime7s = view.findViewById(R.id.mSetTime7s);

        TextView lbSetTimeNow = (TextView) mSetTimeNow.findViewById(R.id.lbSetTime);
        TextView lbSetTime3s = (TextView) mSetTime3s.findViewById(R.id.lbSetTime);
        TextView lbSetTime5s = (TextView) mSetTime5s.findViewById(R.id.lbSetTime);
        TextView lbSetTime7s = (TextView) mSetTime7s.findViewById(R.id.lbSetTime);

        final ImageView imgDoneNow = (ImageView) mSetTimeNow.findViewById(R.id.imgDone);
        final ImageView imgDone3s = (ImageView) mSetTime3s.findViewById(R.id.imgDone);
        final ImageView imgDone5s = (ImageView) mSetTime5s.findViewById(R.id.imgDone);
        final ImageView imgDone7s = (ImageView) mSetTime7s.findViewById(R.id.imgDone);

        lbSetTimeNow.setText(context.getString(R.string.show_answer_now));
        lbSetTime3s.setText(seconds.get(0) + "s");
        lbSetTime5s.setText(seconds.get(1) + "s");
        lbSetTime7s.setText(seconds.get(2) + "s");

        switch (timeSet) {
            case -1:
                imgDoneNow.setVisibility(View.VISIBLE);
                break;
            case 3:
                imgDone3s.setVisibility(View.VISIBLE);
                break;
            case 5:
                imgDone5s.setVisibility(View.VISIBLE);
                break;
            case 7:
                imgDone7s.setVisibility(View.VISIBLE);
                break;
        }


        View.OnClickListener listenerSetTimeNow = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isShow = imgDoneNow.isShown();
                imgDoneNow.setVisibility(isShow ? View.GONE : View.VISIBLE);
                imgDone3s.setVisibility(View.GONE);
                imgDone5s.setVisibility(View.GONE);
                imgDone7s.setVisibility(View.GONE);
            }
        };
        View.OnClickListener listenerSetTime3s = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isShow = imgDone3s.isShown();
                imgDone3s.setVisibility(isShow ? View.GONE : View.VISIBLE);
                imgDoneNow.setVisibility(View.GONE);
                imgDone5s.setVisibility(View.GONE);
                imgDone7s.setVisibility(View.GONE);
                time = 3;
            }
        };
        View.OnClickListener listenerSetTime5s = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isShow = imgDone5s.isShown();
                imgDone5s.setVisibility(isShow ? View.GONE : View.VISIBLE);
                imgDone3s.setVisibility(View.GONE);
                imgDoneNow.setVisibility(View.GONE);
                imgDone7s.setVisibility(View.GONE);
                time = 5;
            }
        };
        View.OnClickListener listenerSetTime7s = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isShow = imgDone7s.isShown();
                imgDone7s.setVisibility(isShow ? View.GONE : View.VISIBLE);
                imgDone3s.setVisibility(View.GONE);
                imgDone5s.setVisibility(View.GONE);
                imgDoneNow.setVisibility(View.GONE);
                time = 7;
            }
        };

        lbSetTimeNow.setOnClickListener(listenerSetTimeNow);
        lbSetTime3s.setOnClickListener(listenerSetTime3s);
        lbSetTime5s.setOnClickListener(listenerSetTime5s);
        lbSetTime7s.setOnClickListener(listenerSetTime7s);

        mSetTimeNow.setOnClickListener(listenerSetTimeNow);
        mSetTime3s.setOnClickListener(listenerSetTime3s);
        mSetTime5s.setOnClickListener(listenerSetTime5s);
        mSetTime7s.setOnClickListener(listenerSetTime7s);
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
