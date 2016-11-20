package com.taipeitech.course;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.taipeitech.R;
import com.taipeitech.model.CourseInfo;
import com.taipeitech.model.StudentCourse;
import com.taipeitech.utility.Utility;

import java.util.ArrayList;
import java.util.Locale;

public class CourseTableLayout extends LinearLayout {
    private static final int TABLE_COL = 9;
    private static final int TABLE_ROW = 14;
    private boolean isInitialized = false;
    private boolean isDisplayABCD = false;
    private boolean isDisplaySat = false;
    private boolean isDisplaySun = false;
    private boolean isDisplayNoTime = false;
    private int ROW_HEIGHT;
    private OnClickListener onClickListener = null;
    private TableInitializeListener initializeListener = null;
    private LinearLayout courseContainer;

    public CourseTableLayout(Context context) {
        super(context);
        inflate(context, R.layout.course_table_layout, this);
    }

    public CourseTableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.course_table_layout, this);
    }

    public void setOnCourseClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setTableInitializeListener(
            TableInitializeListener initializeListener) {
        this.initializeListener = initializeListener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!isInitialized) {
            ROW_HEIGHT = Math.round((bottom - top) / 9.5f);
            initCourseTable();
            isInitialized = true;
            if (initializeListener != null) {
                initializeListener.onTableInitialized(this);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        courseContainer = (LinearLayout) findViewById(R.id.course_container);
    }

    private void initCourseTable() {
        courseContainer.removeAllViews();
        LayoutParams title_row_params = new LayoutParams(
                LayoutParams.MATCH_PARENT, ROW_HEIGHT / 2);
        LayoutParams row_params = new LayoutParams(LayoutParams.MATCH_PARENT,
                ROW_HEIGHT);
        LayoutParams cell_params = new LayoutParams(0,
                LayoutParams.MATCH_PARENT, 1f);
        LayoutParams title_col_params = new LayoutParams(0,
                LayoutParams.MATCH_PARENT, 0.5f);
        for (int i = 0; i < TABLE_ROW; i++) {
            LinearLayout table_row = new LinearLayout(getContext());
            table_row.setOrientation(LinearLayout.HORIZONTAL);
            table_row.setLayoutParams(i == 0 ? title_row_params : row_params);
            table_row.setGravity(Gravity.CENTER);
            table_row.setBackgroundResource(i % 2 != 0 ? R.color.cloud
                    : R.color.white);
            for (int j = 0; j < TABLE_COL; j++) {
                CourseBlock table_cell = new CourseBlock(getContext());
                if (j == 0 && i > 0) {
                    table_cell.setText(Integer.toHexString(i).toUpperCase(
                            Locale.US));
                }
                table_cell.setId(j != TABLE_COL - 1 ? i : 14);
                table_cell.setLayoutParams(j == 0 ? title_col_params
                        : cell_params);
                table_row.addView(table_cell);
            }
            courseContainer.addView(table_row);
        }
        LinearLayout title_row = (LinearLayout) courseContainer.getChildAt(0);
        CourseBlock text = (CourseBlock) title_row.getChildAt(1);
        text.setText("一");
        text = (CourseBlock) title_row.getChildAt(2);
        text.setText("二");
        text = (CourseBlock) title_row.getChildAt(3);
        text.setText("三");
        text = (CourseBlock) title_row.getChildAt(4);
        text.setText("四");
        text = (CourseBlock) title_row.getChildAt(5);
        text.setText("五");
        text = (CourseBlock) title_row.getChildAt(6);
        text.setText("六");
        text = (CourseBlock) title_row.getChildAt(7);
        text.setText("日");
    }

    private void resetCourseTable() {
        for (int i = 1; i < TABLE_ROW; i++) {
            for (int j = 1; j < TABLE_COL; j++) {
                LinearLayout tr = (LinearLayout) courseContainer.getChildAt(i);
                CourseBlock text = (CourseBlock) tr.getChildAt(j);
                text.resetBlock();
            }
        }
        isDisplayABCD = false;
        isDisplaySat = false;
        isDisplaySun = false;
        isDisplayNoTime = false;
        requestLayout();
    }

    private void controlColRowShow() {
        for (int i = 0; i < TABLE_ROW; i++) {
            LinearLayout table_row = (LinearLayout) courseContainer
                    .getChildAt(i);
            CourseBlock sat_text = (CourseBlock) table_row.getChildAt(6);
            sat_text.setVisibility(isDisplaySat ? View.VISIBLE : View.GONE);
            CourseBlock sun_text = (CourseBlock) table_row.getChildAt(7);
            sun_text.setVisibility(isDisplaySun ? View.VISIBLE : View.GONE);
            CourseBlock notime_text = (CourseBlock) table_row.getChildAt(8);
            notime_text.setVisibility(isDisplayNoTime ? View.INVISIBLE
                    : View.GONE);
            if (i > 9) {
                table_row.setVisibility(isDisplayABCD ? View.VISIBLE
                        : View.GONE);
            }
        }
    }

    public void showCourse(StudentCourse studentCourse) {
        resetCourseTable();
        int color_index = 0;
        int[] color_array = getColorArray(studentCourse.getCourseList().size());
        int count = 0;
        for (CourseInfo item : studentCourse.getCourseList()) {
            boolean is_have_time = false;
            for (int i = 0; i < 7; i++) {
                String time = item.getCourseTimes()[i];
                ArrayList<String> s = Utility.splitTime(time);
                for (String t : s) {
                    if (t.length() != 0) {
                        int row = Integer.valueOf(t);
                        int col = i == 0 ? 7 : i;
                        isDisplayABCD = isDisplayABCD || row > 9;
                        isDisplaySun = isDisplaySun || i == 0;
                        isDisplaySat = isDisplaySat || i == 6;
                        setTableCell(row, col, color_array[color_index], item);
                        is_have_time = true;
                    }
                }
            }
            if (!is_have_time) {
                count++;
                isDisplayNoTime = true;
                setTableCell(count, 8, color_array[color_index], item);
            }
            color_index++;
        }
        controlColRowShow();
    }

    private int[] getColorArray(int color_count) {
        int[] color_array = new int[color_count];
        int delta = 360 / color_count;
        int offset = (int) (Math.random() * 360);
        for (int i = 0; i < color_count; i++) {
            color_array[i] = Color.HSVToColor(new float[]{
                    (offset + (delta * i)) % 360, 0.2f, 1f});
        }
        return color_array;
    }

    private void setTableCell(int row, int col, int color, CourseInfo course) {
        LinearLayout table_row = (LinearLayout) courseContainer.getChildAt(row);
        CourseBlock table_cell = (CourseBlock) table_row.getChildAt(col);
        table_cell.setVisibility(View.INVISIBLE);
        table_cell.setText(course.getCourseName());
        table_cell.setTag(course);
        table_cell.setBackgroundColor(color);
        table_cell.setOnClickListener(onClickListener);
        setAnimation(table_cell);
    }

    private void setAnimation(final CourseBlock textview) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        final TranslateAnimation trans_anim = new TranslateAnimation(
                displaymetrics.widthPixels, 0, 0, 0);
        trans_anim.setDuration(500);
        trans_anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                textview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }
        });
        trans_anim.setInterpolator(new OvershootInterpolator());
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                textview.startAnimation(trans_anim);
            }
        }, (long) ((Math.random() * 500) + 500));
    }

    private OnTouchListener onTouchListener;

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        onTouchListener = l;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (onTouchListener != null && onTouchListener.onTouch(this, ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface TableInitializeListener {
        void onTableInitialized(CourseTableLayout course_table);
    }
}
