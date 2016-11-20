package com.taipeitech.runnable;

import android.os.Handler;

import com.taipeitech.utility.NtutccLoginConnector;

public class LoginNtutccRunnable extends BaseRunnable {
    private static final String TEST_URL = "http://www.google.com/";

    public LoginNtutccRunnable(Handler handler) {
        super(handler);
    }

    @Override
    public void run() {
        try {
            String result = NtutccLoginConnector.getRedirectUri(TEST_URL);
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }
}
