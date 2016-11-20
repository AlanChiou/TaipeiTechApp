package com.taipeitech.calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.taipeitech.AnalyticsTrackers;
import com.taipeitech.R;
import com.taipeitech.model.EventInfo;
import com.taipeitech.utility.Utility;

import java.util.List;
import java.util.Locale;

public class CalendarListAdapter extends ArrayAdapter<EventInfo> implements
        OnClickListener, DialogInterface.OnClickListener {
    private static final int LAYOUT_ID = R.layout.calendar_item;
    private LayoutInflater inflater;
    private EventInfo selectedEvent;
    private Tracker tracker;

    public CalendarListAdapter(Context context, List<EventInfo> objects) {
        super(context, LAYOUT_ID, objects);
        tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(LAYOUT_ID, parent, false);
            holder = new ViewHolder();
            holder.date_textview = (TextView) convertView
                    .findViewById(R.id.calendarDate);
            holder.day_textview = (TextView) convertView
                    .findViewById(R.id.calendarDay);
            holder.event_textview = (TextView) convertView
                    .findViewById(R.id.calendarEvent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        EventInfo event = getItem(position);
        holder.date_textview.setText(Utility.getDate(event.getStartDate()));
        holder.day_textview.setText(Utility.getDateString("E",
                event.getStartDate()));
        holder.event_textview.setText(event.getEvent());
        convertView.setTag(R.id.data_tag, event);
        convertView.setOnClickListener(this);
        return convertView;
    }

    private class ViewHolder {
        public TextView date_textview;
        public TextView day_textview;
        public TextView event_textview;
    }

    @Override
    public void onClick(View v) {
        selectedEvent = (EventInfo) v.getTag(R.id.data_tag);
        Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("活動內容");
        String message = null;
        if (selectedEvent.getStartDate().compareTo(selectedEvent.getEndDate()) == 0) {
            message = String.format(
                    Locale.TAIWAN,
                    "%s\n\n時間：%s",
                    selectedEvent.getEvent(),
                    Utility.getDateString("yyyy/MM/dd (E)",
                            selectedEvent.getStartDate()));
        } else {
            message = String.format(
                    Locale.TAIWAN,
                    "%s\n\n開始時間：%s\n結束時間：%s",
                    selectedEvent.getEvent(),
                    Utility.getDateString("yyyy/MM/dd (E)",
                            selectedEvent.getStartDate()),
                    Utility.getDateString("yyyy/MM/dd (E)",
                            selectedEvent.getEndDate()));
        }
        builder.setMessage(message);
        builder.setNegativeButton(R.string.add_to_calendar, this);
        builder.setPositiveButton(R.string.share_using, this);
        builder.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int position) {
        switch (position) {
            case DialogInterface.BUTTON_NEGATIVE:
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(getContext().getString(R.string.analytics_category_calendar))
                        .setAction(getContext().getString(R.string.analytics_action_add_to_calendar))
                        .setLabel(selectedEvent.getEvent())
                        .build());
                try {
                    Intent calendar_intent = new Intent(Intent.ACTION_EDIT);
                    calendar_intent.setType("vnd.android.cursor.item/event");
                    calendar_intent.putExtra("beginTime", selectedEvent
                            .getStartDate().getTime());
                    calendar_intent.putExtra("allDay", true);
                    calendar_intent.putExtra("endTime", selectedEvent.getEndDate()
                            .getTime() + 60 * 60 * 1000);
                    calendar_intent.putExtra("title", selectedEvent.getEvent());
                    getContext().startActivity(calendar_intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), R.string.calendar_not_support,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case DialogInterface.BUTTON_POSITIVE:
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(getContext().getString(R.string.analytics_category_calendar))
                        .setAction(getContext().getString(R.string.analytics_action_share))
                        .setLabel(selectedEvent.getEvent())
                        .build());
                String shareBody = null;
                if (selectedEvent.getStartDate().compareTo(
                        selectedEvent.getEndDate()) == 0) {
                    shareBody = Utility.getDateString("yyyy/MM/dd (E)",
                            selectedEvent.getStartDate())
                            + " "
                            + selectedEvent.getEvent();
                } else {
                    shareBody = Utility.getDateString("yyyy/MM/dd (E)",
                            selectedEvent.getStartDate())
                            + "~"
                            + Utility.getDateString("yyyy/MM/dd (E)",
                            selectedEvent.getEndDate())
                            + " "
                            + selectedEvent.getEvent();
                }
                Intent sharing_intent = new Intent(
                        android.content.Intent.ACTION_SEND);
                sharing_intent.setType("text/plain");
                sharing_intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                sharing_intent.putExtra(android.content.Intent.EXTRA_TEXT,
                        shareBody);
                getContext().startActivity(
                        Intent.createChooser(sharing_intent, getContext()
                                .getResources().getString(R.string.share_using)));
                break;
        }
    }
}
