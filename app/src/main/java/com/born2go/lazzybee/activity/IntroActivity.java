package com.born2go.lazzybee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.SliderAdapter;
import com.born2go.lazzybee.shared.LazzyBeeShare;

public class IntroActivity extends AppCompatActivity {

    private static final String TAG = IntroActivity.class.getSimpleName();
    private ViewPager mSliderViewPaper;
    private LinearLayout mDotLayout;

    private TextView[] mDots;

    private Button mSliderButton;

    private int mCurrentPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mSliderViewPaper = findViewById(R.id.sliderViewPaper);
        mDotLayout = findViewById(R.id.dotsLayout);
        mSliderButton = findViewById(R.id.sliderBtn);

        SliderAdapter sliderAdapter = new SliderAdapter(this);
        mSliderViewPaper.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSliderViewPaper.addOnPageChangeListener(viewlistener);

        mSliderButton.setOnClickListener(view -> {
            Log.d(TAG, mCurrentPage + ":" + mSliderViewPaper.getCurrentItem());
            if (mCurrentPage == 5 && mSliderViewPaper.getCurrentItem() == 5) {
                String ADMOB_PUB_ID = LazzyBeeShare.EMPTY;
                if (getIntent() != null) {
                    ADMOB_PUB_ID = getIntent().getStringExtra(LazzyBeeShare.ADMOB_PUB_ID);
                }
                startMainActivity(ADMOB_PUB_ID);
            }
            mSliderViewPaper.setCurrentItem(mCurrentPage + 1);

        });
    }

    private void startMainActivity(String admob_pub_id) {
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        intent.putExtra(LazzyBeeShare.ADMOB_PUB_ID, admob_pub_id);
        startActivity(intent);
        finish();
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

    final ViewPager.OnPageChangeListener viewlistener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {

            addDotsIndicator(i);

            mCurrentPage = i;

            if (i == mDots.length - 1) {
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
