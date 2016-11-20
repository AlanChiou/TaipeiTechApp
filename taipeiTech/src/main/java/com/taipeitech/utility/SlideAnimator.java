package com.taipeitech.utility;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.taipeitech.R;

public class SlideAnimator {
    private Context context;
    private View v;
    private SlideAnimationListener listener;

    public SlideAnimator(Context context, View v) {
        this.context = context;
        this.v = v;
    }

    public void setSlideAnimationListener(SlideAnimationListener listener) {
        this.listener = listener;
    }

    public void toggle() {
        if (v.isShown()) {
            slide_up();
        } else {
            v.setVisibility(View.VISIBLE);
            slide_down();
        }
    }

    private void slide_down() {
        Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        if (a != null) {
            a.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    if (listener != null) {
                        listener.onSlidedDown(v);
                    }
                }
            });
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    private void slide_up() {
        Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        if (a != null) {
            a.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    v.setVisibility(View.GONE);
                    if (listener != null) {
                        listener.onSlidedUp(v);
                    }
                }
            });
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public interface SlideAnimationListener {
        public void onSlidedUp(View v);

        public void onSlidedDown(View v);
    }
}
