package com.born2go.lazzybee.view.dialog;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

@SuppressLint("ValidFragment")
public class DialogCompleteStudy extends DialogFragment {

    private static final String TAG = "DialogCompleteStudy";
    private Context context;

    public DialogCompleteStudy(Context context) {
        this.context = context;
    }

    public interface ICompleteSutdy {
        void close();
    }

    ICompleteSutdy iCompleteSutdy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_complete_study, container, false);
        Dialog dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set style
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        try {
            int count = LazzyBeeSingleton.learnApiImplements._getCountStreak();
            _initStreakCount(view, count);
            _initStreakDays(view, inflater, count);
            _handlerCompleteStudyLinkClick(view);
            //Play media
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.magic);
            mediaPlayer.start();


        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "onCreateView", e);
        }
        Button btnDone = (Button) view.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
                if (iCompleteSutdy != null) {
                    iCompleteSutdy.close();
                } else {
                    if (getActivity() != null) {
                        getActivity().setResult(LazzyBeeShare.CODE_COMPLETE_STUDY_1000);
                        getActivity().finish();
                    }
                }


            }
        });

        return view;
    }

    private void _handlerCompleteStudyLinkClick(View view) {
        view.findViewById(R.id.lbCompleteLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getString(R.string.complele_text_display_link);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void _initStreakCount(View view, int count) {
        //Define view
        View mCount = view.findViewById(R.id.mCount);
        TextView lbCountStreak = (TextView) mCount.findViewById(R.id.lbCountStreak);
        ImageView streak_ring = (ImageView) mCount.findViewById(R.id.streak_ring);
        //
        lbCountStreak.setText(String.valueOf(count + " " + getString(R.string.streak_day)));

        //set animation
        Animation a = AnimationUtils.loadAnimation(context, R.anim.scale_indefinitely);
        a.setDuration(1000);
        streak_ring.startAnimation(a);


    }

    private void _initStreakDays(View view, LayoutInflater inflater, int countStreak) {
        LinearLayout mDays = (LinearLayout) view.findViewById(R.id.mDays);

        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        ArrayList<String> weeks = _defineWeekbyDayOfWeek(dayOfWeek);
        int startofDay = (int) (LazzyBeeShare.getStartOfDayInMillis() / 1000);
        Log.d(TAG, "Count of streak:" + countStreak);
        Log.d(TAG, "Start of the day:" + startofDay);
        ArrayList<Boolean> showDays = new ArrayList<Boolean>();

        String showDay = LazzyBeeShare.EMPTY;
        if (countStreak > 7) {
            //full day learn
            showDays.add(true);
            showDays.add(true);
            showDays.add(true);
            showDays.add(true);
            showDays.add(true);
            showDays.add(true);
            showDays.add(true);
        } else {
            showDays.clear();
            for (int i = 6; i >= 1; i--) {
                int day = startofDay - (LazzyBeeShare.SECONDS_PERDAY * i);
                String streakDayCount = "SELECT Count(day) FROM streak where day = " + day;
                if (LazzyBeeSingleton.learnApiImplements._queryCount(streakDayCount) == 1) {
                    showDays.add(true);
                    showDay += true + "\t";
                } else {
                    showDays.add(false);
                    showDay += false + "\t";
                }
            }
            showDays.add(true);
            showDay += true + "\t";
        }
        Log.d(TAG, "Show day no soft =" + showDay);
        for (int i = 0; i < weeks.size(); i++) {

            String _day = weeks.get(i);//define dayOfWeek

            FrameLayout _mDayRing = (FrameLayout) inflater.inflate(R.layout.day_ring, null);
            ImageView _dayRing = (ImageView) _mDayRing.findViewById(R.id.dayRing);
            TextView _lbDay = (TextView) _mDayRing.findViewById(R.id.lbDay);
            _lbDay.setText(_day);

            boolean showRing = showDays.get(i);//define show ring
            _dayRing.setImageResource(showRing ? R.drawable.day_ring : R.drawable.day_ring_gray);

            mDays.addView(_mDayRing);
        }
    }

    private ArrayList<String> _defineWeekbyDayOfWeek(int dayOfWeek) {
        ArrayList<String> days = new ArrayList<String>();
        {
            switch (dayOfWeek) {
                case Calendar.MONDAY:
                    days.add(getString(R.string.day_tue));
                    days.add(getString(R.string.day_wed));
                    days.add(getString(R.string.day_thu));
                    days.add(getString(R.string.day_fri));
                    days.add(getString(R.string.day_sat));
                    days.add(getString(R.string.day_sun));
                    days.add(getString(R.string.day_mon));


                    break;
                case Calendar.TUESDAY:
                    days.add(getString(R.string.day_wed));
                    days.add(getString(R.string.day_thu));
                    days.add(getString(R.string.day_fri));
                    days.add(getString(R.string.day_sat));
                    days.add(getString(R.string.day_sun));
                    days.add(getString(R.string.day_mon));
                    days.add(getString(R.string.day_tue));
                    break;
                case Calendar.WEDNESDAY:
                    days.add(getString(R.string.day_thu));
                    days.add(getString(R.string.day_fri));
                    days.add(getString(R.string.day_sat));
                    days.add(getString(R.string.day_sun));
                    days.add(getString(R.string.day_mon));
                    days.add(getString(R.string.day_tue));
                    days.add(getString(R.string.day_wed));
                    break;
                case Calendar.THURSDAY:
                    days.add(getString(R.string.day_fri));
                    days.add(getString(R.string.day_sat));
                    days.add(getString(R.string.day_sun));
                    days.add(getString(R.string.day_mon));
                    days.add(getString(R.string.day_tue));
                    days.add(getString(R.string.day_wed));
                    days.add(getString(R.string.day_thu));
                    break;
                case Calendar.FRIDAY:
                    days.add(getString(R.string.day_sat));
                    days.add(getString(R.string.day_sun));
                    days.add(getString(R.string.day_mon));
                    days.add(getString(R.string.day_tue));
                    days.add(getString(R.string.day_wed));
                    days.add(getString(R.string.day_thu));
                    days.add(getString(R.string.day_fri));
                    break;
                case Calendar.SATURDAY:
                    days.add(getString(R.string.day_sun));
                    days.add(getString(R.string.day_mon));
                    days.add(getString(R.string.day_tue));
                    days.add(getString(R.string.day_wed));
                    days.add(getString(R.string.day_thu));
                    days.add(getString(R.string.day_fri));
                    days.add(getString(R.string.day_sat));
                    break;
                case Calendar.SUNDAY:
                    days.add(getString(R.string.day_mon));
                    days.add(getString(R.string.day_tue));
                    days.add(getString(R.string.day_wed));
                    days.add(getString(R.string.day_thu));
                    days.add(getString(R.string.day_fri));
                    days.add(getString(R.string.day_sat));
                    days.add(getString(R.string.day_sun));
                    break;

            }
        }
        return days;
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        iCompleteSutdy = (ICompleteSutdy) context;
    }
}