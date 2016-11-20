package com.taipeitech.runnable;

import android.content.Context;
import android.os.Handler;

import com.taipeitech.model.ActivityList;
import com.taipeitech.utility.ActivityConnector;

public class ActivityRunnable extends BaseRunnable {

    private Context context;

    public ActivityRunnable(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void run() {
        try {
            ActivityList result = ActivityConnector.getActivityList(context);
            result.checkActivity();
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage("活動讀取時發生錯誤");
        }
    }
}
