package com.taipeitech.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

public class BitmapUtility {

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap loadBitmap(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        options.inInputShareable = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        options.inSampleSize = calculateInSampleSize(options,
                Utility.getScreenWidth(context),
                Utility.getScreenHeight(context));
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), resId,
                options);
    }

    public static Bitmap loadBitmap(Context context, String file_path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        options.inInputShareable = true;
        BitmapFactory.decodeFile(file_path, options);
        options.inSampleSize = calculateInSampleSize(options,
                Utility.getScreenWidth(context),
                Utility.getScreenHeight(context));
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file_path, options);
    }

    public static Bitmap loadEmptyBitmap(Context context, String file_path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file_path, options);
        Bitmap empty_bitmap = Bitmap.createBitmap(options.outWidth,
                options.outHeight, Config.ALPHA_8);
        return empty_bitmap;
    }

    public static Bitmap loadEmptyBitmap(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        Bitmap empty_bitmap = Bitmap.createBitmap(options.outWidth,
                options.outHeight, Config.ALPHA_8);
        return empty_bitmap;
    }
}
