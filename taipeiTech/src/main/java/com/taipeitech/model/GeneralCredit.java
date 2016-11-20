package com.taipeitech.model;

import java.util.ArrayList;

public class GeneralCredit {

    private String typeName = null;// 向度
    private int mustCoreCredit = 0;
    private ArrayList<GeneralCreditInfo> generals = new ArrayList<GeneralCreditInfo>();

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getMustCoreCredit() {
        return mustCoreCredit;
    }

    public void setMustCoreCredit(int mustCoreCredit) {
        this.mustCoreCredit = mustCoreCredit;
    }

    public int getHadCoreCredit() { // 已修核心
        int credit = 0;
        for (GeneralCreditInfo general : this.generals) {
            if (general.isCore()) {
                credit += general.getCredit();
            }
        }
        return credit;
    }

    public int getHadCommonCredit() { // 已修選修
        int credit = 0;
        for (GeneralCreditInfo general : this.generals) {
            if (!general.isCore()) {
                credit += general.getCredit();
            }
        }
        return credit;
    }

    public ArrayList<GeneralCreditInfo> getGenerals() {
        return this.generals;
    }

    public void addGeneral(GeneralCreditInfo general) {
        this.generals.add(general);
    }
}
