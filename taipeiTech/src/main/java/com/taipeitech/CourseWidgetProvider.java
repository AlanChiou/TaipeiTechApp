package com.taipeitech;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.taipeitech.model.CourseInfo;
import com.taipeitech.model.Model;
import com.taipeitech.model.StudentCourse;
import com.taipeitech.utility.Constants;
import com.taipeitech.utility.Utility;

import java.util.ArrayList;

public class CourseWidgetProvider extends AppWidgetProvider {
    private static final int TABLE_COL = 9;
    private static final int TABLE_ROW = 14;

    /**
     * Called when the activity is first created.
     */
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.course_widget);
        refreshCourseTable(context, views);
        appWidgetManager.updateAppWidget(new ComponentName(context,
                CourseWidgetProvider.class), views);
    }

    public void onDeleted(Context context, int[] appWidgetIds) {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(
                Constants.ACTION_COURSEWIDGET_UPDATE_STR)) {
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.course_widget);

            refreshCourseTable(context, views);
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(context);
            appWidgetManager.updateAppWidget(new ComponentName(context,
                    CourseWidgetProvider.class), views);
        }
    }

    private void refreshCourseTable(Context context, RemoteViews views) {
        Intent clickIntent = new Intent(context, MainActivity.class);
        clickIntent.setAction(Intent.ACTION_MAIN);
        clickIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        clickIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                R.id.course_widget_table);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.course_widget_table, pendingIntent);
        Model model = Model.getInstance();
        cleanTable(context, views);
        showCourse(context, views, model.getStudentCourse());
    }

    private void cleanTable(Context context, RemoteViews views) {
        for (int row = 1; row < TABLE_ROW; row++) {
            for (int col = 1; col < TABLE_COL; col++) {
                int table_cell_id = context.getResources().getIdentifier(
                        "course_widget_cell_" + row + "_" + col, "id",
                        context.getPackageName());
                views.setInt(table_cell_id, "setBackgroundColor",
                        Color.TRANSPARENT);
                views.setTextViewText(table_cell_id, null);
            }
        }
    }

    private void showABCD(Context context, RemoteViews views,
                          boolean isDisplayABCD) {
        for (int row = 10; row < TABLE_ROW; row++) {
            int table_cell_id = context.getResources().getIdentifier(
                    "course_widget_row_" + row, "id", context.getPackageName());
            views.setViewVisibility(table_cell_id, isDisplayABCD ? View.VISIBLE
                    : View.GONE);
        }
    }

    private void showSat(Context context, RemoteViews views,
                         boolean isDisplaySat) {
        int col = 6;
        for (int row = 0; row < TABLE_ROW; row++) {
            int table_cell_id = context.getResources().getIdentifier(
                    "course_widget_cell_" + row + "_" + col, "id",
                    context.getPackageName());
            views.setViewVisibility(table_cell_id, isDisplaySat ? View.VISIBLE
                    : View.GONE);
        }
    }

    private void showSun(Context context, RemoteViews views,
                         boolean isDisplaySun) {
        int col = 7;
        for (int row = 0; row < TABLE_ROW; row++) {
            int table_cell_id = context.getResources().getIdentifier(
                    "course_widget_cell_" + row + "_" + col, "id",
                    context.getPackageName());
            views.setViewVisibility(table_cell_id, isDisplaySun ? View.VISIBLE
                    : View.GONE);
        }
    }

    private void showNoTime(Context context, RemoteViews views,
                            boolean isDisplayNotime) {
        int col = 8;
        for (int row = 0; row < TABLE_ROW; row++) {
            int table_cell_id = context.getResources().getIdentifier(
                    "course_widget_cell_" + row + "_" + col, "id",
                    context.getPackageName());
            views.setViewVisibility(table_cell_id,
                    isDisplayNotime ? View.VISIBLE : View.GONE);
        }
    }

    public void showCourse(Context context, RemoteViews views,
                           StudentCourse studentCourse) {
        try {
            boolean isDisplayABCD = false;
            boolean isDisplaySat = false;
            boolean isDisplaySun = false;
            boolean isDisplayNoTime = false;
            int color_index = 0;
            int[] color_array = getColorArray(studentCourse.getCourseList()
                    .size());
            int count = 0;
            for (CourseInfo item : studentCourse.getCourseList()) {
                Boolean is_have_time = false;
                for (int i = 0; i < 7; i++) {
                    String time = item.getCourseTimes()[i];
                    ArrayList<String> s = Utility.splitTime(time);
                    for (String t : s) {
                        if (t.length() != 0) {
                            int row = Integer.valueOf(t);
                            int col = i == 0 ? 7 : i;
                            if (!isDisplayABCD) {
                                isDisplayABCD = row > 9;
                            }
                            if (!isDisplaySun) {
                                isDisplaySun = i == 0;
                            }
                            if (!isDisplaySat) {
                                isDisplaySat = i == 6;
                            }
                            setTableCell(context, views, row, col,
                                    color_array[color_index], item);
                            is_have_time = true;
                        }
                    }
                }
                if (!is_have_time) {
                    count++;
                    isDisplayNoTime = true;
                    setTableCell(context, views, count, 8,
                            color_array[color_index], item);
                }
                color_index++;
            }
            showABCD(context, views, isDisplayABCD);
            showSat(context, views, isDisplaySat);
            showSun(context, views, isDisplaySun);
            showNoTime(context, views, isDisplayNoTime);
        } catch (Exception e) {
            cleanTable(context, views);
            showABCD(context, views, true);
            showSat(context, views, true);
            showSun(context, views, true);
            showNoTime(context, views, true);
        }

    }

    private int[] getColorArray(int color_count) {
        int[] color_array = new int[color_count];
        int delta = 360 / color_count;
        int offset = (int) (Math.random() * 360);
        for (int i = 0; i < color_count; i++) {
            color_array[i] = Color.HSVToColor(0xBf, new float[]{
                    (offset + (delta * i)) % 360, 0.2f, 1f});
        }
        return color_array;
    }

    private void setTableCell(Context context, RemoteViews views, int row,
                              int col, int color, CourseInfo course) {
        int table_cell_id = context.getResources().getIdentifier(
                "course_widget_cell_" + row + "_" + col, "id",
                context.getPackageName());
        views.setInt(table_cell_id, "setBackgroundColor", color);
        views.setTextViewText(table_cell_id, course.getCourseName());
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(context.getString(R.string.analytics_category_course_widget))
                .setAction(context.getString(R.string.analytics_action_add))
                .build());
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(context.getString(R.string.analytics_category_course_widget))
                .setAction(context.getString(R.string.analytics_action_remove))
                .build());
    }
}
