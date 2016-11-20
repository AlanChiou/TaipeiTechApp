package com.taipeitech.credit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.taipeitech.R;

public class RadarChartView extends View {
    private int count;
    private float angle;
    private final float angle_offset = 90;
    private final int point_radius = 5; // 頂點半徑
    private final int valueRulingCount = 3; // 等分
    private int radius;
    private int centerX;
    private int centerY;
    private String[] titles = null;
    private int margin_dp = 25;
    private Point[] pts; // 维度端点
    private Path totalPath;
    private Path corePath;
    private float[] totalValues = null; // 各维度分值
    private float[] coreValues = null; // 各维度分值
    private int maxValue = 6;
    private Point[] total_pts; // 维度端点
    private Point[] core_pts; // 维度端点
    private Paint paint;
    private Paint totalPaint;
    private Paint corePaint;
    private int anim_delta = 30;
    private int total_anim_count = 0;
    private int core_anim_count = 0;
    private int max_anim_count;
    private float total_anim_percentage = 1.0f;
    private float core_anim_percentage = 1.0f;
    private Interpolator interpolator = new OvershootInterpolator();

    public float[] getTotalValues() {
        return totalValues;
    }

    public float[] getCoreValues() {
        return totalValues;
    }

    public void setTotalValues(float[] values) {
        this.totalValues = values;
        invalidate();
    }

    public void setCoreValues(float[] values) {
        this.coreValues = values;
        invalidate();
    }

    public RadarChartView(Context context, int count, String[] titles) {
        super(context);
        this.count = count;
        this.titles = titles;
        this.angle = 360f / (float) count;
        init();
    }

