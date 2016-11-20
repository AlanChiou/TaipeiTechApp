package com.taipeitech.utility;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taipeitech.model.ActivityInfo;
import com.taipeitech.model.ActivityList;

import org.json.JSONObject;

import java.io.File;

public class ActivityConnector {
    private static final String UPDATE_JSON_URL = HttpHelper.SERVER_HOST + "activity.json";

    public static ActivityList getActivityList(Context context)
            throws Exception {
        String result = Connector.getDataByGet(UPDATE_JSON_URL, "utf-8");
        JSONObject jObject = new JSONObject(result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ActivityList activity_list = gson.fromJson(jObject.getJSONArray("data")
                .toString(), ActivityList.class);
        cleanCacheDir(context);
        for (ActivityInfo activity : activity_list) {
            downloadActivityImage(context, activity.getImage());
        }
        return activity_list;
    }

    public static void downloadActivityImage(Context context,
                                             String file_name) {
        try {
            if (!TextUtils.isEmpty(file_name)) {
                String url = Connector
                        .getRedirectUri(HttpHelper.SERVER_HOST
                                + file_name);
                Connector.download(context, url, file_name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void cleanCacheDir(Context context) {
        File dir = context.getCacheDir();
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

}
