package com.taipeitech.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StudentCredit {

    private ArrayList<SemesterCredit> semesters = new ArrayList<SemesterCredit>();
    private ArrayList<GeneralCredit> generals = new ArrayList<GeneralCredit>();
    private ArrayList<SemesterRank> ranks = new ArrayList<SemesterRank>();

    public ArrayList<SemesterCredit> getSemesterCredits() {
        return semesters;
    }

    public void addSemesterCredit(SemesterCredit semesterCredit) {
        this.semesters.add(semesterCredit);
    }

    public ArrayList<GeneralCredit> getGeneralCredits() {
        return generals;
    }

    public void addGeneralCredit(GeneralCredit generalCredit) {
        this.generals.add(generalCredit);
    }

    public ArrayList<SemesterRank> getRnakList() {
        Collections.sort(this.ranks, new Comparator<SemesterRank>() {
            @Override
            public int compare(SemesterRank o1, SemesterRank o2) {
                if (Integer.parseInt(o1.getYear()) == Integer.parseInt(o2
                        .getYear())) {
                    return Integer.parseInt(o1.getSemester())
                            - Integer.parseInt(o2.getSemester());
                } else {
                    return Integer.parseInt(o1.getYear())
                            - Integer.parseInt(o2.getYear());
                }

            }
        });
        return ranks;
    }

    public void addRnak(SemesterRank rank) {
        this.ranks.add(rank);
    }

    public int getGeneralCoreCredits() {
        int sum_credit = 0;
        for (GeneralCredit general : generals) {
            sum_credit += general.getHadCoreCredit();
        }
        return sum_credit;
    }

    public int getGeneralCommonCredits() {
        int sum_credit = 0;
        for (GeneralCredit general : generals) {
            sum_credit += general.getHadCommonCredit();
        }
        return sum_credit;
    }

    public int getTypeCredits(int type) {
        int sum_credit = 0;
        for (SemesterCredit credit : semesters) {
            sum_credit += credit.getTypeCredits(type);
        }
        return sum_credit;
    }

    public int getTotalCredits() {
        int sum_credit = 0;
        for (SemesterCredit credit : semesters) {
            sum_credit += credit.getTotalCredits();
        }
        return sum_credit;
    }

    public String getSemesterName(int index) {
        return semesters.get(index).getYear() + "-"
                + semesters.get(index).getSemester();
    }
}
