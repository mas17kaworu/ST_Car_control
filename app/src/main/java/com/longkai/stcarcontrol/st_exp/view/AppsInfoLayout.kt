package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View


import android.widget.RelativeLayout
import android.widget.TextView
import com.longkai.stcarcontrol.st_exp.R

class AppsInfoLayout : RelativeLayout {

    private var gridLayout: AppsGridView
    private var viewBeans: List<AppProgressViewBean>? = null
    private var views: ArrayList<View> = ArrayList();
    private var selectView: View? = null
    private var appInfoClick: AppInfoClick? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        LayoutInflater.from(context).inflate(R.layout.fragment_car_apps_info_layout, this)
        gridLayout = findViewById(R.id.app_info_layout)

    }

    public fun initData(viewBeans: List<AppProgressViewBean>) {
        this.viewBeans = viewBeans
        gridLayout.removeAllViews()
        views.clear()
        for ((index, viewBean) in viewBeans.withIndex()) {
            var layout =AppInfoItemLayout(context)
            if (viewBean.overlap) {
                layout.setBackgroundResource(R.drawable.app_item_bg_overlap)
            } else {
                layout.setBackgroundResource(R.drawable.app_item_bg)
            }
            layout.setData(viewBean)
            layout.setOnClickListener() {
                selectView = it
                updateSelect()
                appInfoClick?.apply {
                    viewClick(it.tag)
                }
            }
            if (selectView == null) {
                selectView = layout
                layout.isSelected = true
            }
            layout.tag = index
            views.add(layout)
            gridLayout.addView(layout)
        }
    }

    private fun updateSelect() {
        for (view in views) {
            view.isSelected = selectView == view
        }
    }

    public fun updateSelectByIndex(index: Int) {
        var viewIndex = 0
        for (view in views) {
            view.isSelected = viewIndex++ == index
        }
    }

    public fun setClickListener(appInfoClick: AppInfoClick) {
        this.appInfoClick = appInfoClick
    }

    public interface AppInfoClick {
        fun viewClick(index: Any)
    }

}
