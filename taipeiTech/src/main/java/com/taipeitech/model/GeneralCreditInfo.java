package com.taipeitech.model;

public class GeneralCreditInfo {
    private String courseName = null;
    private String year = null;
    private String sem = null;
    private boolean isCore = false;
    private int credit = 0;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public boolean isCore() {
        return isCore;
    }

    public void setCore(boolean isCore) {
        this.isCore = isCore;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

}
