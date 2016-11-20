package com.taipeitech.runnable;

import android.os.Handler;

import com.taipeitech.utility.NtutccLoginConnector;

public class LoginNtutccWay1Runnable extends BaseRunnable {
    private String account;
    private String password;

    public LoginNtutccWay1Runnable(Handler handler, String account,
                                   String password) {
        super(handler);
        this.account = account;
        this.password = password;
    }

    @Override
    public void run() {
        try {
            String result = NtutccLoginConnector.login_1(account, password);
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }

}