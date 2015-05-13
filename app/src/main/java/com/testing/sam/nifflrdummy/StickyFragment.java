package com.testing.sam.nifflrdummy;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StickyFragment extends Fragment implements ObservableScrollView.Callbacks {
    private static final int STATE_ONSCREEN = 0;
    private static final int STATE_OFFSCREEN = 1;
    private static final int STATE_RETURNING = 2;

    private HorizontalScrollView mStickyView;
    private ImageView left, right;
    private TextView mid;
    private RelativeLayout mQuickView;
    private ObservableScrollView mObservableScrollView;
    private int mMinRawY = 0;
    private int mState = STATE_ONSCREEN;
    private int mQuickReturnHeight;
    private int mCachedVerticalScrollRange;

    private static int ptrans;


    public StickyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_content, container, false);

        mObservableScrollView = (ObservableScrollView) rootView.findViewById(R.id.scroll_view);
        mObservableScrollView.setCallbacks(this);

        mStickyView = (HorizontalScrollView) rootView.findViewById(R.id.sticky);
        mQuickView = (RelativeLayout) rootView.findViewById(R.id.quick);
        left = (ImageView) rootView.findViewById(R.id.leftview);
        right = (ImageView) rootView.findViewById(R.id.rightview);
        mid = (TextView) rootView.findViewById(R.id.mid);

        mObservableScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        onScrollChanged();
                        mCachedVerticalScrollRange = mObservableScrollView.computeVerticalScrollRange();
                        mQuickReturnHeight = mQuickView.getHeight();
                    }
                });

        return rootView;
    }

    @Override
    public void onScrollChanged() {
        int rawY = mQuickView.getTop() - Math.min(
                mCachedVerticalScrollRange - mObservableScrollView.getHeight(),
                mObservableScrollView.getScrollY());
        int translationQuickY = 0;
        int translationStickyY = 0;
        int translationUpperY = 0;

        switch (mState) {
            case STATE_OFFSCREEN:
                if (rawY <= mMinRawY) {
                    mMinRawY = rawY;
                } else {
                    mState = STATE_RETURNING;
                }
                translationQuickY = rawY;
                //    Log.e("OFF Screen", "Translation: " + translationQuickY + " RawY: " + rawY + " MinRawY: " + mMinRawY);
                break;

            case STATE_ONSCREEN:
                if (rawY < -mQuickReturnHeight) {
                    mState = STATE_OFFSCREEN;
                    mMinRawY = rawY;
                }
                //   Log.e("On Screen", "Translation: " + translationQuickY + " RawY: " + rawY + " MinRawY: " + mMinRawY);
                translationQuickY = rawY;
                break;
            case STATE_RETURNING:
                translationQuickY = (rawY - mMinRawY) - mQuickReturnHeight;
                if (translationQuickY > 0) {
                    translationQuickY = 0;
                    mMinRawY = rawY - mQuickReturnHeight;
                    //   Log.e("Returning Case 1", "Translation: " + translationQuickY + " RawY: " + rawY + " MinRawY: " + mMinRawY);
                }

                if (rawY > 0) {
                    mState = STATE_ONSCREEN;
                    translationQuickY = rawY;
                    //  Log.e("Returning Case 2", "Translation: " + translationQuickY + " RawY: " + rawY + " MinRawY: " + mMinRawY);
                }

                if (translationQuickY < -mQuickReturnHeight) {
                    mState = STATE_OFFSCREEN;
                    mMinRawY = rawY;
                    //  Log.e("Returning Case 3", "Translation: " + translationQuickY + " RawY: " + rawY + " MinRawY: " + mMinRawY);
                }
                break;
        }

//        if (rawY < -600) {
        if (rawY > mMinRawY && mState == STATE_RETURNING && rawY < -500) {
            translationQuickY = -500;
            //   Log.e("Returning My 1", "Translation: " + translationQuickY + " RawY: " + rawY + " MinRawY: " + mMinRawY);
        } else {
            translationQuickY = rawY;
            //   Log.e("Returning My 2", "Translation: " + translationQuickY + " RawY: " + rawY + " MinRawY: " + mMinRawY);
        }
        //   if (mObservableScrollView.getScrollY() < 400)
        mQuickView.setTranslationY(translationQuickY);
/*        if (rawY < -600) {
            translationStickyY = mObservableScrollView.getScrollY() - 100;
        }*/
        translationStickyY = Math.max(0, mQuickView.getBottom() - mObservableScrollView.getScrollY());
        if (rawY < -600) {
            translationStickyY = 0;
        }
        if (translationQuickY == -500) {
            translationStickyY = 100;
            translationUpperY = 475;
        }
        if (translationQuickY > -475 && mState == STATE_RETURNING) {
            translationUpperY = -translationQuickY;
        }
        left.setTranslationY(translationUpperY);
        right.setTranslationY(translationUpperY);
        mid.setTranslationY(translationUpperY);
        Log.e("Upper View", "Translation: " + translationUpperY + " State: " + mState + " RawY: " + rawY + " MinRawY: " + mMinRawY);
        mStickyView.setTranslationY(translationStickyY);

        Log.e("Sticky View", "Translation: " + translationStickyY + " State: " + mState + " RawY: " + rawY + " MinRawY: " + mMinRawY);
    }
}

