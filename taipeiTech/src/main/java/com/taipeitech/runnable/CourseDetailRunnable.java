package com.taipeitech.runnable;

import android.os.Handler;

import com.taipeitech.utility.CourseConnector;

import java.util.ArrayList;

public class CourseDetailRunnable extends BaseRunnable {
    private String courseNo;

    public CourseDetailRunnable(Handler handler, String courseNo) {
        super(handler);
        this.courseNo = courseNo;
    }

    @Override
    public void run() {
        try {
            ArrayList<String> result = CourseConnector
                    .GetCourseDetail(courseNo);
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }
}