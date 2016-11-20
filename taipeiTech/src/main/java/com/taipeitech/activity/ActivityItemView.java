package com.taipeitech.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.taipeitech.AnalyticsTrackers;
import com.taipeitech.R;
import com.taipeitech.model.ActivityInfo;
import com.taipeitech.utility.ActivityConnector;
import com.taipeitech.utility.BitmapUtility;

import java.io.File;
import java.lang.ref.WeakReference;

public class ActivityItemView extends RelativeLayout implements OnClickListener {
    private static final int CACHE_SIZE = 50 * 1024 * 1024; // 10MB
    private ActivityInfo activityInfo;
    private ImageView imageView;
    private Tracker tracker;
    private static LruCache<String, Bitmap> imageCache;
    private SetBitmapTask task;

    public ActivityItemView(Context context) {
        super(context);
        inflate(context, R.layout.activity_item, this);
        init();
    }

    private void init() {
        if (imageCache == null) {
            imageCache = new LruCache<String, Bitmap>(CACHE_SIZE) {
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }
        tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        imageView = (ImageView) findViewById(R.id.activity_image);
        setOnClickListener(this);
    }

    public static void clearImageCache() {
        if (imageCache != null) {
            imageCache.evictAll();
        }
    }

    public void setActivityInfo(ActivityInfo activity_info) {
        activityInfo = activity_info;
        TextView title = (TextView) findViewById(R.id.title_textview);
        title.setText(activity_info.getName());
        TextView date = (TextView) findViewById(R.id.date_textview);
        date.setText(activity_info.getStart() + " - " + activity_info.getEnd());
        TextView location = (TextView) findViewById(R.id.location_textview);
        location.setText(activity_info.getLocation());
        loadImage(activity_info.getImage());
    }

    private void loadImage(String file_name) {
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        Bitmap bitmap = imageCache.get(file_name);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setTag(file_name);
            task = new SetBitmapTask(getContext(), imageView, file_name);
            task.execute();
        }
    }

    @Override
    public void onClick(View v) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(getContext().getString(R.string.analytics_category_activity))
                .setAction(getContext().getString(R.string.analytics_action_detail))
                .setLabel(activityInfo.getStart() + "-" + activityInfo.getHost() + "-" + activityInfo.getName())
                .build());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(activityInfo.getName());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.activity_dialog_view, null);
        TextView message = (TextView) view.findViewById(R.id.message);
        message.setText(String.format("%s\n------------------\n主辦單位：%s\n",
                activityInfo.getContent(), activityInfo.getHost())
                + (activityInfo.getUrl() != null ? String.format("\n相關連結：%s\n",
                activityInfo.getUrl()) : ""));
        builder.setView(view);
        builder.setNegativeButton(R.string.add_to_calendar,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory(getContext().getString(R.string.analytics_category_activity))
                                .setAction(getContext().getString(R.string.analytics_action_add_to_calendar))
                                .setLabel(activityInfo.getStart() + "-" + activityInfo.getHost() + "-" + activityInfo.getName())
                                .build());
                        try {
                            Intent calendar_intent = new Intent(
                                    Intent.ACTION_EDIT);
                            calendar_intent
                                    .setType("vnd.android.cursor.item/event");
                            calendar_intent.putExtra("beginTime", activityInfo
                                    .getStartDate().getTime());
                            calendar_intent.putExtra("allDay", true);
                            calendar_intent.putExtra("endTime", activityInfo
                                    .getEndDate().getTime() + 60 * 60 * 1000);
                            calendar_intent.putExtra("title",
                                    activityInfo.getHost() + " | "
                                            + activityInfo.getName());
                            calendar_intent.putExtra("eventLocation",
                                    activityInfo.getLocation());
                            getContext().startActivity(calendar_intent);
                        } catch (Exception e) {
                            Toast.makeText(getContext(),
                                    R.string.calendar_not_support,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.setPositiveButton(R.string.share_using,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory(getContext().getString(R.string.analytics_category_activity))
                                .setAction(getContext().getString(R.string.analytics_action_share))
                                .setLabel(activityInfo.getStart() + "-" + activityInfo.getHost() + "-" + activityInfo.getName())
                                .build());
                        String shareBody = null;
                        if (activityInfo.getStart().equals(
                                activityInfo.getEnd())) {
                            shareBody = String.format("%s | %s \n%s\n%s\n",
                                    activityInfo.getHost(),
                                    activityInfo.getName(),
                                    activityInfo.getStart(),
                                    activityInfo.getLocation());
                        } else {
                            shareBody = String.format("%s | %s \n%s\n%s\n",
                                    activityInfo.getHost(),
                                    activityInfo.getName(),
                                    activityInfo.getStart() + " - "
                                            + activityInfo.getEnd(),
                                    activityInfo.getLocation());
                        }
                        Intent sharing_intent = new Intent(
                                android.content.Intent.ACTION_SEND);
                        sharing_intent.setType("text/plain");
                        sharing_intent.putExtra(
                                android.content.Intent.EXTRA_SUBJECT, "");
                        sharing_intent.putExtra(
                                android.content.Intent.EXTRA_TEXT, shareBody);
                        getContext().startActivity(
                                Intent.createChooser(
                                        sharing_intent,
                                        getContext().getResources().getString(
                                                R.string.share_using)));
                    }
                });
        builder.show();
    }

    private class SetBitmapTask extends AsyncTask<Void, Void, Bitmap> {
        private Context context;
        private WeakReference<ImageView> imageViewRef;
        private String fileName;

        public SetBitmapTask(Context context, ImageView image_view, String file_name) {
            this.context = context;
            imageViewRef = new WeakReference<>(image_view);
            fileName = file_name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Bitmap bitmap = null;
            if (!TextUtils.isEmpty(fileName)) {
                String file_path = context.getCacheDir().getPath()
                        + "/" + fileName;
                File file = new File(file_path);
                if (file.exists()) {
                    bitmap = BitmapUtility.loadEmptyBitmap(context, file_path);
                }
            }
            if (bitmap == null) {
                bitmap = BitmapUtility.loadBitmap(context, R.drawable.no_image);
            }
            ImageView image_view = imageViewRef.get();
            if (image_view != null) {
                if (image_view.getTag().equals(fileName)) {
                    image_view.setImageBitmap(bitmap);
                }
            }
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;
            try {
                if (!TextUtils.isEmpty(fileName)) {
                    String file_path = context.getCacheDir().getPath()
                            + "/" + fileName;
                    File file = new File(file_path);
                    if (!file.exists()) {
                        ActivityConnector.downloadActivityImage(context, fileName);
                    }
                    bitmap = BitmapUtility.loadBitmap(context, file_path);
                    if (bitmap != null) {
                        imageCache.put(fileName, bitmap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null) {
                return;
            }
            ImageView image_view = imageViewRef.get();
            if (image_view != null) {
                if (image_view.getTag().equals(fileName)) {
                    image_view.setImageBitmap(bitmap);
                    AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
                    animation.setDuration(500);
                    animation.setInterpolator(new LinearInterpolator());
                    image_view.startAnimation(animation);
                }
            }
        }

    }
}
