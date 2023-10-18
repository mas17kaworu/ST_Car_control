package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.dp2px

 class AppsGridView :ViewGroup{
    private var space= 4.dp2px(context)
    private var space_mid= 8.dp2px(context)
    private var space_bottom= 5.dp2px(context)
    private var column = 3
    private var row: Int = 0
    private var _paddingLeft= 0
    private var _paddingRight =0
    private var noGoneCount: Int =0
    constructor(context: Context):super(context)
    constructor(context: Context,attributeSet: AttributeSet?):super(context,attributeSet){
        initView(attributeSet)
    }

    private fun initView(attributeSet: AttributeSet?) {
         attributeSet?.let {
            var a= context?.obtainStyledAttributes(it,R.styleable.AppGrid)?.apply {
                column = getInteger(R.styleable.AppGrid_column, 3)
                row = getInteger(R.styleable.AppGrid_row, -1)
                space_mid = getDimension(R.styleable.AppGrid_space_mid,8.dp2px(context).toFloat()).toInt()
                space_bottom = getDimension(R.styleable.AppGrid_space_bottom,5.dp2px(context).toFloat()).toInt()
             }
             a?.recycle()
         }
     }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val cellWidth = (width - _paddingLeft - _paddingRight -space_mid *(column -1)) / column
        var cellHeight = 0
        if(row > 0){
            cellHeight = (height/row -space_bottom)
        }

        val childWidthSpec = MeasureSpec.makeMeasureSpec(cellWidth, MeasureSpec.EXACTLY)
        val childHeightSpec = if(row > 0) MeasureSpec.makeMeasureSpec(cellHeight, MeasureSpec.EXACTLY)  else MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 1, MeasureSpec.UNSPECIFIED)
        val count = childCount
        noGoneCount = 0
        var itemVisibleHeight = 0
        for (i in 0 until count) {
            if (getChildAt(i).visibility != View.GONE) {
                getChildAt(i).measure(childWidthSpec, childHeightSpec)
                noGoneCount++
                itemVisibleHeight = getChildAt(i).measuredHeight
            }
        }
        var notGoneRows = Math.ceil((noGoneCount / column.toFloat()).toDouble()).toInt()
        var col = column
        if (row in 1..(notGoneRows - 1)) {
            col = Math.ceil((noGoneCount / row.toFloat()).toDouble()).toInt()
            notGoneRows = row
        }
        setMeasuredDimension(cellWidth * col + _paddingLeft + _paddingRight +space_mid *(column -1)
            , itemVisibleHeight * notGoneRows  +space_bottom *notGoneRows)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var cellWidth: Int
        var cellHeight: Int
        val childCount = childCount
        var left: Int
        var top = 0

        var notGoneItemCount = 0
        val notGoneRows = Math.ceil((noGoneCount / column.toFloat()).toDouble()).toInt()
        var col = column
        if (row in 1..(notGoneRows - 1)) {
            col = Math.ceil((noGoneCount / row.toFloat()).toDouble()).toInt()
        }

        left = _paddingLeft
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }

            cellWidth = child.measuredWidth
            cellHeight = child.measuredHeight
            notGoneItemCount++

            child.layout(left , top, left + cellWidth, top + cellHeight)
            left += if((i+ 1) % column == 0){
                cellWidth
            } else {
                cellWidth+space_mid
            }


            if (notGoneItemCount % col == 0) {
                left = _paddingLeft
                top += cellHeight + space_bottom
            }
        }
    }
}


