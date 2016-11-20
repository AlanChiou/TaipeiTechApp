package com.taipeitech.runnable;

import android.os.Handler;

import com.taipeitech.utility.CreditConnector;

public class CreditLoginRunnable extends BaseRunnable {
    public CreditLoginRunnable(Handler handler) {
        super(handler);
    }

    @Override
    public void run() {
        try {
            String result = CreditConnector.loginCredit();
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }

}
