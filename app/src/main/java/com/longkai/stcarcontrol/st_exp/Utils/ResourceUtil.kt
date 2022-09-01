package com.longkai.stcarcontrol.st_exp.Utils

import android.content.Context

//将dip转成pix
fun dp2px(context: Context, dp: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun Float.dp2px(context: Context) = dp2px(context, this)
fun Int.dp2px(context: Context) = dp2px(context, this.toFloat())
