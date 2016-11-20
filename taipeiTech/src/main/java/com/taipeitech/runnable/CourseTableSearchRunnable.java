package com.taipeitech.runnable;

import android.os.Handler;

public class CourseTableSearchRunnable extends BaseRunnable {
    private String sid;
    private String year;
    private String semester;

    public CourseTableSearchRunnable(Handler handler, String sid, String year,
                                     String semester) {
        super(handler);
        this.sid = sid;
        this.year = year;
        this.semester = semester;
    }

    @Override
    public void run() {

    }
}