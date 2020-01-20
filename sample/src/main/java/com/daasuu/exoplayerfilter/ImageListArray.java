package com.daasuu.exoplayerfilter;
import android.graphics.Bitmap;

public class ImageListArray {
    private String name;
    private Bitmap image;
    public ImageListArray(String name, Bitmap image){
        this.name = name;
        this.image = image;
    }
    public String getName() {
        return name;
    }
    public Bitmap getImage() {
        return image;
    }
}