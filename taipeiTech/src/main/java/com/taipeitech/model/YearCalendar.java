package com.taipeitech.model;

import com.taipeitech.utility.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class YearCalendar {
    private ArrayList<EventInfo> eventList = null;
    private String semester = null;

    public ArrayList<EventInfo> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<EventInfo> eventList) {
        this.eventList = eventList;
    }

    public int getYear() {
        return Integer.parseInt(semester) + 1911;
    }

    public String getSemesterYear() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public ArrayList<EventInfo> searchEventList(String keyword) {
        ArrayList<EventInfo> resultList = new ArrayList<EventInfo>();
        if (eventList != null && keyword != null) {
            for (EventInfo eventInfo : eventList) {
                if (eventInfo.getEvent().contains(keyword)) {
                    resultList.add(eventInfo);
                }
            }
        }
        return resultList;
    }

    public ArrayList<EventInfo> getMonthEventList(String year, String month) {
        ArrayList<EventInfo> resultList = new ArrayList<EventInfo>();
        if (eventList != null && month != null) {
            for (EventInfo eventInfo : eventList) {
                if (Utility.getMonth(eventInfo.getStartDate()).equals(month)
                        && Utility.getYear(eventInfo.getStartDate()).equals(
                        year)) {
                    resultList.add(eventInfo);
                }
            }
        }
        return resultList;
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month + 1, day, 0, 0, 0);
        Date date = cal.getTime();
        return date;
    }

    public static Date getDate(String date_string) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);
        Date date = sdf.parse(date_string);
        return date;
    }

    public ArrayList<String> findEvents(Date date) {
        ArrayList<String> resultList = new ArrayList<String>();
        if (eventList != null && date != null) {
            for (EventInfo eventInfo : eventList) {
                if (eventInfo.getStartDate().equals(date)) {
                    resultList.add(eventInfo.getEvent());
                }
            }
        }
        return resultList;
    }
}
