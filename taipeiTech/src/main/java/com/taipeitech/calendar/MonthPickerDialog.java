package com.taipeitech.calendar;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.taipeitech.R;
import com.triggertrap.seekarc.SeekArc;
import com.triggertrap.seekarc.SeekArc.OnSeekArcChangeListener;

import java.util.Calendar;
import java.util.Locale;

public class MonthPickerDialog extends AlertDialog implements
        OnSeekArcChangeListener {
    private Context mContext;
    private Calendar calendar;
    private TextView month_text;
    private final static int[] months = {5, 6, 7, 8, 9, 10, 11, 0, 1, 2, 3, 4};
    private int startYear;
    private SeekArc monthSeekArc;

    public MonthPickerDialog(Context context, Calendar calendar, int start_year) {
        super(context);
        mContext = context;
        this.calendar = (Calendar) calendar.clone();
        this.calendar.set(Calendar.DAY_OF_MONTH, 15);
        if (calendar.get(Calendar.MONTH) < 7
                && calendar.get(Calendar.YEAR) <= start_year) {
            this.calendar.set(Calendar.MONTH, 7);
            this.calendar.set(Calendar.YEAR, start_year);
        } else if (calendar.get(Calendar.MONTH) > 6
                && calendar.get(Calendar.YEAR) >= start_year + 1) {
            this.calendar.set(Calendar.MONTH, 6);
            this.calendar.set(Calendar.YEAR, start_year + 1);
        }
        startYear = start_year;
        setTitle(String.format(Locale.TAIWAN, "%d 學年度行事曆", start_year - 1911));
        setDialog();
    }

    private void setDialog() {

        setCancelable(true);
        View content = LayoutInflater.from(mContext).inflate(
                R.layout.month_picker_dialog, null);
        setView(content);
        month_text = (TextView) content.findViewById(R.id.month_text);
        monthSeekArc = (SeekArc) content.findViewById(R.id.month_seekarc);
        monthSeekArc.setTouchInSide(true);
        monthSeekArc.setSweepAngle(300);
        monthSeekArc.setClockwise(false);
        monthSeekArc.setArcRotation(30);
        monthSeekArc.setOnSeekArcChangeListener(this);
        int index = months[calendar.get(Calendar.MONTH)];
        int progress = ((index == 11) ? 100 : (int) (index * (100.8 / 12)));
        monthSeekArc.setProgress(progress);
        updateMonthText();
    }

    public void setOnNegativeButtonClickListener(OnClickListener onClickListener) {
        setButton(BUTTON_POSITIVE, "確定", onClickListener);
        setButton(BUTTON_NEGATIVE, "取消", (OnClickListener) null);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    private void updateMonthText() {
        month_text.setText(String.format(Locale.US, "%tB %tY", calendar,
                calendar));
    }

    @Override
    public void onProgressChanged(SeekArc seekArc, int progress,
                                  boolean fromUser) {
        if (fromUser) {
            int index = (int) (progress / (100.8 / 12));
            if (index < 5) {
                this.calendar.set(Calendar.MONTH, index + 7);
                this.calendar.set(Calendar.YEAR, startYear);
            } else {
                this.calendar.set(Calendar.MONTH, index - 5);
                this.calendar.set(Calendar.YEAR, startYear + 1);
            }
            updateMonthText();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekArc seekArc) {

    }

    @Override
    public void onStopTrackingTouch(SeekArc seekArc) {

    }
}
