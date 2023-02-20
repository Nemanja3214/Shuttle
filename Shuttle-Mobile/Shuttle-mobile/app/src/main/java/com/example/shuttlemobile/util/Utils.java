package com.example.shuttlemobile.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class Utils {
    public static final String ServerOrigin = "http://192.168.0.34:8080/";

    private Utils() {}

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap getImageFromBase64(String imageBase64){
        byte[] decodedString = Base64.getDecoder().decode(imageBase64);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static String getBase64Bitmap(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.getEncoder().encodeToString(byteArray);
    }

    /**
     * Converts 'dp' into 'px'
     * @param context The application context. Use <code>getContext()</code>.
     * @param dp The relative dp value.
     * @return Equivalent pixel value, varying for different devices.
     */
    public static double dp2px(Context context, double dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)dp, context.getResources().getDisplayMetrics());
    }
}
