package com.taipeitech.course.data;

/**
 * Created by Alan on 2015/10/17.
 */
public class Semester {
    private String mYear;
    private String mSemester;

    public Semester(String year, String semester) {
        mYear = year;
        mSemester = semester;
    }

    public String getYear() {
        return mYear;
    }

    public String getSemester() {
        return mSemester;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", mYear, mSemester);
    }
}
