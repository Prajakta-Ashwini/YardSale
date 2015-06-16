package com.android.yardsale.helpers.image;


import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class ImageHelper {

    public  static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
