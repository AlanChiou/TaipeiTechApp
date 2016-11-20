package com.taipeitech.runnable;

import android.content.Context;
import android.os.Handler;

import com.taipeitech.utility.Connector;

public class FileDownloadRunnable extends BaseRunnable {
    private Context context;
    private String fileUrl;
    private String fileName;

    public FileDownloadRunnable(Handler handler, Context context,
                                String file_url, String file_name) {
        super(handler);
        this.context = context;
        fileUrl = file_url;
        fileName = file_name;
    }

    @Override
    public void run() {

        boolean result = Connector.download(context, fileUrl, fileName);
        if (result) {
            sendRefreshMessage(null);
        } else {
            sendErrorMessage(null);
        }
    }
}
