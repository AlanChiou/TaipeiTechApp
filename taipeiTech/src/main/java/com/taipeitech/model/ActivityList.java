package com.taipeitech.model;

import java.util.ArrayList;
import java.util.Calendar;

public class ActivityList extends ArrayList<ActivityInfo> {

    /**
     *
     */
    private static final long serialVersionUID = -7516084403535932076L;

    public void checkActivity() {
        Calendar calendar = Calendar.getInstance();
        ActivityList clone = (ActivityList) clone();
        for (ActivityInfo info : clone) {
            Calendar activity_end = Calendar.getInstance();
            activity_end.setTime(info.getEndDate());
            activity_end.add(Calendar.DAY_OF_MONTH, 1);
            if (calendar.after(activity_end)) {
                remove(info);
            }
        }
    }

}
