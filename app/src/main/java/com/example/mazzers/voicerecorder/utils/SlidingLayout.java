package com.example.mazzers.voicerecorder.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

/**
 * voiceRecorder application
 * @author Vitaliy Vashchenko A11B0529P
 * Custom layout for sliding animation
 */
public class SlidingLayout extends RelativeLayout {
    private float yFraction = 0;
    private ViewTreeObserver.OnPreDrawListener preDrawListener = null;

    /**
     * Layout constructor
     *
     * @param context layout context
     */
    public SlidingLayout(Context context) {
        super(context);
    }

    /**
     * Layout constructor
     *
     * @param context layout context
     * @param attrs   layout attributes
     */
    public SlidingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Layout constructor
     * @param context layout context
     * @param attrs layout attributes
     * @param defStyleAttr layout style
     */
    public SlidingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Set height
     * @param fraction height value
     */
    void setYFraction(float fraction) {
        this.yFraction = fraction;
        if (getHeight() == 0) {
            if (preDrawListener == null) {
                preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
                        setYFraction(yFraction);
                        return true;
                    }
                };
                getViewTreeObserver().addOnPreDrawListener(preDrawListener);
            }
            return;
        }
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);

    }

    /**
     * Get height
     * @return height
     */
    public float getYFraction() {
        return this.yFraction;
    }


}
