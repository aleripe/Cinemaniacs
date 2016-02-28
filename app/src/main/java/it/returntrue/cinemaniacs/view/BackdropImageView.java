/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Extends an ImageView to calculate an accurate width based on height and aspect ratio
 */
public class BackdropImageView extends ImageView {
    public BackdropImageView(Context context) {
        super(context);
    }

    public BackdropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BackdropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, (int)(widthMeasureSpec * 1.5));
    }
}