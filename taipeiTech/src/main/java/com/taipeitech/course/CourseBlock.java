package com.taipeitech.course;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.taipeitech.R;

public class CourseBlock extends TextView {

    public CourseBlock(Context context) {
        super(context);
        setTextColor(getResources().getColor(R.color.darken));
        setGravity(Gravity.CENTER);
        setPadding(2, 0, 2, 0);
        setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.courseText));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setBackgroundColor(int color) {
        StateListDrawable background_drawable = new StateListDrawable();
        background_drawable.addState(
                new int[]{android.R.attr.state_pressed}, new ColorDrawable(
                        getResources().getColor(R.color.silver)));
        background_drawable.addState(
                new int[]{android.R.attr.state_enabled}, new ColorDrawable(
                        color));
        setBackgroundDrawable(background_drawable);
    }

    public void resetBlock() {
        setText(null);
        setTag(null);
        super.setBackgroundColor(Color.TRANSPARENT);
        setOnClickListener(null);
    }
}
