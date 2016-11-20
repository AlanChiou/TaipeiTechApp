package com.taipeitech.credit;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taipeitech.R;
import com.taipeitech.utility.SlideAnimator;
import com.taipeitech.utility.SlideAnimator.SlideAnimationListener;

public class CreditGroupView extends LinearLayout implements OnClickListener {
    private SlideAnimator animator;

    public CreditGroupView(Context context) {
        super(context);
        inflate(context, R.layout.credit_group, this);
        View title_background = findViewById(R.id.title_background);
        title_background.setOnClickListener(this);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        container.setVisibility(View.GONE);
        animator = new SlideAnimator(context, container);
    }

    public void setSlideAnimationListener(SlideAnimationListener listener) {
        animator.setSlideAnimationListener(listener);
    }

    public void setGroupTitle(String title) {
        TextView group_title = (TextView) findViewById(R.id.group_title);
        group_title.setText(title);
    }

    public void setGroupPS(String ps) {
        TextView group_ps = (TextView) findViewById(R.id.group_ps);
        group_ps.setText(ps);
    }

    @Override
    public void addView(View v) {
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        container.addView(v);
    }

    @Override
    public void removeAllViews() {
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        container.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_background:
                animator.toggle();
                break;

        }
    }
}
