package com.taipeitech.runnable;

import android.os.Handler;

import com.taipeitech.utility.CreditConnector;

import java.util.ArrayList;

public class StandardCreditRunnable extends BaseRunnable {
    private String year;
    private String department;
    private int index;

    public StandardCreditRunnable(Handler handler, String year, int index,
                                  String department) {
        super(handler);
        this.year = year;
        this.index = index;
        this.department = department;
    }

    @Override
    public void run() {
        try {
            ArrayList<String> result = CreditConnector.getStandardCredit(year,
                    index, department);
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }
}
