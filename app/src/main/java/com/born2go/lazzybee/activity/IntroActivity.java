package com.born2go.lazzybee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.SliderAdapter;
import com.born2go.lazzybee.shared.LazzyBeeShare;

public class IntroActivity extends AppCompatActivity {

    private ViewPager mSliderViewPaper;
    private LinearLayout mDotLayout;

    private TextView[] mDots;

    private SliderAdapter sliderAdapter;
    private Button mSliderButton;

    private int mCurrentPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mSliderViewPaper = findViewById(R.id.sliderViewPaper);
        mDotLayout = findViewById(R.id.dotsLayout);
        mSliderButton = findViewById(R.id.sliderBtn);

        sliderAdapter = new SliderAdapter(this);
        mSliderViewPaper.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSliderViewPaper.addOnPageChangeListener(viewlistener);

        mSliderButton.setOnClickListener(view -> {

            mSliderViewPaper.setCurrentItem(mCurrentPage + 1);
            if (mSliderButton.getText().equals(getString(R.string.intro_activity_get_started))) {
                String ADMOB_PUB_ID = LazzyBeeShare.EMPTY;
                if (getIntent() != null) {
                    ADMOB_PUB_ID = getIntent().getStringExtra(LazzyBeeShare.ADMOB_PUB_ID);
                }
                finish();
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                intent.putExtra(LazzyBeeShare.ADMOB_PUB_ID, ADMOB_PUB_ID);
                startActivity(intent);
            }
        });
    }


    public void addDotsIndicator(int position) {

        mDots = new TextView[6];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorGray));

            mDotLayout.addView(mDots[i]);

        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorBlue));
        }

    }

    ViewPager.OnPageChangeListener viewlistener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {

            addDotsIndicator(i);

            mCurrentPage = i;

            if (i == 0) {

                mSliderButton.setEnabled(true);
                mSliderButton.setText(getString(R.string.intro_activity_next));

            } else if (i == mDots.length - 1) {

                mSliderButton.setEnabled(true);
                mSliderButton.setText(getString(R.string.intro_activity_get_started));

            } else {

                mSliderButton.setEnabled(true);
                mSliderButton.setText(getString(R.string.intro_activity_next));

            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

}
