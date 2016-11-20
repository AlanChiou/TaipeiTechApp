package com.taipeitech;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmInitReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) > 7) {
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 7);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 7);
        }
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

}
