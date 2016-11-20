package com.taipeitech.model;

import java.util.ArrayList;

public class StudentCourse {

    private String sid = null;
    private String year = null;
    private String semester = null;
    private ArrayList<CourseInfo> courseList = null;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public ArrayList<CourseInfo> getCourseList() {
        return courseList;
    }

    public void setCourseList(ArrayList<CourseInfo> courseList) {
        this.courseList = courseList;
    }


}
