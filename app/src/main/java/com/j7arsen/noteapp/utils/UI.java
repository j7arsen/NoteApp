package com.j7arsen.noteapp.utils;

import android.app.Activity;

import com.j7arsen.noteapp.R;

public class UI {

    public static void animationOpenActivity(Activity activity) {
        activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public static void animationCloseActivity(Activity activity) {
        activity.overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    public static void openActivityWithoutAnimation(Activity activity) {
        activity.overridePendingTransition(0, 0);
    }

}
