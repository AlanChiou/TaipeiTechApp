package com.taipeitech.model;

import java.util.ArrayList;

public class StandardCredit {
    private ArrayList<String> credits = null;
    private String year_text;
    private String division_text;
    private String department_text;

    public void setCredits(ArrayList<String> credits) {
        this.credits = credits;
    }

    public ArrayList<String> getCredits() {
        return credits;
    }

    public String getYearText() {
        return year_text;
    }

    public void setYearText(String year_text) {
        this.year_text = year_text;
    }

    public String getDivisionText() {
        return division_text;
    }

    public void setDivisionText(String division_text) {
        this.division_text = division_text;
    }

    public String getDepartmentText() {
        return department_text;
    }

    public void setDepartmentText(String department_text) {
        this.department_text = department_text;
    }

}
