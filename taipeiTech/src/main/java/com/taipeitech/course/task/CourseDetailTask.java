package com.taipeitech.course.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.taipeitech.course.CourseFragment;
import com.taipeitech.model.Model;
import com.taipeitech.utility.NportalConnector;

import java.lang.ref.WeakReference;

/**
 * Created by Alan on 2015/9/13.
 */
public class CourseDetailTask extends AsyncTask<String, Void, Object> {
    private WeakReference<CourseFragment> mCourseFragmentWeakReference;
    private WeakReference<ProgressDialog> mProgressDialogWeakReference;

    public CourseDetailTask(CourseFragment fragment) {
        mCourseFragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        CourseFragment fragment = mCourseFragmentWeakReference.get();
        if (fragment != null && !NportalConnector.isLogin()) {
            ProgressDialog progressDialog = ProgressDialog.show(fragment.getContext(), null, "登入校園入口網站中...");
            mProgressDialogWeakReference = new WeakReference<>(progressDialog);
        }
    }

    @Override
    protected Object doInBackground(String... params) {
        Object result;
        try {
            if (!NportalConnector.isLogin()) {
                String account = Model.getInstance().getAccount();
                String password = Model.getInstance().getPassword();
                NportalConnector.login(account, password);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);
        if (mProgressDialogWeakReference != null) {
            ProgressDialog progressDialog = mProgressDialogWeakReference.get();
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
        CourseFragment fragment = mCourseFragmentWeakReference.get();
        if (fragment != null) {
            fragment.startCourseDetail(object);
        }
    }
}
