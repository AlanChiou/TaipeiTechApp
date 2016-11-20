package com.taipeitech.credit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.taipeitech.MenuSpinner;
import com.taipeitech.R;
import com.taipeitech.model.StandardCredit;
import com.taipeitech.runnable.BaseRunnable;
import com.taipeitech.runnable.StandardCreditRunnable;
import com.taipeitech.runnable.StandardDepartmentRunnable;
import com.taipeitech.runnable.StandardDivisionRunnable;
import com.taipeitech.runnable.StandardYearRunnable;
import com.taipeitech.utility.Utility;
import com.taipeitech.utility.WifiUtility;

import java.util.ArrayList;

public class CreditStandardDialog extends AlertDialog implements
        View.OnClickListener, OnShowListener {

    private static View contentView;
    private String year = null;
    private int division_index;
    private MenuSpinner year_list;
    private ArrayAdapter<String> year_adapter;
    private MenuSpinner division_list;
    private ArrayAdapter<String> division_adapter;
    private MenuSpinner department_list;
    private ArrayAdapter<String> department_adapter;
    private ArrayList<String> years = new ArrayList<String>();
    private ArrayList<String> divisions = new ArrayList<String>();
    private ArrayList<String> departments = new ArrayList<String>();
    private Boolean isUser = false;
    private StandardCredit standardCredit = null;
    private Boolean isCorrect = false;
    private ProgressDialog progressDialog;
    private DialogListener dialogListener;

    public CreditStandardDialog(Context context, StandardCredit standardCredit) {
        super(context);
        setTitle("畢業學分標準設定");
        setView(getView(standardCredit));
        setButton(BUTTON_NEGATIVE, "取消", (OnClickListener) null);
        setButton(BUTTON_POSITIVE, "儲存", (OnClickListener) null);
        setOnShowListener(this);
    }

    public void setDialogListener(DialogListener listener) {
        dialogListener = listener;
    }

    private View getView(StandardCredit standardCredit) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        contentView = inflater.inflate(R.layout.credit_standard_dialog, null);
        if (standardCredit != null) {
            years.add(standardCredit.getYearText());
            divisions.add(standardCredit.getDivisionText());
            departments.add(standardCredit.getDepartmentText());
        } else {
            years.add("請選擇入學年度");
        }
        year_list = (MenuSpinner) contentView.findViewById(R.id.year_list);
        year_adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, years);
        year_list.setAdapter(year_adapter);
        year_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year_list
                .setOnItemSelectedEvenIfUnchangedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {
                        isCorrect = false;
                        if (years.size() == 1) {
                            return;
                        }
                        if (isUser) {
                            progressDialog = ProgressDialog.show(getContext(),
                                    null, "學制清單讀取中~", true);
                            year = years.get(position).split(" ")[1];
                            Thread t = new Thread(new StandardDivisionRunnable(
                                    divisionHandler, year));
                            t.start();
                            lockSpinner(division_list, true);
                            lockSpinner(department_list, true);
                        }
                        isUser = false;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });

        year_list.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (WifiUtility.isNetworkAvailable(getContext())) {
                        progressDialog = ProgressDialog.show(getContext(),
                                null, "入學年度清單讀取中~", true);
                        Thread t = new Thread(new StandardYearRunnable(
                                yearHandler));
                        t.start();
                        isUser = false;
                    } else {
                        Toast.makeText(getContext(),
                                R.string.check_network_available,
                                Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            }
        });

        division_list = (MenuSpinner) contentView
                .findViewById(R.id.division_list);
        division_adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, divisions);
        division_list.setAdapter(division_adapter);
        division_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        division_list
                .setOnItemSelectedEvenIfUnchangedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int position, long arg3) {
                        isCorrect = false;
                        if (isUser) {
                            progressDialog = ProgressDialog.show(getContext(),
                                    null, "系所清單讀取中~", true);
                            division_index = position;
                            Thread t = new Thread(
                                    new StandardDepartmentRunnable(
                                            departmentHandler, year,
                                            division_index));
                            t.start();
                            lockSpinner(department_list, true);
                        }
                        isUser = false;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });

        lockSpinner(division_list, true);

        department_list = (MenuSpinner) contentView
                .findViewById(R.id.department_list);
        department_adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, departments);
        department_list.setAdapter(department_adapter);
        department_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        department_list
                .setOnItemSelectedEvenIfUnchangedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int position, long arg3) {
                        isCorrect = false;
                        if (isUser) {
                            progressDialog = ProgressDialog.show(getContext(),
                                    null, "畢業學分標準讀取中~", true);
                            Thread t = new Thread(new StandardCreditRunnable(
                                    creditsHandler, year, division_index,
                                    departments.get(position)));
                            t.start();
                        }
                        isUser = false;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
        lockSpinner(department_list, true);
        showStandardCredits(standardCredit);
        return contentView;
    }

    private void lockSpinner(MenuSpinner spinner, boolean isLock) {
        if (isLock) {
            spinner.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        } else {
            spinner.setOnTouchListener(null);
        }
    }

    private void showStandardCredits(StandardCredit standardCredit) {
        if (standardCredit != null) {
            String types[] = getContext().getResources().getStringArray(
                    R.array.type_name);
            LinearLayout container = (LinearLayout) contentView
                    .findViewById(R.id.container);
            container.removeAllViews();
            LinearLayout.LayoutParams params = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);
            for (int i = 0; i < 8; i++) {
                TextView text = new TextView(getContext());
                text.setLayoutParams(params);
                text.setTextAppearance(getContext(),
                        android.R.style.TextAppearance_Medium);
                text.setTextColor(getContext().getResources().getColor(
                        R.color.darken));
                text.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                if (i % 2 == 0) {
                    text.setBackgroundResource(R.color.cloud);
                } else {
                    text.setBackgroundResource(android.R.color.transparent);
                }
                text.setText(types[i] + "："
                        + String.valueOf(standardCredit.getCredits().get(i)));
                container.addView(text);
            }
        }
    }

    private ArrayList<String> castStringArray(Object object) {
        if (!(object instanceof ArrayList)) {
            return null;
        }
        ArrayList<?> list = (ArrayList<?>) object;
        ArrayList<String> temp = new ArrayList<String>();
        for (Object ob : list) {
            if (ob instanceof String) {
                temp.add((String) ob);
            } else if (ob == null) {
                temp.add(null);
            } else {
                return null;
            }
        }
        return temp;
    }

    private Handler yearHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    if (msg.obj instanceof ArrayList<?>) {
                        ArrayList<String> result = castStringArray(msg.obj);
                        years.clear();
                        years.addAll(result);
                        year_adapter.notifyDataSetChanged();
                        year_list.setOnTouchListener(null);
                        year_list.setSelection(0);
                        year_list.performClick();
                    }
                    break;
                case BaseRunnable.ERROR:
                    Utility.showDialog("提示", (String) msg.obj, getContext());
                    break;
            }
            progressDialog.dismiss();
            isUser = true;
        }
    };

    private Handler divisionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    if (msg.obj instanceof ArrayList<?>) {
                        ArrayList<String> result = castStringArray(msg.obj);
                        divisions.clear();
                        divisions.addAll(result);
                        division_adapter.notifyDataSetChanged();
                        division_list.setOnTouchListener(null);
                        division_list.setSelection(0);
                    }
                    break;
                case BaseRunnable.ERROR:
                    Utility.showDialog("提示", (String) msg.obj, getContext());
                    break;
            }
            progressDialog.dismiss();
            isUser = true;
        }
    };

    private Handler departmentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    if (msg.obj instanceof ArrayList<?>) {
                        ArrayList<String> result = castStringArray(msg.obj);
                        departments.clear();
                        departments.addAll(result);
                        department_adapter.notifyDataSetChanged();
                        department_list.setOnTouchListener(null);
                        department_list.setSelection(0);
                    }
                    break;
                case BaseRunnable.ERROR:
                    Utility.showDialog("提示", (String) msg.obj, getContext());
                    break;
            }
            progressDialog.dismiss();
            isUser = true;
        }
    };

    private Handler creditsHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    if (msg.obj instanceof ArrayList<?>) {
                        ArrayList<String> result = castStringArray(msg.obj);
                        standardCredit = new StandardCredit();
                        standardCredit.setCredits(result);
                        standardCredit.setYearText((String) year_list
                                .getSelectedItem());
                        standardCredit.setDivisionText((String) division_list
                                .getSelectedItem());
                        standardCredit.setDepartmentText((String) department_list
                                .getSelectedItem());
                        showStandardCredits(standardCredit);
                        isCorrect = true;
                    }
                    break;
                case BaseRunnable.ERROR:
                    Utility.showDialog("提示", (String) msg.obj, getContext());
                    break;
            }
            progressDialog.dismiss();
            isUser = true;
        }
    };

    @Override
    public void onClick(View v) {
        if (standardCredit != null && isCorrect) {
            dialogListener.onSaveButtonClick(standardCredit);
            dismiss();
        } else {
            Toast.makeText(getContext(), "入學標準未完成設定！", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        getButton(BUTTON_POSITIVE).setOnClickListener(this);

    }

    public interface DialogListener {
        public void onSaveButtonClick(StandardCredit standardCredit);

    }

}
