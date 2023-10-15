package com.longkai.stcarcontrol.st_exp.Utils

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import java.text.DecimalFormat

fun View.hideSoftKeyboard() {
    val imm = getSystemService(context, InputMethodManager::class.java) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}
var decimalFormat =  DecimalFormat("#.0")