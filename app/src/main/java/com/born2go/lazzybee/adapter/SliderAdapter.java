package com.born2go.lazzybee.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
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

    final Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
        slider_desc=context.getResources().getStringArray(R.array.intro);
    }

    public final int[] slider_images ={
            R.drawable.ico_2000,
            R.drawable.ico_five_min,
            R.drawable.ico_time_for_review,
            R.drawable.ico_sample,
            R.drawable.ico_importance,
            R.drawable.ico_rearbanner
    };

    public final String[] slider_desc;

    @Override
    public int getCount() {
        return slider_desc.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return  view == o;
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position){
        //noinspection AccessStaticViaInstance
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_slide, container, false);

        ImageView sliderImageView = view.findViewById(R.id.slider_images);
        TextView SlideDescription = view.findViewById(R.id.slider_desc);

        if (position == 4) {
            sliderImageView.setOnClickListener(onClick -> optionDialog());
        }

        sliderImageView.setImageResource(slider_images[position]);
        SlideDescription.setText(slider_desc[position]);

        container.addView(view);

        return view;
    }

    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }

    public void optionDialog() {

        Log.e("click", "optionDialog: ");

    }

}
