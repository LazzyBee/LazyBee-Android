package com.born2go.lazzybee.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import java.util.List;


public class DialogCompleteStudy extends DialogFragment {

    private Context context;
    private MediaPlayer mpintro;

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
            _initStreakCount(view);
            _initStreakDays(view, inflater);

            //Play media
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.magic);
            mediaPlayer.start();


        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
        Button btnDone = (Button) view.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
                iCompleteSutdy.close();
            }
        });

        return view;
    }

    private void _initStreakCount(View view) {
        //get count
        int count = LazzyBeeSingleton.learnApiImplements._getCountStreak();
        //Define view
        View mCount = view.findViewById(R.id.mCount);
        TextView lbCountStreak = (TextView) mCount.findViewById(R.id.lbCountStreak);
        ImageView streak_ring = (ImageView) mCount.findViewById(R.id.streak_ring);
        //
        lbCountStreak.setText(count + " " +getString(R.string.streak_day));

        //set animation
        Animation a = AnimationUtils.loadAnimation(context, R.anim.scale_indefinitely);
        a.setDuration(1000);
        streak_ring.startAnimation(a);
    }

    private void _initStreakDays(View view, LayoutInflater inflater) {
        LinearLayout mDays = (LinearLayout) view.findViewById(R.id.mDays);
        ArrayList<String> strings = new ArrayList<String>();
        strings.addAll(Arrays.asList(context.getResources().getStringArray(R.array.days)));

        int startOfDay = (int) (LazzyBeeShare.getStartOfDayInMillis() / 1000);
        List<Integer> dayCompleteStudys = LazzyBeeSingleton.learnApiImplements._getListDayStudyComplete();

        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String day = LazzyBeeShare.EMPTY;

        if (dayOfWeek > 1) {
            day = strings.get(dayOfWeek - 2);
        } else if (dayOfWeek == 1) {//Day of week ==1 -> Sunday
            day = strings.get(6);
        }

        ArrayList<String> weeks = _defineWeekbyDayOfWeek(dayOfWeek);
        int countWeeks = weeks.size();
        for (int i = 0; i < weeks.size(); i++) {
            //
            String _day = weeks.get(i);

            //define day of week
            int longDateOfWeek = startOfDay - (84600 * countWeeks--);

            FrameLayout _mDayRing = (FrameLayout) inflater.inflate(R.layout.day_ring, null);
            ImageView _dayRing = (ImageView) _mDayRing.findViewById(R.id.dayRing);
            TextView _lbDay = (TextView) _mDayRing.findViewById(R.id.lbDay);
            _lbDay.setText(_day);

            if (day.equals(_day)) {
                _dayRing.setImageResource(R.drawable.day_ring);
            } else if (dayCompleteStudys.contains(longDateOfWeek)) {
                _dayRing.setImageResource(R.drawable.day_ring);
            } else {
                _dayRing.setImageResource(R.drawable.day_ring_gray);
            }

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
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        iCompleteSutdy = (ICompleteSutdy) activity;
    }
}