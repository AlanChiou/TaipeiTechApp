package com.taipeitech.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityInfo {
    private static final SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy/MM/dd", Locale.TAIWAN);
    private Date start;
    private Date end;
    private String name;
    private String image;
    private String host;
    private String content;
    private String url;
    private String location;

    public String getStart() {
        return sdf.format(start);
    }

    public Date getStartDate() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public String getEnd() {
        return sdf.format(end);
    }

    public Date getEndDate() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
