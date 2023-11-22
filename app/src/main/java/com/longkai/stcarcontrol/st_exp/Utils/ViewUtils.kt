package com.longkai.stcarcontrol.st_exp.Utils

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import java.text.DecimalFormat

fun View.hideSoftKeyboard() {
    val imm = getSystemService(context, InputMethodManager::class.java) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

var decimalFormat = DecimalFormat("#.0")

const val MSG_REFRESH_VIEW_RECYCLE = 0X01
val listRunnable: ArrayList<Runnable> = ArrayList();
var isRunnableViewsVisible = true
var recycleHandler = object : Handler(Looper.getMainLooper()) {
    override fun dispatchMessage(msg: Message) {
        try {
            super.dispatchMessage(msg)
            if (msg != null && msg.what == MSG_REFRESH_VIEW_RECYCLE) {
                Log.d("recycleHandler", "isRunnableViewsVisible $isRunnableViewsVisible")
                try {
                    if (isRunnableViewsVisible) {
                        for (run in listRunnable) {
                            run.run()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (listRunnable.size > 0) {
                    removeMessages(MSG_REFRESH_VIEW_RECYCLE)
                    sendEmptyMessageDelayed(MSG_REFRESH_VIEW_RECYCLE, 1000);
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun addViewRefreshRunnable(runnable: Runnable) {
    try {
        if (!listRunnable.contains(runnable)) {
            listRunnable.add(runnable)
            isRunnableViewsVisible = true
        }
        if (!recycleHandler.hasMessages(MSG_REFRESH_VIEW_RECYCLE)) {
            recycleHandler.sendEmptyMessageDelayed(MSG_REFRESH_VIEW_RECYCLE, 1000)
        }
        Log.d("recycleHandler ", "list: ${listRunnable.size}")
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun removeViewRefreshRunnable(runnable: Runnable) {
    try {
        if (listRunnable.contains(runnable)) {
            listRunnable.remove(runnable)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun clearViewRefreshRunnable() {
    try {
        recycleHandler.removeMessages(MSG_REFRESH_VIEW_RECYCLE)
        listRunnable.clear()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun pauseRunnable() {
    isRunnableViewsVisible = false
}

fun resumeRunnable() {
    isRunnableViewsVisible = true
}