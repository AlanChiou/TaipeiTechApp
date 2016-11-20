package com.taipeitech.credit;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.taipeitech.R;
import com.taipeitech.model.CreditInfo;
import com.taipeitech.model.Model;
import com.taipeitech.model.SemesterCredit;
import com.taipeitech.utility.Utility;

import java.util.ArrayList;

public class CreditTypeListActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private int type;
    private LinearLayout credit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credit_type_list);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        Intent i = getIntent();
        type = i.getIntExtra("type", 0);
        String type_text = getResources().getStringArray(R.array.type_name)[type - 1];
        setActionBar(type_text + "  總和："
                + Model.getInstance().getStudentCredit().getTypeCredits(type));
        credit = (LinearLayout) findViewById(R.id.credit);
        createSemesterGroups();
    }

    private void createSemesterGroups() {
        LayoutInflater inflater = getLayoutInflater();
        for (SemesterCredit semesterCredit : Model.getInstance().getStudentCredit()
                .getSemesterCredits()) {
            CreditGroupView group = new CreditGroupView(this);
            group.setGroupTitle(semesterCredit.getYear() + "-"
                    + semesterCredit.getSemester());
            group.setGroupPS("小計：" + semesterCredit.getTypeCredits(type));
            ArrayList<CreditInfo> credits = semesterCredit.getCredits();
            int i = 0;
            for (CreditInfo credit : credits) {
                if (credit.getType() == type) {
                    View item = inflater.inflate(R.layout.credit_item, null,
                            false);
                    item.setBackgroundResource(i % 2 == 0 ? R.color.white
                            : R.color.cloud);
                    TextView courseNo = (TextView) item
                            .findViewById(R.id.courseNo);
                    courseNo.setText(credit.getCourseNo());
                    TextView courseName = (TextView) item
                            .findViewById(R.id.courseName);
                    courseName.setText(credit.getCourseName());
                    TextView credit_text = (TextView) item
                            .findViewById(R.id.credit);
                    credit_text.setText(String.valueOf(credit.getCredit()));
                    Spinner type = (Spinner) item.findViewById(R.id.type);
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                            this, R.layout.credit_type_textview, getResources()
                            .getStringArray(R.array.credit_type));
                    dataAdapter
                            .setDropDownViewResource(R.layout.credit_type_textview);
                    type.setAdapter(dataAdapter);
                    type.setSelection(credit.getType());
                    type.setClickable(false);
                    type.setEnabled(false);
                    TextView score = (TextView) item.findViewById(R.id.score);
                    score.setText(credit.getScore());
                    group.addView(item);
                    i++;
                }
            }
            if (i > 0) {
                credit.addView(group);
            }
        }
    }

    public void setActionBar(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            actionBar.setTitle(title);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue)));
        }
        Utility.setStatusBarColor(this, getResources().getColor(R.color.dark_blue));
    }
}
