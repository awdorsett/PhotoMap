package com.example.andrewdorsett.photomap;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import java.io.IOException;

/**
 * Created by admin on 2/18/18.
 */

public class ImageResizeUtil {

    public static Bitmap getScaledBitMap(ContentResolver contentResolver, Uri uri, DisplayMetrics metrics){
        Bitmap bm = null;
        try {
            bm = MediaStore.Images.Media.getBitmap(contentResolver, uri);
        } catch (IOException e) {
            // TODO handle real exception
        }

        return getScaledBitMap(contentResolver, uri, metrics.widthPixels, getHeight(bm, metrics));
    }

    public static Bitmap getScaledBitMap(ContentResolver contentResolver, Uri uri, int width, int height){
        Bitmap bm = null;
        try {
            bm = MediaStore.Images.Media.getBitmap(contentResolver, uri);
        } catch (IOException e) {
            // TODO handle real exception
        }


        return Bitmap.createScaledBitmap(bm, width, height, false);
    }


    private static int getHeight(Bitmap bm, DisplayMetrics dm) {
        int newHeight = bm.getHeight();
        if (bm.getWidth() > dm.widthPixels) {
            double ratio = dm.widthPixels / (double) bm.getWidth();
            newHeight = (int) Math.round(bm.getHeight() * ratio);
        }

        return newHeight;
    }

}