    public void setDuration(int duration) {
        max_anim_count = (duration / anim_delta) / 2;
        resetAnimation();
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public void resetAnimation() {
        total_anim_percentage = 0.0f;
        core_anim_percentage = 0.0f;
        invalidate();
    }

    public void startAnimation() {
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (total_anim_count < max_anim_count) {
                    total_anim_count++;
                } else {
                    if (core_anim_count < max_anim_count) {
                        core_anim_count++;
                    }
                }
                total_anim_percentage = interpolator
                        .getInterpolation((float) total_anim_count
                                / max_anim_count);
                core_anim_percentage = interpolator
                        .getInterpolation((float) core_anim_count
                                / max_anim_count);
                invalidate();
                if (core_anim_count < max_anim_count) {
                    startAnimation();
                } else {
                    total_anim_count = 0;
                    core_anim_count = 0;
                }
            }
        }, anim_delta);
    }

    private void init() {
        paint = new Paint();
        totalPaint = new Paint();
        pts = new Point[count];
        total_pts = new Point[count];
        totalPath = new Path();
        corePaint = new Paint();
        core_pts = new Point[count];
        corePath = new Path();
        for (int i = 0; i < count; i++) {
            pts[i] = new Point();
            total_pts[i] = new Point();
            core_pts[i] = new Point();
        }
    }

    public RadarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadarChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, margin_dp, getResources()
                        .getDisplayMetrics());
        radius = Math.min(h, w) / 2 - margin;
        centerX = w / 2;
        centerY = h / 2;

        for (int i = 0; i < count; i++) {
            pts[i].x = centerX
                    + (int) (radius * Math.cos(Math.toRadians(angle_offset
                    + angle * i)));
            pts[i].y = centerY
                    - (int) (radius * Math.sin(Math.toRadians(angle_offset
                    + angle * i)));
        }
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /* 设置画布的颜色 */
        canvas.drawColor(getResources().getColor(R.color.white));

        paint.setAntiAlias(true);
        // 画边框线
        paint.setColor(getResources().getColor(R.color.darken));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (int i = 0; i < count; i++) {
            int end = i + 1 == count ? 0 : i + 1;

            for (int j = 1; j <= valueRulingCount; j++) {
                canvas.drawLine(
                        centerX + (pts[i].x - centerX) / valueRulingCount * j,
                        centerY + (pts[i].y - centerY) / valueRulingCount * j,
                        centerX + (pts[end].x - centerX) / valueRulingCount * j,
                        centerY + (pts[end].y - centerY) / valueRulingCount * j,
                        paint);
            }

            canvas.drawLine(centerX, centerY, pts[i].x, pts[i].y, paint);
        }

        // 畫各向度總和
        for (int i = 0; i < count; i++) {
            total_pts[i].x = (int) (centerX + (pts[i].x - centerX)
                    * totalValues[i] * total_anim_percentage / maxValue);
            total_pts[i].y = (int) (centerY + (pts[i].y - centerY)
                    * totalValues[i] * total_anim_percentage / maxValue);
        }

        totalPath.reset();
        totalPaint.setAntiAlias(true);
        totalPaint.setColor(getResources().getColor(R.color.blue));
        totalPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (int i = 0; i < pts.length; i++) {
            // 给valuePath赋值
            if (i == 0)
                totalPath.moveTo(total_pts[i].x, total_pts[i].y);
            else
                totalPath.lineTo(total_pts[i].x, total_pts[i].y);
        }
        totalPaint.setAlpha(150);
        canvas.drawPath(totalPath, totalPaint);

        // 畫各向度核心
        for (int i = 0; i < count; i++) {
            core_pts[i].x = (int) (centerX + (pts[i].x - centerX)
                    * coreValues[i] * core_anim_percentage / maxValue);
            core_pts[i].y = (int) (centerY + (pts[i].y - centerY)
                    * coreValues[i] * core_anim_percentage / maxValue);
        }

        corePath.reset();
        corePaint.setAntiAlias(true);
        corePaint.setColor(getResources().getColor(R.color.blue));
        corePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (int i = 0; i < pts.length; i++) {
            // 给valuePath赋值
            if (i == 0)
                corePath.moveTo(core_pts[i].x, core_pts[i].y);
            else
                corePath.lineTo(core_pts[i].x, core_pts[i].y);
        }
        corePaint.setAlpha(150);
        canvas.drawPath(corePath, corePaint);

        // 畫頂點

        for (int i = 0; i < pts.length; i++) {
            canvas.drawCircle(total_pts[i].x, total_pts[i].y, point_radius,
                    paint);
            canvas.drawCircle(core_pts[i].x, core_pts[i].y, point_radius, paint);
        }

        // 標上刻度
        paint.setTextSize(getResources().getDimension(R.dimen.textMedium));
        paint.setColor(getResources().getColor(R.color.darken));
        paint.setTextAlign(Align.RIGHT);
        for (int i = 0; i <= 6; i += 2) {
            canvas.drawText(String.valueOf(i), centerX - 5
                    + (pts[0].x - centerX) * i / maxValue, centerY + 25
                    + (pts[0].y - centerY) * i / maxValue, paint);
        }

        // 標示文字
        paint.setTextSize(getResources().getDimension(R.dimen.textMedium));
        paint.setColor(getResources().getColor(R.color.darken));
        FontMetrics fontMetrics = paint.getFontMetrics();
        float fontHegiht = -fontMetrics.ascent;
        for (int i = 0; i < count; i++) {
            int offsetX = 0;
            int offsetY = -5;
            int angle_temp = ((int) (angle_offset + angle * i) % 360);
            if (angle_temp < 0 || angle_temp > 180) {
                offsetY = (int) fontHegiht + 5;
            }
            if (angle_temp > 90 && angle_temp < 270) {
                offsetX = 10;
            } else if (angle_temp > 270 || angle_temp < 90) {
                offsetX = -10;
            }
            paint.setTextAlign(Align.CENTER);
            canvas.drawText(titles[i], pts[i].x + offsetX, pts[i].y + offsetY,
                    paint);
        }
    }
}
