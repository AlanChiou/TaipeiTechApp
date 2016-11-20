package com.taipeitech.course;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.taipeitech.course.data.Semester;

import java.util.ArrayList;

/**
 * Created by Alan on 2015/10/17.
 */
public class SemesterSelector extends Button implements View.OnClickListener {
    private OnSemesterSelectedListener mOnSemesterSelectedListener;
    private ArrayList<Semester> mSemesterList = new ArrayList<>();
    private String[] mSemesterArray;

    public SemesterSelector(Context context, AttributeSet attrs) {
        super(context, attrs, android.support.v7.appcompat.R.attr.spinnerStyle);
    }

    public SemesterSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnClickListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        super.setOnClickListener(this);
    }

    public void setOnSemesterSelectedListener(OnSemesterSelectedListener listener) {
        mOnSemesterSelectedListener = listener;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
    }

    public void setSemesterList(ArrayList<Semester> list) {
        mSemesterList.clear();
        mSemesterList.addAll(list);
        mSemesterArray = new String[list.size()];
        for (int i = list.size() - 1; i >= 0; i--) {
            mSemesterArray[i] = list.get(i).toString();
        }
    }

    public void setText(Semester semester) {
        if (semester != null) {
            setText(semester.toString());
        } else {
            setText("");
        }
    }

    @Override
    public void onClick(View v) {
        if (mSemesterArray == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("學期");
        builder.setItems(mSemesterArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mOnSemesterSelectedListener != null) {
                    mOnSemesterSelectedListener.onSemesterSelected(mSemesterList.get(which));
                }
            }
        });
        builder.show();
    }

    public interface OnSemesterSelectedListener {
        void onSemesterSelected(Semester semester);
    }
}
