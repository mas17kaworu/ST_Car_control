/**
 * BitmapUtil.java
 * YUNEEC_SDK_ANDROID
 *
 * Copyright @ 2016 Yuneec.
 * All rights reserved.
 *
 */
package com.longkai.stcarcontrol.st_exp.Utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap

/**
 * 用于加工bitmap的工具类
 *
 * @author zp
 * @version 1.0, 17-2-28
 * @since 1.0
 */
object BitmapUtil {
    /**
     * 在bitmap上面添加文字
     * @param  drawable 需要转换的drawable
     * @return bitmap 加工完成的bitmap
     */
    @JvmStatic
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        val config =
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        val bitmap = Bitmap.createBitmap(w, h, config)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }

    fun getRoundedCornerBitmap(bitmap: Bitmap, roundPx: Float): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, w, h)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
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
    private fun drawTextToBitmap(
        context: Context, bitmap: Bitmap, text: String,
        paint: Paint, paddingLeft: Float, paddingTop: Float
    ): Bitmap {
        val bitmapConfig = bitmap.config
        paint.isDither = true // 获取跟清晰的图像采样
        paint.isFilterBitmap = true // 过滤一些
        //        if (bitmapConfig == null) {
//            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
//        }
//        bitmap = bitmap.copy(bitmapConfig, true);
        val canvas = Canvas(bitmap)
        canvas.drawText(text, paddingLeft, paddingTop, paint)
        return bitmap
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
    fun readyAndTextToBitmap(
        context: Context, bitmap: Bitmap, text: String,
        size: Int, paddingLeft: Float, paddingTop: Float
    ): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.RED
        paint.textSize = size.toFloat()
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return drawTextToBitmap(context, bitmap, text, paint, paddingLeft, paddingTop)
        //        paint.setTextSize(dp2px(context, size));
//        Rect bounds = new Rect();
//        paint.getTextBounds(text, 0, text.length(), bounds);
//        return drawTextToBitmap(context, bitmap, text, paint,
//                dp2px(context, paddingLeft),
//                dp2px(context, paddingTop));
    }
}


fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}


fun Context.getBitmapFromVectorDrawable(
    @DrawableRes resId: Int,
    @ColorRes tintColor: Int? = null
): Bitmap {
    val drawable = AppCompatResources.getDrawable(this, resId)!!
    tintColor?.let { drawable.setTint(getColor(it)) }
    return drawable.toBitmap()
}