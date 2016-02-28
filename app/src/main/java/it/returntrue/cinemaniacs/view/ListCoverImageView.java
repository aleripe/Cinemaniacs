/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import it.returntrue.cinemaniacs.MoviesApplication;

/**
 * Extends an ImageView to calculate an accurate height based on width and aspect ratio
 */
public class ListCoverImageView extends ImageView {
    public ListCoverImageView(Context context) {
        super(context);
    }

    public ListCoverImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListCoverImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        MoviesApplication application = (MoviesApplication)getContext().getApplicationContext();

        if (application.getGridCoverHeight() == 0) {
            application.setGridCoverHeight((int)(widthMeasureSpec * 1.5));
        }

        setMeasuredDimension(widthMeasureSpec, application.getGridCoverHeight());
    }
}