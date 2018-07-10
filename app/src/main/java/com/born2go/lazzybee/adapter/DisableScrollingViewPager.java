package com.born2go.lazzybee.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.born2go.lazzybee.gtools.LazzyBeeSingleton;

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
        if (this.isPagingEnabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isPagingEnabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.isPagingEnabled = enabled;
    }

    @Override
    public void scrollTo(int x, int y) {
        if(isPagingEnabled) {
            super.scrollTo(x, y);
        }
    }
}
