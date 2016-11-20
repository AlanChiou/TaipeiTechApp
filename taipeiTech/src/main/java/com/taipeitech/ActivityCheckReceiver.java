package com.taipeitech;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.taipeitech.model.ActivityInfo;
import com.taipeitech.model.Model;
import com.taipeitech.utility.BitmapUtility;
import com.taipeitech.utility.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

public class ActivityCheckReceiver extends BroadcastReceiver {
    private static final String CHECK_FLAG = "activity_check_flag";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle.get("action").equals("com.taipeitech.action.ACTIVITY_CHECK")) {
            CheckTask task = new CheckTask(context);
            task.execute();
        }
    }

    private boolean dateEquals(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(Calendar.DAY_OF_MONTH) == calendar2
                .get(Calendar.DAY_OF_MONTH)
                && calendar1.get(Calendar.MONTH) == calendar2
                .get(Calendar.MONTH)
                && calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR);
    }

    private boolean needToCheck(Context context) {
        String date = Utility.getDateString("yyyy-MM-dd", Calendar
                .getInstance().getTime());
        SharedPreferences settings = context.getSharedPreferences(
                MainApplication.SETTING_NAME, Context.MODE_PRIVATE);
        String date_flag = settings.getString(CHECK_FLAG, null);
        settings.edit().putString(CHECK_FLAG, date).apply();
        return !date.equals(date_flag);
    }

    private void setNextAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        Intent receiver_intent = new Intent(context,
                ActivityCheckReceiver.class);
        receiver_intent.putExtra("action",
                "com.taipeitech.action.ACTIVITY_CHECK");
        PendingIntent pi = PendingIntent.getBroadcast(context, 1,
                receiver_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
    }

    private class CheckTask extends AsyncTask<Void, Void, HashMap<ActivityInfo, Bitmap>> {
        private Context context;

        public CheckTask(Context context) {
            this.context = context;
        }

        @Override
        protected HashMap<ActivityInfo, Bitmap> doInBackground(Void... params) {
//            if (WifiUtility.isWifiOpen(context) && WifiUtility.isNetworkAvailable(context)) {
//                try {
//                    ActivityList activityList = ActivityConnector.getActivityList(context);
//                    Model.getInstance().setActivityArray(activityList);
//                    Model.getInstance().saveActivityArray();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }

            if (!needToCheck(context)){
                return null;
            }
            HashMap<ActivityInfo, Bitmap> result = new HashMap<>();
            Calendar calendar = Calendar.getInstance();
            ArrayList<ActivityInfo> activityInfos = Model.getInstance()
                    .getActivityArray();
            if (activityInfos == null) {
                return null;
            }
            for (ActivityInfo activity : activityInfos) {
                Calendar temp = Calendar.getInstance();
                temp.setTime(activity.getStartDate());
                temp.add(Calendar.DAY_OF_MONTH, -1);
                Calendar temp2 = Calendar.getInstance();
                temp2.setTime(activity.getStartDate());
                if (dateEquals(calendar, temp)
                        || dateEquals(calendar, temp2)) {
                    Bitmap bitmap = null;
                    try {
                        if (!TextUtils.isEmpty(activity.getImage())) {
                            String file_path = context.getCacheDir().getPath()
                                    + "/" + activity.getImage();
                            bitmap = BitmapUtility.loadBitmap(context, file_path);
                        } else {
                            bitmap = BitmapUtility.loadBitmap(context, R.drawable.no_image);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result.put(activity, bitmap);
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(HashMap<ActivityInfo, Bitmap> result) {
            super.onPostExecute(result);
            if (result != null) {
                Set<ActivityInfo> keys = result.keySet();
                for (ActivityInfo activity : keys) {
                    Bitmap bitmap = result.get(activity);
                    String message;
                    if (activity.getStart().equals(
                            activity.getEnd())) {
                        message = String.format(
                                "%s\n%s\n%s", activity.getStart(), activity.getLocation(),
                                activity.getHost());
                    } else {
                        message = String.format(
                                "%s - %s\n%s\n%s", activity.getStart(),
                                activity.getEnd(), activity.getLocation(),
                                activity.getHost());
                    }
                    Utility.showActivityNotification(context, String.format("%s",
                            activity.getName()), message, bitmap);
                }
                setNextAlarm(context);
            }
        }
    }
}
