package com.taipeitech.model;

import java.util.ArrayList;

public class SemesterCredit {
    private ArrayList<CreditInfo> credits = new ArrayList<CreditInfo>();
    private String year = null;
    private String sem = null;
    private String score = null;
    private String conduct_score = null;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return sem;
    }

    public void setSemester(String sem) {
        this.sem = sem;
    }

    public ArrayList<CreditInfo> getCredits() {
        return credits;
    }

    public void setCreditInfoType(int index, int type) {
        credits.get(index).setType(type);
    }

    public void addCreditInfo(CreditInfo credit) {
        this.credits.add(credit);
    }

    public int getTypeCredits(int type) {
        int sum_credit = 0;
        for (CreditInfo credit : credits) {
            if (credit.getType() == type) {
                sum_credit += credit.getCredit();
            }
        }
        return sum_credit;
    }

    public int getTotalCredits() {
        int sum_credit = 0;
        for (CreditInfo credit : credits) {
            if (credit.getType() != 0) { // 0表示不計算，包含撤選、不及格、缺考
                sum_credit += credit.getCredit();
            }
        }
        return sum_credit;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getConductScore() {
        return conduct_score;
    }

    public void setConductScore(String conduct_score) {
        this.conduct_score = conduct_score;
    }

}
