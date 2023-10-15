package com.longkai.stcarcontrol.st_exp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.mockMessage.MockMessageServiceImpl
import com.longkai.stcarcontrol.st_exp.view.AppProgressViewBean
import com.longkai.stcarcontrol.st_exp.view.AppsInfoLayout

class CarInfoFragment : Fragment() {
    private var mRootView: View? = null
    private var mList: List<AppProgressViewBean> = ArrayList()
    private var mListView: ArrayList<View> = ArrayList()
    private var selectIndex = 0;
    private var appsInfoLayout: AppsInfoLayout? = null
    private var viewClick: OnClickListener = OnClickListener {
        it.tag?.apply {
            selectIndex = this as Int
            appsInfoLayout?.updateSelectByIndex(selectIndex)
            refreshViewSelect()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mList = initAppInfo()
        mRootView = inflater.inflate(R.layout.fragment_carinfo_layout, null)
        appsInfoLayout = mRootView?.findViewById<AppsInfoLayout>(R.id.apps_info)?.apply {
            initData(mList)
            setClickListener(object : AppsInfoLayout.AppInfoClick {
                override fun viewClick(index: Any) {
                    selectIndex = index as Int
                    refreshViewSelect()
                }
            })
        }
        addViews()
//        MockMessageServiceImpl.getService().StartService(CarInfoFragment::class.java.toString())
        return mRootView
    }

    private fun initAppInfo(): List<AppProgressViewBean> {
        var list = ArrayList<AppProgressViewBean>()
        list.add(AppProgressViewBean(text = "APP #1", percent = 0.10f, des = "VCU"))
        list.add(AppProgressViewBean("APP #2", percent = 0.20f, des = "Traction"))
        list.add(AppProgressViewBean("APP #3", percent = 0.30f, des = "BMS"))
        list.add(AppProgressViewBean("APP #4", percent = 0.50f, des = "OBC"))
        list.add(AppProgressViewBean("APP #5", percent = 0.60f, des = "DC/DC"))
        list.add(AppProgressViewBean("APP #6", percent = 0.80f, des = "AutoSAR & FuSa"))
        return list;
    }

    private fun addViews() {
        mListView.clear()
        var index = 0;
        mRootView?.let {
            var view1 = it.findViewById<View>(R.id.car_info_layout_1)
            view1.tag = index++
            view1.isSelected = true
            view1.setOnClickListener(viewClick)
            mListView.add(view1)

            var view2 = it.findViewById<View>(R.id.car_info_layout_2)
            view2.tag = index++
            view2.setOnClickListener(viewClick)
            mListView.add(view2)

            var view3 = it.findViewById<View>(R.id.car_info_layout_3)
            view3.tag = index++
            view3.setOnClickListener(viewClick)
            mListView.add(view3)

            var view4 = it.findViewById<View>(R.id.car_info_layout_4)
            view4.tag = index++
            view4.setOnClickListener(viewClick)
            mListView.add(view4)

            var view5 = it.findViewById<View>(R.id.car_info_layout_5)
            view5.tag = index++
            view5.setOnClickListener(viewClick)
            mListView.add(view5)

            var view6 = it.findViewById<View>(R.id.car_info_layout_6)
            view6.tag = index++
            view6.setOnClickListener(viewClick)
            mListView.add(view6)
        }
    }

    private fun refreshViewSelect() {
        var index = 0
        for (view in mListView) {
            view.isSelected = selectIndex == index++
        }

    }
}