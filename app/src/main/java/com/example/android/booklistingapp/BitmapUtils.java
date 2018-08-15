package com.example.android.booklistingapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * A helper class to convert bitmaps to byte [] and vice versa
 */
public class BitmapUtils {

    public BitmapUtils() {
        // not instantiable
    }

    // convert bitmap to byte []
    public static byte[] getBytes(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] bitmapBytes = stream.toByteArray();
        try {
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmapBytes;
    }

    // convert byte [] to bitmap
    public static Bitmap getBitmap(byte[] bitmapBytes) {
        if (bitmapBytes != null)
            return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        return null;
    }
}
