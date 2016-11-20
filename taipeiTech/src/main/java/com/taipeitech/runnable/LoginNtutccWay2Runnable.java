package com.taipeitech.runnable;

import android.os.Handler;

import com.taipeitech.utility.NtutccLoginConnector;

public class LoginNtutccWay2Runnable extends BaseRunnable {
    private String account;
    private String password;
    private String uri;

    public LoginNtutccWay2Runnable(Handler handler, String uri, String account,
                                   String password) {
        super(handler);
        this.account = account;
        this.password = password;
        this.uri = uri;
    }

    @Override
    public void run() {
        try {
            String result = NtutccLoginConnector.login_2_1(uri, account,
                    password);
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }
}