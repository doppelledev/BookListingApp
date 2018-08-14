package com.example.android.booklistingapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class BitmapUtils {

    public BitmapUtils() {
        // not instantiable
    }

    public static byte [] getBytes(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte [] bitmapBytes = stream.toByteArray();
        try {
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmapBytes;
    }

    public static Bitmap getBitmap(byte [] bitmapBytes) {
        return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
    }
}
