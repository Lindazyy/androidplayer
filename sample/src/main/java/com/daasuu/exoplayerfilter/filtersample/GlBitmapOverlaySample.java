package com.daasuu.exoplayerfilter.filtersample;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.daasuu.epf.filter.GlOverlayFilter;



public class GlBitmapOverlaySample extends GlOverlayFilter {

    private Bitmap bitmap;

    public GlBitmapOverlaySample(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    protected void drawCanvas(Canvas canvas) {

        canvas.drawBitmap(bitmap, 0, 0, null);

    }
}