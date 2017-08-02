/**
 * BitmapUtil.java
 * YUNEEC_SDK_ANDROID
 *
 * Copyright @ 2016 Yuneec.
 * All rights reserved.
 *
 */

package com.longkai.stcarcontrol.st_exp.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * 用于加工bitmap的工具类
 *
 * @author zp
 * @version 1.0, 17-2-28
 * @since 1.0
 */
public class BitmapUtil {

    /**
     * 在bitmap上面添加文字
     * @param  drawable 需要转换的drawable
     * @return bitmap 加工完成的bitmap
     */

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                : Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);

        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 在bitmap上面添加文字
     * @param  context 上下文
     * @param bitmap 需要修改的bitmap
     * @param text 需要添加的文字
     * @param paint 画笔
     * @param paddingLeft 左边偏量
     * @param paddingTop  顶部变量
     * @return bitmap 加工完成的bitmap
     */

    private static Bitmap drawTextToBitmap(Context context, Bitmap bitmap, String text,
                                           Paint paint, float paddingLeft, float paddingTop) {
        Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
//        if (bitmapConfig == null) {
//            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
//        }
//        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, paddingLeft, paddingTop, paint);

        return bitmap;
    }

    /**
     * 在bitmap上面添加文字
     * @param  context 上下文
     * @param bitmap 需要修改的bitmap
     * @param text 需要添加的文字
     * @param size 尺寸
     * @param paddingLeft 左边偏量
     * @param paddingTop  顶部变量
     * @return bitmap 加工完成的bitmap
     */
    public static Bitmap readyAndTextToBitmap(Context context,Bitmap bitmap, String text,

                                              int size, float paddingLeft, float paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setTextSize(size);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, paddingLeft, paddingTop);
//        paint.setTextSize(dp2px(context, size));
//        Rect bounds = new Rect();
//        paint.getTextBounds(text, 0, text.length(), bounds);
//        return drawTextToBitmap(context, bitmap, text, paint,
//                dp2px(context, paddingLeft),
//                dp2px(context, paddingTop));
    }
    //将dip转成pix
//    public static int dp2px(Context context, float dp) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dp * scale + 0.5f);
//    }


}
