package com.j7arsen.noteapp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

public class ResUtils {

    public static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static List<String> getStringList(Context context, int resId) {
        return Arrays.asList(context.getResources().getStringArray(resId));
    }

    public static int getInt(Context context, int resId) {
        return context.getResources().getInteger(resId);
    }

    public static int getColor(Context context, int resId) {
        return ContextCompat.getColor(context, resId);
    }

    public static int getDimenInPx(Context context, int resId) {
        return context.getResources().getDimensionPixelSize(resId);
    }

    public static Drawable getDrawable(Context context, int resId) {
        return ContextCompat.getDrawable(context, resId);
    }

}
