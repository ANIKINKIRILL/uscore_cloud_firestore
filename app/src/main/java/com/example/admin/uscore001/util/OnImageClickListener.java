package com.example.admin.uscore001.util;

import android.graphics.Bitmap;
import android.net.Uri;

public interface OnImageClickListener {
    void getBitmapPath(Bitmap bitmap);
    void getUriPath(Uri uri);
}
