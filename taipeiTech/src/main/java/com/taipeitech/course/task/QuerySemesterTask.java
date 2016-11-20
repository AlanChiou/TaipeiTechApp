package com.taipeitech.course.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.taipeitech.utility.Constants;
import com.taipeitech.course.CourseFragment;
import com.taipeitech.model.Model;
import com.taipeitech.utility.CourseConnector;
import com.taipeitech.utility.NportalConnector;

import java.lang.ref.WeakReference;

/**
 * Created by Alan on 2015/9/13.
 */
public class QuerySemesterTask extends AsyncTask<String, Void, Object> {
    private WeakReference<CourseFragment> mCourseFragmentWeakReference;
    private WeakReference<ProgressDialog> mProgressDialogWeakReference;

    public QuerySemesterTask(CourseFragment fragment) {
        mCourseFragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        CourseFragment fragment = mCourseFragmentWeakReference.get();
        if (fragment != null) {
            ProgressDialog progressDialog = ProgressDialog.show(fragment.getContext(), null, "學期清單查詢中...");
            mProgressDialogWeakReference = new WeakReference<>(progressDialog);
        } else {
            cancel(true);
        }
    }

    @Override
    protected Object doInBackground(String... params) {
        int retryCount = 0;
        Object result;
        do {
            try {
                if (!NportalConnector.isLogin()) {
                    String account = Model.getInstance().getAccount();
                    String password = Model.getInstance().getPassword();
                    NportalConnector.login(account, password);
                }
                if (!CourseConnector.isLogin()) {
                    CourseConnector.loginCourse();
                }
                result = CourseConnector.getCourseSemesters(params[0]);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
                retryCount++;
            }
        } while (retryCount <= Constants.RETRY_MAX_COUNT_INT);
        return result;
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);
        ProgressDialog progressDialog = mProgressDialogWeakReference.get();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        CourseFragment fragment = mCourseFragmentWeakReference.get();
        if (fragment != null) {
            fragment.obtainSemesterList(object);
        }
    }
}
