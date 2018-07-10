package com.born2go.lazzybee.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Hue on 12/3/2015.
 */
public class DisableScrollingViewPager extends ViewPager {

    private boolean isPagingEnabled = true;

    public DisableScrollingViewPager(Context context) {
        super(context);
    }

    public DisableScrollingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return this.isPagingEnabled && super.onInterceptTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            return this.isPagingEnabled && super.onInterceptTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }
}
