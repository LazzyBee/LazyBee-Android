package com.born2go.lazzybee.fragment;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class DialogStatistics extends DialogFragment {

    public static final String TAG = "DialogStatistics";
    private ColumnChartView chart;
    private ColumnChartData data;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = false;
    private Context context;

    public DialogStatistics(Context context) {
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_statistics, container, false);
        final Dialog dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            _initChart(view);
            _initStreakCount(view);
        } catch (Exception e) {
        }
        view.findViewById(R.id.mClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return view;
    }

    private void _initChart(View view) {
        chart = (ColumnChartView) view.findViewById(R.id.chart);
        generateDefaultData();
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

    private void generateDefaultData() {
        List<Integer> listCountCardbyLevel=LazzyBeeSingleton.learnApiImplements._getListCountCardbyLevel();


        int numSubcolumns = 1;
        int numColumns = 6;
        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;


        List<AxisValue> axisXValues = new ArrayList<AxisValue>();
        List<AxisValue> axisTopValues = new ArrayList<AxisValue>();
        //Gestion of the two axes for the graphic


        for (int i = 0; i < listCountCardbyLevel.size(); ++i) {
            values = new ArrayList<SubcolumnValue>();
            int count=listCountCardbyLevel.get(i);
            axisXValues.add(new AxisValue(i).setLabel(String.valueOf(i + 1)));
            axisTopValues.add(new AxisValue(i).setLabel(String.valueOf(count)));
            values.add(new SubcolumnValue(count, ChartUtils.pickColor()));

            Column column = new Column(values);
            column.setHasLabels(hasLabels);
            column.setHasLabelsOnlyForSelected(hasLabelForSelected);
            columns.add(column);
        }
        data = new ColumnChartData(columns);

        Axis axeX = new Axis(axisXValues);
        Axis axisTop = new Axis(axisTopValues).setHasLines(true);
        axeX.setHasLines(true);
        axeX.setName(context.getString(R.string.dialog_statistical_level));
        data.setAxisXBottom(axeX);

        data.setAxisXTop(axisTop);

        chart.setColumnChartData(data);

    }

    private void _initStreakCount(View view) {
        //get count
        int count = LazzyBeeSingleton.learnApiImplements._getCountStreak();
        //Define view
        View mCount = view.findViewById(R.id.mCount);
        TextView lbCountStreak = (TextView) mCount.findViewById(R.id.lbCountStreak);
        ImageView streak_ring = (ImageView) mCount.findViewById(R.id.streak_ring);
        //
        lbCountStreak.setText(count + " " + getString(R.string.streak_day));

        //set animation
        Animation a = AnimationUtils.loadAnimation(context, R.anim.scale_indefinitely);
        a.setDuration(1000);
        streak_ring.startAnimation(a);
    }


}
