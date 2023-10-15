package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.longkai.stcarcontrol.st_exp.R

class NinthLayout:RelativeLayout {
    private var gridView:AppsGridView?=null
    constructor(context: Context):super(context)

    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet)

    init {
        LayoutInflater.from(context).inflate(R.layout.ninth_layout,this)
//        gridView = findViewById(R.id.ninth_girdview)
//        initView()
    }

    private fun initView(){
        gridView?.let {
            for(index in 0 until 12){
                val view = LayoutInflater.from(context).inflate(R.layout.ninth_logo_item_view,null)
                it.addView(view)
            }
        }
    }


}