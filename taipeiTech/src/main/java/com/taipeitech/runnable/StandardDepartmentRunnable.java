package com.taipeitech.runnable;

import android.os.Handler;

import com.taipeitech.utility.CreditConnector;

import java.util.ArrayList;

public class StandardDepartmentRunnable extends BaseRunnable {
    private String year;
    private int index;

    public StandardDepartmentRunnable(Handler handler, String year, int index) {
        super(handler);
        this.year = year;
        this.index = index;
    }

    @Override
    public void run() {
        try {
            ArrayList<String> result = CreditConnector.getDepartmentList(year,
                    index);
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }
}
