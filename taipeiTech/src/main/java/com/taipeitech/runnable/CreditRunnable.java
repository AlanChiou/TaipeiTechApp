package com.taipeitech.runnable;

import android.os.Handler;

import com.taipeitech.model.StudentCredit;
import com.taipeitech.utility.CreditConnector;

public class CreditRunnable extends BaseRunnable {
    Handler progressHandler;

    public CreditRunnable(Handler handler, Handler progressHandler) {
        super(handler);
        this.progressHandler = progressHandler;
    }

    @Override
    public void run() {
        try {
            StudentCredit result = CreditConnector.getCredits(progressHandler);
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }
}
