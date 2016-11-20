package com.taipeitech.credit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.taipeitech.BaseFragment;
import com.taipeitech.R;
import com.taipeitech.model.CreditInfo;
import com.taipeitech.model.GeneralCredit;
import com.taipeitech.model.Model;
import com.taipeitech.model.SemesterCredit;
import com.taipeitech.model.StandardCredit;
import com.taipeitech.model.StudentCredit;
import com.taipeitech.runnable.BaseRunnable;
import com.taipeitech.runnable.CreditLoginRunnable;
import com.taipeitech.runnable.CreditRunnable;
import com.taipeitech.utility.CreditConnector;
import com.taipeitech.utility.NportalConnector;
import com.taipeitech.utility.SlideAnimator.SlideAnimationListener;
import com.taipeitech.utility.Utility;
import com.taipeitech.utility.WifiUtility;

import java.util.ArrayList;

public class CreditFragment extends BaseFragment implements OnClickListener,
        CreditStandardDialog.DialogListener {
    private static View fragmentView;
    private ProgressDialog pd;
    private Thread nextThread = null;
    private LinearLayout credit;
    private CreditGroupView total_group = null;
    public static int CONTENT_ROW_HEIGHT = 100;
    private boolean isUser = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        CONTENT_ROW_HEIGHT = Math.round(displaymetrics.widthPixels / 8);
        fragmentView = inflater.inflate(R.layout.fragment_credit, container, false);
        credit = (LinearLayout) fragmentView.findViewById(R.id.credit);
        View start_button = fragmentView.findViewById(R.id.start_button);
        start_button.setOnClickListener(this);
        initView();
        return fragmentView;
    }

    private void initView() {
        View start_button = fragmentView.findViewById(R.id.start_button);
        StudentCredit studentCredit = Model.getInstance().getStudentCredit();
        credit.removeAllViews();
        total_group = null;
        if (studentCredit != null) {
            start_button.setVisibility(View.GONE);
            createTotalGroup(studentCredit);
            if (studentCredit.getGeneralCredits().size() > 0) {
                createGeneralGroup(studentCredit);
            }
            createSemesterGroups(studentCredit);
        } else {
            start_button.setVisibility(View.VISIBLE);
        }
    }

    public void refreshTotal() {
        StudentCredit studentCredit = Model.getInstance().getStudentCredit();
        String types[] = getResources().getStringArray(R.array.type_name);
        if (total_group != null) {
            String credit_text;
            StandardCredit standardCredit = Model.getInstance()
                    .getStandardCredit();
            if (standardCredit != null) {
                credit_text = String.valueOf(studentCredit.getTotalCredits())
                        + " / " + standardCredit.getCredits().get(7);
            } else {
                credit_text = String.valueOf(studentCredit.getTotalCredits());
            }
            total_group.setGroupPS("實得學分:" + credit_text);
            total_group.removeAllViews();
            for (int i = 1; i < 7; i++) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                TextView text = (TextView) inflater.inflate(
                        R.layout.credit_textview, null, false);
                text.setBackgroundResource(i % 2 == 0 ? R.color.cloud
                        : R.color.white);
                if (standardCredit != null) {
                    credit_text = String.valueOf(studentCredit
                            .getTypeCredits(i))
                            + " / "
                            + standardCredit.getCredits().get(i - 1);
                } else {
                    credit_text = String.valueOf(studentCredit
                            .getTypeCredits(i));
                }
                text.setText(types[i - 1] + "：" + credit_text);
                text.setTag(i);
                if (studentCredit.getTypeCredits(i) > 0) {
                    text.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(),
                                    CreditTypeListActivity.class);
                            intent.putExtra("type", (Integer) view.getTag());
                            getActivity().startActivity(intent);
                        }
                    });
                }
                total_group.addView(text);
            }
        }
    }

    private void createTotalGroup(StudentCredit studentCredit) {
        total_group = new CreditGroupView(getActivity());
        total_group.setGroupTitle("學分總覽");
        refreshTotal();
        credit.addView(total_group);
    }

    private void createGeneralGroup(StudentCredit studentCredit) {
        CreditGroupView group = new CreditGroupView(getActivity());
        group.setGroupTitle("博雅總覽");
        group.setGroupPS("實得核心:" + studentCredit.getGeneralCoreCredits()
                + "  實得選修:" + studentCredit.getGeneralCommonCredits());
        int count = studentCredit.getGeneralCredits().size();
        String[] titles = new String[count];
        float[] totals = new float[count];
        float[] cores = new float[count];
        int i = 0;
        for (GeneralCredit general : studentCredit.getGeneralCredits()) {
            titles[i] = general.getTypeName();
            totals[i] = general.getHadCoreCredit()
                    + general.getHadCommonCredit();
            cores[i] = general.getHadCoreCredit();
            i++;
        }
        int width = Utility.getScreenWidth(getActivity());
        LinearLayout.LayoutParams chart_params = new LayoutParams(width, width);
        RadarChartView radar_chart = new RadarChartView(getActivity(), count,
                titles);
        radar_chart.setId(R.id.radar_chart);
        radar_chart.setDuration(700);
        radar_chart.setOnClickListener(this);
        radar_chart.setTotalValues(totals);
        radar_chart.setCoreValues(cores);
        radar_chart.setLayoutParams(chart_params);
        group.addView(radar_chart);
        group.setSlideAnimationListener(new SlideAnimationListener() {

            @Override
            public void onSlidedUp(View v) {
                RadarChartView radar_chart = (RadarChartView) v
                        .findViewById(R.id.radar_chart);
                if (radar_chart != null) {
                    radar_chart.resetAnimation();
                }
            }

            @Override
            public void onSlidedDown(View v) {
                RadarChartView radar_chart = (RadarChartView) v
                        .findViewById(R.id.radar_chart);
                if (radar_chart != null) {
                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.analytics_category_credit))
                            .setAction(getString(R.string.analytics_action_detail))
                            .setLabel(RadarChartView.class.getSimpleName())
                            .build());
                    radar_chart.startAnimation();
                }
            }
        });
        credit.addView(group);
    }

    private void createSemesterGroups(StudentCredit studentCredit) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        for (SemesterCredit semesterCredit : studentCredit.getSemesterCredits()) {
            CreditGroupView group = new CreditGroupView(getActivity());
            group.setGroupTitle(semesterCredit.getYear() + "-"
                    + semesterCredit.getSemester());
            ArrayList<CreditInfo> credits = semesterCredit.getCredits();
            for (int i = 0; i < credits.size(); i++) {
                View item = inflater.inflate(R.layout.credit_item, null, false);
                item.setBackgroundResource(i % 2 == 0 ? R.color.white
                        : R.color.cloud);
                CreditInfo creditInfo = credits.get(i);

                TextView coursNo = (TextView) item.findViewById(R.id.courseNo);
                coursNo.setText(creditInfo.getCourseNo());
                TextView coursName = (TextView) item
                        .findViewById(R.id.courseName);
                coursName.setText(creditInfo.getCourseName());
                coursName.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            Toast.makeText(v.getContext(),
                                    ((TextView) v).getText(),
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                TextView credit = (TextView) item.findViewById(R.id.credit);
                credit.setText(String.valueOf(creditInfo.getCredit()));
                Spinner type = (Spinner) item.findViewById(R.id.type);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                        getActivity(), R.layout.credit_type_textview,
                        getResources().getStringArray(R.array.credit_type));
                dataAdapter
                        .setDropDownViewResource(R.layout.credit_type_textview);
                type.setAdapter(dataAdapter);
                type.setOnItemSelectedListener(iSlis);
                type.setOnTouchListener(new OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            isUser = true;
                        }
                        return false;
                    }
                });
                type.setTag(creditInfo);
                type.setSelection(creditInfo.getType());
                TextView score = (TextView) item.findViewById(R.id.score);
                score.setText(String.valueOf(credits.get(i).getScore()));
                group.addView(item);
            }
            credit.addView(group);
        }
    }

    private void loginNportal() {
        String account = Model.getInstance().getAccount();
        String password = Model.getInstance().getPassword();
        if (account.length() > 0 && password.length() > 0) {
            NportalConnector.login(account, password, loginHandler);
        } else {
            pd.dismiss();
            showAlertMessage("請先至帳號設定，設定校園入口網站帳號密碼！");
        }
    }

    private Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    if (nextThread != null) {
                        nextThread.start();
                        nextThread = null;
                    } else {
                        pd.dismiss();
                    }
                    break;
                case BaseRunnable.ERROR:
                    pd.dismiss();
                    Utility.showDialog("提示", (String) msg.obj, getActivity());
            }
        }
    };

    private Handler creditLoginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    Thread t = new Thread(new CreditRunnable(creditHandler,
                            progressHandler));
                    t.start();
                    break;
                case BaseRunnable.ERROR:
                    pd.dismiss();
                    Utility.showDialog("提示", (String) msg.obj, getActivity());
                    break;
            }
        }
    };

    private Handler creditHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    if (msg.obj instanceof StudentCredit) {
                        StudentCredit result = (StudentCredit) msg.obj;
                        Model.getInstance().setStudentCredit(result);
                        Model.getInstance().saveStudentCredit();
                        initView();
                        pd.dismiss();
                        if (CreditConnector.isHaveError) {
                            Utility.showDialog("學分資訊查詢完成",
                                    "可以開始瀏覽學分及歷年成績！\n查詢過程有發生錯誤，請重新查詢或自行修正！",
                                    getActivity());
                        } else {
                            Utility.showDialog("學分資訊查詢完成", "可以開始瀏覽學分及歷年成績！",
                                    getActivity());
                        }
                    }
                    break;
                case BaseRunnable.ERROR:
                    pd.dismiss();
                    Utility.showDialog("提示", (String) msg.obj, getActivity());
                    break;
            }
        }
    };

    public Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (msg.obj instanceof Integer) {
                        int i = (Integer) msg.obj;
                        pd.setProgress(i);
                    }
                    break;
                case 1:
                    if (msg.obj instanceof Integer) {
                        int count = (Integer) msg.obj;
                        pd.setMax(count);
                    }
                    break;

            }
        }
    };

    private void queryCredit() {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.analytics_category_credit))
                .setAction(getString(R.string.analytics_action_update))
                .setLabel(getString(R.string.analytics_label_click))
                .build());
        if (WifiUtility.isNetworkAvailable(getActivity())) {
            if(Utility.checkAccount(getActivity())) {
                pd = new ProgressDialog(getActivity());
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setProgress(0);
                pd.setTitle("學分資料查詢中~");
                pd.setCancelable(false);
                pd.show();
                nextThread = new Thread(new CreditLoginRunnable(creditLoginHandler));
                loginNportal();
            }
        } else {
            Toast.makeText(getActivity(), R.string.check_network_available,
                    Toast.LENGTH_LONG).show();
        }
    }

    private OnItemSelectedListener iSlis = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View v, int position,
                                   long arg3) {
            if (isUser) {
                CreditInfo creditInfo = (CreditInfo) ((View) v.getParent())
                        .getTag();
                if (creditInfo != null) {
                    creditInfo.setType(position);
                    Model.getInstance().saveStudentCredit();
                    refreshTotal();
                }
            }
            isUser = false;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                queryCredit();
                break;
            default:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("博雅總覽");
                View view = new CreditGeneralView(getActivity(), Model
                        .getInstance().getStudentCredit());
                builder.setView(view);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.back, null);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
    }

    @Override
    public void onSaveButtonClick(StandardCredit standardCredit) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.analytics_category_credit))
                .setAction(getString(R.string.analytics_action_save))
                .setLabel(standardCredit.getYearText() + "-" + standardCredit.getDivisionText()
                        + "-" + standardCredit.getDepartmentText())
                .build());
        Model.getInstance().setStandardCredit(standardCredit);
        Model.getInstance().saveStandardCredit();
        refreshTotal();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_credit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_query:
                queryCredit();
                break;
            case R.id.item_standard_credit:
                CreditStandardDialog dialog = new CreditStandardDialog(
                        getActivity(), Model.getInstance().getStandardCredit());
                dialog.setDialogListener(CreditFragment.this);
                dialog.show();
                break;
            case R.id.item_clear:
                Model.getInstance().deleteStudentCredit();
                initView();
                break;
            case R.id.item_clear_standard_credit:
                Model.getInstance().deleteStandardCredit();
                refreshTotal();
                break;
        }
        return true;
    }

    @Override
    public int getTitleColorId() {
        return R.color.dark_blue;
    }

    @Override
    public int getTitleStringId() {
        return R.string.credit_text;
    }
}
