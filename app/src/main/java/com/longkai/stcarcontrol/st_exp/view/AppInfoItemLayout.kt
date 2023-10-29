package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import com.longkai.stcarcontrol.st_exp.R

object TractionStatus {
    var resolver = false
    var DFA = false
}

class AppInfoItemLayout : RelativeLayout {
    private var bean: AppProgressViewBean? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        LayoutInflater.from(context).inflate(R.layout.fragment_car_app_item_layout, this)
    }

    public fun setData(bean: AppProgressViewBean) {
        this.bean = bean
        setViewData()
    }

    private fun setViewData() {
        bean?.let {
            this.findViewById<TextView>(R.id.app_title)?.text =
                it.text + " " + (it.percent * 100).toInt() + "%"
            var tx = it.des
            this.findViewById<TextView>(R.id.app_desc)?.apply {
                text = tx
                if (text.length > 8) {
                    textSize = 12f
                }
            }
            refreshView()
        }
    }

    private fun refreshView() {
        bean?.let {
            this.findViewById<TextView>(R.id.app_title)?.apply {
                var percent = (it.percent * 100).toInt() + (Math.random() * it.maxRand).toInt()
                if (it.needCheckResolver) {
                    percent = 32+ (Math.random() * 16).toInt()
                    changeTextColor(this,percent,it.text,TractionStatus.resolver)
                }  else if(it.needCheckDFA){
                    percent = 42 + (Math.random() * 16).toInt()
                    changeTextColor(this,percent,it.text,TractionStatus.DFA)
                } else {
                    text = it.text + " " + percent + "%"
                }
            }
            postDelayed({ refreshView() }, 1000)
        }
    }

    private fun changeTextColor(textView:TextView, percent:Int, text:String,needChange:Boolean){
        if (needChange) {
            textView.setTextColor(Color.GREEN)
            var sp = SpannableString(text + " " + percent + "%")
            sp.setSpan(
                ForegroundColorSpan(Color.GREEN),
                text.length,
                sp.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textView.movementMethod = LinkMovementMethod.getInstance()
            textView.text = sp
        } else {
            textView.setTextColor(Color.parseColor("#d9ffffff"))
            textView.text= text + " " + percent + "%"
        }
    }
}