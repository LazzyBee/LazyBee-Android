package com.born2go.lazzybee.view.dialog;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

@SuppressLint("ValidFragment")
public class DialogStatistics extends DialogFragment {

    public static final String TAG = "DialogStatistics";
    RelativeLayout mStatistic, mChart;
    TextView mlazzybee;
    private ColumnChartView chart;
    private ColumnChartData data;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = false;
    private Context context;
    Button btnShare;

    public DialogStatistics(Context context) {
        this.context = context;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_statistics, container, false);
        final Dialog dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            _initChart(view);
            _initStreakCount(view);

            //Play media
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.magic);
            mediaPlayer.start();

            btnShare = (Button) view.findViewById(R.id.btnShared);
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    screenViewChart();
                    _shareCard();

                    if(getActivity()!=null){
                        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, new Bundle());
                    }


                }
            });
            view.findViewById(R.id.mClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "onCreateView", e);
        }


        return view;
    }

    private void screenViewChart() {
        try {
            String mPath = Environment.getExternalStorageDirectory().toString() + "/statitis_scren.jpg";
            View v1 = mStatistic;
            mlazzybee.setVisibility(View.VISIBLE);
            btnShare.setVisibility(View.INVISIBLE);
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

            outputStream.flush();
            outputStream.close();
            mlazzybee.setVisibility(View.INVISIBLE);
            btnShare.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            LazzyBeeShare.showErrorOccurred(context, "screenViewChart", e);
            e.printStackTrace();
        }
    }

    private void _initChart(View view) {
        mStatistic = (RelativeLayout) view.findViewById(R.id.mStatistic);
        mChart = (RelativeLayout) view.findViewById(R.id.mChart);
        chart = (ColumnChartView) view.findViewById(R.id.chart);
        chart.setZoomEnabled(false);
        mlazzybee = (TextView) view.findViewById(R.id.mlazzybee);
        generateDefaultData();
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

    @SuppressWarnings("SuspiciousNameCombination")
    private void generateDefaultData() {
        try {
            List<Integer> listCountCardbyLevel = LazzyBeeSingleton.learnApiImplements._getListCountCardbyLevel();
            // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
            List<Column> columns = new ArrayList<Column>();
            List<SubcolumnValue> values;


            List<AxisValue> axisXValues = new ArrayList<AxisValue>();
            List<AxisValue> axisTopValues = new ArrayList<AxisValue>();

            //Gestion of the two axes for the graphic

            int total = 0;
            for (int i = 0; i < listCountCardbyLevel.size(); ++i) {
                values = new ArrayList<SubcolumnValue>();
                int count = listCountCardbyLevel.get(i);
                total += count;
                axisXValues.add(new AxisValue(i).setLabel(String.valueOf(i + 1)));

                axisTopValues.add(new AxisValue(i).setLabel(String.valueOf(count)));
                if (count > 0) {
                    SubcolumnValue valueColum =
                            new SubcolumnValue(count, ChartUtils.pickColor()).setLabel(String.valueOf(count));//define Subcolum

                    values.add(valueColum);
                }
                Column column = new Column(values);

                column.setHasLabels(hasLabels);
                column.setHasLabelsOnlyForSelected(hasLabelForSelected);

                columns.add(column);

            }
            data = new ColumnChartData(columns);
            Axis axeX = new Axis(axisXValues);
            Axis axisTop = new Axis(axisTopValues).setHasLines(true);
            axisTop.setName(context.getString(R.string.dialog_statistical_total, total));
            axeX.setTextColor(R.color.text_color_number_count_card_by_level);
            axisTop.setTextColor(R.color.text_color_number_count_card_by_level);
            axeX.setHasLines(true);
            axeX.setName(context.getString(R.string.dialog_statistical_level));
            data.setAxisXBottom(axeX);

            data.setAxisXTop(axisTop);
            chart.setColumnChartData(data);
            chart.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
                @Override
                public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {

                }

                @Override
                public void onValueDeselected() {

                }
            });
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "generateDefaultData", e);
        }
    }

    private void _initStreakCount(View view) {
        try {
            //get count
            int count = LazzyBeeSingleton.learnApiImplements._getCountStreak();
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
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_initStreakCount", e);
        }
    }

    private void _shareCard() {
        try {
            Uri screenshotUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "statitis_scren.jpg"));

            //Share statitic
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Title");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Statitis");
            sendIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            sendIntent.setType("image/jpeg");
            startActivity(sendIntent);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_shareCard", e);
        }

    }


}
