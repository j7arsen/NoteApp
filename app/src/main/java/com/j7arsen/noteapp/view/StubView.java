package com.j7arsen.noteapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j7arsen.noteapp.R;

public class StubView extends LinearLayout {

    private ImageView ivIcon;
    private TextView tvTitle;

    private int iconId;
    private int bgColor;
    private String text;

    public StubView(Context context) {
        super(context);
        initView(context, null);
    }

    public StubView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public StubView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StubView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StubView, 0, 0);
        bgColor = a.getColor(R.styleable.StubView_color, -1);
        iconId = a.getResourceId(R.styleable.StubView_icon, -1);
        text = a.getString(R.styleable.StubView_text);
        if (text == null || text.isEmpty()) {
            text = context.getResources().getString(R.string.message_universal);
        }
        if (iconId == -1) {
            iconId = R.mipmap.ic_launcher;
        }

        setBackgroundColor(bgColor);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        int defaultMargin = dpToPx(8, context);
        LayoutParams params = new LayoutParams(dpToPx(64, context), dpToPx(64, context));
        params.setMargins(defaultMargin, 0, defaultMargin, 0);
        ivIcon = new ImageView(context);
        ivIcon.setLayoutParams(params);
        ivIcon.setImageResource(iconId);

        LayoutParams textViewParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.setMargins(defaultMargin, defaultMargin, defaultMargin, defaultMargin);
        tvTitle = new TextView(context);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setText(text);
        tvTitle.setTextColor(getResources().getColor(R.color.colorAccent));
        tvTitle.setLayoutParams(textViewParams);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(16, context));
        addView(ivIcon);
        addView(tvTitle);
        invalidate();

    }

    public void hide() {
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    private int dpToPx(float valueInDp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
}
