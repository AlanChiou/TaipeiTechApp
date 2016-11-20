package com.taipeitech.runnable;

import android.os.Handler;

import com.taipeitech.model.YearCalendar;
import com.taipeitech.utility.CalendarConnector;

public class CalendarRunnable extends BaseRunnable {
    public CalendarRunnable(Handler handler) {
        super(handler);
    }

    @Override
    public void run() {
        try {
            YearCalendar result = CalendarConnector.getEventList();
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }
}
