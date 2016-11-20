package com.taipeitech.credit;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.taipeitech.R;
import com.taipeitech.model.GeneralCredit;
import com.taipeitech.model.GeneralCreditInfo;
import com.taipeitech.model.StudentCredit;

public class CreditGeneralView extends ScrollView {
    StudentCredit studentCredit;
    LinearLayout container;

    public CreditGeneralView(Context context) {
        super(context);
    }

    public CreditGeneralView(Context context, StudentCredit studentCredit) {
        this(context);
        this.studentCredit = studentCredit;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                LayoutParams.MATCH_PARENT,
                CreditFragment.CONTENT_ROW_HEIGHT * 8);
        setLayoutParams(params);
        init();
    }

    private void init() {
        container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setBackgroundResource(R.color.silver);
        for (GeneralCredit general : studentCredit.getGeneralCredits()) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            TextView title_text = (TextView) inflater.inflate(
                    R.layout.credit_textview, this, false);
            title_text.setBackgroundResource(R.color.cloud);
            title_text.setTypeface(title_text.getTypeface(), Typeface.BOLD);
            title_text.setText(general.getTypeName());
            container.addView(title_text);
            TextView text = (TextView) inflater.inflate(
                    R.layout.credit_textview, this, false);
            text.setBackgroundResource(R.color.white);
            String credit_text = "應修核心:" + general.getMustCoreCredit()
                    + "  實得核心:" + general.getHadCoreCredit() + "  實得選修:"
                    + general.getHadCommonCredit();
            text.setText(credit_text);
            container.addView(text);
            addData(general);
        }
        addView(container);
    }

    private void addData(GeneralCredit general) {
        for (GeneralCreditInfo course : general.getGenerals()) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            TextView text = (TextView) inflater.inflate(
                    R.layout.credit_textview, this, false);
            text.setBackgroundResource(R.color.white);
            String course_text = course.getYear() + "-" + course.getSem()
                    + "  " + course.getCourseName();
            text.setText(course_text);
            if (course.isCore()) {
                text.setTextColor(getResources().getColor(R.color.red));
            }
            container.addView(text);
        }
    }
}
