package com.taipeitech;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.taipeitech.activity.ActivityFragment;
import com.taipeitech.calendar.CalendarFragment;
import com.taipeitech.course.CourseFragment;
import com.taipeitech.credit.CreditFragment;
import com.taipeitech.setting.AccountSettingFragment;
import com.taipeitech.utility.PermissionRequestListener;
import com.taipeitech.wifi.WifiFragment;

/**
 * Created by Alan on 2015/9/12.
 */
public class MainActivity extends AppCompatActivity {
    private Tracker tracker;
    private Toolbar mToolbar;
    private NavigationView mSideBar;
    private DrawerLayout mDrawerLayout;
    private CourseFragment courseFragment = new CourseFragment();
    private CreditFragment creditFragment = new CreditFragment();
    private WifiFragment wifiFragment = new WifiFragment();
    private CalendarFragment calendarFragment = new CalendarFragment();
    private AccountSettingFragment accountSettingFragment = new AccountSettingFragment();
    private ActivityFragment activityFragment = new ActivityFragment();
    private BaseFragment currentFragment;
    private Boolean lockFinish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        setupSidePanel();
        setupDrawer();
        setupVersionText();
        String first_func = MainApplication.readSetting("first_func");
        if (!TextUtils.isEmpty(first_func)) {
            switchFragment(Integer.parseInt(first_func));
        } else {
            switchFragment(2);
        }
    }

    private void setupSidePanel() {
        mSideBar = (NavigationView) findViewById(R.id.sidebar);
        mSideBar.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.sidebar_item_activity:
                        switchFragment(0);
                        break;
                    case R.id.sidebar_item_calendar:
                        switchFragment(1);
                        break;
                    case R.id.sidebar_item_course:
                        switchFragment(2);
                        break;
                    case R.id.sidebar_item_credit:
                        switchFragment(3);
                        break;
                    case R.id.sidebar_item_wifi:
                        switchFragment(4);
                        break;
                    case R.id.sidebar_item_account_setting:
                        switchFragment(5);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

    }

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                closeSoftKeyboard();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }


    private void setupVersionText() {
        try {
            String versionName = getPackageManager().getPackageInfo(
                    getPackageName(), 0).versionName;
            TextView version_text_view = (TextView) findViewById(R.id.main_version_text_view);
            version_text_view.setText(getString(R.string.version_text, versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void switchFragment(int index) {
        switch (index) {
            case 0:
                tracker.setScreenName(getString(R.string.analytics_category_activity));
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_activity_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_activity_selector));
                changeFragment(activityFragment);
                break;
            case 1:
                tracker.setScreenName(getString(R.string.analytics_category_calendar));
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_calendar_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_calendar_selector));
                changeFragment(calendarFragment);
                break;
            case 2:
                tracker.setScreenName(getString(R.string.analytics_category_course));
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_course_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_course_selector));
                changeFragment(courseFragment);
                break;
            case 3:
                tracker.setScreenName(getString(R.string.analytics_category_credit));
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_credit_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_credit_selector));
                changeFragment(creditFragment);
                break;
            case 4:
                tracker.setScreenName(getString(R.string.analytics_category_wifi));
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_wifi_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_wifi_selector));
                changeFragment(wifiFragment);
                break;
            case 5:
                tracker.setScreenName(getString(R.string.analytics_category_setting));
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_account_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_account_selector));
                changeFragment(accountSettingFragment);
                break;
        }
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void changeFragment(BaseFragment to) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        to.setAnimationListener(fragmentAnimationListener);
        if (currentFragment != null) {
            if (currentFragment.equals(to)) {
                return;
            }
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(currentFragment)
                        .add(R.id.fragment_container, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(currentFragment).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        } else {
            transaction.add(R.id.fragment_container, to).commit();
        }
        currentFragment = to;
        setActionBar();
    }

    public void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(currentFragment.getTitleStringId());
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(currentFragment.getTitleColorId())));
        }
        setStatusBarColor(getResources().getColor(currentFragment.getTitleColorId()));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            if (color == Color.BLACK
                    && window.getNavigationBarColor() == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setStatusBarColor(color);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (lockFinish) {
                Toast.makeText(MainActivity.this, R.string.press_again_to_exit,
                        Toast.LENGTH_SHORT).show();
                lockFinish = false;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(2000);
                            }
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        lockFinish = true;
                    }
                };
                thread.start();
            } else {
                finish();
            }
        }
    }

    public void showAppDialog(View view) {
        ImageView qrImageView = new ImageView(this);
        qrImageView.setImageResource(R.drawable.qrcode);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title);
        builder.setView(qrImageView);
        builder.setNegativeButton(R.string.play_store_text,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=com.taipeitech"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this,
                                    R.string.play_store_not_support,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        builder.setPositiveButton(R.string.donate_text,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this,
                                DonateActivity.class);
                        startActivity(intent);
                    }
                });
        builder.create().show();
    }

    private Animation.AnimationListener fragmentAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    public void closeSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }

    private static final int PERMISSION_REQUEST_CODE = 1;
    private PermissionRequestListener mPermissionRequestListener = null;

    public void requestPermission(String permission, PermissionRequestListener listener) {
        mPermissionRequestListener = listener;
        ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                int requestResult = PackageManager.PERMISSION_DENIED;
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    requestResult = grantResults[0];
                }
                if (mPermissionRequestListener != null) {
                    mPermissionRequestListener.onRequestPermissionsResult(permissions[0], requestResult);
                    mPermissionRequestListener = null;
                }
            }
        }
    }
}
