package com.taipeitech;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.analytics.Tracker;

public abstract class BaseFragment extends Fragment {
    protected Tracker tracker;
    private Animation.AnimationListener mAnimationListener;

    protected void showAlertMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(R.string.back, null);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.create().show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }

    public abstract int getTitleColorId();

    public abstract int getTitleStringId();

    public void closeSoftKeyboard() {
        Activity activity = getActivity();
        if (activity != null && activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation animation;
        if (enter) {
            animation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
            animation.setAnimationListener(mAnimationListener);
        } else {
            animation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        }
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(animation);
        return animationSet;
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        mAnimationListener = listener;
    }
}
