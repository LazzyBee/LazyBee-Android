package com.born2go.lazzybee.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.born2go.lazzybee.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
        slider_desc=context.getResources().getStringArray(R.array.intro);
    }

    public int[] slider_images ={
            R.drawable.ico_2000,
            R.drawable.ico_five_min,
            R.drawable.ico_time_for_review,
            R.drawable.ico_sample,
            R.drawable.ico_importance,
            R.drawable.ico_rearbanner
    };

    public String[] slider_desc = {
            "LazzyBee helps you learn and remember 2000 new words per year",
            "... If you spend 5 minutes each day",
            "Words you learned will never be forgotten due to in-time-review",
            "As you learn, swipe left to access embedded dictionary",
            "You read giudeline carefully to understand our special methoddology",
            "GOOD LUCK!!!"
    };

    @Override
    public int getCount() {
        return slider_desc.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return  view == (RelativeLayout) o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_slide, container, false);

        ImageView sliderImageView = (ImageView) view.findViewById(R.id.slider_images);
        TextView SlideDescription = (TextView) view.findViewById(R.id.slider_desc);

        if (position == 4) {
            sliderImageView.setOnClickListener(onClick -> optionDialog());
        }

        sliderImageView.setImageResource(slider_images[position]);
        SlideDescription.setText(slider_desc[position]);

        container.addView(view);

        return view;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    public void optionDialog() {

        Log.e("click", "optionDialog: ");

    }

}
