package com.taipeitech.runnable;

import android.os.Handler;

import com.taipeitech.utility.CourseConnector;

import java.util.ArrayList;

public class ClassmateRunnable extends BaseRunnable {
    private String courseNo;

    public ClassmateRunnable(Handler handler, String courseNo) {
        super(handler);
        this.courseNo = courseNo;
    }

    @Override
    public void run() {
        try {
            ArrayList<String> result = CourseConnector.GetClassmate(courseNo);
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }
}