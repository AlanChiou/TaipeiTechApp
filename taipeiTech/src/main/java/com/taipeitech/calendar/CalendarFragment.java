package com.taipeitech.calendar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.taipeitech.BaseFragment;
import com.taipeitech.R;
import com.taipeitech.model.Model;
import com.taipeitech.model.YearCalendar;
import com.taipeitech.runnable.BaseRunnable;
import com.taipeitech.runnable.CalendarRunnable;
import com.taipeitech.utility.WifiUtility;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;

public class CalendarFragment extends BaseFragment implements OnClickListener,
        OnPageChangeListener {

    private static View fragmentView = null;
    private static ProgressDialog progressDialog = null;
    private Calendar calendar = Calendar.getInstance();
    private CalendarHandler calendarHandler = new CalendarHandler(this);
    private ViewPager calendarViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_calendar, container, false);
        int[] ids = {R.id.leftButton, R.id.rightButton,
                R.id.calendarYearMonthTextView, R.id.start_button};
        for (int id : ids) {
            View button = fragmentView.findViewById(id);
            button.setOnClickListener(this);
        }
        calendarViewPager = (ViewPager) fragmentView
                .findViewById(R.id.calendar_viewpager);
        calendarViewPager.setOnPageChangeListener(this);
        calendar.set(Calendar.DAY_OF_MONTH, 15);
        showYearMonth();
        setData();
        return fragmentView;
    }

    @Override
    public void onDestroy() {
        calendarHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void showYearMonth() {
        TextView year_month = (TextView) fragmentView
                .findViewById(R.id.calendarYearMonthTextView);
        year_month.setText(String.format(Locale.US, "%tB %tY", calendar,
                calendar));
    }

    private void setData() {
        View start_button = fragmentView.findViewById(R.id.start_button);
        if (Model.getInstance().getYearCalendar() != null) {
            start_button.setVisibility(View.GONE);
            calendarViewPager.setAdapter(new CalendarPageAdapter(getActivity(),
                    calendar));
            calendarViewPager.setCurrentItem(1);
        } else {
            start_button.setVisibility(View.VISIBLE);
        }
    }

    private void obtainCalendarResult(YearCalendar result) {
        if (result != null) {
            Model.getInstance().setYearCalendar(result);
            Model.getInstance().saveYearCalendar();
            showYearMonth();
            setData();
            Toast.makeText(getActivity(), "行事曆更新完成，可以開始瀏覽行事曆！",
                    Toast.LENGTH_LONG).show();
        } else {
            showAlertMessage("此服務發生錯誤，請檢查網路狀態或使用原網頁查詢！");
        }
        progressDialog.dismiss();
    }

    private void updateCalendar() {
        if (WifiUtility.isNetworkAvailable(getActivity())) {
            progressDialog = ProgressDialog.show(getActivity(), null, "資料讀取中~",
                    true);
            Thread t = new Thread(new CalendarRunnable(
                    new CalendarHandler(this)));
            t.start();
        } else {
            Toast.makeText(getActivity(), R.string.check_network_available,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.analytics_category_calendar))
                        .setAction(getString(R.string.analytics_action_update))
                        .setLabel(getString(R.string.analytics_label_click))
                        .build());
                updateCalendar();
                break;
            case R.id.leftButton:
                calendarViewPager.setCurrentItem(0, true);
                break;
            case R.id.rightButton:
                calendarViewPager.setCurrentItem(2, true);
                break;
            case R.id.calendarYearMonthTextView:
                if (Model.getInstance().getYearCalendar() != null) {
                    MonthPickerDialog month_dialog = new MonthPickerDialog(
                            getActivity(), calendar, Model.getInstance()
                            .getYearCalendar().getYear());
                    month_dialog
                            .setOnNegativeButtonClickListener(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    int month = ((MonthPickerDialog) dialog)
                                            .getMonth();
                                    int year = ((MonthPickerDialog) dialog)
                                            .getYear();
                                    calendar.set(Calendar.YEAR, year);
                                    calendar.set(Calendar.MONTH, month - 1);
                                    showYearMonth();
                                    setData();
                                }
                            });
                    month_dialog.show();
                } else {
                    Toast.makeText(getActivity(), "請先更新行事曆！", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_calendar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_update:
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.analytics_category_calendar))
                        .setAction(getString(R.string.analytics_action_update))
                        .setLabel(getString(R.string.analytics_label_click))
                        .build());
                updateCalendar();
                break;
        }
        return true;
    }

    @Override
    public int getTitleColorId() {
        return R.color.dark_orange;
    }

    @Override
    public int getTitleStringId() {
        return R.string.calendar_text;
    }

    static class CalendarHandler extends Handler {
        private WeakReference<CalendarFragment> fragmentRef;

        public CalendarHandler(CalendarFragment fragment) {
            fragmentRef = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    if (msg.obj instanceof YearCalendar) {
                        YearCalendar result = (YearCalendar) msg.obj;
                        CalendarFragment fragment = fragmentRef.get();
                        if (fragment != null) {
                            fragment.obtainCalendarResult(result);
                        }
                    }
                    break;
                case BaseRunnable.ERROR:
                    CalendarFragment fragment = fragmentRef.get();
                    if (fragment != null) {
                        fragment.showAlertMessage((String) msg.obj);
                    }
                    break;
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                setData();
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                break;
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                calendar.add(Calendar.MONTH, -1);
                showYearMonth();
                break;
            case 2:
                calendar.add(Calendar.MONTH, 1);
                showYearMonth();
                break;
        }
    }

}
