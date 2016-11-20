package com.taipeitech.model;

import java.util.Date;

public class EventInfo {
    private String event;
    private Date startDate;
    private Date endDate;

    public EventInfo(String event, Date startDate, Date endDate) {
        this.event = event;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public EventInfo(String event, Date startDate) {
        this.event = event;
        this.startDate = startDate;
        this.endDate = startDate;
    }

    public String getEvent() {
        return event;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}
