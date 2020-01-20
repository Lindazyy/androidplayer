package com.daasuu.exoplayerfilter;

import android.content.Context;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class MovieWrapperView extends FrameLayout {

    public MovieWrapperView(@NonNull Context context) {
        super(context);
    }

    public MovieWrapperView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MovieWrapperView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int orientation = getResources().getConfiguration().orientation;
        int measuredWidth = getMeasuredWidth();
        int measureHeight = getMeasuredHeight();
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
             measuredWidth = getMeasuredHeight();
             measureHeight = getMeasuredWidth();
        }

//        setMeasuredDimension(measuredWidth, measuredWidth / 16 * 9);
        setMeasuredDimension(measuredWidth, measureHeight);
    }
}
